package com.example.andrii.flashchat.data.actions;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionRegister implements Action{
    private String action;
    private String name;
    private String birth;
    private String email;
    private String number;
    private String password;
    private String gender;

    public ActionRegister(String name, String birth, String email, String number, String password,String gender) {
        action= "action_register";
        this.name = name;
        this.birth = birth;
        this.email = email;
        this.number = number;
        this.password = password;
        this.gender = gender;
    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionRegister action = new ActionRegister(name,birth,email,number,password,gender);
        String json = gson.toJson(action);
        Log.d("qwe","Json:" + json);
        out.println(json);
    }

}
