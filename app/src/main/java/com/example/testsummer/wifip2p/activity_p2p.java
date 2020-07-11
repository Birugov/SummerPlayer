package com.example.testsummer.wifip2p;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.MediaDataSource;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.testsummer.PlayerTask;
import com.example.testsummer.R;
import com.example.testsummer.Services.WiFiDirectBroadcastReceiver;
import com.example.testsummer.activity_main;

import net.protyposis.android.mediaplayer.MediaPlayer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import wseemann.media.FFmpegMediaPlayer;

public class activity_p2p extends AppCompatActivity {

    Button btnOnOff, btnDiscover, btnSend;
    ListView listView;
    TextView read_msg_box;
    public TextView connectionStatus;
    EditText writeMsg;

    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> peers = new ArrayList<>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;

    public boolean booled = true;
    static final int MESSAGE_READ = 1;
    static int byteArraySize = 0;

    ServerClass serverClass;
    ClientClass clientClass;
    //SendReceive sendReceive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p2p);

        initialWork();
        exqListener();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) message.obj;
                    String tempMsg = new String(readBuff, 0, message.arg1);
                    read_msg_box.setText(tempMsg);
                    break;
            }
            return true;
        }
    });

    private void exqListener() {
        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                    btnOnOff.setText("WIFI ON");
                } else {
                    wifiManager.setWifiEnabled(true);
                    btnOnOff.setText("WIFI OFF");
                }
            }
        });
        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connectionStatus.setText("Discovering");
                        mManager.requestPeers(mChannel, peerListListener);
                    }

                    @Override
                    public void onFailure(int i) {
                        connectionStatus.setText("Discovery failure");
                    }
                });
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final WifiP2pDevice device = deviceArray[i];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;


                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(getApplicationContext(), "Connection failure (with " + device.deviceName + ")", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = writeMsg.getText().toString();
                Log.d("WTF", msg);
                if (msg != null)
                    clientClass.write();
            }
        });
    }


    private void initialWork() {
        btnOnOff = (Button) findViewById(R.id.onOff);
        btnDiscover = (Button) findViewById(R.id.discover);
        btnSend = (Button) findViewById(R.id.sendButton);
        listView = (ListView) findViewById(R.id.peerListView);
        read_msg_box = (TextView) findViewById(R.id.readMsg);
        connectionStatus = (TextView) findViewById(R.id.connectionStatus);
        writeMsg = (EditText) findViewById(R.id.writeMsg);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if (!peerList.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                deviceNameArray = new String[peerList.getDeviceList().size()];
                int index = 0;
                for (WifiP2pDevice device : peerList.getDeviceList()) {
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index] = device;
                    index++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                listView.setAdapter(adapter);
            }
            if (peers.size() == 0) {
                Toast.makeText(getApplicationContext(), "No device found", Toast.LENGTH_LONG).show();
                return;
            }
        }
    };

    public WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;

            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                connectionStatus.setText("Host");
                serverClass = new ServerClass();
                serverClass.execute();
            } else if (wifiP2pInfo.groupFormed) {
                connectionStatus.setText("Client");
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.execute();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public static int getByteArraySize() {
        return byteArraySize;
    }

    private MediaPlayer mediaPlayer;

    public class AudioPlayerClass extends AsyncTask {
        FFmpegMediaPlayer mp = new FFmpegMediaPlayer();
        private String source;
        private int currentPosition;

        public AudioPlayerClass(String source, int currentPosition) {
            this.source = source;
            this.currentPosition = currentPosition;
        }

        public void run() {
            mp.setOnPreparedListener(new FFmpegMediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(FFmpegMediaPlayer mp) {
                    Log.d("7", "1");
                    mp.seekTo(currentPosition);
                    mp.start();
                    if (mp.isPlaying())
                        Log.d("Play", "true");
                    else
                        Log.d("Play", "false");
                }
            });
            mp.setOnErrorListener(new FFmpegMediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(FFmpegMediaPlayer mp, int what, int extra) {
                    Log.d("6", "1");
                    mp.release();
                    return false;
                }
            });
            Log.d("99", getCacheDir() + "//cacheaudio.mp3");
            try {
                mp.setDataSource(getCacheDir() + "//cacheaudio.mp3");
                mp.prepareAsync();
                Log.d("0", "1");
            } catch (IllegalArgumentException e) {
                Log.d("1", "1");
                e.printStackTrace();
            } catch (SecurityException e) {
                Log.d("2", "1");
                e.printStackTrace();
            } catch (IllegalStateException e) {
                Log.d("3", "1");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("4", "1");
                e.printStackTrace();
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            run();
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public class ServerClass extends AsyncTask {
        Socket socket;
        ServerSocket serverSocket;

        private DataInputStream inputStream;

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                Log.d("CLOS2", "CIRCLE2");
                serverSocket = new ServerSocket(16384);

                Log.d("CLOS2", "CIRCLE2.45");
                socket = serverSocket.accept();
                Log.d("CLOS2", "CIRCLE2.5");
                inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                Log.d("CLOS2", "CIRCLE3");
                int num = 0;

                int bytes;
                boolean started = false;
                if (socket != null)
                    byteArraySize = inputStream.readInt();
                byte[] buffer = new byte[byteArraySize];
                mediaPlayer = activity_main.mediaPlayer;
                ByteArrayMediaDataSource po = new ByteArrayMediaDataSource();
                BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(getCacheDir() + "//cacheaudio.mp3"));
                int currentPos = 0;
                int tmpOld1 = inputStream.read();
                int tmpOld2 = inputStream.read();
               // while (socket != null) {
                Log.d("ye", "ye");
                    try {
                        while (true) {
                            int tmp = inputStream.read();
                            if (tmp != -1) {
                                fileOut.write(tmpOld1);
                                tmpOld1 = tmpOld2;
                                tmpOld2 = tmp;
                            }
                            else {
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
                        e.printStackTrace();
                    }
                inputStream.close();

                Log.d("ye", "ye");
               // }
                //fileOut.close();
                //po.deleteLastInt();
                //Log.d("PLAYING", "OK " + po.size + " vs " + R.raw.sound1);
//                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.stop();
//                    mediaPlayer.reset();
//                }

                PlayerTask playerTask = new PlayerTask(mediaPlayer, "Sound", getCacheDir() + "//cacheaudio.mp3", currentPos);
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
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

//    private class SendReceive extends Thread {
//        private Socket socket;
//        private OutputStream outputStream2;
//        DataOutputStream outputStream;
//
//        public SendReceive(Socket socket) {
//            this.socket = socket;
//            try {
//
//                outputStream2 = socket.getOutputStream();
//
//                if (outputStream2 == null)
//                    Log.d("WTF", "OUPUT NULL");
//                else
//                    Log.d("WTF", "OUPUT NOT NULL");
//                outputStream = new DataOutputStream(outputStream);
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//
//        @Override
//        public void run() {
//            byte[] buffer = new byte[24];
//            int bytes;
//
//            while (socket != null) {
//                try {
//                    bytes = inputStream.read(buffer);
//                    if (bytes > 0) {
//                        handler.obtainMessage(MESSAGE_READ, bytes,  -1, buffer).sendToTarget();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        public void write(String text) {
//            try {
//                outputStream.writeUTF(text);
//                outputStream.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public class ClientClass extends AsyncTask {
        private DataOutputStream outputStream = null;
        Socket socket;
        String hostAdd;
        MediaPlayer mediaPlayer = activity_main.mediaPlayer;

        public ClientClass(InetAddress hostAddress) {
            Log.d("CLOS", "FUCK3");
            try {
                hostAdd = hostAddress.getHostAddress();
                Log.d("CLOS", hostAdd);
                socket = new Socket();
            } catch (Exception e) {

                Log.d("CLOS", "FUCK");
                e.printStackTrace();
            }

        }

        public void write() {
            if (mediaPlayer == null)
                return;
            try {
                int soundSize = (int) new File(activity_main.getStream()).length();
                Log.d("SENDING", "OK");
                if (outputStream == null)
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
            try {
                if (outputStream != null)
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            if (mediaPlayer == null && !mediaPlayer.isPlaying())
                return null;
            InputStream inputStream = null;
            try {
                socket.bind(null);
                socket.connect(new InetSocketAddress(hostAdd, 16384), 500);
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
            }
            return null;
        }
    }
}
