package com.example.andrii.flashchat.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.andrii.flashchat.Activitys.MessagesListActivity;
import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.data.SingletonConnection;
import com.example.andrii.flashchat.data.actions.Action;
import com.example.andrii.flashchat.data.actions.ActionLogin;
import com.example.andrii.flashchat.data.actions.ActionLoginFromFacebook;
import com.example.andrii.flashchat.data.interfaces.EmailHelper;
import com.example.andrii.flashchat.data.interfaces.LoginHelper;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginFragment extends Fragment
implements EmailHelper{
    private final String TAG = "LoginFragment"; 
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CircleImageView mRegister;
    private CircleImageView mFacebook;
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
        mFacebook = v.findViewById(R.id.cv_facebook);

        mLoginFacebook = v.findViewById(R.id.login_button);
        mLoginFacebook.setReadPermissions(Arrays.asList("public_profile", "user_friends","user_birthday"));
        mLoginFacebook.setFragment(this);

        callbackManager = CallbackManager.Factory.create();
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

                                long id;
                                String name;
                                String url = null;
                                String gender;
                                String email;
                                String birthday;

                                JSONObject pictureData = null;
                                try {
                                    pictureData = object.getJSONObject("picture").getJSONObject("data");
                                    url = pictureData.optString("url");
                                } catch (JSONException e) {
                                    url = "null";
                                }
                                id = Long.parseLong(object.optString("id"));

                                try {
                                    name = object.getString("name");
                                } catch (JSONException e) {
                                    name = "null";
                                }

                                try {
                                    gender  = object.getString("gender");
                                } catch (JSONException e) {
                                    gender = "null";
                                }
                                try {
                                    email = object.getString("email");
                                } catch (JSONException e) {
                                    email = "null";
                                }

                                try {
                                    birthday = object.getString("birthday");
                                } catch (JSONException e) {
                                    birthday = "null";
                                }

                                person[0] = new Person(name,birthday,"",email,gender);
                                person[0].setId(String.valueOf(id));
                                person[0].setPhotoUrl(url);

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
                           SingletonConnection.getInstance().connect();
                           SingletonConnection.getInstance().executeAction(action);

                           return read();
                        });
                        Observable<String>sereverConnectionObservable = Observable.empty();
                sereverConnectionObservable.mergeWith(graphConnectionObservable)
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
                                        Log.d(TAG,"facebook onError");
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


        populateAutoComplete();

        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button mEmailSignInButton = v.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());

        mRegister.setOnClickListener(view -> ((LoginHelper)getActivity()).changeFragment(LoginHelper.REGISTER_FRAGMENT));

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
            String userId = QueryPreferences.getActiveUserId(getActivity());
            Intent intent = MessagesListActivity.newIntent(getActivity(),userId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
            getActivity().finish();

        }
    }


    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getActivity().getLoaderManager().initLoader(0,null,(android.app.LoaderManager.LoaderCallbacks<Cursor>)getActivity());
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (getActivity().checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, v -> requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS));
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);

            ActionLogin actionLogin = new ActionLogin(email,password);

            Observable<Action> serverConnectObservable = Observable.just(actionLogin);

            Observable<String> observable = Observable.empty();
            observable.mergeWith(serverConnectObservable.observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
                            .map(action -> {
                                SingletonConnection.getInstance().connect();
                                SingletonConnection.getInstance().executeAction(action);
                                return read();
                            }))
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
                                Log.e(TAG,"OnError: ",e);

                                showProgress(false);
                                mEmailView.setError("Error with connecting to server");
                                SingletonConnection.getInstance().close();
                            }

                            @Override
                            public void onNext(String s) {
                                Log.d(TAG,"OnNext s = " + s);

                                showProgress(false);
                                if (s.equals("incorrect")) mPasswordView.setError(getString(R.string.error_incorrect_password));
                                else{
                                    QueryPreferences.setActiveUserId(getActivity(),s);

                                }

                            }
                    });

        }
    }

    private boolean isEmailValid(String email) {
        //return email.contains("@");
        return true;
    }

    private boolean isPasswordValid(String password) {
        //return password.length() > 8 && password.length() <= 16;
        return true;
    }

    public String read(){
        BufferedReader in = SingletonConnection.getInstance().getReader();
        Log.d(TAG,"in = null:" + String.valueOf(in == null));
        String answer = "";
        try {
            answer = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        mRegister.setClickable(!show);
    }

    @Override
    public void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private class UserLoginTask extends AsyncTask<Void, Void, Integer> {
        private static final int RESULT_SUCCES = 1;
        private static final int RESULT_INCORRECT_PASSWORD = 2;
        private static final int RESULT_NO_ANSWER_SERVER = 3;
        private String userId;
        BufferedReader in;


        @Override
        protected Integer doInBackground(Void... params) {
            in = SingletonConnection.getInstance().getReader();
            String answer = "";

            //доробить
            try {
                answer = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!answer.equals("incorrect")) {
                if(answer.equals("")){
                    return RESULT_NO_ANSWER_SERVER;
                }
                else{
                    userId = answer;
                    return RESULT_SUCCES;
                }
            } else {
                return RESULT_INCORRECT_PASSWORD;
            }
        }

        @Override
        protected void onPostExecute(final Integer result) {
            Log.d(TAG,String.valueOf(result));
            switch (result){
                case RESULT_INCORRECT_PASSWORD:
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    break;
                case RESULT_SUCCES:
                mEmailView.setText(userId);
                    break;
                default:
                    mPasswordView.setError("No answer from server");
            }
            showProgress(false);
            mAuthTask = null;
        }


    }
}
