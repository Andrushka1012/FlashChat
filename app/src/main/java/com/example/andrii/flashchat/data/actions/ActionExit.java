package com.example.andrii.flashchat.data.actions;

import android.os.AsyncTask;
import android.util.Log;

import com.example.andrii.flashchat.data.SingletonConnection;
import com.google.gson.Gson;

import java.io.PrintWriter;

public class ActionExit implements Action {
    public String action;

    public ActionExit(){
        this.action = "exit";
    }
    public ActionExit(String action){
        this.action = action;
    }

    @Override
    public void execute(PrintWriter out) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                Gson gson = new Gson();
                ActionExit actionExit = new ActionExit(action);
                String json = gson.toJson(actionExit);
                Log.d("qwe","Json:" + String.valueOf(json==null));
                out.println(json);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                SingletonConnection.getInstance().closeSocket();
            }
        };
        task.execute();

    }

}
