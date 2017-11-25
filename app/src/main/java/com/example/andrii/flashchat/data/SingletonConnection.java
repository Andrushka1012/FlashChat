package com.example.andrii.flashchat.data;

import android.os.AsyncTask;
import android.util.Log;

import com.example.andrii.flashchat.data.actions.Action;
import com.example.andrii.flashchat.data.actions.ActionExit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SingletonConnection {
    private static final SingletonConnection ourInstance = new SingletonConnection();
    private static final String IP_SERVER = "192.168.43.14";
    private static final int PORT = 50000;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

   public static SingletonConnection getInstance() {
        return ourInstance;
    }

    private SingletonConnection() {
    }

    public BufferedReader getReader(){
        return in;
    }

    public void connect() {
        try {
            socket = new Socket(IP_SERVER, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            Log.e("qwe","Connection error",e);
        }


    }

    public void close(){
        if (socket != null) new ActionExit().execute(out);
    }

    public void closeSocket(){
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            Log.e("qwe","Clothing socket error",e);
        }
        in = null;
        out = null;
        socket = null;
    }

    public void executeAction(Action action){
        action.execute(out);
    }

    private class ConnectToServerItemTask extends AsyncTask<Void, Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                socket = new Socket(IP_SERVER, PORT);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                Log.e("qwe","Connection error",e);
            }
            return null;
        }
    }

}
