package com.example.andrii.flashchat.data.actions;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionGetOnlinePersonData implements Action{
    private String action;
    private String userId;

    public ActionGetOnlinePersonData(String userId) {
        action = "action_get_online_person_data";
        this.userId = userId;
    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionGetOnlinePersonData action = new ActionGetOnlinePersonData(userId);
        String json = gson.toJson(action);

        out.println(json);
    }
}
