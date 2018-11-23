package com.example.andrii.flashchat.tools;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {
    private static final String PREF_ACTIVE_USER_ID = "PREF_ACTIVE_USER_ID";
    private static final String PREF_FCM_DEVICE_TOKEN = "PREF_FCM_DEVICE_TOKEN";

    public static String getActiveUserId(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_ACTIVE_USER_ID,null);
    }

    public static void setActiveUserId(Context context,String id){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_ACTIVE_USER_ID,id)
                .apply();
    }

    public static String getDeviceToken(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_FCM_DEVICE_TOKEN,null);
    }

    public static void setDeviceToken(Context context,String token){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_FCM_DEVICE_TOKEN,token)
                .apply();
    }



}
