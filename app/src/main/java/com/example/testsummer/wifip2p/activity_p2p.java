package com.example.testsummer.wifip2p;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
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

import com.example.testsummer.R;
import com.example.testsummer.Services.WiFiDirectBroadcastReceiver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    ServerClass serverClass;
    ClientClass clientClass;
    //SendReceive sendReceive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p2p);
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
        }, 1);

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
                    clientClass.write(msg.getBytes());
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

    public class ServerClass extends AsyncTask {
        Socket socket;
        ServerSocket serverSocket;

        private InputStream inputStream;


        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                Log.d("CLOS2", "CIRCLE2");
                serverSocket = new ServerSocket(16384);

                Log.d("CLOS2", "CIRCLE2.45");
                socket = serverSocket.accept();
                Log.d("CLOS2", "CIRCLE2.5");
                inputStream = socket.getInputStream();
                Log.d("CLOS2", "CIRCLE3");
                MediaPlayer mediaPlayer = new MediaPlayer();
                ByteArrayMediaDataSource po = new ByteArrayMediaDataSource();

                int num = 0;
                byte[] buffer = new byte[1024];
                int bytes;
                boolean started = false;
                while (socket != null) {
                    try {
                        bytes = inputStream.read(buffer);
                        if (bytes > 0) {
                            po.addBytes(buffer);
                            Log.d("ADDING", "OK");
                            num++;
                        }
                        if (bytes == -1)
                            break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("PLAYING", "OK " + po.size + " vs " + R.raw.sound1);
                mediaPlayer.setDataSource(po);
                mediaPlayer.prepare();
                mediaPlayer.start();
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
        private OutputStream outputStream = null;
        Socket socket;
        String hostAdd;

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

        public void write(byte[] text) {
            try {
                Log.d("SENDING", "OK");
                if (outputStream == null)
                    outputStream = socket.getOutputStream();
                Log.d("SENDING", "OK3");
                InputStream inputStream = null;
                inputStream = new ByteArrayInputStream(text);
                int len;
                while ((len = inputStream.read(text)) != -1) {
                    Log.d("SENDING", "OK2");
                    outputStream.write(text, 0, len);
                }
                Log.d("SENDINGERROR", String.valueOf(len));
                //outputStream.close();
                outputStream.flush();
                inputStream.close();
            } catch (IOException e) {
                Log.d("ClientSocket.TAG", e.toString());
            } catch (Exception ex) {
                Log.d("SENDING", ex.getMessage());
//                if (socket != null) {
//                    if (socket.isConnected()) {
//                        try {
//                            socket.close();
//                        } catch (IOException e) {
//                            //catch logic
//                        }
//                    }
//                }
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            try {
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                socket.bind(null);
                socket.connect(new InetSocketAddress(hostAdd, 16384), 500);
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream inputStream = getResources().openRawResource(R.raw.sound1);

            int length;
            long size = 0;
            byte[] buff = new byte[1024];
            while (true) {
                try {
                    if (((length = inputStream.read(buff)) == -1)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                write(buff);
                size += buff.length;
            }
            Log.d("LEN", String.valueOf(size));
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
