package com.example.testsummer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.contentcapture.ContentCaptureCondition;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;


public class activity_main extends AppCompatActivity {

    private static final String[] PERMISSION = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int REQUEST_PERMISSIONS = 12345;
    private static final int PERMISSION_COUNT = 1;

    boolean prepared = false;
    boolean started = false;

    private Button toPlay;
    MediaPlayer mediaPlayer;
    private ListView listOfSongs;
    private String stream;
    private String title;

    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    ArrayList<String> arrayStreams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_main);
        toPlay = findViewById(R.id.toPlay);
        toPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_play);
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
                stream = currentLocation;

                arrayList.add("Title: " + currentTitle + "\n"
                        + "Artist: " + currentArtist + "\n" + "Location: " + currentLocation);
            } while (songCursor.moveToNext());
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
                stream = arrayList.get(position);
                int val = stream.indexOf("Location: ");
                stream = stream.substring(val + 10);
                title = arrayList.get(position);
                val = title.indexOf("Title: ");
                int val2 = title.indexOf("Artist: ");
                title = title.substring(val + 6, val2);
                new PLayerTask().execute(stream);


            }
        });

    }

    private class PLayerTask extends AsyncTask<String, Void, Boolean> {
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
    }

      /* @SuppressLint("NewAPI")
     private boolean arePermissionsDenied(){
         for (int i = 0; i < PERMISSION_COUNT; i++){
             if (checkSelfPermission(PERMISSION[i]) != PackageManager.PERMISSION_GRANTED) {
                 return true;
             }
         }
         return false;
     }*/
}
