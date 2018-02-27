package com.example.andrii.flashchat.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.adapters.AttachmentsAdapter;
import com.example.andrii.flashchat.tools.QueryAction;

import java.util.ArrayList;

public class AttachmentsActivity extends AppCompatActivity {
    private static String PHOTOS_EXTRA = "PHOTOS_EXTRA";

    public static Intent newIntent(Context context,ArrayList<String> photos){
        Intent intent = new Intent(context,AttachmentsActivity.class);
        intent.putStringArrayListExtra(PHOTOS_EXTRA,photos);

        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachments);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        ArrayList<String> photosId = getIntent().getStringArrayListExtra(PHOTOS_EXTRA);

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
        recyclerView.setAdapter(new AttachmentsAdapter(this,photosId));

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
