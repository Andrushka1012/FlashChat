package com.example.andrii.flashchat.data.actions;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionGetImage implements Action {
    private String action;
    private String msgId;

    public ActionGetImage(String msgId) {
        this.msgId = msgId;
        action = "action_get_image";
    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionGetImage action = new ActionGetImage(msgId);
        String json = gson.toJson(action);
        Log.d("qwe","Json:" + json);
        out.println(json);
    }
}
