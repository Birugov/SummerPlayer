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
        activity_p2p.mediaPlayer = activity_main.mediaPlayer;
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
        if (activity_p2p.mediaPlayer == null)
            return;
        try {
            int soundSize = (int) new File(activity_main.getStream()).length();
            Log.d("SENDING", "OK");
            //if (outputStream == null)
                outputStream = new DataOutputStream(socket.getOutputStream());
            Log.d("SENDING", "OK3");

            InputStream inputStream = new FileInputStream(activity_main.getStream());
            int len;
            byte[] text = new byte[soundSize];
            outputStream.writeInt(soundSize);
            inputStream.read(text);
            outputStream.write(text);
            outputStream.writeInt(activity_main.mediaPlayer.getCurrentPosition());
//                while ((len = inputStream.read(text)) != -1) {
//                    Log.d("SENDING", "OK2");
//                    outputStream.write(text);
//                }
            //Log.d("SENDINGERROR", String.valueOf(len));
            //outputStream.close();
            //outputStream.writeInt(mediaPlayer.getCurrentPosition());
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
//
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        activity_p2p.activeClient = true;
        if (activity_p2p.mediaPlayer == null && !activity_p2p.mediaPlayer.isPlaying())
            return null;
        InputStream inputStream = null;
        try {
            if (socket == null) {
                socket = new Socket();
                socket.bind(null);
                socket.connect(new InetSocketAddress(hostAdd, 16384), 500);
                //socket.setKeepAlive(true);
            }
            //inputStream = new FileInputStream((activity_main.getStream()));
            //inputStream = getResources().openRawResource(R.raw.sound1);
            write();
//                int length;
//                long size = 0;
//                byte[] buff = new byte[1024];
//                while (true && inputStream != null) {
//                    try {
//                        if (((length = inputStream.read(buff)) == -1)) break;
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    write(buff);
//                    size += buff.length;
//                }
//                Log.d("LEN", String.valueOf(size));
//                try {
//                    outputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
        } catch (IOException e) {
            e.printStackTrace();
            socket = null;
        }
        return null;
    }
}
