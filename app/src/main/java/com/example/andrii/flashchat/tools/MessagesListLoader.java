package com.example.andrii.flashchat.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.example.andrii.flashchat.data.DB.MessageDb;
import com.example.andrii.flashchat.data.Model.Person;
import com.example.andrii.flashchat.data.SingletonConnection;
import com.example.andrii.flashchat.data.actions.ActionGetNewMessages;
import com.example.andrii.flashchat.data.actions.ActionGetOnlinePersonData;
import com.example.andrii.flashchat.fragments.RecyclerViewFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class MessagesListLoader {
    private static String TAG = "MessagesListLoader";
    private Runnable runnable;
    private Observable<RecyclerViewFragment> fragmentObservable;
    private Context context;
    private Realm realm;
    List<Person> onlineList = new ArrayList<>();

    public MessagesListLoader(Context context,Observable<RecyclerViewFragment> fragmentObservable) {
        this.context = context;
        this.fragmentObservable = fragmentObservable;
        this.realm = Realm.getDefaultInstance();
        runnable = this::updateOnline;
        runnable.run();
    }

    public void update(){
     runnable.run();
    }

    private void updateMessages(){
        ActionGetNewMessages action = new ActionGetNewMessages(QueryPreferences.getActiveUserId(context));
        Subscription subscription = QueryAction.executeAnswerQuery(action).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG,"OnError:",e);
                fragmentObservable.subscribe(recyclerViewFragment -> recyclerViewFragment.updateUi(null));
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG,String.valueOf(!s.equals("error")) + s);
                if (!s.equals("error")) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<MessageDb>>(){}.getType();
                    List<MessageDb> list = gson.fromJson(s,listType);
                    String root = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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
                                            ImageTools tools = new ImageTools(context);
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
                                fragmentObservable.subscribe(recyclerViewFragment -> recyclerViewFragment.updateUi(onlineList));
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

    private void updateOnline(){
        ActionGetOnlinePersonData action = new ActionGetOnlinePersonData(QueryPreferences.getActiveUserId(context));
        Subscription subscription = QueryAction.executeAnswerQuery(action).subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        updateMessages();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"OnError:",e);
                        fragmentObservable.subscribe(recyclerViewFragment -> recyclerViewFragment.updateUi(null));
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG,"onNext:" + s);
                        if (!s.equals("error")){
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Person>>(){}.getType();
                            onlineList = gson.fromJson(s,listType);
                            fragmentObservable.subscribe(recyclerViewFragment -> recyclerViewFragment.updateUi(onlineList));
                        }else {
                            onError(new Throwable("Request given answer:error"));
                        }
                    }
                });
        QueryAction.addSubscription(subscription);
    }

}
