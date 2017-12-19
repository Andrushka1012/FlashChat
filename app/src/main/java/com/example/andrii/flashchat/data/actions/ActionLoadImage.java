package com.example.andrii.flashchat.data.actions;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionLoadImage implements Action{
    private String action;
    private String userId;
    private String imageId;

    public ActionLoadImage(String userId, String imageId) {
        action = "action_load_image";
        this.userId = userId;
        this.imageId = imageId;
    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionLoadImage action = new ActionLoadImage(userId,imageId);
        String json = gson.toJson(action);
        Log.d("qwe","Json PersonData" + json);
        out.println(json);
    }
}
