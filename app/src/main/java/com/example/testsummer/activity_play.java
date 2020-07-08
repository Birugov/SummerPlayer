package com.example.testsummer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class activity_play extends AppCompatActivity {

    private ImageButton mainImageButton;
    private ImageButton settingImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mainImageButton = findViewById(R.id.mainImageButton);
        settingImageButton = findViewById(R.id.settingImageButton);
        mainImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_main);
            }
        });

        settingImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_acticity_setting);
            }
        });


    }




}
