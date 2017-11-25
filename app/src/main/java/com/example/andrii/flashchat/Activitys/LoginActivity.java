package com.example.andrii.flashchat.Activitys;

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

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        LoginHelper{

    private EmailHelper mHelper;
    private String mFragmentTag = "";
    private int stackSize = 0;

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
        mHelper = (EmailHelper) fragment;


    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        mHelper.addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public void changeFragment(int fragmentType) {
        switch (fragmentType){
            case LOGIN_FRAGMENT:
                mFragmentTag = "FragmentLogin";
                //String type = "";
                break;
            case REGISTER_FRAGMENT:
                mFragmentTag = "FragmentRegister";
                break;

        }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, RegisterFragment .newInstance(),mFragmentTag)
                    .addToBackStack("LoginFragment")
                    .commit();
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().findFragmentByTag(mFragmentTag) != null){
            getSupportFragmentManager().popBackStack(mFragmentTag,FragmentManager.POP_BACK_STACK_INCLUSIVE);
            mFragmentTag = "";
        }else super.onBackPressed();
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }





}
