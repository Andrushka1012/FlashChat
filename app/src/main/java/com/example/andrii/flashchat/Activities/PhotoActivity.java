package com.example.andrii.flashchat.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.andrii.flashchat.R;
import com.github.florent37.camerafragment.CameraFragment;
import com.github.florent37.camerafragment.configuration.Configuration;
import com.github.florent37.camerafragment.listeners.CameraFragmentResultListener;
import com.github.florent37.camerafragment.listeners.CameraFragmentStateListener;
import com.github.florent37.camerafragment.widgets.CameraSettingsView;
import com.github.florent37.camerafragment.widgets.CameraSwitchView;
import com.github.florent37.camerafragment.widgets.FlashSwitchView;
import com.github.florent37.camerafragment.widgets.RecordButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoActivity extends AppCompatActivity implements CameraFragmentResultListener ,
        CameraFragmentStateListener{
    public static final String EXTRA_MSG_ID = "EXTRA_MSG_ID";
    private String mPhotoName;
    RecordButton record;
    CameraSettingsView cameraSettings;
    FlashSwitchView flashSwitch;
    CameraSwitchView cameraSwitch;

    public static Intent newIntent(Context context, String photoName) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra(EXTRA_MSG_ID, photoName);

        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_photo);
        mPhotoName = getIntent().getStringExtra(EXTRA_MSG_ID);
        File externalFileDir = null;
        CameraFragment fragment = setCameraFragment();

        externalFileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFileDir == null) {
            externalFileDir = new File(String.valueOf(getFilesDir()));
        }

        CameraFragment finalFragment = fragment;
        File finalExternalFileDir = externalFileDir;

        record = (RecordButton) findViewById(R.id.record_button);
        cameraSettings = (CameraSettingsView) findViewById(R.id.settings_view);
        flashSwitch = (FlashSwitchView) findViewById(R.id.flash_switch_view);
        cameraSwitch = (CameraSwitchView) findViewById(R.id.front_back_camera_switcher);

        record.setOnClickListener(v ->{
            File f = new File(finalExternalFileDir, mPhotoName + ".jpg");
            if(f.exists()){
                if (f.delete()){
                    Toast.makeText(this,"Photo changed",Toast.LENGTH_LONG).show();
                }
            }
            finalFragment.takePhotoOrCaptureVideo(this, finalExternalFileDir.toString(), mPhotoName);
        });
        cameraSettings.setOnClickListener(v -> finalFragment.openSettingDialog());
        flashSwitch.setOnClickListener(v -> finalFragment.toggleFlashMode());
        cameraSwitch.setOnClickListener(v -> finalFragment.switchCameraTypeFrontBack());

    }


    @Override
    public void onVideoRecorded(String filePath) {
    }

    @Override
    public void onPhotoTaken(byte[] bytes, String filePath) {
        Intent intent = new Intent();
        intent.putExtra("Path",filePath);
        setResult(RESULT_OK,intent);
        finish();
    }

    private CameraFragment setCameraFragment(){
        FragmentManager fm = getSupportFragmentManager();

        CameraFragment fragment = (CameraFragment) fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT > 15) {
                    final String[] permissions = {
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE};

                    final List<String> permissionsToRequest = new ArrayList<>();
                    for (String permission : permissions) {
                        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                            permissionsToRequest.add(permission);
                        }
                    }
                    if (!permissionsToRequest.isEmpty()) {
                        ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), 100);
                    }

                }
            }
            Configuration.Builder builder = new Configuration.Builder();
            builder.setCamera(Configuration.CAMERA_FACE_FRONT)
                    .setFlashMode(Configuration.FLASH_MODE_AUTO)
                    .setMediaAction(Configuration.MEDIA_ACTION_PHOTO);
            fragment = CameraFragment.newInstance(builder.build());
            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }
        fragment.setStateListener(this);

        return fragment;
    }

    @Override
    public void onCurrentCameraBack() {
        cameraSwitch.displayBackCamera();
    }

    @Override
    public void onCurrentCameraFront() {
        cameraSwitch.displayFrontCamera();
        flashSwitch.setVisibility(View.GONE);
    }

    @Override
    public void onFlashAuto() {
        flashSwitch.displayFlashAuto();
    }

    @Override
    public void onFlashOn() {
        flashSwitch.displayFlashOn();
    }

    @Override
    public void onFlashOff() {
        flashSwitch.displayFlashOff();
    }

    @Override
    public void onCameraSetupForPhoto() {

    }

    @Override
    public void onCameraSetupForVideo() {

    }

    @Override
    public void onRecordStateVideoReadyForRecord() {

    }

    @Override
    public void onRecordStateVideoInProgress() {

    }

    @Override
    public void onRecordStatePhoto() {

    }

    @Override
    public void shouldRotateControls(int degrees) {

    }

    @Override
    public void onStartVideoRecord(File outputFile) {

    }

    @Override
    public void onStopVideoRecord() {

    }
}
