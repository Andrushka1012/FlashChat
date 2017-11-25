package com.example.andrii.flashchat.tools;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {
    private static final String PREF_ACTIVE_USER_ID = "PREF_ACTIVE_USER_ID";

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



}
