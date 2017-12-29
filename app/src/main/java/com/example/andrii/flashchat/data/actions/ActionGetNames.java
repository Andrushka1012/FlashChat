package com.example.andrii.flashchat.data.actions;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionGetNames implements Action{

    private String action;
    private String userId;

    public ActionGetNames(String userId) {
        action = "action_get_names";
        this.userId = userId;
    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionGetNames action = new ActionGetNames(userId);
        String json = gson.toJson(action);
        Log.d("qwe","Json:" + json);
        out.println(json);

    }
}
