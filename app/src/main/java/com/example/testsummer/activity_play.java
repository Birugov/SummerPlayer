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
import android.widget.TextView;

import com.example.testsummer.Services.OnClearFromRecentService;

import java.util.ArrayList;

public class activity_play extends AppCompatActivity {

    ImageButton mainImageButton, settingImageButton, playImageButton, leftImageButton, rightImageButton;
    TextView nameSong, authorSong;
    int currentSong = 0;
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

    private boolean firstTime = true;
    private void playPrevios() {
        currentSong = activity_main.currentSong;
        if (currentSong > 0){
            if (firstTime) {
                mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(activity_play.this, Uri.parse(activity_main.arrayTracks.get(currentSong).file));
                mediaPlayer.start();
                firstTime = false;
                changeInfo(currentSong);
            }
            else {
                activity_main.currentSong--;
                currentSong = activity_main.currentSong;
                mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(activity_play.this, Uri.parse(activity_main.arrayTracks.get(currentSong).file));
                mediaPlayer.start();
                firstTime = true;
                changeInfo(currentSong);
            }
            playImageButton.setImageResource(R.drawable.baseline_pause_24);
        }
        CreateNotification.createNotification(getApplicationContext(), activity_main.arrayTracks.get(currentSong),
                R.drawable.baseline_pause_24, currentSong, activity_main.arrayTracks.size() - 1);

    }
    private void playNext() {
        currentSong = activity_main.currentSong;
        if (currentSong < activity_main.arrayTracks.size() - 1){
            activity_main.currentSong++;
            currentSong = activity_main.currentSong;
            mediaPlayer.stop();
            mediaPlayer = MediaPlayer.create(activity_play.this, Uri.parse(activity_main.arrayTracks.get(currentSong).file));
            mediaPlayer.start();
            playImageButton.setImageResource(R.drawable.baseline_pause_24);
            changeInfo(currentSong);
        }
        CreateNotification.createNotification(getApplicationContext(), activity_main.arrayTracks.get(currentSong),
                R.drawable.baseline_pause_24, currentSong, activity_main.arrayTracks.size() - 1);
    }
    private void pausePlay() {
        currentSong = activity_main.currentSong;
        Log.d("CLICK", "cc");
        if (mediaPlayer.isPlaying()){
            playImageButton.setImageResource(R.drawable.baseline_play_arrow_24);
            mediaPlayer.pause();
            CreateNotification.createNotification(getApplicationContext(), activity_main.arrayTracks.get(currentSong),
                    R.drawable.baseline_play_arrow_24, currentSong, activity_main.arrayTracks.size() - 1);
            Log.d("CLICK", "cc2");
        }
        else{
            try{
                mediaPlayer.getTrackInfo();
                mediaPlayer.start();
            } catch (Exception ex) {
                mediaPlayer = MediaPlayer.create(activity_play.this, Uri.parse(activity_main.arrayTracks.get(currentSong).file));
                mediaPlayer.start();
            }


//            if (mediaPlayer. != null)
//                mediaPlayer.start();
//            else {
//                mediaPlayer = MediaPlayer.create(activity_play.this, Uri.parse(activity_main.arrayTracks.get(currentSong).file));
//                mediaPlayer.start();
//            }
            playImageButton.setImageResource(R.drawable.baseline_pause_24);
            CreateNotification.createNotification(getApplicationContext(), activity_main.arrayTracks.get(currentSong),
                    R.drawable.baseline_pause_24, currentSong, activity_main.arrayTracks.size() - 1);
            Log.d("CLICK", "cc3");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        currentSong = activity_main.currentSong;

        mainImageButton = findViewById(R.id.mainImageButton);
        settingImageButton = findViewById(R.id.settingImageButton);
        playImageButton = findViewById(R.id.playImageButton);
        leftImageButton = findViewById(R.id.leftImageButton);
        rightImageButton = findViewById(R.id.rightImageButton);

        nameSong = findViewById(R.id.name_song);
        authorSong = findViewById(R.id.author_song);

        nameSong.setText(activity_main.nameS);
        authorSong.setText(activity_main.authorS);





        if (!mediaPlayer.isPlaying())
            playImageButton.setImageResource(R.drawable.baseline_play_arrow_24);

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
                activity_main.mediaPlayer = mediaPlayer;
                finish();
            }
        });

        settingImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(activity_play.this, acticity_setting.class);
                startActivity(intent);
                finish();
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

    public void changeInfo(int index){
        nameSong.setText(activity_main.arrayTracks.get(index).getTitle());
        authorSong.setText(activity_main.arrayTracks.get(index).getArtist());
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