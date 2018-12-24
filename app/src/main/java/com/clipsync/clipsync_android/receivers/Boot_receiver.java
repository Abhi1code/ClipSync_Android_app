package com.clipsync.clipsync_android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.clipsync.clipsync_android.services.Copyservice;

public class Boot_receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, Copyservice.class));
    }
}
