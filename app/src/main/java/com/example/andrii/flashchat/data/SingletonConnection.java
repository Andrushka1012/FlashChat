package com.example.andrii.flashchat.data;

import android.util.Log;

import com.example.andrii.flashchat.data.actions.Action;
import com.example.andrii.flashchat.data.actions.ActionExit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SingletonConnection {
    private static final SingletonConnection ourInstance = new SingletonConnection();
    private static final String IP_SERVER = "192.168.43.242";//localhost

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
       /* InetAddress address = null;
        try {
            address = InetAddress.getByName("ec2-13-59-71-192.us-east-2.compute.amazonaws.com");
        } catch (UnknownHostException e) {
            Log.e("error","SingletonConnection:",e);
        }
        IP_SERVER = address.getHostAddress();*/
            Log.d("SingletonConnection","IP_SERVER:" + IP_SERVER);
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
            return false;
        }
        close();
        return true;
    }

    public void executeAction(Action action){
        if (out != null)action.execute(out);
    }

}
