package com.example.andrii.flashchat.Activitys;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.Person;
import com.example.andrii.flashchat.data.SingletonConnection;
import com.example.andrii.flashchat.data.actions.ActionSaveProfileChanges;
import com.example.andrii.flashchat.tools.ImageTools;
import com.example.andrii.flashchat.tools.QueryAction;
import com.example.andrii.flashchat.tools.QueryPreferences;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.Observer;

public class ProfileActivity extends AppCompatActivity{
    private static final String TAG = "ProfileActivity";
    private static final int MY_ACTIVITY_RESULT_REQUEST_CODE = 1;
    private static final String PERSON_EXTRA = "PERSON_EXTRA";
    private Person person;
    private CircleImageView civPhoto;
    private EditText etName;
    private EditText etEmail;
    private EditText etDate;
    private EditText etNumber;
    private Spinner genderSpinner;
    private MenuItem itemSave;

    private boolean dataChanged = false;
    private boolean userWasInteracting = false;
    private ImageButton ibtnPhoto;
    private FrameLayout btnLayout;

    public static Intent newIntent(Context context, Person person) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(PERSON_EXTRA, person);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_profile);

        person =  getIntent().getParcelableExtra(PERSON_EXTRA);
        initializeView();

        if (!person.getId().equals(QueryPreferences.getActiveUserId(this))) setNotClickable();

        setPhoto();

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setTitle(person.getName());
            getSupportActionBar().setTitle(person.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.main_collapsing);
        collapsingToolbarLayout.setTitle(person.getName());
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this,R.color.colorPrimary));

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.profile_menu, menu);
            itemSave = menu.getItem(0);
            itemSave.setEnabled(false);
            if (person.getPhotoUrl().equals("offline")) setNotClickable();
            if (!person.getId().equals(QueryPreferences.getActiveUserId(this))) itemSave.setVisible(false);

            return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case MY_ACTIVITY_RESULT_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                   setPhoto();
                    String root = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
                    File file = new File(root,person.getId() + ".jpg");

                    ImageTools tools = new ImageTools(this);
                    tools.sendImage(file,person.getId(),person.getId(),person.getId());
                }
                break;

                default:
                    super.onActivityResult(requestCode, resultCode, data);
        }


    }

    @Override
    public void onBackPressed() {
        final boolean[] result = new boolean[1];
        if (dataChanged){
            result[0] = false;
            AlertDialog.Builder back_dialog = new AlertDialog.Builder(this);
            back_dialog.setMessage("You have not saving data changed.Delete changed?")
                    .setCancelable(true)
                    .setPositiveButton("Delete", (dialog, which) ->super.onBackPressed())
                    .setNegativeButton("Back", (dialog, which) -> result[0] = false)
                    .show();
            if (result[0]) {
                super.onBackPressed();
            }

        } else super.onBackPressed();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_save:
                if(checkValidData()){
                    ActionSaveProfileChanges action = new ActionSaveProfileChanges(person.getId(),
                            etName.getText().toString(),etDate.getText().toString(),etEmail.getText().toString(),
                            etNumber.getText().toString(),genderSpinner.getSelectedItem().toString());

                    Observable<String> observable = QueryAction.executeAnswerQuery(this,action,TAG);
                    observable.subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            SingletonConnection.getInstance().close();
                            dataChanged = false;
                            itemSave.setEnabled(false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG,"onError",e);
                            SingletonConnection.getInstance().close();

                        }

                        @Override
                        public void onNext(String s) {
                            if (!s.equals("error")) Toast.makeText(getApplicationContext(),"Data was changed.",Toast.LENGTH_LONG).show();
                            else  Toast.makeText(getApplicationContext(),"Server Error.",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkValidData() {
        boolean valid = true;
        View view = null;
        if (etName.getText().toString().length() < 3){
            valid = false;
            etName.setError("Write correct name");
            view = etName;
        }
        if (!etEmail.getText().toString().contains("@")){
            valid = false;
            etEmail.setError("This email is invalided");
            view = etEmail;
        }
        if (etDate.getText().toString().length()!=10){
            valid = false;
            etEmail.setError("This email is invalided");
            view = etEmail;
        }
        if (etNumber.getText().toString().length()<8){
            valid = false;
            etNumber.setError("This number is invalided");
            view = etNumber;
        }
        if (genderSpinner.getSelectedItem() == null){
            valid = false;
            etName.setError("Write correct gender");
            view = genderSpinner;
        }

        if (view != null){
            view.requestFocus();
        }

        return valid;
    }

    @Override
    public void onUserInteraction() {
        userWasInteracting = true;
        super.onUserInteraction();
    }

    private void setPhoto() {
        String root = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
        File file = new File(root,person.getId() + ".jpg");
        Bitmap image;
        if (file.exists()){
            Picasso.with(this)
                    .load(file)
                    .error(R.drawable.ic_action_person)
                    .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                    .into(civPhoto);
        }else{
            //server try
            image = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_action_person);
            civPhoto.setImageBitmap(image);
        }
    }

    private void setNotClickable() {
        etName.setFocusable(false);
        etDate.setFocusable(false);
        etEmail .setFocusable(false);
        etNumber.setFocusable(false);
        genderSpinner.setEnabled(false);
        btnLayout.setVisibility(View.GONE);

    }

    private void initializeView() {
        civPhoto = findViewById(R.id.circle_image_view_photo);
        etName = findViewById(R.id.tv_name);
        etDate = findViewById(R.id.tv_date);
        etEmail = findViewById(R.id.tv_email);
        etNumber = findViewById(R.id.tv_number);
        genderSpinner = findViewById(R.id.spiner_gender);

        etName.setText(person.getName());
        etDate.setText(person.getBirthDate());
        etEmail.setText(person.getEmail());
        etNumber.setText(person.getPhoneNumber());

        if (person.getGender().equals("female")) genderSpinner.setSelection(2);


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (userWasInteracting){
                dataChanged = true;
                itemSave.setEnabled(true);}
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        etName.addTextChangedListener(textWatcher);
        etDate.addTextChangedListener(textWatcher);
        etEmail.addTextChangedListener(textWatcher);
        etNumber.addTextChangedListener(textWatcher);

        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(this, R.array.genderlist, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genderSpinner.setAdapter(adapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                if (userWasInteracting){
                dataChanged = true;
                itemSave.setEnabled(true);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ibtnPhoto = findViewById(R.id.ib_take_photo);
        btnLayout= findViewById(R.id.framelayout_btn);

        ibtnPhoto.setOnClickListener(v ->{
            Intent intent = PhotoActivity.newIntent(this, person.getId());
            startActivityForResult(intent, MY_ACTIVITY_RESULT_REQUEST_CODE);
        });
    }


}
