package com.example.andrii.flashchat.data;

import android.content.Context;
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

    public void connect(Context context) {
        try {
            socket = new Socket(IP_SERVER, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
          /*  if (QueryPreferences.getActiveUserId(context) != null){
                ActionConnection action = new ActionConnection(QueryPreferences.getActiveUserId(context));

            Observable<String> connectionToServerObservable = Observable.just(action)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .map(act -> {
                        executeAction(action);

                        BufferedReader in = SingletonConnection.getInstance().getReader();
                        String answer = "";
                        try {
                            answer = in.readLine();
                        } catch (IOException e) {
                            Log.d("SingletonConnection", "in = null:" + String.valueOf(in == null));
                        }
                        return answer;
                    });

            Observable<String> observable = Observable.empty();
            observable.mergeWith(connectionToServerObservable)
                    .timeout(5, TimeUnit.SECONDS, Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("SingletonConnection", "Error with connection to server");
                            Toast.makeText(context, "Error with connection to server", Toast.LENGTH_LONG).show();

                            close();
                        }

                        @Override
                        public void onNext(String s) {
                            if (!s.equals("error"))
                                Log.d("SingletonConnection", "onNext:Connected");
                            else {
                                Log.e("SingletonConnection", "Error with connection to server");
                                Toast.makeText(context, "Error with connection to server", Toast.LENGTH_LONG).show();

                                close();
                            }

                        }
                    });

        }*/
        } catch (IOException e) {
            Log.e("qwe", "Connection error", e);
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
