package com.example.andrii.flashchat.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.Person;
import com.squareup.picasso.Picasso;

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

    Context context;

    public ImageTools(Context context) {
        this.context = context;
    }

    public void downloadImage(ImageView imageView, Person p) {

            String root = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
            File file = new File(root,p.getId() + ".jpg");

            if (file.exists()){
                Log.d("qwe","from from cash");
                String path = file.getPath();
                Uri uri = Uri.fromFile(new File(path));
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                } catch (IOException e) {
                    Log.e("qwe","IOException",e);
                    image = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_action_person);
                }
                imageView.setImageBitmap(image);
            }else{
                if (!p.getPhotoUrl().equals("no_facebook_url")){
                        Uri uri = Uri.parse(p.getPhotoUrl());

                        Observable<Bitmap> downloadObservable = Observable.just(uri)
                                .observeOn(Schedulers.io())
                                .subscribeOn(Schedulers.io())
                                .map(u -> {
                                    Bitmap bitmap = null;
                                    try {
                                        bitmap = Picasso.with(context).load(u).get();
                                    } catch (IOException e) {
                                        Log.e("qwe","Picasso error",e);
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
                                        Log.d("qwe","Downloaded photo from facebook");
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e("qwe","onErrorPicasso",e);

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
                    //просим у сервера
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
}
