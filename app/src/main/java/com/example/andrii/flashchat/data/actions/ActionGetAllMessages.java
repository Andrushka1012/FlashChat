package com.example.andrii.flashchat.data.actions;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionGetAllMessages implements Action{
    private String action;
    private String userId;

    public ActionGetAllMessages(String userId) {
        action = "action_get_all_messages";
        this.userId = userId;
    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionGetAllMessages action = new ActionGetAllMessages(userId);
        String json = gson.toJson(action);
        Log.d("qwe","Json:" + json);
        out.println(json);

    }
}
