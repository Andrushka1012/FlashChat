package com.example.andrii.flashchat.data.actions;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionSearch implements Action{
    private String action;
    private String searchString;
    private String userId;

    public ActionSearch(String searchString,String userId) {
        action = "action_search";
        this.searchString = searchString;
        this.userId = userId;

    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionSearch action = new ActionSearch(searchString,userId);
        String json = gson.toJson(action);
        Log.d("qwe","Json:" + json);
        out.println(json);
    }
}
