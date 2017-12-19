package com.example.andrii.flashchat.Activitys;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.fragments.SearchFragment;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = SearchFragment.newInstance();
            fm.beginTransaction().add(R.id.fragmentContainer,fragment).commit();
        }


    }

}

