package com.example.andrii.flashchat.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.adapters.SearchListAdapter;
import com.example.andrii.flashchat.data.SearchItem;
import com.example.andrii.flashchat.data.actions.ActionSearch;
import com.example.andrii.flashchat.fragments.SearchFragment;
import com.example.andrii.flashchat.tools.QueryAction;
import com.example.andrii.flashchat.tools.QueryPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

import static android.widget.Toast.LENGTH_LONG;

public class SearchActivity extends AppCompatActivity {
    public static final String TAG = "SearchActivity";
    private static final String IsSpeechOn_EXTRA = "IsSpeechOn_EXTRA";
    private static final String SEARCH_TEXT = "SEARCH_TEXT";
    private static int SPEECH_REQ = 11;
    private EditText etSearch;
    private Subscription subscription;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView tvEmpty;

    public static Intent newIntent(Context context,boolean isSpeechOn){
        Intent intent = new Intent(context,SearchActivity.class);
        intent.putExtra(IsSpeechOn_EXTRA,isSpeechOn);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton ibBack = toolbar.findViewById(R.id.ib_back);
        ibBack.setOnClickListener(v -> onBackPressed());

        etSearch = findViewById(R.id.etSearch);
        if(savedInstanceState != null){
            String text = savedInstanceState.getString(SEARCH_TEXT);
            if (text != null) {
                etSearch.setText(text);
            }
        }


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = etSearch.getText().toString();

                tvEmpty.setVisibility(s.isEmpty()?View.VISIBLE:View.GONE);
                mRecyclerView.setVisibility(s.isEmpty()?View.GONE:View.VISIBLE);
                if (!s.isEmpty()){
                    showProgress(true);
                    if (subscription != null){
                        if (!subscription.isUnsubscribed()) {
                            subscription.unsubscribe();
                            Log.d(TAG,"UnSubscribe");
                        }
                    }
                    ActionSearch actionSearch = new ActionSearch(s, QueryPreferences.getActiveUserId(SearchActivity.this));
                    Observable<String> observable = QueryAction.executeAnswerQuery(actionSearch);
                    subscription = observable.subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {

                            showProgress(false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(SearchActivity.this,"Server error", LENGTH_LONG).show();
                            showProgress(false);
                            tvEmpty.setText("Connection Error");
                            tvEmpty.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onNext(String answer) {
                            Log.d(TAG,"Answer:" + answer);
                            if (!answer.equals("error")){
                                Type listType = new TypeToken<List<SearchItem>>(){}.getType();
                                Gson gson = new Gson();
                                List<SearchItem> list = gson.fromJson(answer,listType);
                                if (list.isEmpty()){
                                    tvEmpty.setText(R.string.the_search_has_not_given_any_results);
                                    tvEmpty.setVisibility(View.VISIBLE);
                                    mRecyclerView.setVisibility(View.GONE);
                                }

                                mRecyclerView.setAdapter(new SearchListAdapter(SearchActivity.this,list));
                            }else{
                                Toast.makeText(SearchActivity.this,"Server error", LENGTH_LONG).show();
                            }

                        }
                    });

                }else{
                    tvEmpty.setText(R.string.enter_which_person_you_search);
                }
            }
        });
        QueryAction.addSubscription(subscription);

        ImageButton ibSpeech = findViewById(R.id.ib_speech);
        ibSpeech.setOnClickListener(view -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            Locale[] languages  = {Locale.getDefault(), Locale.ENGLISH} ;
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,languages);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something:");

            startActivityForResult(intent,SPEECH_REQ);

        });

        mProgressBar = findViewById(R.id.progress_bar);
        tvEmpty = findViewById(R.id.empty_search_view);

        mRecyclerView = findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<SearchItem> list = new ArrayList<>();
        mRecyclerView.setAdapter(new SearchListAdapter(this, list));


        boolean isSpeechOn = getIntent().getBooleanExtra(IsSpeechOn_EXTRA,false);
        if (isSpeechOn)ibSpeech.callOnClick();
        etSearch.requestFocus();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_TEXT,etSearch.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QueryAction.unsubscribeAll();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQ){
            if(resultCode == RESULT_OK && null != data){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String text = result.get(0);

                etSearch.setText(text);
            }
        }
    }


    private void showProgress(boolean b) {
        mRecyclerView.setVisibility(b? View.GONE:View.VISIBLE);
        mProgressBar.setVisibility(b?View.VISIBLE:View.GONE);
    }
}

