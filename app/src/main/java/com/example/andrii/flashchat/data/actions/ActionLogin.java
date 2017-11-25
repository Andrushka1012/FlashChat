package com.example.andrii.flashchat.data.actions;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionLogin implements Action {

    private String action;
    private String email;
    private String password;

    public ActionLogin(String email, String password){
        action = "action_login";
        this.email = email;
        this.password = password;
    }
    public ActionLogin(String action,String email,String password){
        this.action = action;
        this.email = email;
        this.password = password;

    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionLogin actionLogin = new ActionLogin(action,email,password);
        String json = gson.toJson(actionLogin);
        Log.d("qwe","Json login" + json);
        out.println(json);


    }
}
