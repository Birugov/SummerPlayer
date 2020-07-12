package com.example.testsummer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    SharedPreferences appSettingPrefs;
    Setting_Loader settingLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appSettingPrefs = getSharedPreferences("AppSettingPrefs", MODE_PRIVATE);
        settingLoader = new Setting_Loader(appSettingPrefs);
        settingLoader.load();

    }

    private static final String[] PERMISSION = {
        Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private static final int REQUEST_PERMISSIONS = 12345;

    private static final int PERMISSION_COUNT = 1;

    @SuppressLint("NewAPI")
    private boolean arePermissionsDenied(){
        for (int i = 0; i < PERMISSION_COUNT; i++){
            if (checkSelfPermission(PERMISSION[i]) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    public void switchOnSetting(View view){
        Intent intent = new Intent(this, acticity_setting.class);
        startActivity(intent);
    }


}
