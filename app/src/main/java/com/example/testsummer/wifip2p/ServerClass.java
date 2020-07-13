package com.example.testsummer.wifip2p;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.testsummer.PlayerTask;
import com.example.testsummer.activity_main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerClass extends AsyncTask {
    static int currentPos = 0;
    Socket socket;
    ServerSocket serverSocket;

    private DataInputStream inputStream;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected Object doInBackground(Object[] objects) {
        activity_p2p.activeServer = true;
        try {
            Log.d("CLOS2", "CIRCLE2");
            if (serverSocket == null) {
                serverSocket = new ServerSocket(16384);

                Log.d("CLOS2", "CIRCLE2.45");
                socket = serverSocket.accept();
                Log.d("CLOS2", "CIRCLE2.47");
                //socket.setKeepAlive(true);
                Log.d("CLOS2", "CIRCLE2.48");
            }
            Log.d("CLOS2", "CIRCLE2.49");
            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            Log.d("CLOS2", "CIRCLE2.5");

            if (socket != null)
                activity_p2p.byteArraySize = inputStream.readInt();
            byte[] buffer = new byte[activity_p2p.byteArraySize];
            ByteArrayMediaDataSource po = new ByteArrayMediaDataSource();
            BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(activity_main.appContext.getCacheDir() + "//cacheaudio.mp3"));
            int tmpOld1 = inputStream.read();
            int tmpOld2 = inputStream.read();
            try {
                while (true) {
                    int tmp = inputStream.read();
                    if (tmp != -1) {
                        fileOut.write(tmpOld1);
                        tmpOld1 = tmpOld2;
                        tmpOld2 = tmp;
                    } else {
                        currentPos = tmpOld1 << 8 | tmpOld2;
                        break;
                    }
                }
//                        bytes = inputStream.read(buffer);
//                        if (bytes > 0) {
//                            po.addBytes(buffer);
//
//                            Log.d("ADDING", "OK");
//                            num++;
                //currentPos = buffer[size - 2] << 8 | data[size - 1];
                //}
//                        if (bytes == -1) {
//                            Log.d("BREAKED", "-1");
//                            break;
//                        }
            } catch (IOException e) {
                serverSocket.close();
                serverSocket = null;
                e.printStackTrace();
            }
            inputStream.close();

            Log.d("CLOS2", "ye");
            // }
            //fileOut.close();
            //po.deleteLastInt();
            //Log.d("PLAYING", "OK " + po.size + " vs " + R.raw.sound1);
//                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.stop();
//                    mediaPlayer.reset();
//                }

            PlayerTask playerTask = new PlayerTask(activity_main.mediaPlayer, "Sound", activity_main.appContext.getCacheDir() + "//cacheaudio.mp3", currentPos);
            playerTask.execute();
//                AudioPlayerClass audioPlayerClass = new AudioPlayerClass(getCacheDir() + "//cacheaudio.mp3", currentPos);
//                audioPlayerClass.execute();


//                new File (getCacheDir() + "/cacheaudio.mp3").setReadable(true);
//                //mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(getCacheDir() + "/cacheaudio.mp3"));
//                mediaPlayer.setAudioAttributes(
//                        new AudioAttributes.Builder()
//                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                                .setUsage(AudioAttributes.USAGE_MEDIA)
//                                .build()
//                );
//                mediaPlayer.setDataSource(getCacheDir() + "/cacheaudio.mp3");
//                //mediaPlayer.setDataSource(po);
//                //mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound1);
//                //mediaPlayer.seekTo(currentPos);
//                mediaPlayer.start();
            //serverSocket.close();
            serverSocket.close();
            serverSocket = null;
            socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}