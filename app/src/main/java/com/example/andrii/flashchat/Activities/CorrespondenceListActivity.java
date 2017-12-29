package com.example.andrii.flashchat.Activities;

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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.Views.LinerLayoutWithMaxHeight;
import com.example.andrii.flashchat.adapters.ChatListAdapter;
import com.example.andrii.flashchat.data.DB.MessageDb;
import com.example.andrii.flashchat.data.Message;
import com.example.andrii.flashchat.data.MessageItem;
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.data.SingletonConnection;
import com.example.andrii.flashchat.data.actions.ActionGetMessages;
import com.example.andrii.flashchat.data.actions.ActionGetPersonData;
import com.example.andrii.flashchat.data.actions.ActionSendMessage;
import com.example.andrii.flashchat.tools.ImageTools;
import com.example.andrii.flashchat.tools.QueryAction;
import com.example.andrii.flashchat.tools.QueryPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import static android.widget.Toast.LENGTH_LONG;

public class CorrespondenceListActivity extends AppCompatActivity {
    private static final int MY_ACTIVITY_RESULT_REQUEST_CODE = 103;
    private static final int PICK_IMAGE_REQUEST_CODE = 1;

    private static final String TAG = "CorrListAct";
    public static final String TITLE_EXTRA = "TITLE_EXTRA";
    public static final String PERSON_EXTRA = "PERSON_EXTRA";

    private Realm realm;
    private RecyclerView mRecyclerView;
    private EditText mEtMessage;
    private Person currentUser;
    private Person subject;
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
        //realm = SingletonRealm.getInstance().getRealm(this);

        realm = Realm.getDefaultInstance();
        setUserData();

        subject = getIntent().getParcelableExtra(PERSON_EXTRA);
        getSupportActionBar().setTitle(subject.getName());

        mRecyclerView = findViewById(R.id.recycler_view);
        setupRecyclerView();

        LinerLayoutWithMaxHeight mLlSend = findViewById(R.id.ll_write);
        mLlSend.setMaxHeight(158);

