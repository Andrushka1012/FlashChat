package com.example.andrii.flashchat.data.actions;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionGetMessages implements Action{
    private String action;
    private String userId;
    private String recipientId;

    public ActionGetMessages(String userId,String recipientId) {
        action = "action_get_messages";
        this.userId = userId;
        this.recipientId = recipientId;
    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionGetMessages action = new ActionGetMessages(userId,recipientId);
        String json = gson.toJson(action);
        Log.d("qwe","Json:" + json);
        out.println(json);
    }
}
