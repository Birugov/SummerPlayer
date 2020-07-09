package com.example.testsummer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import java.util.ArrayList;

public class activity_play extends AppCompatActivity {

    ImageButton mainImageButton, settingImageButton, playImageButton, leftImageButton, rightImageButton;
    int currentSong = 0;
    private ArrayList<Integer> listSong = new ArrayList<>();
    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer = MediaPlayer.create(activity_play.this, R.raw.three);
        mediaPlayer.start();

        //тест песенками
        listSong.add(R.raw.one);
        listSong.add(R.raw.two);
        listSong.add(R.raw.three);
        listSong.add(R.raw.four);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        currentSong = 2;

        mainImageButton = findViewById(R.id.mainImageButton);
        settingImageButton = findViewById(R.id.settingImageButton);
        playImageButton = findViewById(R.id.playImageButton);
        leftImageButton = findViewById(R.id.leftImageButton);
        rightImageButton = findViewById(R.id.rightImageButton);

        leftImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSong > 0){
                    mediaPlayer.stop();
                    mediaPlayer= MediaPlayer.create(activity_play.this, listSong.get(currentSong - 1));
                    mediaPlayer.start();
                    currentSong--;
                }
            }
        });

        rightImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSong < listSong.size() - 1){
                    currentSong++;
                    mediaPlayer.stop();
                    mediaPlayer = MediaPlayer.create(activity_play.this, listSong.get(currentSong));
                    mediaPlayer.start();
                }
            }
        });



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
