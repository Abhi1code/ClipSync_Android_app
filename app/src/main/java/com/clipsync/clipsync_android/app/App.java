package com.clipsync.clipsync_android.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.IntentFilter;
import android.os.Build;

import com.clipsync.clipsync_android.modal.Constant;
import com.clipsync.clipsync_android.receivers.Clip_receiver;


public class App extends Application {

    public static final String CHANNEL_ID = "notification_channel";
    private Clip_receiver clip_receiver = new Clip_receiver();

    @Override
    public void onCreate() {
        super.onCreate();
        create_channel();
        register_receiver();
    }

    private void register_receiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            IntentFilter filter = new IntentFilter(Constant.receiver_key);
            registerReceiver(clip_receiver, filter);
        }
    }

    private void create_channel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Foreground service channel", NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
