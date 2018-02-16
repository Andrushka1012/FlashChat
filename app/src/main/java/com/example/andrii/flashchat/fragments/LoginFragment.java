package com.example.andrii.flashchat.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.andrii.flashchat.Activities.LoadingActivity;
import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.data.SingletonConnection;
import com.example.andrii.flashchat.data.actions.ActionLogin;
import com.example.andrii.flashchat.data.actions.ActionLoginFromFacebook;
import com.example.andrii.flashchat.data.interfaces.LoginHelper;
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
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginFragment extends Fragment{
    private final String TAG = "LoginFragment"; 
    private static final int REQUEST_READ_CONTACTS = 0;


    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CircleImageView mRegister;
    private LoginButton mLoginFacebook;
    private CallbackManager callbackManager;


    public static LoginFragment newInstance(){
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        login();

        View v = inflater.inflate(R.layout.fragment_login,container,false);
        mEmailView = v.findViewById(R.id.email);
        mPasswordView = v.findViewById(R.id.password);
        mProgressView = v.findViewById(R.id.login_progress);
        mLoginFormView = v.findViewById(R.id.login_form);
        mRegister = v.findViewById(R.id.cv_new);
        CircleImageView mFacebook = v.findViewById(R.id.cv_facebook);

        mLoginFacebook = v.findViewById(R.id.login_button);
        mLoginFacebook.setReadPermissions(Arrays.asList("public_profile", "user_friends","user_birthday"));
        mLoginFacebook.setFragment(this);

        callbackManager = CallbackManager.Factory.create();
        registerFacebookCallback();

        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button mEmailSignInButton = v.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());

        mRegister.setOnClickListener(view -> ((LoginHelper)getActivity()).changeFragment());

        mFacebook.setOnClickListener(view -> mLoginFacebook.callOnClick());


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }


    private void login(){
        if (QueryPreferences.getActiveUserId(getActivity()) != null){
            Intent intent = LoadingActivity.newIntent(getActivity());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
            getActivity().finish();

        }
    }

    private void attemptLogin() {
       boolean cancel = validate();

        if (!cancel) {
            showProgress(true);

            String email = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();
            ActionLogin actionLogin = new ActionLogin(email,password);

            Observable<String> observable = QueryAction.executeAnswerQuery(actionLogin);
            Subscription subscription = observable.subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG,"OnError: ",e);

                                showProgress(false);
                                mEmailView.setError("Error with connecting to server");
                            }

                            @Override
                            public void onNext(String s) {
                                Log.d(TAG,"OnNext s = " + s);

                                showProgress(false);
                                if (s.equals("incorrect") || s.equals("error")) mPasswordView.setError(getString(R.string.error_incorrect_password));
                                else{
                                    QueryPreferences.setActiveUserId(getActivity(),s);
                                    login();
                                }
                            }
                    });
            QueryAction.addSubscription(subscription);
        }
    }

    private void registerFacebookCallback() {
        mLoginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG,"onSuccess");
                showProgress(true);

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
                                login();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG,"Facebook error",e);
                                SingletonConnection.getInstance().close();
                                showProgress(false);
                                Toast.makeText(getActivity(),"FlashChat server error.",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onNext(String answer) {
                                showProgress(false);
                                if(!answer.equals("error")){
                                    Log.d(TAG,"facebook success");
                                    QueryPreferences.setActiveUserId(getActivity(),answer);

                                }else{
                                    Toast.makeText(getActivity(),"FlashChat server error.",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), "Login Cancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG,"onError " + error.getMessage());
            }
        });
    }

    private boolean validate() {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        if (cancel)focusView.requestFocus();
        return cancel;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.length() >6;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8 && password.length() <= 16;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        mRegister.setClickable(!show);
    }

}
