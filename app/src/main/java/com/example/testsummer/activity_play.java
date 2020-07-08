package com.example.testsummer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

public class activity_play extends AppCompatActivity {

    ImageButton mainImageButton, settingImageButton, playImageButton;
    private ArrayList listSong = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        listSong.add("one");
        listSong.add("two");
        listSong.add("three");
        listSong.add("four");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mainImageButton = findViewById(R.id.mainImageButton);
        settingImageButton = findViewById(R.id.settingImageButton);
        playImageButton = findViewById(R.id.playImageButton);

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

        final MediaPlayer mediaPlayer = MediaPlayer.create(activity_play.this, R.raw.one);

        playImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }
                else{
                    mediaPlayer.start();
                }
            }
        });




    }




}
