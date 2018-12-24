package com.clipsync.clipsync_android.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clipsync.clipsync_android.R;
import com.clipsync.clipsync_android.modal.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Dialog extends BottomSheetDialogFragment implements View.OnClickListener {

    public inter_face inter_face;
    public String data;
    private TextView cop_data;
    private CardView send_data;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            data = getArguments().getString("params");
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inter_face = (inter_face) getActivity();
        View v = inflater.inflate(R.layout.dialog, container, false);
        cop_data = v.findViewById(R.id.cop_data);
        send_data = v.findViewById(R.id.send_card);
        cop_data.setText(data);
        send_data.setOnClickListener(this);
        return v;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        //Toast.makeText(getActivity(), "Dismissed", Toast.LENGTH_SHORT).show();
        inter_face.call();
    }

    @Override
    public void onClick(View view) {
        if (data != null) {
            String jsonString = string_to_json(Constant.device_id, data);
            Map<String, Object> jsonMap = new Gson().fromJson(jsonString, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("post").push().setValue(jsonMap);
            inter_face.call();
        }
    }

    public String string_to_json(String device_id, String data) {
        String json = null;

        try {
            JSONObject jObj = new JSONObject();
            jObj.put("device_id", device_id);
            jObj.put("data", data);

            json = jObj.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public interface inter_face {
        void call();
    }

}