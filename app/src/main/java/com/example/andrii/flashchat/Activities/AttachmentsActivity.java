package com.example.andrii.flashchat.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.adapters.AttachmentsAdapter;
import com.example.andrii.flashchat.data.MessageItem;
import com.example.andrii.flashchat.tools.QueryAction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class AttachmentsActivity extends AppCompatActivity {
    private static String PHOTOS_EXTRA = "PHOTOS_EXTRA";

    public static Intent newIntent(Context context,String photos){
        Intent intent = new Intent(context,AttachmentsActivity.class);
        intent.putExtra(PHOTOS_EXTRA,photos);

        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachments);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        String json = getIntent().getStringExtra(PHOTOS_EXTRA);
        Log.d("qwe",json);
        Gson gson = new Gson();
        Type type = new TypeToken<List<MessageItem>>(){}.getType();
        List<MessageItem> photos = gson.fromJson(json,type);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position >3) return 1;
                else return 2;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = 10;
                outRect.right = 10;
                outRect.top = 10;
            }
        });
        recyclerView.setAdapter(new AttachmentsAdapter(this,photos));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QueryAction.unsubscribeAll();
    }
}
