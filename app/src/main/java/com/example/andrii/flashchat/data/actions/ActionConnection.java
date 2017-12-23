package com.example.andrii.flashchat.data.actions;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionConnection implements Action{
    private String action;
    private String id;

    public ActionConnection(String id) {
        action = "action_connection";
        this.id = id;
    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionConnection action = new ActionConnection(id);
        String json = gson.toJson(action);
        Log.d("qwe","Json:" + json);
        out.println(json);
    }
}
