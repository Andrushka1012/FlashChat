package com.example.andrii.flashchat.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.data.actions.Action;
import com.example.andrii.flashchat.data.actions.ActionLoadImage;
import com.example.andrii.flashchat.data.actions.ActionSendImage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ImageTools {
    private final String TAG = "ImageTools";
    private Context context;

    public ImageTools(Context context) {
        this.context = context;
    }

    public void downloadPersonImage(ImageView imageView, Person p) {
            
            String root = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
            File file = new File(root,p.getId() + ".jpg");

            if (file.exists()){
                Log.d(TAG,"from from cash");
                String path = file.getPath();

                Picasso.with(context)
                        .load(new File(path))
                        .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                        .into(imageView);
            }else{
                if (!p.getPhotoUrl().equals("no_facebook_url")){
                        Uri uri = Uri.parse(p.getPhotoUrl());

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
                                        imageView.setImageBitmap(bitmap);
                                        String root = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
                                        File file = new File(root,p.getId() + ".jpg");
                                        saveImage(file,bitmap);
                                    }
                                });
                }else {
                    Toast.makeText(context,"Downloading from server",Toast.LENGTH_LONG).show();
                    ActionLoadImage actionLoadImage = new ActionLoadImage(p.getId(),p.getId());
                    QueryAction.executeAnswerQuery(actionLoadImage)
                            .subscribe(new Observer<String>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(context,"Error with downloading photo from server.",Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onNext(String s) {
                                    if (s.equals("error")) {
                                        Toast.makeText(context,"Error with downloading photo from server.",Toast.LENGTH_LONG).show();
                                        Picasso.with(context).load(R.drawable.ic_action_person).into(imageView);
                                    }
                                    else{
                                        Toast.makeText(context,"Complete",Toast.LENGTH_LONG).show();
                                        Log.d(TAG,"String:" + s);
                                        Log.d(TAG,"len:" + s.length());
                                        JsonParser jsonParser = new JsonParser();
                                        JsonObject obj = (JsonObject) jsonParser.parse(s);
                                        String endcodedString = obj.get("str").getAsString();
                                        Log.d(TAG,endcodedString);

                                        byte[] imageBytes = Base64.decode(endcodedString,Base64.DEFAULT);
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

                }

        }
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
        QueryAction.executeAnswerQuery(actionObservable)
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





    }
}
