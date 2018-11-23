package com.example.andrii.flashchat.data.actions;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionLogin implements Action {

    private String action;
    private String email;
    private String password;
    private String deviceToken;

    public ActionLogin(String email, String password, String deviceToken){
        action = "action_login";
        this.email = email;
        this.password = password;
        this.deviceToken = deviceToken;
    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionLogin actionLogin = new ActionLogin(email,password, deviceToken);
        String json = gson.toJson(actionLogin);
        Log.d("qwe","Json:" + json);
        out.println(json);


    }
}
