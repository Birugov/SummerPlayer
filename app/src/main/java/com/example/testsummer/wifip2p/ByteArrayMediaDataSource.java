package com.example.testsummer.wifip2p;

import android.media.MediaDataSource;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.M)
public class ByteArrayMediaDataSource extends MediaDataSource {

    public byte[] data;
    int size = 0;

    public ByteArrayMediaDataSource() {
        this.data = new byte[9000000];
    }

    public void addBytes(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            this.data[size] = data[i];
            size++;
        }
    }
    @Override
    public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
        System.arraycopy(data, (int)position, buffer, offset, size);
        return size;
    }

    @Override
    public long getSize() throws IOException {
        return size;
    }

    @Override
    public void close() throws IOException {
        // Nothing to do here
    }
}
