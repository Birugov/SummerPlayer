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
import android.widget.Toast;

import com.example.testsummer.Services.OnClearFromRecentService;

import wseemann.media.FFmpegMediaPlayer;


public class activity_play extends AppCompatActivity {

    ImageButton mainImageButton, settingImageButton, playImageButton, leftImageButton, rightImageButton;
    int currentSong = 0;
    FFmpegMediaPlayer mediaPlayer = activity_main.mediaPlayer;

    static NotificationManager notificationManager;


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

    public boolean playPrevios() {
        currentSong = activity_main.currentSong;
        if (currentSong > 0) {
            if (firstTime) {
                try {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(activity_main.arrayTracks.get(currentSong).file);
                    mediaPlayer.prepareAsync();
                } catch (Exception ex) {
                }

                mediaPlayer.setOnPreparedListener(new FFmpegMediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(FFmpegMediaPlayer mp) {
                        mediaPlayer.start();
                    }
                });
                firstTime = false;
            } else {
                activity_main.currentSong--;
                currentSong = activity_main.currentSong;
                try {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(activity_main.arrayTracks.get(currentSong).file);
                    mediaPlayer.prepareAsync();
                } catch (Exception ex) {

                }
                mediaPlayer.setOnPreparedListener(new FFmpegMediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(FFmpegMediaPlayer mp) {
                        mediaPlayer.start();
                    }
                });
                firstTime = true;
            }
        }
        CreateNotification.createNotification(activity_main.appContext, activity_main.arrayTracks.get(currentSong),
                R.drawable.baseline_pause_24, currentSong, activity_main.arrayTracks.size() - 1);
        return firstTime;
    }

    public void playNext() {
        currentSong = activity_main.currentSong;
        if (currentSong < activity_main.arrayTracks.size() - 1) {
            activity_main.currentSong++;
            currentSong = activity_main.currentSong;
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(activity_main.arrayTracks.get(currentSong).file);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new FFmpegMediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(FFmpegMediaPlayer mp) {
                        mediaPlayer.start();
                    }
                });
            } catch (Exception ex) {

            }

//FFmpegMediaPlayer.create(activity_main.appContext, Uri.parse(activity_main.arrayTracks.get(currentSong).file));
        }
        CreateNotification.createNotification(activity_main.appContext, activity_main.arrayTracks.get(currentSong),
                R.drawable.baseline_pause_24, currentSong, activity_main.arrayTracks.size() - 1);
    }

    public boolean pausePlay() {
        boolean isPlaying = false;
        currentSong = activity_main.currentSong;
        try {
            if (mediaPlayer.isPlaying()) {
                isPlaying = true;
                mediaPlayer.pause();
                CreateNotification.createNotification(activity_main.appContext, activity_main.arrayTracks.get(currentSong),
                        R.drawable.baseline_play_arrow_24, currentSong, activity_main.arrayTracks.size() - 1);
            } else {
                try {
                    //mediaPlayer.getTrackInfo();
                    mediaPlayer.start();
                } catch (Exception ex) {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(activity_main.arrayTracks.get(currentSong).file);
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new FFmpegMediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(FFmpegMediaPlayer mp) {
                            mediaPlayer.start();
                        }
                    });
                }
                CreateNotification.createNotification(activity_main.appContext, activity_main.arrayTracks.get(currentSong),
                        R.drawable.baseline_pause_24, currentSong, activity_main.arrayTracks.size() - 1);
            }
        } catch (Exception ex) {
            Toast.makeText(activity_main.appContext, "Error playing", Toast.LENGTH_LONG).show();
        }

        return isPlaying;
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

        try {
            if (!mediaPlayer.isPlaying())
                playImageButton.setImageResource(R.drawable.baseline_play_arrow_24);
        } catch (Exception ex) {
            playImageButton.setImageResource(R.drawable.baseline_pause_24);
        }

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
                playImageButton.setImageResource(R.drawable.baseline_pause_24);
            }
        });


        mainImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_main.mediaPlayer = mediaPlayer;
                Intent intent = new Intent(activity_play.this, activity_main.class);
                startActivity(intent);
            }
        });

        settingImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_play.this, acticity_setting.class);
                startActivity(intent);
            }
        });

        playImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPlaying = pausePlay();
                if (isPlaying)
                    playImageButton.setImageResource(R.drawable.baseline_play_arrow_24);
                else
                    playImageButton.setImageResource(R.drawable.baseline_pause_24);
            }
        });


    }



}
