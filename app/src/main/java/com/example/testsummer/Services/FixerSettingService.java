package com.example.testsummer.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class FixerSettingService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("TRACKS_TRACKS2")
                .putExtra("actionname2", intent.getAction()));
    }
}
