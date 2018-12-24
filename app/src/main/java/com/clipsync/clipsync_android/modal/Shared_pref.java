package com.clipsync.clipsync_android.modal;

import android.content.Context;
import android.content.SharedPreferences;

public class Shared_pref {

    private static volatile SharedPreferences sharedPreferences;
    Context context;
    SharedPreferences.Editor editor;

    public Shared_pref(Context context) {
        this.context = context;
        prepare_sharedprefs();
    }

    public void prepare_sharedprefs() {
        sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void put_userdata(String email, String name, String photourl, String device_id, String token_id) {
        editor.putString("email", email);
        editor.putString("name", name);
        editor.putString("photourl", photourl);
        editor.putString("device_id", device_id);
        editor.putString("token_id", token_id);
        editor.commit();
    }

    public String get_email() {
        return sharedPreferences.getString("email", null);
    }

    public String get_name() {
        return sharedPreferences.getString("name", null);
    }

    public String get_photo() {
        return sharedPreferences.getString("photourl", null);
    }

    public String get_deviceid() {
        return sharedPreferences.getString("device_id", "Android");
    }

    public boolean get_session() {
        return sharedPreferences.getBoolean("session", false);
    }

    public String get_token() {
        return sharedPreferences.getString("token_id", null);
    }

    public void set_session() {
         editor.putBoolean("session", true);
         editor.commit();
    }

    public void reset_session() {
        editor.putBoolean("session", false);
        editor.remove("email");
        editor.remove("name");
        editor.remove("photourl");
        editor.remove("device_id");
        editor.remove("token_id");
        editor.commit();
    }

}
