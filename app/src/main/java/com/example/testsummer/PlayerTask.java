package com.example.testsummer;

import android.os.AsyncTask;

import android.util.Log;


import com.example.testsummer.wifip2p.ClientClass;
import com.example.testsummer.wifip2p.ServerClass;
import com.example.testsummer.wifip2p.activity_p2p;

import net.protyposis.android.mediaplayer.FileSource;
import net.protyposis.android.mediaplayer.MediaPlayer;


import java.io.File;

import java.io.IOException;



import static com.example.testsummer.activity_main.appContext;
import static com.example.testsummer.activity_main.playPauseBtn;
import static com.example.testsummer.activity_main.toPlay;


public class PlayerTask extends AsyncTask<String, Void, Boolean> {
    MediaPlayer mediaPlayer;
    String title;
    String source = null;
    int currentPost = 0;

    static Integer fixForSetting = null;

    public PlayerTask(MediaPlayer mediaPlayer, String title) {
        this.mediaPlayer = mediaPlayer;
        this.title = title;
        fixForSetting = activity_main.currentSong;
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
                playPauseBtn.setImageResource(R.drawable.baseline_pause_black_36);
                try {
                    activity_play.playImageButton.setImageResource(R.drawable.baseline_pause_black_48);
                } catch (Exception ex) {};
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
                FileSource mediaSource = new FileSource(new File(activity_main.arrayTracks.get(activity_main.currentSong).file));
                mediaPlayer.setDataSource(mediaSource);

            }
            mediaPlayer.prepareAsync();
            if (activity_p2p.activeClient) {
                ClientClass clientClass = new ClientClass(activity_p2p.groupOwnerAddress);
                clientClass.execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("KAKA", "fromPlayerTask");
                activity_play activityPlay = new activity_play();
                Log.d("eee", "3");
                if (activity_p2p.activeServer) {
                    ServerClass serverClass = new ServerClass();
                    serverClass.execute();
                } else {
                    Log.d("eee", "33");
                    activityPlay.playNext();
                    try {
                        activity_play.songAuthor.setText(activity_main.arrayTracks.get(activityPlay.currentSong).artist);
                        activity_play.nameSong.setText(activity_main.arrayTracks.get(activityPlay.currentSong).title);
                    } catch (Exception ex) {}
                    playPauseBtn.setImageResource(R.drawable.baseline_pause_black_36);
                    toPlay.setText(activity_main.arrayTracks.get(activityPlay.currentSong).title);
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
        if (activity_p2p.activeServer) {
            ServerClass serverClass = new ServerClass();
            serverClass.execute();
        }
    }
}