package com.example.andrii.flashchat.tools;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.andrii.flashchat.Activities.CorrespondenceListActivity;
import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.data.actions.ActionCheckNewMessages;
import com.google.gson.Gson;

import rx.Observer;

public class PollService extends IntentService {
    private static final String TAG = "PollService";
    public static final int POLL_INTERVAL = 1000 * 60;
    //public static final long POLL_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    private static final String IP_SERVER = "192.168.43.14";
    private static final int PORT = 50000;

    public PollService() {
        super(TAG);
    }

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context,PollService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }


    public static void setServiceAlarm(Context context, boolean isOn) {
        //Intent i = PollService.newIntent(context);
        Intent i = new Intent("com.android.techtrainner");
        PendingIntent pi = PendingIntent.getService(context,0,i,0);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        if (isOn){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(),POLL_INTERVAL,pi);
        }else{
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context){
        //Intent i = PollService.newIntent(context);
        Intent i = new Intent("com.android.techtrainner");
        PendingIntent pi = PendingIntent
                .getService(context,0,i,PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG,"onHandleIntent");
        /*if (!isNetworkAvailableAndConnected() && QueryPreferences.getActiveUserId(this) == null){
            Log.e(TAG,"Network is not available or no set active user");
            return;
        }*/


        ActionCheckNewMessages action = new ActionCheckNewMessages(QueryPreferences.getActiveUserId(this));
        QueryAction.executeAnswerQuery(action)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
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
                            Intent i = CorrespondenceListActivity.newIntent(PollService.this,person);
                            PendingIntent pi = PendingIntent.getActivity(PollService.this,0,i,0);
                            int count = item.getCount();
                            Uri alarSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                            Notification notification = new NotificationCompat.Builder(PollService.this)
                                    .setTicker("You have " + String.valueOf(count) + (count >= 1?" message":" messages"))
                                    .setSmallIcon(R.drawable.ic_email_icon)
                                    .setContentTitle(item.getSenderName())
                                    .setContentText(item.getText())
                                    .setContentIntent(pi)
                                    .setAutoCancel(true)
                                    .setSound(alarSound)
                                    .build();

                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(PollService.this);
                            notificationManagerCompat.notify(0,notification);


                        }

                    }
                });

    }


    private boolean isNetworkAvailableAndConnected(){
        //return SingletonConnection.getInstance().isConnectionAvailable(TAG);
        return true;
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
