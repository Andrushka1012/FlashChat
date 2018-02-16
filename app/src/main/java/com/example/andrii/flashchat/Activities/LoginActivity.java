package com.example.andrii.flashchat.Activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.interfaces.EmailHelper;
import com.example.andrii.flashchat.data.interfaces.LoginHelper;
import com.example.andrii.flashchat.fragments.LoginFragment;
import com.example.andrii.flashchat.fragments.RegisterFragment;
import com.example.andrii.flashchat.tools.QueryAction;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity
        implements LoginHelper{


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);


        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = LoginFragment.newInstance();
            fm.beginTransaction().add(R.id.fragmentContainer,fragment).commit();
        }


    }

    @Override
    public void changeFragment() {

            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack("LoginFragment")
                    .replace(R.id.fragmentContainer, RegisterFragment.newInstance(),"RegisterFragment")
                    .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QueryAction.unsubscribeAll();
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().findFragmentByTag("RegisterFragment") != null){
            getSupportFragmentManager().popBackStack("RegisterFragment",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }else super.onBackPressed();
    }







}
