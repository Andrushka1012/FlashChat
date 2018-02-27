package com.example.andrii.flashchat.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.Presenter.LoginFragmentPresenter;
import com.example.andrii.flashchat.data.interfaces.LoginChanger;
import com.example.andrii.flashchat.data.interfaces.LoginView;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginFragment extends Fragment implements LoginView {
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CircleImageView mRegister;
    private LoginButton mLoginFacebook;
    private CallbackManager callbackManager;
    private LoginFragmentPresenter presenter;

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
        presenter = new LoginFragmentPresenter(this);
        presenter.login(getActivity());

        return initialise(inflater,container);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void showProgress(boolean show) {

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

    @Override
    public void showEmailError(String error) {
        mEmailView.setError(error);
    }

    @Override
    public void showPasswordError(String error) {
        mPasswordView.setError(error);
    }

    private View initialise(LayoutInflater inflater, ViewGroup container){
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
        presenter.registerFacebookCallback(getActivity(),mLoginFacebook,callbackManager);

       /* mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();

                presenter.attemptLogin(getActivity(),email,password);
                return true;
            }
            return false;
        });*/

        Button mEmailSignInButton = v.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(view ->{
            String email = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();

            presenter.attemptLogin(getActivity(),email,password);
        });

        mRegister.setOnClickListener(view -> ((LoginChanger)getActivity()).changeFragment());

        mFacebook.setOnClickListener(view -> mLoginFacebook.callOnClick());
        return v;
    }


}
