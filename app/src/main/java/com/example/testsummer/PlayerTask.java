package com.example.testsummer;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.testsummer.wifip2p.ClientClass;
import com.example.testsummer.wifip2p.ServerClass;
import com.example.testsummer.wifip2p.activity_p2p;

import net.protyposis.android.mediaplayer.FileSource;
import net.protyposis.android.mediaplayer.MediaPlayer;
import net.protyposis.android.mediaplayer.MediaSource;
import net.protyposis.android.mediaplayer.dash.DashSource;
import net.protyposis.android.mediaplayer.dash.SimpleRateBasedAdaptationLogic;

import java.io.File;
import java.io.IOException;

import wseemann.media.FFmpegMediaPlayer;

public class PlayerTask extends AsyncTask<String, Void, Boolean> {
    MediaPlayer mediaPlayer;
    String title;
    String source = null;
    int currentPost = 0;

    public PlayerTask(MediaPlayer mediaPlayer, String title) {
        this.mediaPlayer = mediaPlayer;
        this.title = title;
    }

    public PlayerTask(MediaPlayer mediaPlayer, String title, String source, int currentPost) {
        this.mediaPlayer = mediaPlayer;
        this.title = title;
        this.source = source;
        this.currentPost = currentPost;
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.seekTo(currentPost);
                mp.start();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mp.release();
                return false;
            }
        });

        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            if (source != null) {
                FileSource mediaSource = new FileSource(new File(source));
                mediaPlayer.setDataSource(mediaSource);
            } else {
                FileSource mediaSource = new FileSource(new File(activity_main.getStream()));
                mediaPlayer.setDataSource(mediaSource);
            }
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("KAKA", "fromPlayerTask");
                activity_play activityPlay = new activity_play();
                if (activity_p2p.activeServer) {
                    ServerClass serverClass = new ServerClass();
                    serverClass.execute();
                } else {
                    activityPlay.playNext();
                }
                if (activity_p2p.activeClient) {
                    ClientClass clientClass = new ClientClass(activity_p2p.groupOwnerAddress);
                    clientClass.execute();
                }
            }
        });
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        Toast.makeText(activity_main.appContext, "Playing..  " + title, Toast.LENGTH_SHORT).show();
    }
}