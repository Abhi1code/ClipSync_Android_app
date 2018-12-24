package com.clipsync.clipsync_android.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.clipsync.clipsync_android.R;
import com.clipsync.clipsync_android.activity.Mainactivity;
import com.clipsync.clipsync_android.modal.Constant;
import com.clipsync.clipsync_android.modal.Shared_pref;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.clipsync.clipsync_android.app.App.CHANNEL_ID;

public class Copyservice extends Service {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private boolean in_or_out = true;
    private Shared_pref shared_pref;
    public NotificationCompat.Builder builder;
    public Notification notification;
    private String prev_data = "clipsync is listening to clipboard events";

    private OnPrimaryClipChangedListener listener = new OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            Log.d("ABHI", "Triggered");
            performClipboardCheck();
        }
    };

    private void performClipboardCheck() {
        ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (cb.hasPrimaryClip()) {
            ClipData cd = cb.getPrimaryClip();

            if (cd.toString() != null && !TextUtils.isEmpty(cd.toString())) {

                if (in_or_out) {
                    String curr_data = cd.getItemAt(0).getText().toString().trim();

                    Intent intent = new Intent(Constant.receiver_key);
                    intent.putExtra("copied_text", curr_data);
                    sendBroadcast(intent);

                    //Log.d("ABHI", "" + (cd.getItemAt(0).getText()).toString());

                } else {
                    in_or_out = true;
                }

            }
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        shared_pref = new Shared_pref(this);
        if (shared_pref.get_session()) {
            ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).addPrimaryClipChangedListener(listener);
            clip_service();
        }
        //Log.d("ABHI", "oncreate");
    }

    private void clip_service() {
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("post").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    String msg = child.child("data").getValue(String.class);
                    String id = child.child("device_id").getValue(String.class);
                    //Log.d("ABHI", "data added");
                    in_or_out = false;
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", msg);
                    clipboard.setPrimaryClip(clip);
                    prev_data = msg;
                    if (builder != null){
                        builder.setContentText(prev_data);
                        startForeground(1, builder.build());
                    }
                    in_or_out = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).removePrimaryClipChangedListener(listener);
                stopSelf();
                ContextCompat.startForegroundService(getApplicationContext(), new Intent(getApplicationContext(), Copyservice.class));
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //notifiy();
        Intent notify_intent = new Intent(this, Mainactivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notify_intent, 0);

        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("ClipSync is running")
                .setContentText(prev_data)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setColor(Color.parseColor("#104E8B"))
                .setPriority(Notification.PRIORITY_LOW);

        notification = builder.build();

        startForeground(1, notification);

        //Log.d("ABHI", "onstart");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).removePrimaryClipChangedListener(listener);
        stopSelf();
        ContextCompat.startForegroundService(this, new Intent(this, Copyservice.class));
        super.onDestroy();
        //Log.d("ABHI", "ondestroy");
    }

    private void notifiy() {
        Intent intent = new Intent(getApplicationContext(), Mainactivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("title");
        builder.setContentText("message");
        builder.setSmallIcon(R.drawable.ic_menu_camera);
        builder.setColor(Color.parseColor("#FF07167A"));
        builder.setAutoCancel(false);
        builder.setTicker("knock knock knock");
        builder.setContentIntent(pendingIntent);
        builder.setPriority(Notification.PRIORITY_LOW);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);
    }
}
