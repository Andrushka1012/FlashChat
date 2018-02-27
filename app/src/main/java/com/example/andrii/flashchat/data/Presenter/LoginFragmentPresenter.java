package com.example.andrii.flashchat.data.Presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.andrii.flashchat.Activities.LoadingActivity;
import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.Model.Person;
import com.example.andrii.flashchat.data.SingletonConnection;
import com.example.andrii.flashchat.data.actions.ActionLogin;
import com.example.andrii.flashchat.data.actions.ActionLoginFromFacebook;
import com.example.andrii.flashchat.data.interfaces.LoginView;
import com.example.andrii.flashchat.tools.QueryAction;
import com.example.andrii.flashchat.tools.QueryPreferences;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginFragmentPresenter {

    private LoginView view;
    private static String TAG ="LoginFragmentPresenter";

    public LoginFragmentPresenter(LoginView view) {
        this.view = view;
    }

    public void login(FragmentActivity context){
        if (QueryPreferences.getActiveUserId(context) != null){
            Intent intent = LoadingActivity.newIntent(context);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            context.finish();
        }
    }

    public void attemptLogin(FragmentActivity context,String email,String password){
        boolean cancel = validate(context,email,password);

        if (!cancel){

            ActionLogin actionLogin = new ActionLogin(email,password);

            Observable<String> observable = QueryAction.executeAnswerQuery(actionLogin);
            Subscription subscription = observable
                    .doOnSubscribe(() -> view.showProgress(true))
                    .doOnTerminate(() ->view.showProgress(false))
                    .subscribe(new Observer<String>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG,"OnError: ",e);
                    view.showEmailError("Error with connecting to server");
                }

                @Override
                public void onNext(String s) {
                    Log.d(TAG,"OnNext s = " + s);
                    if (s.equals("incorrect") || s.equals("error")) view.showPasswordError(context.getString(R.string.error_incorrect_password));
                    else{
                        QueryPreferences.setActiveUserId(context,s);
                        login(context);
                    }
                }
            });
            QueryAction.addSubscription(subscription);
        }
    }

    private boolean validate(Context context,String email,String password) {
        view.showEmailError(null);
        view.showPasswordError(null);

        boolean cancel = false;

        if (!isPasswordValid(password)) {
            view.showPasswordError(context.getString(R.string.error_invalid_password));
            cancel = true;
        }

        if (!isEmailValid(email)) {
            view.showEmailError(context.getString(R.string.error_field_required));
            cancel = true;
        }

        return cancel;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.length() >6;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8 && password.length() <= 16;
    }

    public void registerFacebookCallback(FragmentActivity context, LoginButton loginFacebook, CallbackManager callbackManager) {
        loginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG,"onSuccess");
                view.showProgress(true);

                Observable<String> graphConnectionObservable = Observable.just(loginResult).observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .map(loginResult1 -> {
                            AccessToken token = loginResult.getAccessToken();
                            final Person[] person = new Person[1];
                            GraphRequest request = GraphRequest.newMeRequest(token, (object, response) -> {
                                Log.d(TAG, response.toString());
                                Log.d(TAG, object.toString());

                                JSONObject pictureData;

                                try {
                                    person[0] = new Person(object.getString("name"));
                                } catch (JSONException e) {
                                    person[0] = new Person("");
                                }
                                person[0].setId((object.optString("id")));
                                try {
                                    pictureData = object.getJSONObject("picture").getJSONObject("data");
                                    person[0].setPhotoUrl(pictureData.optString("url"));
                                } catch (JSONException e) {
                                    person[0].setPhotoUrl("null");
                                }

                                try {
                                    person[0].setName(object.getString("name"));
                                } catch (JSONException e) {
                                    person[0].setName("null");
                                }

                                try {
                                    person[0].setGender(object.getString("gender"));
                                } catch (JSONException e) {
                                    person[0].setGender("null");
                                }
                                try {
                                    person[0].setEmail(object.getString("email"));
                                } catch (JSONException e) {
                                    person[0].setEmail("null");
                                }

                                try {
                                    person[0].setBirthDate(object.getString("birthday"));
                                } catch (JSONException e) {
                                    person[0].setBirthDate("null");
                                }
                            });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,picture.type(large),gender,birthday");
                            request.setParameters(parameters);
                            request.executeAndWait();
                            return person[0];
                        })
                        .map(person -> {
                            ActionLoginFromFacebook action =
                                    new ActionLoginFromFacebook(person.getId(),person.getName(),person.getBirthDate(),person.getPhoneNumber(),person.getEmail(),person.getGender(),person.getPhotoUrl());
                            try {
                                SingletonConnection.getInstance().connect();
                            } catch (IOException e) {
                                Log.e("qwe","Connection error",e);
                                return "error";
                            }
                            SingletonConnection.getInstance().executeAction(action);
                            BufferedReader in = SingletonConnection.getInstance().getReader();
                            Log.d(TAG,"in = null:" + String.valueOf(in == null));
                            String answer = "";
                            try {
                                answer = in.readLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return answer;
                        });
                Observable<String>serverConnectionObservable = Observable.empty();
                serverConnectionObservable.mergeWith(graphConnectionObservable)
                        .timeout(5, TimeUnit.SECONDS)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {
                                SingletonConnection.getInstance().close();
                                login(context);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG,"Facebook error",e);
                                SingletonConnection.getInstance().close();
                                view.showProgress(false);
                                Toast.makeText(context,"FlashChat server error.",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onNext(String answer) {
                                view.showProgress(false);
                                if(!answer.equals("error")){
                                    Log.d(TAG,"facebook success");
                                    QueryPreferences.setActiveUserId(context,answer);

                                }else{
                                    Toast.makeText(context,"FlashChat server error.",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }

            @Override
            public void onCancel() {
                Toast.makeText(context, "Login Cancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG,"onError " + error.getMessage());
            }
        });
    }



}
