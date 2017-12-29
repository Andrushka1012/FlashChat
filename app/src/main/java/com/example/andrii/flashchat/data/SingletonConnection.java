package com.example.andrii.flashchat.data;

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


    public void connect() throws IOException {

            socket = new Socket(IP_SERVER, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);


        Log.d("qwe","Connected");
    }

    public void close() {
        if (out != null) new ActionExit().execute(out);
    }

    public void closeSocket(){

        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            Log.e("qwe","Clothing socket error",e);
        }
        in = null;
        out = null;
        socket = null;
        Log.d("qwe","Closed");
    }

    public boolean isConnectionAvailable(String tag){
        try {
            connect();
        } catch (IOException e) {
            Log.e(tag,"Error:",e);
        }
        boolean isAvailable = socket != null && in != null && out != null;
        close();
        return isAvailable;
    }

    public void executeAction(Action action){
        if (out != null)action.execute(out);
    }

}
