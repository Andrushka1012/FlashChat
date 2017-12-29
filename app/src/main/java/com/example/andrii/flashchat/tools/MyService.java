package com.example.andrii.flashchat.tools;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.andrii.flashchat.Activities.CorrespondenceListActivity;
import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.data.actions.ActionCheckNewMessages;
import com.google.gson.Gson;

import rx.Observer;
import rx.Subscription;

public class MyService extends Service {
    public static final String TAG = "MyService";
    public static final int POLL_INTERVAL = 1000 * 30;
    private Subscription subscription;
    private Runnable runnable = this::doTask;
    private final Handler handler = new Handler();
    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        Log.d(TAG,"onStartCommand");
        doTask();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy service");
        Intent broadcastIntent = new Intent("com.android.techtrainner");
        sendBroadcast(broadcastIntent);
        stopTask();
        super.onDestroy();
    }

    private void doTask() {
        Log.d(TAG,"doTask");
        /*if (!isNetworkAvailableAndConnected() && QueryPreferences.getActiveUserId(this) == null){
            Log.e(TAG,"Network is not available or no set active user");
            return;
        }*/


        ActionCheckNewMessages action = new ActionCheckNewMessages(QueryPreferences.getActiveUserId(this));
        subscription =  QueryAction.executeAnswerQuery(action)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        repeatTask();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"onError service:",e);
                        repeatTask();
                    }

                    @Override
                    public void onNext(String s) {
                        if (s.equals("no new messages")) return;

                        if (s.equals("error")){
                            onError(new Throwable(s));
                        }else{
                            Gson gson = new Gson();
                            NotificationItem item = gson.fromJson(s,NotificationItem.class);

                            Person person = new Person(item.getSenderId(),item.getSenderName());
                            Intent i = CorrespondenceListActivity.newIntent(MyService.this,person);
                            PendingIntent pi = PendingIntent.getActivity(MyService.this,0,i,0);
                            int count = item.getCount();
                            Uri alarSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                            Notification notification = new NotificationCompat.Builder(MyService.this)
                                    .setTicker("You have " + String.valueOf(count) + (count >= 1?" message":" messages"))
                                    .setSmallIcon(R.drawable.ic_email_icon)
                                    .setContentTitle(item.getSenderName())
                                    .setContentText(item.getText())
                                    .setContentIntent(pi)
                                    .setAutoCancel(true)
                                    .setSound(alarSound)
                                    .build();

                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MyService.this);
                            notificationManagerCompat.notify(0,notification);


                        }

                    }
                });

    }

    private void repeatTask(){
        Log.d(TAG,"repeatTask");
        handler.postDelayed(runnable,POLL_INTERVAL);
    }

    private void stopTask() {
        Log.d(TAG,"stopTask");
        handler.removeCallbacks(runnable);
        if (subscription != null) subscription.unsubscribe();

    }


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    class NotificationItem {
        private int count;
        private String senderId;
        private String senderName;
        private String text;

        public NotificationItem(int count, String senderId, String senderName, String text) {
            this.count = count;
            this.senderId = senderId;
            this.senderName = senderName;
            this.text = text;
        }

        public int getCount() {
            return count;
        }

        public String getSenderId() {
            return senderId;
        }

        public String getSenderName() {
            return senderName;
        }

        public String getText() {
            return text;
        }
    }
}
