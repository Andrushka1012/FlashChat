package com.example.andrii.flashchat.tools;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.andrii.flashchat.Activities.CorrespondenceListActivity;
import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.Model.Person;
import com.example.andrii.flashchat.data.SingletonConnection;
import com.example.andrii.flashchat.data.actions.ActionCheckNewMessages;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import rx.Observer;
import rx.Subscription;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    public static final String NEW_MESSAGE_KEY = "NEW_MESSAGE_KEY";
    private static final String TAG = "MyFirebaseMessaging";

    public void onMessageReceived(RemoteMessage message) {
        if (!isNetworkAvailableAndConnected() && QueryPreferences.getActiveUserId(this) == null){
            Log.e(TAG,"Network is not available or no set active user");
            return;
        }

        ActionCheckNewMessages action = new ActionCheckNewMessages(QueryPreferences.getActiveUserId(this));
        final String[] senderId = {""};
        Subscription subscription = QueryAction.executeAnswerQuery(action)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        if (!senderId[0].isEmpty()){
                            Intent intent = new Intent(NEW_MESSAGE_KEY);
                            intent.putExtra("senderId", senderId[0]);
                            LocalBroadcastManager.getInstance(MyFirebaseMessaging.this).sendBroadcast(intent);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"onError service:",e);
                    }

                    @Override
                    public void onNext(String s) {
                        if (s.equals("no new messages")) return;

                        if (s.equals("error")){
                            onError(new Throwable(s));
                        }else{
                            Gson gson = new Gson();
                            NotificationItem item = gson.fromJson(s,NotificationItem.class);

                            Person person = new Person(item.senderId,item.senderName);
                            Intent i = CorrespondenceListActivity.newIntent(MyFirebaseMessaging.this,person,false);
                            PendingIntent pi = PendingIntent.getActivity(MyFirebaseMessaging.this,0,i,0);
                            int count = item.getCount();
                            Uri alarSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                            Notification notification = new NotificationCompat.Builder(MyFirebaseMessaging.this)
                                    .setTicker("You have " + String.valueOf(count) + (count >= 1?" message":" messages"))
                                    .setSmallIcon(R.drawable.ic_email_icon)
                                    .setContentTitle(item.getSenderName())
                                    .setContentText(item.getText())
                                    .setContentIntent(pi)
                                    .setAutoCancel(true)
                                    .setSound(alarSound)
                                    .build();

                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MyFirebaseMessaging.this);
                            notificationManagerCompat.notify(0,notification);
                            senderId[0] = item.senderId;
                        }

                    }
                });
        QueryAction.addSubscription(subscription);
    }


    private boolean isNetworkAvailableAndConnected(){
        return SingletonConnection.getInstance().isConnectionAvailable(TAG);
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
