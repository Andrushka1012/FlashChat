package com.example.andrii.flashchat.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.actions.ActionRegister;
import com.example.andrii.flashchat.tools.QueryAction;

import java.io.File;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class RegisterFragment extends Fragment {
    private final String TAG = "RegisterFragment"; 
    private AutoCompleteTextView mName;
    private AutoCompleteTextView mBirthDate;
    private AutoCompleteTextView mEmail;
    private AutoCompleteTextView mNumber;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private Switch mSwitch;

    private TextView mLogin;

    private View mProgressView;
    private View mRegisterFormView;

    public static RegisterFragment newInstance(){
        return new RegisterFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initializeView(inflater,container);
    }

    private View initializeView(LayoutInflater inflater,ViewGroup container){
        View v = inflater.inflate(R.layout.fragment_register,container,false);

        mName = v.findViewById(R.id.name);
        mBirthDate = v.findViewById(R.id.date_of_birth);
        mEmail = v.findViewById(R.id.email);
        mNumber = v.findViewById(R.id.phone_number);
        mPassword = v.findViewById(R.id.password);
        mConfirmPassword = v.findViewById(R.id.confirm_password);
        mSwitch = v.findViewById(R.id.gender_switch);
        Button mRegisterButton = v.findViewById(R.id.register_btn);
        mLogin = v.findViewById(R.id.login);
        mProgressView = v.findViewById(R.id.register_progress);
        mRegisterFormView = v.findViewById(R.id.register_layout);


        mRegisterButton.setOnClickListener(view ->{
            if (validateData()){
                String name = mName.getText().toString();
                String date = mBirthDate.getText().toString();
                String email = mEmail.getText().toString();
                String number = mNumber.getText().toString();
                String password = mPassword.getText().toString();
                String gender = mSwitch.isChecked() ? "male":"female";

                showProgress(true);


                ActionRegister action = new ActionRegister(name,date,email,number,password,gender);

                Observable<String> observable = QueryAction.executeAnswerQuery(action);
                Subscription subscription = observable
                        .doOnSubscribe(() ->showProgress(true))
                        .doOnTerminate(() ->showProgress(false))
                        .subscribe(new Observer<String>() {
                    @SuppressLint("ShowToast")
                    @Override
                    public void onCompleted() {}

                    @SuppressLint("ShowToast")
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(),"Error with connecting to server",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(String answer) {
                        Log.d(TAG,"OnNext s = " + answer);
                        switch (answer) {
                            case "invalid":
                                Toast.makeText(getActivity(), "Invalid data", Toast.LENGTH_LONG).show();
                                break;
                            case "error":
                                Toast.makeText(getActivity(), "Server error", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(getActivity(), "Your account was created", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
                QueryAction.addSubscription(subscription);
            }
        });
        mLogin.setOnClickListener(view -> getActivity().onBackPressed());

        return v;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        mLogin.setClickable(!show);
    }

    private boolean validateData(){
        boolean valid = true;
        View view = null;
        if (mName.getText().toString().length() < 3){
            valid = false;
            mName.setError("Write correct name");
            view = mName;
        }
        if (!mEmail.getText().toString().contains("@")){
            valid = false;
            mEmail.setError("This email is invalided");
            view = mEmail;
        }
        if (mNumber.getText().toString().length()<8){
            valid = false;
            mNumber.setError("This number is invalided");
            view = mNumber;
        }
        if (mPassword.getText().toString().length() < 6){
            valid = false;
            mPassword.setError("Short password(must be more 6 symbols)");
            view = mPassword;
        }
        if (mConfirmPassword.getText().toString().isEmpty()
                || !mConfirmPassword.getText().toString().equals(mPassword.getText().toString())){
            valid = false;
            mConfirmPassword.setError("Invalid confirming password");
            view = mConfirmPassword;
        }

        if (view != null){
            view.requestFocus();
        }

        return valid;
    }
}
