package com.example.testsummer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import java.util.ArrayList;

public class activity_play extends AppCompatActivity {

    ImageButton mainImageButton, settingImageButton, playImageButton, leftImageButton, rightImageButton;
    int currentSong = 0;
    private ArrayList<Integer> listSong = new ArrayList<>();
    MediaPlayer mediaPlayer = activity_main.mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("CLICK", "SSSS");
        mediaPlayer.start();
//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer = MediaPlayer.create(activity_play.this, Uri.parse(activity_main.getStream()));
//        mediaPlayer.start();

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
                    mediaPlayer= MediaPlayer.create(activity_play.this, Uri.parse(activity_main.getStream()));
                    mediaPlayer.start();
                    currentSong--;
                }
            }
        });

        rightImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSong < activity_main.arrayTracks.size() - 1){
                    currentSong++;
                    mediaPlayer.stop();
                    mediaPlayer = MediaPlayer.create(activity_play.this, Uri.parse(String.valueOf(activity_main.arrayTracks.get(currentSong))));
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
                Log.d("CLICK", "cc");
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    Log.d("CLICK", "cc2");
                }
                else{
                    mediaPlayer.start();
                    Log.d("CLICK", "cc3");
                }
            }
        });

    }


}
