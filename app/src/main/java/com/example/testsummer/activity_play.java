package com.example.testsummer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testsummer.Services.OnClearFromRecentService;

import net.protyposis.android.mediaplayer.FileSource;
import net.protyposis.android.mediaplayer.MediaPlayer;
import net.protyposis.android.mediaplayer.MediaSource;
import net.protyposis.android.mediaplayer.dash.DashSource;
import net.protyposis.android.mediaplayer.dash.SimpleRateBasedAdaptationLogic;

import java.io.File;
import java.io.IOException;


import static com.example.testsummer.activity_main.playPauseBtn;


public class activity_play extends AppCompatActivity {

    static ImageButton mainImageButton, settingImageButton, playImageButton, leftImageButton, rightImageButton;
    SeekBar seekBar;
    static TextView nameSong, songAuthor;
    int currentSong = 0;
    MediaPlayer mediaPlayer = activity_main.mediaPlayer;
    static Context appPlayContext;

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
                    activity_main.stream = activity_main.arrayTracks.get(currentSong).file;
                    PlayerTask playerTask = new PlayerTask(mediaPlayer, activity_main.arrayTracks.get(currentSong).title);
                    playerTask.execute();
                } catch (Exception ex) {
                }

                firstTime = false;
            } else {
                activity_main.currentSong--;
                currentSong = activity_main.currentSong;
                try {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    activity_main.stream = activity_main.arrayTracks.get(currentSong).file;
                    activity_main.toPlay.setText(activity_main.arrayTracks.get(currentSong).title);
                    PlayerTask playerTask = new PlayerTask(mediaPlayer, activity_main.arrayTracks.get(currentSong).title);
                    playerTask.execute();
                } catch (Exception ex) {

                }

                firstTime = true;
            }
        }
        CreateNotification.createNotification(activity_main.appContext, activity_main.arrayTracks.get(currentSong),
                R.drawable.baseline_pause_24, currentSong, activity_main.arrayTracks.size() - 1);
        try {
            setNameSongAndAuthor(activity_main.arrayTracks.get(currentSong));
        } catch (Exception ex) {}
        return firstTime;
    }

    public void playNext() {
        currentSong = activity_main.currentSong;
        Log.d("FFFFFF", "" + currentSong);
        if (currentSong < activity_main.arrayTracks.size() - 1) {
            activity_main.currentSong++;
            Log.d("FFFFFF", "" + currentSong);
            currentSong = activity_main.currentSong;
            Log.d("FFFFFF", "" + currentSong);
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                activity_main.toPlay.setText(activity_main.arrayTracks.get(currentSong).title);
                PlayerTask playerTask = new PlayerTask(mediaPlayer, activity_main.arrayTracks.get(currentSong).file);
                playerTask.execute();
//                activity_main.stream = activity_main.arrayTracks.get(currentSong).file;
//                FileSource mediaSource = new FileSource(new File(activity_main.stream));
//                mediaPlayer.setDataSource(mediaSource);
//                //mediaPlayer.setDataSource(activity_main.arrayTracks.get(currentSong).file);
//                mediaPlayer.prepareAsync();
//                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        mediaPlayer.start();
//                        playPauseBtn.setImageResource(R.drawable.baseline_pause_black_36);
//                    }
//                });
            } catch (Exception ex) {

            }

//FFmpegMediaPlayer.create(activity_main.appContext, Uri.parse(activity_main.arrayTracks.get(currentSong).file));
        }
        CreateNotification.createNotification(activity_main.appContext, activity_main.arrayTracks.get(currentSong),
                R.drawable.baseline_pause_24, currentSong, activity_main.arrayTracks.size() - 1);
        try {
            setNameSongAndAuthor(activity_main.arrayTracks.get(currentSong));
        } catch (Exception ex) {}
    }

    public boolean pausePlay() {
        boolean isPlaying = false;
        currentSong = activity_main.currentSong;
        try {
            if (mediaPlayer.isPlaying()) {
                isPlaying = true;
                mediaPlayer.pause();
                playPauseBtn.setImageResource(R.drawable.baseline_play_arrow_black_36);
                CreateNotification.createNotification(activity_main.appContext, activity_main.arrayTracks.get(currentSong),
                        R.drawable.baseline_play_arrow_24, currentSong, activity_main.arrayTracks.size() - 1);
            } else {
                try {
                    //mediaPlayer.getTrackInfo();
                    mediaPlayer.start();
                    playPauseBtn.setImageResource(R.drawable.baseline_pause_black_36);
                } catch (Exception ex) {
                    Log.d("eee", "5");
                    PlayerTask playerTask = new PlayerTask(mediaPlayer, activity_main.arrayTracks.get(currentSong).file);
                    playerTask.execute();
//                    mediaPlayer.reset();
//                    FileSource mediaSource = new FileSource(new File(activity_main.arrayTracks.get(currentSong).file));
//                    mediaPlayer.setDataSource(mediaSource);
//                    //mediaPlayer.setDataSource(activity_main.arrayTracks.get(currentSong).file);
//                    mediaPlayer.prepareAsync();
//                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                        @Override
//                        public void onPrepared(MediaPlayer mp) {
//                            mediaPlayer.start();
//                        }
//                    });
                }
                CreateNotification.createNotification(activity_main.appContext, activity_main.arrayTracks.get(currentSong),
                        R.drawable.baseline_pause_24, currentSong, activity_main.arrayTracks.size() - 1);
            }
            try {
                setNameSongAndAuthor(activity_main.arrayTracks.get(currentSong));
            } catch (Exception ex) {}
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
        appPlayContext = getApplicationContext();
        setContentView(R.layout.activity_play);
        currentSong = activity_main.currentSong;

        mainImageButton = findViewById(R.id.mainImageButton);
        settingImageButton = findViewById(R.id.settingImageButton);
        playImageButton = findViewById(R.id.playImageButton);
        leftImageButton = findViewById(R.id.leftImageButton);
        rightImageButton = findViewById(R.id.rightImageButton);
        seekBar = findViewById(R.id.seekBar2);
        nameSong = findViewById(R.id.name_song);
        songAuthor = findViewById(R.id.author_song);

        Thread seekBarThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition() * 100 / mediaPlayer.getDuration());
                    } catch (Exception ex) {

                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        seekBarThread.start();
        try {
            if (!mediaPlayer.isPlaying()) {
                playImageButton.setImageResource(R.drawable.baseline_play_arrow_black_48);
            }
        } catch (Exception ex) {
            playImageButton.setImageResource(R.drawable.baseline_pause_black_48);
        }
        try {
            setNameSongAndAuthor(activity_main.arrayTracks.get(currentSong));
        } catch (Exception ex) {}


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress() * mediaPlayer.getDuration() / 100);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress() * mediaPlayer.getDuration() / 100);
            }
        });

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
                playImageButton.setImageResource(R.drawable.baseline_pause_black_48);
            }
        });


        mainImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_main.mediaPlayer = mediaPlayer;
                finish();
//                Intent intent = new Intent(activity_play.this, activity_main.class);
//                startActivity(intent);
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
                    playImageButton.setImageResource(R.drawable.baseline_play_arrow_black_48);
                else
                    playImageButton.setImageResource(R.drawable.baseline_pause_black_48);
            }
        });
    }

    void setNameSongAndAuthor(Track track) {
        songAuthor.setText(track.artist);
        nameSong.setText(track.title);
    }

}
