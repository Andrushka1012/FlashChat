package com.example.andrii.flashchat.data.actions;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionSendImage implements Action{
    private String action;
    private String image_id;
    private String image_source;
    private String sender_id;
    private String recipient_id;

    public ActionSendImage(String image_id, String image_source, String sender_id, String recipient_id) {
        action = "action_send_image";
        this.image_id = image_id;
        this.image_source = image_source;
        this.sender_id = sender_id;
        this.recipient_id = recipient_id;
    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionSendImage act  = new ActionSendImage(image_id,image_source,sender_id,recipient_id);
        String json = gson.toJson(act);
        Log.d("qwe","Json:" + json);
        out.println(json);
    }
}
