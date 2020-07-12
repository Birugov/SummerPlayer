package com.example.testsummer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.testsummer.Services.OnClearFromRecentService;
import com.example.testsummer.wifip2p.activity_p2p;

import net.protyposis.android.mediaplayer.FileSource;
import net.protyposis.android.mediaplayer.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import wseemann.media.FFmpegMediaPlayer;


public class activity_main extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 12345;

    private Button toPlay;
    private Button btnSetting;
    public static MediaPlayer mediaPlayer = null;
    private ListView listOfSongs;
    public static String stream = null;
    private String title;
    protected static Integer currentSong = 0;
    protected static PlayerTask playerTask = null;

    public static Context appContext;

    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    static ArrayList<Track> arrayTracks;

    public static String getStream() {
        return stream;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_MEDIA_LOCATION,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
        }, 1);
        appContext = getApplicationContext();

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        toPlay = findViewById(R.id.toPlay);
        btnSetting = findViewById(R.id.settings);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_main.this, activity_p2p.class);
                startActivity(intent);
            }
        });
        toPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_main.this, activity_play.class);
                startActivity(intent);
            }
        });


        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity_play activityPlay = new activity_play();
                String action = intent.getExtras().getString("actionname");

                switch (action) {
                    case CreateNotification.ACTION_PREVIOUS:
                        activityPlay.playPrevios();
                        break;
                    case CreateNotification.ACTION_NEXT:
                        activityPlay.playNext();
                        break;
                    case CreateNotification.ACTION_PLAY:
                        activityPlay.pausePlay();
                        break;
                }
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(activity_main.appContext, OnClearFromRecentService.class));
        }

        if (ContextCompat.checkSelfPermission(activity_main.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity_main.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(activity_main.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            } else {
                ActivityCompat.requestPermissions(activity_main.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            }
        } else {
            doStuff();
        }
    }


    public void getMusic() {
        ContentResolver contentResolver = getContentResolver();
        Uri uriSong = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor songCursor = contentResolver.query(uriSong, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {

            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                String currentTitle = songCursor.getString(songTitle) == null ? "unknown": songCursor.getString(songTitle);
                title = currentTitle;
                //Log.d("INFOART", "" + currentTitle + "");
                String currentArtist = songCursor.getString(songArtist) == null ? "unknown": songCursor.getString(songArtist);
                String currentLocation = songCursor.getString(songLocation);

                byte[] image = null;
                boolean isReadable = false;
                try {
                    MediaMetadataRetriever mediaMetadataRetriever = (MediaMetadataRetriever) new MediaMetadataRetriever();
                    File file = new File(currentLocation);
                    Log.d("CANREAD", String.valueOf(file.canRead()));
                    file.setReadable(true);
                    Log.d("CANREAD", String.valueOf(file.canRead()));
                    if (file.canRead()) {
                        isReadable = true;
                        Uri uri = (Uri) Uri.fromFile(new File(currentLocation));
                        mediaMetadataRetriever.setDataSource(activity_main.this, uri);
                        image = mediaMetadataRetriever.getEmbeddedPicture();
                    }

                } catch (Exception ex) {}
                if (isReadable) {
                    Track track = new Track(currentTitle, currentArtist, currentLocation, image);
                    arrayTracks.add(track);
                    arrayList.add("Title: " + track.title + "\n"
                            + "Artist: " + track.artist);
                }
            } while (songCursor.moveToNext());
            stream = arrayTracks.get(0).file;
            currentSong = 0;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(activity_main.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                        doStuff();
                    }
                } else {
                    Toast.makeText(this, "No permission granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }

    }

    public void doStuff() {
        listOfSongs = findViewById(R.id.listOfSongs);
        arrayList = new ArrayList<>();
        arrayTracks = new ArrayList<>();

        getMusic();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listOfSongs.setAdapter(adapter);

        listOfSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stream = arrayTracks.get((int) id).file;
                title = arrayList.get(position).substring(arrayList.get(position).indexOf("Title: ")+6, arrayList.get(position).indexOf("Artist: "));
                currentSong = (int) id;
                try {
                    if (mediaPlayer.isPlaying() == true) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                } catch (Exception ex) {
                    mediaPlayer = new MediaPlayer();
                }
                playerTask = new PlayerTask(mediaPlayer, title);
                playerTask.execute(stream);
                CreateNotification.createNotification(activity_main.appContext, arrayTracks.get((int) id), R.drawable.baseline_pause_24, position, arrayTracks.size() - 1);
            }
        });

    }


}