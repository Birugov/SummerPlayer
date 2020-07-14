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

    @Override
    protected Object doInBackground(Object[] objects) {
        activity_p2p.activeServer = true;
        try {
            Log.d("CLOS2", "CIRCLE2");
            if (serverSocket == null) {
                serverSocket = new ServerSocket(16384);

                Log.d("CLOS2", "CIRCLE2.45");
                socket = serverSocket.accept();
            }
            Log.d("CLOS2", "CIRCLE2.49");
            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            Log.d("CLOS2", "CIRCLE2.5");

            if (socket != null)
                activity_p2p.byteArraySize = inputStream.readInt();
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
            } catch (IOException e) {
                serverSocket.close();
                serverSocket = null;
                e.printStackTrace();
            }
            inputStream.close();

            Log.d("CLOS2", "ye");
            PlayerTask playerTask = new PlayerTask(activity_main.mediaPlayer, "Sound", activity_main.appContext.getCacheDir() + "//cacheaudio.mp3", currentPos);
            playerTask.execute();

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