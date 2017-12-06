package com.example.andrii.flashchat.tools;

import android.util.Log;

import com.example.andrii.flashchat.data.SingletonConnection;
import com.example.andrii.flashchat.data.actions.Action;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class QueryAction {
    /**
     * @param action which be executed
     * @param logTag LogTag
     * @return Observable that emits {@link String} answer.Answer timeout 5 sec.
     */
    public static Observable<String> executeAnswerQuery(Action action, String logTag){
        Observable<String> connectionToServerObservable = Observable.just(action)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(act -> {
                    SingletonConnection.getInstance().connect();
                    SingletonConnection.getInstance().executeAction(act);

                    BufferedReader in = SingletonConnection.getInstance().getReader();
                    String answer = "";
                    try {
                        answer = in.readLine();
                    } catch (IOException e) {
                        Log.d(logTag,"in = null:" + String.valueOf(in == null));
                    }
                    return answer;
                });

        Observable<String> observable = Observable.empty();

        return observable.mergeWith(connectionToServerObservable)
                .timeout(5, TimeUnit.SECONDS, Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
