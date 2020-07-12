package com.example.testsummer.wifip2p;

import android.media.MediaDataSource;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;

@RequiresApi(api = Build.VERSION_CODES.M)
public class InputStreamMediaDataSource extends MediaDataSource {
    private InputStream is;
    private long streamLength = -1, lastReadEndPosition;

    public InputStreamMediaDataSource(InputStream is, long streamLength) {
        this.is = is;
        this.streamLength = streamLength;
        if (streamLength <= 0){
            try {
                this.streamLength = is.available(); //Correct value of InputStream#available() method not always supported by InputStream implementation!
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void close() throws IOException {
        is.close();
    }

    @Override
    public synchronized int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
        if (position >= streamLength)
            return -1;

        if (position + size > streamLength)
            size -= (position + size) - streamLength;

        if (position < lastReadEndPosition) {
            is.close();
            lastReadEndPosition = 0;
            //is = getNewCopyOfInputStreamSomeHow();//new FileInputStream(mediaFile) for example.
        }

        long skipped = is.skip(position - lastReadEndPosition);
        if (skipped == position - lastReadEndPosition) {
            int bytesRead = is.read(buffer, offset, size);
            lastReadEndPosition = position + bytesRead;
            return bytesRead;
        } else {
            return -1;
        }
    }

    @Override
    public synchronized long getSize() throws IOException {
        return streamLength;
    }
}
