package com.example.testsummer.wifip2p;

import android.os.AsyncTask;
import android.util.Log;

import com.example.testsummer.activity_main;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientClass extends AsyncTask {
    private DataOutputStream outputStream = null;
    Socket socket;
    String hostAdd;

    public ClientClass(InetAddress hostAddress) {

        Log.d("CLOS", "FUCK3");
        try {
            hostAdd = hostAddress.getHostAddress();
            Log.d("CLOS", hostAdd);
        } catch (Exception e) {

            Log.d("CLOS", "FUCK");
            e.printStackTrace();
        }
    }

    public void write() {
        if (activity_main.mediaPlayer == null)
            return;
        try {
            int soundSize = (int) new File(activity_main.arrayTracks.get(activity_main.currentSong).file).length();
            Log.d("SENDING", "OK");
            //if (outputStream == null)
                outputStream = new DataOutputStream(socket.getOutputStream());
            Log.d("SENDING", "OK3");

            InputStream inputStream = new FileInputStream(activity_main.arrayTracks.get(activity_main.currentSong).file);
            int len;
            byte[] text = new byte[soundSize];
            outputStream.writeInt(soundSize);
            inputStream.read(text);
            outputStream.write(text);
            outputStream.writeInt(activity_main.mediaPlayer.getCurrentPosition());
            outputStream.flush();
            outputStream.close();

            inputStream.close();
        } catch (IOException e) {
            Log.d("ClientSocket.TAG", e.toString());
        } catch (Exception ex) {
            Log.d("SENDING", ex.getMessage());

        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        activity_p2p.activeClient = true;
        if (activity_main.mediaPlayer == null && !activity_main.mediaPlayer.isPlaying())
            return null;
        try {
            if (socket == null) {
                socket = new Socket();
                socket.bind(null);
                socket.connect(new InetSocketAddress(hostAdd, 16384), 500);
                //socket.setKeepAlive(true);
            }
            write();
        } catch (IOException e) {
            e.printStackTrace();
            socket = null;
        }
        return null;
    }
}
