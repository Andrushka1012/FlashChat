package com.example.andrii.flashchat.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.DB.UserNamesBd;
import com.example.andrii.flashchat.data.Model.Person;
import com.example.andrii.flashchat.data.actions.Action;
import com.example.andrii.flashchat.data.actions.ActionLoadImage;
import com.example.andrii.flashchat.data.actions.ActionSendImage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ImageTools {
    private final String TAG = "ImageTools";
    private Context context;
    private static List<String> downloaded = new ArrayList<>();

    public ImageTools(Context context) {
        this.context = context;
    }

    public void downloadPersonImage(ImageView imageView, Person p,boolean downloadFromServer) {
            Log.d(TAG,p.getId() + " " + p.getName());
            String root = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
            File file = new File(root,p.getId() + ".jpg");

            if (file.exists()){
                Log.d(TAG,"from from cash");
                String path = file.getPath();

                Picasso.with(context)
                        .load(new File(path))
                        .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                        .into(imageView);
            }else
                if (downloadFromServer){
                    downloadFromServer(imageView,p);
                } else{
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_action_person);
                    imageView.setImageBitmap(bitmap);
                }
    }

    public void downloadFromServer(ImageView imageView, Person p){
        Log.d(TAG,"From Server " + p.getId() + " " + p.getName());
        String url = p.getPhotoUrl();
        if (url == null){
            Realm realm = Realm.getDefaultInstance();
            UserNamesBd unb = realm.where(UserNamesBd.class).equalTo("userId",p.getId()).findFirst();
            if (unb == null) {
                Log.e(TAG,"not found user uri");
                url = "no_facebook_url";
            }else url = unb.getImageSrc();
            realm.close();
        }

        if (!url.equals("no_facebook_url")){
            Uri uri = Uri.parse(url);
            Log.d(TAG,"Downloading from server url:" + url + " id:" + p.getId());
            Observable<Bitmap> downloadObservable = Observable.just(uri)
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .map(u -> {
                        Bitmap bitmap = null;
                        try {
                            bitmap = Picasso.with(context)
                                    .load(u)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                                    .get();
                        } catch (IOException e) {
                            Log.e(TAG,"Picasso error",e);
                            bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_action_person);
                            imageView.setImageBitmap(bitmap);
                            return null;
                        }
                        return bitmap;
                    });

            Observable<Bitmap> observable = Observable.empty();
            observable.mergeWith(downloadObservable)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .timeout(5, TimeUnit.SECONDS)
                    .subscribe(new Observer<Bitmap>() {
                        @Override
                        public void onCompleted() {
                            Log.d(TAG,"Downloaded photo from facebook");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG,"onErrorPicasso",e);

                        }

                        @Override
                        public void onNext(Bitmap bitmap) {
                            if (bitmap == null) onError(new Throwable("Bitmap == null"));
                            imageView.setImageBitmap(bitmap);
                            String root = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
                            File file = new File(root,p.getId() + ".jpg");
                            saveImage(file,bitmap);
                        }
                    });
        }else {

            ActionLoadImage actionLoadImage = new ActionLoadImage(p.getId(),p.getId());

           Subscription subscription = QueryAction.executeAnswerQuery(actionLoadImage)
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(String s) {
                            if (s.equals("not found")){
                                Log.d(TAG,"not found image");
                                Picasso.with(context).load(R.drawable.ic_action_person).into(imageView);
                            }
                            if (s.equals("error")) {
                                Picasso.with(context).load(R.drawable.ic_action_person).into(imageView);
                            }
                            else{
                                Log.d(TAG,"String:" + s);
                                Log.d(TAG,"len:" + s.length());
                                JsonParser jsonParser = new JsonParser();
                                JsonObject obj = (JsonObject) jsonParser.parse(s);
                                String encodedString = obj.get("str").getAsString();
                                Log.d(TAG,encodedString);

                                byte[] imageBytes = Base64.decode(encodedString,Base64.DEFAULT);
                                Bitmap image = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);


                                Matrix matrix = new Matrix();
                                matrix.postRotate(-90);

                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(image,image.getWidth(),image.getHeight(),true);

                                Bitmap rotated= Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);


                                String root = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
                                File file = new File(root,p.getId() + ".jpg");
                                saveImage(file,rotated);
                            }
                        }
                    });
            QueryAction.addSubscription(subscription);
        }


    }

    public void downloadMessageImageAndSave(String userID,String msgID,File file){
        ActionLoadImage actionLoadImage = new ActionLoadImage(userID,msgID);
        QueryAction.executeAnswerQuery(actionLoadImage)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG,"Image " + msgID + " was downloaded");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"Error:",e);
                    }

                    @Override
                    public void onNext(String s) {
                        if (s.equals("not found")){
                            onError(new Throwable("Not found image"));
                        }
                        if (s.equals("error")){
                            onError(new Throwable("Error with downloading image"));

                        }else{
                            JsonParser jsonParser = new JsonParser();
                            JsonObject obj = (JsonObject) jsonParser.parse(s);
                            String encodedString = obj.get("str").getAsString();
                            Log.d(TAG,"imageString:" + encodedString);

                            byte[] imageBytes = Base64.decode(encodedString,Base64.DEFAULT);
                            Bitmap image = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
                            saveImage(file, image);
                        }
                    }
                });
    }

    public void saveImage(File file,Bitmap image){
        Observable.just(image)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(bitmap -> {
                    if (file.exists ()) file.delete();

                    try {
                        FileOutputStream out = new FileOutputStream(file);

                        image.compress(Bitmap.CompressFormat.JPEG, 90, out);

                        out.flush();
                        out.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void sendImage(File file,String msg_id,String sender_id,String recipient_id) {

        Observable<Action> actionObservable = Observable.just(file)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(f -> {
                    Uri uri = Uri.fromFile(f);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                    } catch (IOException e) {
                        Log.e(TAG,"IoEx:",e);
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    ActionSendImage action = new ActionSendImage(msg_id,encodedImage,sender_id,recipient_id);

                    return action;
                });
        Subscription subscription = QueryAction.executeAnswerQuery(actionObservable)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG,"onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"onError",e);
                    }

                    @Override
                    public void onNext(String s) {
                        if (s.equals("error")) Toast.makeText(context,"Error with sanding photo.",Toast.LENGTH_LONG).show();
                    }
                });
        QueryAction.addSubscription(subscription);
    }

    public void saveToGallery(String msgId){

        String root = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
        File file = new File(root,msgId + ".jpg");


        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(Uri.fromFile(file));
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
            MediaStore.Images.Media.insertImage(context.getContentResolver(),bmp,msgId,"FlashChatImage");
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap roundCorners(Bitmap bitmap){
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        Rect rect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        RectF rectF = new RectF(rect);
        float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0,0,0,0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF,roundPx,roundPx,paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap,rect,rect,paint);

        return output;
    }







}
