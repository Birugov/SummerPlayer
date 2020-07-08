package com.example.testsummer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.service.autofill.OnClickAction;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.testsummer.adapters.SongAdapter;

import java.util.ArrayList;

public class activity_main extends AppCompatActivity {
    private Button toPlay;
    private ViewPager viewPager;
    private ListView songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toPlay = findViewById(R.id.toPlay);
        toPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_play);
            }
        });
        songs = findViewById(R.id.list_playlist);
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
