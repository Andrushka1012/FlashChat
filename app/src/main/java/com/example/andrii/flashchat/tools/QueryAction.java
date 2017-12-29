package com.example.andrii.flashchat.tools;
import android.util.Log;

import com.example.andrii.flashchat.data.SingletonConnection;
import com.example.andrii.flashchat.data.actions.Action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class QueryAction {
    /**
     * @param action which be executed
     * @return Observable that emits {@link String} answer.Answer timeout 5 sec.
     */
    public static Observable<String> executeAnswerQuery(Action action){

        Observable<String> connectionToServerObservable = Observable.just(action)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(act -> {
                    try {
                        SingletonConnection.getInstance().connect();
                    } catch (Exception e) {
                        Log.e("qwe","Connection error",e);
                        SingletonConnection.getInstance().close();
                        return "error";
                    }
                    SingletonConnection.getInstance().executeAction(action);
                    BufferedReader in = SingletonConnection.getInstance().getReader();
                    String answer = "";

                    if (in != null){
                            try {
                            answer = in.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else return "error";

                    return answer;
                });

        Observable<String> observable = Observable.empty();

        return observable.mergeWith(connectionToServerObservable)
                .timeout(5, TimeUnit.SECONDS, Schedulers.io())
                .doOnUnsubscribe(()->SingletonConnection.getInstance().close())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<String> executeAnswerQuery(List<Action> actions){

        Observable<String> connectionToServerObservable = Observable.from(actions.toArray())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(act -> {
                    try {
                        SingletonConnection.getInstance().connect();
                    } catch (Exception e) {
                        Log.e("qwe","Connection error",e);
                        SingletonConnection.getInstance().close();
                        return "error";
                    }
                    SingletonConnection.getInstance().executeAction((Action) act);
                    BufferedReader in = SingletonConnection.getInstance().getReader();
                    String answer = "";
                    if (in != null){
                        try {
                            answer = in.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else return "error";

                    return answer;
                });

        Observable<String> observable = Observable.empty();

        return observable.mergeWith(connectionToServerObservable)
                .timeout(5, TimeUnit.SECONDS, Schedulers.io())
                .doOnUnsubscribe(()->SingletonConnection.getInstance().close())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<String> executeAnswerQuery(Observable<Action> actionObservable){
        Observable<String> connectionToServerObservable = actionObservable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(act -> {
                    try {
                        SingletonConnection.getInstance().connect();
                    } catch (Exception e) {
                        Log.e("qwe","Connection error",e);

                    }
                    SingletonConnection.getInstance().executeAction(act);
                    BufferedReader in = SingletonConnection.getInstance().getReader();
                    String answer = "";
                    if (in != null){
                        try {
                            answer = in.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else return "error";

                    return answer;
                });

        Observable<String> observable = Observable.empty();

        return observable.mergeWith(connectionToServerObservable)
                .timeout(5, TimeUnit.SECONDS, Schedulers.io())
                .doOnUnsubscribe(()->SingletonConnection.getInstance().close())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<String> executeAnswerQuery(Action action,String ip,int port,String logTag){

        final Socket[] socket = new Socket[1];
        final BufferedReader[] in = {null};
        final PrintWriter[] out = {null};

        Observable<String> connectionToServerObservable = Observable.just(action)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(action1 -> {
                    try {
                        socket[0] = new Socket(ip,port);
                        in[0] = new BufferedReader(new InputStreamReader(socket[0].getInputStream()));
                        out[0] = new PrintWriter(socket[0].getOutputStream(), true);
                    } catch (IOException e) {
                        Log.e(logTag,"Service error:",e);
                    }

                    if (out[0] != null){
                        action1.execute(out[0]);
                    }else return "error";

                    String answer = "";

                    if (in[0] != null){
                        try {
                            answer = in[0].readLine();
                        } catch (IOException e) {
                            Log.e(logTag,"Service error:",e);
                        }
                    }else return "error";

                    return answer;
                });

        Observable<String> observable = Observable.empty();

        return observable.mergeWith(connectionToServerObservable)
                .timeout(5, TimeUnit.SECONDS, Schedulers.io())
                .doOnUnsubscribe(()->{
                    try {
                        if (in[0] != null) in[0].close();
                        if (out[0] != null) out[0].close();
                        if (socket[0] != null) socket[0].close();
                    } catch (IOException e) {
                        Log.e(logTag,"Clothing socket error",e);
                    }
                    in[0] = null;
                    out[0] = null;
                    socket[0] = null;
                    Log.d(logTag,"Closed");
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
