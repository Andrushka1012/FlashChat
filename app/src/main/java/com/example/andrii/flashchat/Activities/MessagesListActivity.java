package com.example.andrii.flashchat.Activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.adapters.ViewPagerAdapter;
import com.example.andrii.flashchat.data.DB.UserNamesBd;
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.data.actions.ActionGetPersonData;
import com.example.andrii.flashchat.fragments.RecyclerViewFragment;
import com.example.andrii.flashchat.tools.ImageTools;
import com.example.andrii.flashchat.tools.MyService;
import com.example.andrii.flashchat.tools.PollService;
import com.example.andrii.flashchat.tools.QueryAction;
import com.example.andrii.flashchat.tools.QueryPreferences;
import com.google.gson.Gson;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import rx.Observable;
import rx.Observer;

import static android.widget.Toast.LENGTH_LONG;

public class MessagesListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "MessagesListActivity";
    private static final String USER_ID_ARG_KEY = "USER_ID_ARG_KEY";
    private ViewPager mViewPager;
    private Person currentUser;
    private CircleImageView ivProfilePhoto;
    private TextView tvUserName;
    private Intent serviceIntent;

    public static Intent newIntent(Context context,String userId){
        Intent intent = new Intent(context,MessagesListActivity.class);
        intent.putExtra(USER_ID_ARG_KEY,userId);

        return intent;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);
        serviceIntent = new Intent(this,MyService.class);

         startService(serviceIntent);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);

        ivProfilePhoto = hView.findViewById(R.id.imageView);
        tvUserName = hView.findViewById(R.id.textView);
        LinearLayout ll = hView.findViewById(R.id.ll_nav_header);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.nav_header);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,500,400,true);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),scaledBitmap);
        ll.setBackground(bitmapDrawable);


        TabLayout mTabLayout = findViewById(R.id.tabs);
        mViewPager = findViewById(R.id.viewPager);
        setUpViewPager();
        mTabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    protected void onResume() {
        setUpUserData(getIntent().getStringExtra(USER_ID_ARG_KEY));
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.messages_list, menu);
        MenuItem toggleItem = menu.findItem(R.id.action_toggle_polling);
        toggleItem.setTitle(PollService.isServiceAlarmOn(this)?R.string.stop_polling:R.string.polling_on);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                startActivity(new Intent(this,SearchActivity.class));
                return true;
            case R.id.item_search:
                startActivity(new Intent(this,SearchActivity.class));
            return true;
            case R.id.action_toggle_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(this);
                PollService.setServiceAlarm(this,shouldStartAlarm);
                this.invalidateOptionsMenu();
                return true;
         default:
             return super.onOptionsItemSelected(item);
        }


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        int id = item.getItemId();
        switch (id){
            case R.id.nav_profile:
                intent = ProfileActivity.newIntent(this,currentUser);
                startActivity(intent);
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_exit:
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(r -> {
                    r.deleteAll();
                    r.close();
                });

                QueryPreferences.setActiveUserId(this,null);
                intent = new Intent(this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;


        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setUpUserData(String userId) {
        ActionGetPersonData actionGetPersonData = new ActionGetPersonData(userId);


        Observable<String> observable = QueryAction.executeAnswerQuery(actionGetPersonData);
        observable.subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG,"onCompleted");
                        setInformation();
                    }

                    @Override
                    public void onError(Throwable e) {
                        /*if(e.getClass() == TimeoutException.class || e.getClass() == SocketException.class){
                            String root = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
                            File file = new File(root,QueryPreferences.getActiveUserId(getApplicationContext()) + ".jpg");
                            if (file.exists()) {
                                String path = file.getPath();
                                Uri uri = Uri.fromFile(new File(path));
                                Bitmap image;
                                try {
                                    image = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                                } catch (IOException ex) {
                                    Log.e(TAG,"IOException",ex);
                                    image = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_action_person);
                                }
                                ivProfilePhoto.setImageBitmap(image);
                            }
                        }else{
                            Bitmap image = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_action_person);
                            ivProfilePhoto.setImageBitmap(image);
                        }*/
                        Log.e(TAG,"OnError",e);

                        Toast.makeText(getApplicationContext(),"Server error", LENGTH_LONG).show();
                        Realm realm = Realm.getDefaultInstance();

                        UserNamesBd unbd = realm.where(UserNamesBd.class)
                                .equalTo("userId",QueryPreferences.getActiveUserId(MessagesListActivity.this))
                                .findFirst();
                        String name = unbd == null?null:unbd.getName();

                        currentUser = new Person(
                                QueryPreferences.getActiveUserId(MessagesListActivity.this),name,new Date().toString(),"Number","email@email.com","gender","offline");
                        setInformation();
                    }

                    @Override
                    public void onNext(String answer) {
                        Log.d(TAG,"OnNext");
                        Log.d(TAG,answer);


                        if (answer.equals("error")){
                            Toast.makeText(getApplicationContext(),"Server error", LENGTH_LONG).show();
                            onError(new Throwable(answer));
                        }else{
                            Gson gson = new Gson();
                            currentUser = gson.fromJson(answer,Person.class);

                        }
                    }
                });

    }

    private void setInformation(){
        ImageTools tools = new ImageTools(this);
        tools.downloadPersonImage(ivProfilePhoto,currentUser);

        tvUserName.setText(currentUser.getName());
    }


    private void setUpViewPager(){

        ViewPagerAdapter mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(RecyclerViewFragment.newInstance(RecyclerViewFragment.FRAGMENT_TYPE_MESSAGES),"Messages");
        mAdapter.addFragment(RecyclerViewFragment.newInstance(RecyclerViewFragment.FRAGMENT_TYPE_ONLINE),"Online");
        mAdapter.addFragment(RecyclerViewFragment.newInstance(RecyclerViewFragment.FRAGMENT_TYPE_GROUPS),"Groups");

        mViewPager.setAdapter(mAdapter);
    }

    private boolean isMyServiceAlive(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: manager.getRunningServices(Integer.MAX_VALUE)){
            Log.d(TAG,"My service running:" + true);
            return true;
        }
        Log.d(TAG,"My service running:" + false);
        return false;
    }


}
