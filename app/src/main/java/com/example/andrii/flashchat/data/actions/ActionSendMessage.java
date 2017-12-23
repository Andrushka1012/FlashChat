package com.example.andrii.flashchat.data.actions;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionSendMessage implements Action{
    private String action;
    private String msgId;
    private String text;
    private String senderId ;
    private String recipientId;
    private int type;

    public ActionSendMessage(String msgId, String text, String senderId, String recipientId, int type) {
        action = "action_send_message";
        this.msgId = msgId;
        this.text = text;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.type = type;
    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionSendMessage action = new ActionSendMessage(msgId,text,senderId,recipientId,type);
        String json = gson.toJson(action);
        Log.d("qwe","Json:" + json);
        out.println(json);
    }
}