        ImageButton mIbSend = findViewById(R.id.ib_send);
        ImageButton mIbTakePhoto = findViewById(R.id.ib_take_photo);
        mEtMessage = findViewById(R.id.et_message);
        mEtMessage.setOnClickListener(v -> mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1));

        mIbSend.setOnClickListener((View view) -> {
            if (mEtMessage.getText().toString().equals("")) return;
            Message msg = new Message(mEtMessage.getText().toString(), currentUser,Message.MESSAGE_TEXT_TYPE);

            ActionSendMessage action = new ActionSendMessage(
                    msg.getID().toString(),msg.getText(),currentUser.getId(),subject.getId(),Message.MESSAGE_TEXT_TYPE);
            QueryAction.executeAnswerQuery(action)
                    .doOnUnsubscribe(()->{})
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            SingletonConnection.getInstance().close();
                            updateDataBase();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG,"onError:",e);
                            Toast.makeText(CorrespondenceListActivity.this,"Message was not sent.",LENGTH_LONG).show();
                        }

                        @Override
                        public void onNext(String s) {
                            if (s.equals("error")){
                                onError(new Throwable(s));
                            }

                        }
                    });
            MessageItem msgItem = new MessageItem(msg,subject);
            ((ChatListAdapter)mRecyclerView.getAdapter()).addMessage(msgItem);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_ACTIVITY_RESULT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String filePath = data.getStringExtra("Path");
                Message msg = new Message(filePath, currentUser,Message.MESSAGE_IMAGE_TYPE);
                msg.setID(mId);
                mId = null;

                MessageItem msgItem = new MessageItem(msg,subject);
                ((ChatListAdapter)mRecyclerView.getAdapter()).addMessage(msgItem);
                mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);

                ImageTools tools = new ImageTools(this);
                tools.sendImage(new File(filePath),msg.getID().toString(),currentUser.getId(),subject.getId());

                ActionSendMessage action = new ActionSendMessage
                        (msg.getID().toString(),"image message",currentUser.getId(),subject.getId(),Message.MESSAGE_IMAGE_TYPE);
                QueryAction.executeAnswerQuery(action)
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(CorrespondenceListActivity.this,"Message was not sent.",LENGTH_LONG).show();
                            }

                            @Override
                            public void onNext(String s) {

                            }
                        });
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

            MessageItem msgItem = new MessageItem(msg,subject);
            ((ChatListAdapter)mRecyclerView.getAdapter()).addMessage(msgItem);
            mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);

            ImageTools tools = new ImageTools(this);
            tools.sendImage(file,msg.getID().toString(),currentUser.getId(),subject.getId());

            ActionSendMessage action = new ActionSendMessage
                    (msg.getID().toString(),"image message",currentUser.getId(),subject.getId(),Message.MESSAGE_IMAGE_TYPE);
            QueryAction.executeAnswerQuery(action)
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(CorrespondenceListActivity.this,"Message was not sent.",LENGTH_LONG).show();
                        }

                        @Override
                        public void onNext(String s) {

                        }
                    });

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //SingletonRealm.getInstance().close();
        realm.close();
    }

    private void updateDataBase(){
        ActionGetMessages action = new ActionGetMessages(currentUser.getId(),subject.getId());
        QueryAction.executeAnswerQuery(action)
                .timeout(10, TimeUnit.SECONDS, Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("CorrespondenceActivity","OnError",e);
                        Toast.makeText(CorrespondenceListActivity.this,"Messages was not received.",LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG,s);
                        if (!s.equals("error")) {
                           Gson gson = new Gson();
                            Type listType = new TypeToken<List<MessageDb>>(){}.getType();
                            List<MessageDb> list = gson.fromJson(s,listType);

                            realm.executeTransactionAsync(
                                    r -> {
                                        //transaction
                                        for (MessageDb m:list){
                                            r.insertOrUpdate(m);

                                            if (m.getType() == 1){
                                                String root = CorrespondenceListActivity.this
                                                        .getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
                                                File file = new File(root,m.getMsgID() + ".jpg");
                                                if (!file.exists()) {
                                                    String encodedString = m.getText();
                                                    Log.d(TAG,"encoded string:" + encodedString);

                                                    byte[] imageBytes = Base64.decode(encodedString,Base64.DEFAULT);
                                                    Bitmap image = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);

                                                    ImageTools tools = new ImageTools(CorrespondenceListActivity.this);
                                                    tools.saveImage(file, image);
                                                }
                                            }
                                        }
                                    },
                                    () -> {
                                        //onSuccess
                                        Toast.makeText(CorrespondenceListActivity.this,"onCompleted",LENGTH_LONG).show();

                                        RealmResults<MessageDb> results = realm.where(MessageDb.class)
                                                .equalTo("recipient_id",subject.getId())
                                                .and()
                                                .equalTo("senderId",currentUser.getId())
                                                .or()
                                                .equalTo("recipient_id",currentUser.getId())
                                                .and()
                                                .equalTo("senderId",subject.getId())
                                                .findAll();


                                        results.sort("date");
                                        List<MessageItem> itemsList = MessageItem.convertToList(results);
                                        mRecyclerView.setAdapter(new ChatListAdapter(CorrespondenceListActivity.this,itemsList));
                                        mRecyclerView.getAdapter().notifyDataSetChanged();
                                        //mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                                    },
                                    error -> {
                                        //onError
                                        Log.e(TAG,"Transaction error");
                                        onError(error);
                            });
                        } else{
                            Toast.makeText(CorrespondenceListActivity.this,"Messages was not received.",LENGTH_LONG).show();
                            onError(new Throwable(s));
                        }

                    }
                });

    }

    private void setUserData() {
        ActionGetPersonData actionGetPersonData = new ActionGetPersonData(QueryPreferences.getActiveUserId(this));
        Observable<String> observable = QueryAction.executeAnswerQuery(actionGetPersonData);
        observable.doOnUnsubscribe(() ->{})
                .subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                SingletonConnection.getInstance().close();
                updateDataBase();
            }

            @Override
            public void onError(Throwable e) {
                SingletonConnection.getInstance().close();
                Toast.makeText(getApplicationContext(),"Server error", LENGTH_LONG).show();
                currentUser = new Person(QueryPreferences.getActiveUserId(CorrespondenceListActivity.this),"");
            }

            @Override
            public void onNext(String s) {
                Gson gson = new Gson();
                currentUser = gson.fromJson(s,Person.class);
            }
        });

    }


    private void setupRecyclerView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RealmResults<MessageDb> results = realm.where(MessageDb.class)
                .equalTo("recipient_id",subject.getId())
                .and()
                .equalTo("senderId",QueryPreferences.getActiveUserId(this))
                .or()
                .equalTo("recipient_id",QueryPreferences.getActiveUserId(this))
                .and()
                .equalTo("senderId",subject.getId())
                .findAll();

        Log.d("qwe","resultSize:" + results.size());
        results.sort("date");
        List<MessageItem> itemsList = MessageItem.convertToList(results);
        mRecyclerView.setAdapter(new ChatListAdapter(this,itemsList));
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);

    }
}