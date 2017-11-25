package com.example.andrii.flashchat.data.actions;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionGetPersonData implements Action {

    private String action;
    private String userId;

    public ActionGetPersonData(String userId) {
        action = "action_get_person_date";
        this.userId = userId;

    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionGetPersonData action = new ActionGetPersonData(userId);
        String json = gson.toJson(action);
        Log.d("qwe","Json PersonData" + json);
        out.println(json);

    }
}
