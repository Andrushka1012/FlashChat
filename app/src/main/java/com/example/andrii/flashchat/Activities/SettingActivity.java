package com.example.andrii.flashchat.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.actions.ActionChanePassword;
import com.example.andrii.flashchat.tools.QueryAction;
import com.example.andrii.flashchat.tools.QueryPreferences;

import rx.Observer;
import rx.Subscription;

public class SettingActivity extends AppCompatActivity {
    AutoCompleteTextView oldPassword;
    AutoCompleteTextView newPassword;

    public static Intent newIntent(Context context){
        return new Intent(context,SettingActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        oldPassword = findViewById(R.id.old_password);
        newPassword = findViewById(R.id.new_password);

        Button apply = findViewById(R.id.apply);
        apply.setOnClickListener(v ->{
            apply.setEnabled(false);
            if (oldPassword.getText().toString().length()<8 ){
                oldPassword.setError(getString(R.string.error_invalid_password));
                apply.setEnabled(true);
                return;
            }
            if (newPassword.getText().toString().length()<8 ){
                newPassword.setError(getString(R.string.error_invalid_password));
                apply.setEnabled(true);
                return;
            }
            if (newPassword.getText().toString().equals(oldPassword.getText().toString())){
                newPassword.setError("New password is equal to old");
                apply.setEnabled(true);
                return;
            }

            ActionChanePassword action = new ActionChanePassword(QueryPreferences.getActiveUserId(SettingActivity.this),
                    oldPassword.getText().toString(),newPassword.getText().toString());
           Subscription subscription = QueryAction.executeAnswerQuery(action)
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            apply.setEnabled(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            apply.setEnabled(true);
                            Toast.makeText(SettingActivity.this,"Connection Error",Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onNext(String s) {
                            if (s.equals("error")){
                                onError(new Throwable("error"));
                            }else{
                                if (s.equals("incorrect")) Toast.makeText(SettingActivity.this,"Incorrect password",Toast.LENGTH_LONG)
                                        .show();
                                else Toast.makeText(SettingActivity.this,"Password was changed",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            QueryAction.addSubscription(subscription);
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QueryAction.unsubscribeAll();
    }

}
