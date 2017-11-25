package com.example.andrii.flashchat.Activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.adapters.ViewPagerAdapter;
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.data.SingletonConnection;
import com.example.andrii.flashchat.data.actions.ActionGetPersonData;
import com.example.andrii.flashchat.fragments.RecyclerViewFragment;
import com.example.andrii.flashchat.tools.ImageTools;
import com.example.andrii.flashchat.tools.QueryPreferences;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.widget.Toast.LENGTH_LONG;

public class MessagesListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "MessagesListActivity";
    private static final String USER_ID_ARG_KEY = "USER_ID_ARG_KEY";
    private ViewPager mViewPager;
    private Person currentUser;
    private CircleImageView ivProfilePhoto;
    private TextView tvUserName;

    public static Intent newIntent(Context context,String userId){
        Intent intent = new Intent(context,MessagesListActivity.class);
        intent.putExtra(USER_ID_ARG_KEY,userId);

        return intent;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);

        String userId = getIntent().getStringExtra(USER_ID_ARG_KEY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);

        ivProfilePhoto = hView.findViewById(R.id.imageView);
        tvUserName = hView.findViewById(R.id.textView);



        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = findViewById(R.id.viewPager);
        setUpViewPager();
        mTabLayout.setupWithViewPager(mViewPager);

        setUpUserData(userId);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.nav_profile:
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_exit:
                QueryPreferences.setActiveUserId(this,null);
                Intent intent = new Intent(this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setUpUserData(String userId) {
        ActionGetPersonData actionGetPersonData = new ActionGetPersonData(userId);

        Observable<String> connectionToServerObservable = Observable.just(actionGetPersonData).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .map(action -> {
                    SingletonConnection.getInstance().connect();
                    SingletonConnection.getInstance().executeAction(action);

                    BufferedReader in = SingletonConnection.getInstance().getReader();
                    Log.d(TAG,"in = null:" + String.valueOf(in == null));
                    String answer = "";
                    try {
                        answer = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return answer;
                });
        Observable<String> observable = Observable.empty();
        observable.mergeWith(connectionToServerObservable)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(5, TimeUnit.SECONDS)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG,"onCompleted");
                        SingletonConnection.getInstance().close();
                        setInformation();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"",e);
                        SingletonConnection.getInstance().close();
                        Toast.makeText(getApplicationContext(),"Server error", LENGTH_LONG).show();

                    }

                    @Override
                    public void onNext(String answer) {
                        Log.d(TAG,"OnNext");
                        Log.d(TAG,answer);

                        if (answer.equals("not found")){
                            Toast.makeText(getApplicationContext(),"Server error", LENGTH_LONG).show();
                        }else{
                            Gson gson = new Gson();
                            currentUser = gson.fromJson(answer,Person.class);
                        }
                    }
                });

    }

    private void setInformation(){
        ImageTools tools = new ImageTools(this);
        tools.downloadImage(ivProfilePhoto,currentUser);

        tvUserName.setText(currentUser.getName());
    }


    private void setUpViewPager(){

        ViewPagerAdapter mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(RecyclerViewFragment.newInstance(RecyclerViewFragment.FRAGMENT_TYPE_MESSAGES),"Messages");
        mAdapter.addFragment(RecyclerViewFragment.newInstance(RecyclerViewFragment.FRAGMENT_TYPE_ONLINE),"Online");
        mAdapter.addFragment(RecyclerViewFragment.newInstance(RecyclerViewFragment.FRAGMENT_TYPE_GROUPS),"Groups");

        mViewPager.setAdapter(mAdapter);
    }


}
