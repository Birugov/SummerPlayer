package com.example.testsummer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.testsummer.Services.OnClearFromRecentService;

import java.util.ArrayList;

public class activity_play extends AppCompatActivity {

    ImageButton mainImageButton, settingImageButton, playImageButton, leftImageButton, rightImageButton;
    int currentSong = 0;
    private ArrayList<Integer> listSong = new ArrayList<>();
    MediaPlayer mediaPlayer = activity_main.mediaPlayer;

    NotificationManager notificationManager;

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID, "Summer Player", NotificationManager.IMPORTANCE_LOW);
            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

    }

    private void stopIfPlaying() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }
    private boolean firstTime = true;
    private void playPrevios() {
        stopIfPlaying();
        currentSong = activity_main.currentSong;
        if (currentSong > 0){
            if (firstTime) {
                mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(activity_play.this, Uri.parse(activity_main.arrayTracks.get(currentSong).file));
                mediaPlayer.start();
                firstTime = false;
            }
            else {
                activity_main.currentSong--;
                currentSong = activity_main.currentSong;
                mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(activity_play.this, Uri.parse(activity_main.arrayTracks.get(currentSong).file));
                mediaPlayer.start();
                firstTime = true;
            }
        }
        CreateNotification.createNotification(getApplicationContext(), activity_main.arrayTracks.get(currentSong),
                R.id.leftImageButton, currentSong, activity_main.arrayTracks.size() - 1);

    }
    private void playNext() {
        stopIfPlaying();
        currentSong = activity_main.currentSong;
        if (currentSong < activity_main.arrayTracks.size() - 1){
            activity_main.currentSong++;
            currentSong = activity_main.currentSong;
            mediaPlayer.stop();
            mediaPlayer = MediaPlayer.create(activity_play.this, Uri.parse(activity_main.arrayTracks.get(currentSong).file));
            mediaPlayer.start();
        }
        CreateNotification.createNotification(getApplicationContext(), activity_main.arrayTracks.get(currentSong),
                R.id.rightImageButton, currentSong, activity_main.arrayTracks.size() - 1);
    }
    private void pausePlay() {
        currentSong = activity_main.currentSong;
        Log.d("CLICK", "cc");
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            Log.d("CLICK", "cc2");
        }
        else{
            mediaPlayer.start();
            Log.d("CLICK", "cc3");
        }
        CreateNotification.createNotification(getApplicationContext(), activity_main.arrayTracks.get(currentSong),
                R.id.playImageButton, currentSong, activity_main.arrayTracks.size() - 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mediaPlayer = activity_main.mediaPlayer;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }

//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer = MediaPlayer.create(activity_play.this, Uri.parse(activity_main.getStream()));
//        mediaPlayer.start();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        currentSong = activity_main.currentSong;

        mainImageButton = findViewById(R.id.mainImageButton);
        settingImageButton = findViewById(R.id.settingImageButton);
        playImageButton = findViewById(R.id.playImageButton);
        leftImageButton = findViewById(R.id.leftImageButton);
        rightImageButton = findViewById(R.id.rightImageButton);

        leftImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrevios();
            }
        });

        rightImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
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
                pausePlay();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action) {
                case CreateNotification.ACTION_PREVIOUS:
                    playPrevios();
                    break;
                case CreateNotification.ACTION_NEXT:
                    playNext();
                    break;
                case CreateNotification.ACTION_PLAY:
                    pausePlay();
                    break;
            }
        }
    };



}
