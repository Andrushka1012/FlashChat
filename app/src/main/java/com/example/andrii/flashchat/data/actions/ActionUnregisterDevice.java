package com.example.andrii.flashchat.data.actions;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionUnregisterDevice implements Action{
    private String action;
    private String deviceToken;

    public ActionUnregisterDevice(String deviceToken){
        action = "action_unregister_device";
        this.deviceToken = deviceToken;
    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionUnregisterDevice actionLogin = new ActionUnregisterDevice( deviceToken);
        String json = gson.toJson(actionLogin);
        Log.d("qwe","Json:" + json);
        out.println(json);

    }
}
