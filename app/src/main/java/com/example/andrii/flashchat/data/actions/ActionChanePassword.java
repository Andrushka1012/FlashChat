package com.example.andrii.flashchat.data.actions;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionChanePassword implements Action{
    private String action;
    private String id;
    private String oldPassword;
    private String newPassword;

    public ActionChanePassword(String id, String oldPassword, String newPassword) {
        action = "action_change_password";
        this.id = id;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionChanePassword action = new ActionChanePassword(id,oldPassword,newPassword);
        String json = gson.toJson(action);
        Log.d("qwe","Json:" + json);
        out.println(json);
    }
}
