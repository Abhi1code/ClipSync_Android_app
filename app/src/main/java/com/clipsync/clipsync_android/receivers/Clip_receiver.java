package com.clipsync.clipsync_android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.clipsync.clipsync_android.fragment.Fragment_adaptor;

public class Clip_receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String data = intent.getStringExtra("copied_text");
        //Log.d("ABHI", ""+data);
        if (data != null) {
            Intent data_intent = new Intent(context, Fragment_adaptor.class);
            data_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            data_intent.putExtra("copied_text", data);
            context.startActivity(data_intent);
        }
    }
}
