package com.clipsync.clipsync_android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.clipsync.clipsync_android.R;
import com.clipsync.clipsync_android.fragment.Dialog;

public class Fragment_adaptor extends AppCompatActivity implements Dialog.inter_face {

    private Dialog dialog;
    private String text;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheet);

        if (getIntent().getStringExtra("copied_text") == null) {
            dialog.dismiss();
            finish();
        } else {
            text = getIntent().getStringExtra("copied_text");
        }

        if (savedInstanceState != null) {
            finish();
        }

        dialog = new Dialog();
        Bundle bundle = new Bundle();
        bundle.putString("params", text);
        // set MyFragment Arguments
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "Dialog");

    }

    @Override
    public void call() {
        //Toast.makeText(this, "des", Toast.LENGTH_SHORT).show();
        if (dialog != null) {
            dialog.dismiss();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (dialog.isVisible()) {
            dialog.dismiss();
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
