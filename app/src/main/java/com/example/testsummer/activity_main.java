package com.example.testsummer;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;


public class activity_main extends AppCompatActivity {

    private static final String[] PERMISSION = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int REQUEST_PERMISSIONS = 12345;
    private static final int PERMISSION_COUNT = 1;

    private LinearLayout ll;
    private ImageButton b_next, b_play, b_prev, b_open;

//    boolean prepared = false;

    static MediaPlayer mediaPlayer;
    private ListView listOfSongs;
    private static String stream = null;
    private String title;
    protected static Integer currentSong;
    protected static PLayerTask playerTask = null;



    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    static ArrayList<Track> arrayTracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_main);

        ll = findViewById(R.id.QuickMenu);
        b_next = findViewById(R.id.QuickNext);
        b_play = findViewById(R.id.QuickPlay);
        b_prev = findViewById(R.id.QuickPrev);
        b_open = findViewById(R.id.QuickOpen);
        b_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_main.this, activity_play.class);
                startActivity(intent);
            }
        });

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

    public static String getStream() {
        return stream;
    }


    public void getMusic() throws IOException {
        ContentResolver contentResolver = getContentResolver();
        Uri uriSong = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor songCursor = contentResolver.query(uriSong, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {

            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                String currentTitle = songCursor.getString(songTitle);
                title = currentTitle;
                String currentArtist = songCursor.getString(songArtist);
                String currentLocation = songCursor.getString(songLocation);
                Track track = new Track(currentTitle, currentArtist, currentLocation, R.id.playImageButton);
                arrayTracks.add(track);
                arrayList.add("Title: " + track.title + "\n"
                        + "Artist: " + track.artist);
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
        try {
            getMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listOfSongs.setAdapter(adapter);

        listOfSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stream = arrayTracks.get((int) id).file;
                title = arrayList.get(position).substring(arrayList.get(position).indexOf("Title: ")+6, arrayList.get(position).indexOf("Artist: "));
                currentSong = (int) id;
                while (mediaPlayer.isPlaying() == true) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (playerTask != null) {
                    playerTask.stopMusic();
                }
                playerTask = new PLayerTask();
                playerTask.execute(stream);
                CreateNotification.createNotification(activity_main.this, arrayTracks.get((int) id), 0, position, arrayTracks.size() - 1);
            }
        });



    }


    private class PLayerTask extends AsyncTask<String, Void, Boolean> {
        protected boolean prepared = false;
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                mediaPlayer.setDataSource(stream);
                mediaPlayer.prepare();
                prepared = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mediaPlayer.start();
            Toast.makeText(activity_main.this, "Playing..  " + title, Toast.LENGTH_SHORT).show();
        }

        public void stopMusic() {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

}