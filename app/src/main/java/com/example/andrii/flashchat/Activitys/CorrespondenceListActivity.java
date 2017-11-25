package com.example.andrii.flashchat.Activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.Views.LinerLayoutWithMaxHeight;
import com.example.andrii.flashchat.adapters.ChatListAdapter;
import com.example.andrii.flashchat.data.Message;
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.data.TestData;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

public class CorrespondenceListActivity extends AppCompatActivity {
    private static final int MY_ACTIVITY_RESULT_REQUEST_CODE = 103;
    private static final int PICK_IMAGE_REQUEST_CODE = 1;

    public static final String TITLE_EXTRA = "TITLE_EXTRA";
    private RecyclerView mRecyclerView;
    private LinerLayoutWithMaxHeight mLlSend;
    private ImageButton mIbSend;
    private ImageButton mIbTakePhoto;
    private EditText mEtMessage;
    private Person I = new Person("Andruszka");
    private UUID mId;


    public static Intent newIntent(Context context, String title) {
        Intent intent = new Intent(context, CorrespondenceListActivity.class);
        intent.putExtra(TITLE_EXTRA, title);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correspondence_list);
        String title = getIntent().getStringExtra(TITLE_EXTRA);
        getSupportActionBar().setTitle(title);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new ChatListAdapter(this, TestData.getMessages(I), I.getId()));

        mLlSend = (LinerLayoutWithMaxHeight) findViewById(R.id.ll_write);
        mLlSend.setMaxHeight(158);

        mIbSend = (ImageButton) findViewById(R.id.ib_send);
        mIbTakePhoto = (ImageButton) findViewById(R.id.ib_take_photo);
        mEtMessage = (EditText) findViewById(R.id.et_message);
        mEtMessage.setOnClickListener(v -> {
            mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                });

        mIbSend.setOnClickListener(view -> {
            if (mEtMessage.getText().toString().equals("")) return;
            Message msg = new Message(mEtMessage.getText().toString(),I,Message.MESSAGE_TEXT_TYPE);
            ((ChatListAdapter) mRecyclerView.getAdapter()).addMessage(msg);
            mRecyclerView.getAdapter().notifyDataSetChanged();
            mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
            mEtMessage.setText("");
        });

        mIbTakePhoto.setOnClickListener(view -> {
            Message msg = new Message("",I,Message.MESSAGE_IMAGE_TYPE);
            Intent intent = PhotoActivity.newIntent(this, msg.getID().toString());
            mId =  msg.getID();
            startActivityForResult(intent, MY_ACTIVITY_RESULT_REQUEST_CODE);
        });
        mIbTakePhoto.setOnLongClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
            return true;
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_ACTIVITY_RESULT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String filePath = data.getStringExtra("Path");
                Message msg = new Message(filePath,I,Message.MESSAGE_IMAGE_TYPE);
                msg.setID(mId);
                mId = null;
                ((ChatListAdapter) mRecyclerView.getAdapter()).addMessage(msg);
                mRecyclerView.getAdapter().notifyDataSetChanged();
                mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Log.d("qwe","Nothing selected");
                return;
            }

            File file = null;
            Message msg = new Message("",I,Message.MESSAGE_IMAGE_TYPE);
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);


                String root = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
                file = new File(root,msg.getID().toString() + ".jpg");
                if (file.exists ()) file.delete();

                    FileOutputStream out = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            String filePath = file.getPath();
            msg.setImagePath(filePath);
            ((ChatListAdapter) mRecyclerView.getAdapter()).addMessage(msg);
            mRecyclerView.getAdapter().notifyDataSetChanged();
            mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);

        }


    }
}