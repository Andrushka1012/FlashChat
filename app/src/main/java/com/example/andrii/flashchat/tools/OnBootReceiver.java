package com.example.andrii.flashchat.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("OnBootReceiver","OnBootReceiver Received callback");

        context.startService(new Intent(context,MyService.class));
    }
}
