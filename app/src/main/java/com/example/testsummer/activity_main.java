package com.example.testsummer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class activity_main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
