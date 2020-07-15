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
       // this.data = new byte[(int) activity_p2p.getByteArraySize()];
        this.data = new byte[900000];
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

    public int getLastInt() {
        int number = data[size - 2] << 8 | data[size - 1];
        return number;
    }

    public void deleteLastInt() {
        data[size - 2] = 0;
        data[size - 1] = 0;
        size-=2;
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
