package com.example.andrii.flashchat.data.actions;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionGetNewMessages implements Action{
    private String action;
    private String userId;

    public ActionGetNewMessages(String userId) {
        action = "action_get_new_messages";
        this.userId = userId;
    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionGetNewMessages action = new ActionGetNewMessages(userId);
        String json = gson.toJson(action);
        Log.d("qwe","Json:" + json);
        out.println(json);

    }
}
