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
import android.widget.Toast;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.Views.LinerLayoutWithMaxHeight;
import com.example.andrii.flashchat.adapters.ChatListAdapter;
import com.example.andrii.flashchat.data.Message;
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.data.SingletonConnection;
import com.example.andrii.flashchat.data.TestData;
import com.example.andrii.flashchat.data.actions.ActionGetPersonData;
import com.example.andrii.flashchat.tools.QueryAction;
import com.example.andrii.flashchat.tools.QueryPreferences;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

import rx.Observable;
import rx.Observer;

import static android.widget.Toast.LENGTH_LONG;

public class CorrespondenceListActivity extends AppCompatActivity {
    private static final int MY_ACTIVITY_RESULT_REQUEST_CODE = 103;
    private static final int PICK_IMAGE_REQUEST_CODE = 1;

    private static final String TAG = "CorrespondenceListActivity";
    public static final String TITLE_EXTRA = "TITLE_EXTRA";
    public static final String PERSON_EXTRA = "PERSON_EXTRA";
    private RecyclerView mRecyclerView;
    private LinerLayoutWithMaxHeight mLlSend;
    private ImageButton mIbSend;
    private ImageButton mIbTakePhoto;
    private EditText mEtMessage;
    private Person currentUser = new Person("Andruszka");
    private UUID mId;

    @Deprecated
    public static Intent newIntent(Context context, String title) {
        Intent intent = new Intent(context, CorrespondenceListActivity.class);
        intent.putExtra(TITLE_EXTRA, title);

        return intent;
    }

    public static Intent newIntent(Context context, Person p) {
        Intent intent = new Intent(context, CorrespondenceListActivity.class);
        intent.putExtra(PERSON_EXTRA, p);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correspondence_list);
        setUserData();

        Person subject = getIntent().getParcelableExtra(PERSON_EXTRA);
        getSupportActionBar().setTitle(subject.getName());

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new ChatListAdapter(this, TestData.getMessages(currentUser), currentUser.getId()));

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
            Message msg = new Message(mEtMessage.getText().toString(), currentUser,Message.MESSAGE_TEXT_TYPE);
            ((ChatListAdapter) mRecyclerView.getAdapter()).addMessage(msg);
            mRecyclerView.getAdapter().notifyDataSetChanged();
            mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
            mEtMessage.setText("");
        });

        mIbTakePhoto.setOnClickListener(view -> {
            Message msg = new Message("", currentUser,Message.MESSAGE_IMAGE_TYPE);
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

    private void setUserData() {
        ActionGetPersonData actionGetPersonData = new ActionGetPersonData(QueryPreferences.getActiveUserId(this));
        Observable<String> observable = QueryAction.executeAnswerQuery(this,actionGetPersonData,TAG);
        observable.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                SingletonConnection.getInstance().close();
            }

            @Override
            public void onError(Throwable e) {
                SingletonConnection.getInstance().close();
                Toast.makeText(getApplicationContext(),"Server error", LENGTH_LONG).show();
            }

            @Override
            public void onNext(String s) {
                Gson gson = new Gson();
                currentUser = gson.fromJson(s,Person.class);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_ACTIVITY_RESULT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String filePath = data.getStringExtra("Path");
                Message msg = new Message(filePath, currentUser,Message.MESSAGE_IMAGE_TYPE);
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
            Message msg = new Message("", currentUser,Message.MESSAGE_IMAGE_TYPE);
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