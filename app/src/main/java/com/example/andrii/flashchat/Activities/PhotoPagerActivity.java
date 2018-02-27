package com.example.andrii.flashchat.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.DB.MessageDb;
import com.example.andrii.flashchat.data.DB.UserNamesBd;
import com.example.andrii.flashchat.data.Model.MessageItem;
import com.example.andrii.flashchat.fragments.PhotoPagerFragment;
import com.example.andrii.flashchat.tools.ImageTools;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

public class PhotoPagerActivity extends AppCompatActivity {
    private static String PHOTOS_EXTRAS = "PHOTOS_EXTRAS";
    private static String START_POSITION_EXTRAS = "START_POSITION_EXTRAS";
    private TextView tvIndex;
    private TextView tvName;
    private TextView tvDate;
    private CircleImageView imageView;
    private int currentPosition;
    private List<MessageItem> photos = new ArrayList<>();

    public static Intent newIntent(Context context, ArrayList<String> photos, int startPosition){
        Intent intent = new Intent(context,PhotoPagerActivity.class);
        intent.putStringArrayListExtra(PHOTOS_EXTRAS,photos);
        intent.putExtra(START_POSITION_EXTRAS,startPosition);

        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton imageButton = findViewById(R.id.ibtn_back);
        imageButton.setOnClickListener(v ->onBackPressed());

        tvIndex = toolbar.findViewById(R.id.tv_current_index);
        imageView = findViewById(R.id.profile_photo);
        tvName = findViewById(R.id.tv_name);
        tvDate = findViewById(R.id.tv_date);

        ArrayList<String> idList = getIntent().getStringArrayListExtra(PHOTOS_EXTRAS);
        ViewPager viewPager = findViewById(R.id.pager);
        Realm realm = Realm.getDefaultInstance();
        for (String str :idList){
            MessageDb db = realm.where(MessageDb.class).equalTo("msgID",str).findFirst();
            MessageItem messageItem =
                    new MessageItem(db.getMsgID(),"image Item",db.getSenderId(),db.getRecipient_id(),db.getDate(),1,1);
            photos.add(messageItem);

        }
        realm.close();

        currentPosition = getIntent().getIntExtra(START_POSITION_EXTRAS,0);

        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(),photos));
        viewPager.setCurrentItem(currentPosition);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateUI(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ImageButton imSave = findViewById(R.id.ibSave);
        imSave.setOnClickListener(v ->{
            ImageTools tools = new ImageTools(this);
            tools.saveToGallery(photos.get(currentPosition).getMsgID());
            Toast.makeText(this,"Image was saved",Toast.LENGTH_SHORT).show();
        });
        Log.d("asd",currentPosition + " " + photos.size());
        updateUI(currentPosition);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter{
        List<MessageItem> photos;
        public MyPagerAdapter(FragmentManager fm, List<MessageItem> photos) {
            super(fm);
            this.photos = photos;
        }

        @Override
        public Fragment getItem(int position) {
            return PhotoPagerFragment.newInstance(photos.get(position).getMsgID());
        }

        @Override
        public int getCount() {
            return photos.size();
        }
    }

    public void updateUI(int currentPosition){
        tvIndex.setText((currentPosition + 1) + "/" + photos.size());
        String root = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
        File file = new File(root,photos.get(currentPosition).getSenderId() + ".jpg");
        Picasso.with(this).load(file).into(imageView);

        tvDate.setText(photos.get(currentPosition).getDate().toString());
        Realm realm = Realm.getDefaultInstance();

        String name = realm
                .where(UserNamesBd.class)
                .equalTo("userId",photos.get(currentPosition).getSenderId())
                .findFirst()
                .getName();
        if (name == null) name = "Error with loading name";
        tvName.setText(name);
        this.currentPosition = currentPosition;
    }

}
