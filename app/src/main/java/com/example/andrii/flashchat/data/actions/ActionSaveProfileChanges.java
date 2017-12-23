package com.example.andrii.flashchat.data.actions;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionSaveProfileChanges implements Action{
    private String action;
    private String id;
    private String name;
    private String birth;
    private String email;
    private String number;
    private String gender;

    public ActionSaveProfileChanges(String id,String name, String birth, String email, String number, String gender) {
        action = "action_save_profile_changes";
        this.id = id;
        this.name = name;
        this.birth = birth;
        this.email = email;
        this.number = number;
        this.gender = gender;
    }

    @Override
    public void execute(PrintWriter out) {
        Gson gson = new Gson();
        ActionSaveProfileChanges action = new ActionSaveProfileChanges(id,name,birth,email,number,gender);
        String json = gson.toJson(action);
        Log.d("qwe","Json:" + json);
        out.println(json);
    }
}
