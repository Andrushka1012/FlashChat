package com.example.andrii.flashchat.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.adapters.ViewPagerAdapter;
import com.example.andrii.flashchat.data.DB.MessageDb;
import com.example.andrii.flashchat.data.DB.UserNamesBd;
import com.example.andrii.flashchat.data.Model.Person;
import com.example.andrii.flashchat.fragments.RecyclerViewFragment;
import com.example.andrii.flashchat.tools.ImageTools;
import com.example.andrii.flashchat.tools.MessagesListLoader;
import com.example.andrii.flashchat.tools.QueryAction;
import com.example.andrii.flashchat.tools.QueryPreferences;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;

public class MessagesListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private final String TAG = "MessagesListActivity";
    private static final String USER_ID_ARG_KEY = "USER_ID_ARG_KEY";
    private static final String USER_KAY = "USER_KAY";

    private ViewPager mViewPager;
    private Person currentUser;
    private String userId = QueryPreferences.getActiveUserId(this);
    private CircleImageView ivProfilePhoto;
    private TextView tvUserName;
    private Bundle savedInstance = null;
    private Realm realm;
    private MessagesListLoader loader;

    public static Intent newIntent(Context context,String userId){
        Intent intent = new Intent(context,MessagesListActivity.class);
        intent.putExtra(USER_ID_ARG_KEY,userId);

        return intent;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);
        savedInstance = savedInstanceState;
        setupLayout();

        realm = Realm.getDefaultInstance();
        UserNamesBd user = realm.where(UserNamesBd.class).equalTo("userId",userId).findFirst();
        if (user != null) {
            tvUserName.setText(user.getName());
            currentUser = new Person(userId,user.getName());
        }else{
            currentUser = new Person(userId,"");
        }

        ImageTools tools = new ImageTools(this);
        tools.downloadPersonImage(ivProfilePhoto,new Person(userId,""),true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpUserData(getIntent().getStringExtra(USER_ID_ARG_KEY));
        loader.startLoading();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (loader.isLoading()) loader.stopLoading();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(USER_KAY,currentUser);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QueryAction.unsubscribeAll();
        realm.close();
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
        getMenuInflater().inflate(R.menu.messages_list,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.action_search:
                startActivity(SearchActivity.newIntent(this,false));
            return true;
            case R.id.action_speech:
                startActivity(SearchActivity.newIntent(this,true));
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
                ProfileActivity.startActivity(this,QueryPreferences.getActiveUserId(this));
                break;
            case R.id.nav_manage:
               intent = SettingActivity.newIntent(this);
                startActivity(intent);
                break;
            case R.id.nav_exit:
                realm.executeTransactionAsync(r -> {
                    RealmResults<MessageDb> resultsMessages = r.where(MessageDb.class).findAll();
                    resultsMessages.deleteAllFromRealm();
                    for(MessageDb messageDb:resultsMessages){messageDb.deleteFromRealm();}
                    RealmResults<UserNamesBd> resultsUsers = r.where(UserNamesBd.class).findAll();
                    for(UserNamesBd userNamesBd:resultsUsers){userNamesBd.deleteFromRealm();}
                    resultsUsers.deleteAllFromRealm();

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

    private void setupLayout(){

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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

        TabLayout mTabLayout = findViewById(R.id.tabs);
        mViewPager = findViewById(R.id.viewPager);
        setUpViewPager();
        mTabLayout.setupWithViewPager(mViewPager);


    }

    private void setUpUserData(String userId) {
        if (savedInstance != null && savedInstance.getParcelable(USER_KAY) != null){
            currentUser = savedInstance.getParcelable(USER_KAY);
                setInformation();
        }else{
            currentUser = new Person(userId,"");
        }
    }

    private void setInformation(){
        ImageTools tools = new ImageTools(this);
        tools.downloadPersonImage(ivProfilePhoto,currentUser,false);

        tvUserName.setText(currentUser.getName());
    }


    private void setUpViewPager(){
        RecyclerViewFragment fragmentMessages = RecyclerViewFragment.newInstance(RecyclerViewFragment.FRAGMENT_TYPE_MESSAGES);
        RecyclerViewFragment fragmentOnline = RecyclerViewFragment.newInstance(RecyclerViewFragment.FRAGMENT_TYPE_ONLINE);

        ViewPagerAdapter mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(fragmentMessages,"Messages");
        mAdapter.addFragment(fragmentOnline,"Online");
        mViewPager.setAdapter(mAdapter);

        Observable<RecyclerViewFragment> fragmentObservable = Observable.just(fragmentOnline);
        loader = new MessagesListLoader(this,fragmentObservable);

    }


}
