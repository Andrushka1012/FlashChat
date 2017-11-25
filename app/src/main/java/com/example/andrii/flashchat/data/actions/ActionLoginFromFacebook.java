package com.example.andrii.flashchat.data.actions;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionLoginFromFacebook implements Action{
    private String action;
    private String id;
    private String name;
    private String birthDate;
    private String phoneNumber;
    private String email;
    private String gender;
    private String photoUrl;

    public ActionLoginFromFacebook(String id, String name, String birthDate, String phoneNumber, String email, String gender, String photoUrl) {
        action = "action_LoginFromFacebook";
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.gender = gender;
        this.photoUrl = photoUrl;
    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionLoginFromFacebook action = new ActionLoginFromFacebook(id, name,birthDate,phoneNumber,email,gender,photoUrl);
        String json = gson.toJson(action);
        Log.d("qwe","Json loginFromFacebook" + json);
        out.println(json);
    }
}
