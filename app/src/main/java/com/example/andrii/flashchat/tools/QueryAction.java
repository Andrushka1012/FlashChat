package com.example.andrii.flashchat.tools;

import android.content.Context;
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
    public static Observable<String> executeAnswerQuery(Context context, Action action, String logTag){
        Observable<String> connectionToServerObservable = Observable.just(action)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(act -> {
                    SingletonConnection.getInstance().connect(context);
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
    public static Observable<String> executeAnswerQuery(Context context,Observable<Action> actionObservable, String logTag){
      /*  ActionConnection actionConnection = new ActionConnection(QueryPreferences.getActiveUserId(context));
        Observable<String> connectionObservable = QueryAction.executeAnswerQuery(context,actionConnection,"qwe");
        connectionObservable.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(context,"Server error",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(String s) {
                if (s.equals("error")) Toast.makeText(context,"Server error",Toast.LENGTH_LONG).show();
            }
        });
        */

        Observable<String> connectionToServerObservable = actionObservable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(act -> {
                    SingletonConnection.getInstance().connect(context);
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
