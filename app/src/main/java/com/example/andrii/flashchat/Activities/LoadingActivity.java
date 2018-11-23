package com.example.andrii.flashchat.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.DB.MessageDb;
import com.example.andrii.flashchat.data.DB.UserNamesBd;
import com.example.andrii.flashchat.data.SingletonConnection;
import com.example.andrii.flashchat.data.actions.ActionGetAllMessages;
import com.example.andrii.flashchat.data.actions.ActionGetNames;
import com.example.andrii.flashchat.tools.ImageTools;
import com.example.andrii.flashchat.tools.QueryAction;
import com.example.andrii.flashchat.tools.QueryPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import rx.Observer;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class LoadingActivity extends AppCompatActivity {
    private static String TAG = "LoadingActivity";
    private Realm realm;

    public static Intent newIntent(Context context){
        return new Intent(context,LoadingActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);
        Log.d(TAG,"Loading activity start");

        realm = Realm.getDefaultInstance();

        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/MISTRAL.TTF");
        TextView tvAppName = findViewById(R.id.tv_app_name);
        tvAppName.setTypeface(typeface);

        downloadMessages();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        QueryAction.unsubscribeAll();
    }

    private void downloadMessages() {
        String id = QueryPreferences.getActiveUserId(this);
        ActionGetAllMessages action = new ActionGetAllMessages(id);
        Subscription subscription = QueryAction.executeAnswerQuery(action)
                .timeout(100, TimeUnit.SECONDS, Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"Error with getting messages",e);
                        String userId = QueryPreferences.getActiveUserId(LoadingActivity.this);
                        Intent intent = MessagesListActivity.newIntent(LoadingActivity.this,userId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Toast.makeText(LoadingActivity.this,"Error with loading data.Please check your connection.",Toast.LENGTH_LONG).show();
                        LoadingActivity.this.startActivity(intent);
                        LoadingActivity.this.finish();

                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG,String.valueOf(!s.equals("error")) + s);
                        if (!s.equals("error")) {
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<MessageDb>>(){}.getType();
                            List<MessageDb> list = gson.fromJson(s,listType);
                            String root = LoadingActivity.this
                                    .getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                                    .getPath();
                            realm.executeTransactionAsync(
                                    r -> {
                                        //transaction
                                        for (MessageDb m:list){
                                            Log.d(TAG,m.toString());
                                            if (m.getType() == 1){
                                                File file = new File(root,m.getMsgID() + ".jpg");
                                                if (!file.exists()) {
                                                    byte[] imageBytes = Base64.decode(m.getText(),Base64.DEFAULT);
                                                    Bitmap image = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
                                                    ImageTools tools = new ImageTools(LoadingActivity.this);
                                                    tools.saveImage(file, image);
                                                    m.setText("Image");
                                                }
                                            }
                                            r.insertOrUpdate(m);
                                        }
                                    },
                                    () -> {
                                        //onSuccess
                                        SingletonConnection.getInstance().close();
                                        downloadNames();
                                    },
                                    error -> {
                                        //onError
                                        Log.e(TAG,"Transaction error");
                                        onError(error);
                                    });
                        } else{
                            Log.e(TAG,"Error with getting messages");
                            onError(new Throwable("error"));
                        }
                    }
                });
        QueryAction.addSubscription(subscription);
    }

    private void downloadNames(){
        ActionGetNames action = new ActionGetNames(QueryPreferences.getActiveUserId(this));
        Subscription subscription = QueryAction.executeAnswerQuery(action)
                .timeout(100, TimeUnit.SECONDS, Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"Error with getting names",e);
                        String userId = QueryPreferences.getActiveUserId(LoadingActivity.this);
                        Intent intent = MessagesListActivity.newIntent(LoadingActivity.this,userId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Toast.makeText(LoadingActivity.this,"Error with loading data.Please check your connection.",Toast.LENGTH_LONG).show();
                        LoadingActivity.this.startActivity(intent);
                        LoadingActivity.this.finish();
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG,s);
                        if (!s.equals("error")) {
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<UserNamesBd>>(){}.getType();
                            List<UserNamesBd> list = gson.fromJson(s,listType);

                            String root = LoadingActivity.this
                                    .getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                                    .getPath();
                            realm.executeTransactionAsync(
                                    r -> {
                                        //transaction
                                        for (UserNamesBd m:list){
                                            if(!m.getImageSrc().contains("https:") && !m.getImageSrc().equals("no_facebook_url")){
                                                File file = new File(root,m.getUserId() + ".jpg");
                                                if (!file.exists()) {
                                                    byte[] imageBytes = Base64.decode(m.getImageSrc(),Base64.DEFAULT);
                                                    Bitmap image = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
                                                    Matrix matrix = new Matrix();
                                                    matrix.postRotate(-90);

                                                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(image,image.getWidth(),image.getHeight(),true);

                                                    Bitmap rotated = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                                                    ImageTools tools = new ImageTools(LoadingActivity.this);
                                                    tools.saveImage(file, rotated);
                                                    m.setImageSrc("no_facebook_url");
                                                }
                                            }
                                            r.insertOrUpdate(m);
                                        }
                                    },
                                    () -> {
                                        //onSuccess
                                        String userId = QueryPreferences.getActiveUserId(LoadingActivity.this);
                                        Intent intent = MessagesListActivity.newIntent(LoadingActivity.this,userId);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        LoadingActivity.this.startActivity(intent);
                                        LoadingActivity.this.finish();
                                    },
                                    error -> {
                                        //onError
                                        Log.e(TAG,"Transaction error");
                                        onError(error);
                                    });
                        } else{
                            Log.e(TAG,"Error with getting names");
                            onError(new Throwable("error"));
                        }
                    }
                });
        QueryAction.addSubscription(subscription);
    }
}
