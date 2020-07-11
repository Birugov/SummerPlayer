package com.example.testsummer;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;

import wseemann.media.FFmpegMediaPlayer;

public class PlayerTask extends AsyncTask<String, Void, Boolean> {
    FFmpegMediaPlayer mediaPlayer;
    String title;

    public PlayerTask(FFmpegMediaPlayer mediaPlayer, String title) {
        this.mediaPlayer = mediaPlayer;
        this.title = title;
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        mediaPlayer.setOnPreparedListener(new FFmpegMediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(FFmpegMediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.setOnErrorListener(new FFmpegMediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(FFmpegMediaPlayer mp, int what, int extra) {
                mp.release();
                return false;
            }
        });

        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(activity_main.getStream());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        Toast.makeText(activity_main.appContext, "Playing..  " + title, Toast.LENGTH_SHORT).show();
    }
}