package com.clipsync.clipsync_android.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.clipsync.clipsync_android.R;
import com.clipsync.clipsync_android.activity.adaptor.Message_adaptor;
import com.clipsync.clipsync_android.activity.adaptor.Message_gs;
import com.clipsync.clipsync_android.modal.Constant;
import com.clipsync.clipsync_android.modal.Shared_pref;
import com.clipsync.clipsync_android.services.Copyservice;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class Mainactivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<Message_gs> arrayList;

    private ProgressBar progressBar;
    private ProgressBar main_progressbar;
    private FloatingActionButton send;
    private EditText msg_view;
    private CircleImageView profile_image;
    private TextView name, email;

    private Boolean isscrolling = false;
    private int current_item, total_item, scrolled_item;
    private double start = 1;
    private double end = 2;
    private String key;
    private int scroll_count = 1;

    private Shared_pref shared_pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepare_views();
        prepare_classes();
        prepare_drawer();
        setArrayList();
        setAdapter();
        fetchprimarydata();
        setscrolllistener();
        send.setOnClickListener(this);
        ContextCompat.startForegroundService(this, new Intent(getApplicationContext(), Copyservice.class));
        status_listener();
    }

    private void prepare_drawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        profile_image = headerView.findViewById(R.id.profile_image);
        name = headerView.findViewById(R.id.name);
        email = headerView.findViewById(R.id.email);

        Glide.with(this).load(shared_pref.get_photo()).into(profile_image);
        name.setText(shared_pref.get_name());
        email.setText(shared_pref.get_email());
    }

    private void prepare_classes() {
        shared_pref = new Shared_pref(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
    }

    private void prepare_views() {
        msg_view = findViewById(R.id.msg_edittext);
        send = findViewById(R.id.send);
        recyclerView = findViewById(R.id.chat_view);
        progressBar = findViewById(R.id.progressBar);
        main_progressbar = findViewById(R.id.main_progressbar);
    }

    private void status_listener() {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    //System.out.println("connected");
                    databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("devices").child(shared_pref.get_deviceid()).child("status").setValue("true");
                } else {
                    //System.out.println("not connected");
                    databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("devices").child(shared_pref.get_deviceid()).child("status").setValue("false");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("devices").child(shared_pref.get_deviceid()).child("status").onDisconnect().setValue("false");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setAdapter(){
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        (layoutManager).setReverseLayout(true);
        (layoutManager).setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Message_adaptor(this,arrayList);
        recyclerView.setAdapter(adapter);
        scroll_recyclerview();

    }

    private void scroll_recyclerview(){
        recyclerView.scrollToPosition(0);
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (arrayList.size() == 0){
                                return;
                            }
//                            recyclerView.smoothScrollToPosition(
//                                    recyclerView.getAdapter().getItemCount() - 1);
                            recyclerView.smoothScrollToPosition(0);
                        }
                    }, 100);
                }
            }
        });
    }

    private void setArrayList(){
        arrayList = new ArrayList<>();

    }

    @Override
    public void onClick(View view) {
        String msg = msg_view.getText().toString().trim();
        if (!TextUtils.isEmpty(msg) && msg != null){
            //arrayList.add(new Message_gs("Me", msg, getCurrentTime(), true));
            //Log.d("ABHI", msg);
            arrayList.add(0, new Message_gs("Me", msg, getCurrentTime(), true));
            adapter.notifyItemInserted(0);
            //adapter.notifyDataSetChanged();
            msg_view.setText("");
            scroll_recyclerview();
            String jsonString = string_to_json(Constant.device_id, msg);
            Map<String, Object> jsonMap = new Gson().fromJson(jsonString, new TypeToken<HashMap<String, Object>>() {}.getType());
            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("post").push().setValue(jsonMap);
        }
    }

    public static String getCurrentTime() {
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        String time1 = sdf.format(dt);
        return time1;
    }

    public String string_to_json(String device_id, String data){
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

    private void fetchprimarydata() {
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("post").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    String msg = child.child("data").getValue(String.class);
                    String id = child.child("device_id").getValue(String.class);
                    //Log.d("ABHI", "data added");
                    if (id != null && id.equals(Constant.device_id)) {
//                        arrayList.add(0, new Message_gs("Me", msg, getCurrentTime(), true));
//                        adapter.notifyItemChanged(0);
                    }else {
                        arrayList.add(0, new Message_gs("Me", msg, getCurrentTime(), false));
                        adapter.notifyItemChanged(0);
                    }
//                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                    ClipData clip = ClipData.newPlainText("label", msg);
//                    clipboard.setPrimaryClip(clip);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("post").limitToLast(8).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    String msg = child.child("data").getValue(String.class);
                    String id = child.child("device_id").getValue(String.class);
                    if (i == 0) {
                        key = child.getKey();
                        //Log.d("ABHI", key + "");
                    }
                    if (id != null && id.equals(Constant.device_id)) {
                        arrayList.add(0, new Message_gs("Me", msg, getCurrentTime(), true));
                    }else {
                        arrayList.add(0, new Message_gs("Me", msg, getCurrentTime(), false));
                    }
                    adapter.notifyItemChanged(0);
                    i++;

                }
                main_progressbar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                scroll_recyclerview();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Mainactivity.this, "Network error..", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setscrolllistener() {
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                this.currentScrollState = newState;
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE) {
                    if (arrayList.size() != 0){
                        fetchdata();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                this.currentFirstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                this.currentVisibleItemCount = layoutManager.getChildCount();
                this.totalItem = layoutManager.getItemCount();

            }
        });
    }

    private void fetchdata() {
        progressBar.setVisibility(View.VISIBLE);
        //Log.d("ABHI",""+key);
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("post").orderByKey().limitToLast(8+(5*scroll_count)).endAt(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                int arraylist_size = arrayList.size();
                if (dataSnapshot.getChildrenCount() == 0 || !dataSnapshot.exists() || !dataSnapshot.hasChildren()){
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    String msg = child.child("data").getValue(String.class);
                    String id = child.child("device_id").getValue(String.class);
                    if (i == 0) {
                        key = child.getKey();
                        //Log.d("ABHI", key + "");
                    }

                    if (id != null && id.equals(Constant.device_id)) {
                        arrayList.add(arraylist_size, new Message_gs("Me", msg, getCurrentTime(), true));
                    }else {
                        arrayList.add(arraylist_size, new Message_gs("Me", msg, getCurrentTime(), false));
                    }
                    //adapter.notifyItemChanged(0);
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    i++;
                }
                arrayList.remove(arraylist_size);
                scroll_count++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Mainactivity.this, "Network error..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        startService(new Intent(this, Copyservice.class));
    }
}

