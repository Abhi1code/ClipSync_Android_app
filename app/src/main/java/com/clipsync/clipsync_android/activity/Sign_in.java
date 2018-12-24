package com.clipsync.clipsync_android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.clipsync.clipsync_android.R;
import com.clipsync.clipsync_android.modal.Constant;
import com.clipsync.clipsync_android.modal.Shared_pref;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Sign_in extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private SignInButton signin;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog progressdialog;
    private Shared_pref shared_pref;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        signin = findViewById(R.id.signin);
        configure();
        signin.setOnClickListener(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(Sign_in.this, Mainactivity.class));
                    finish();
                }
            }
        };
        progressdialog = new ProgressDialog(this);
        progressdialog.setTitle("Logging In");
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);
        progressdialog.setMessage("Please wait...");
        shared_pref = new Shared_pref(this);
    }

    private void configure() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Something is not right ...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        progressdialog.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, Constant.request_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.request_code) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                // Google Sign In was successful, save Token and a state then authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                shared_pref.put_userdata(account.getEmail(), account.getDisplayName(), account.getPhotoUrl().toString(), Constant.device, account.getIdToken());

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuthWithGoogle(credential);

            } else {
                // Google Sign In failed, update UI appropriately
                //Log.e("ABHI", "Login Unsuccessful. ");
                Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
                progressdialog.dismiss();
            }
        }
    }

    private void firebaseAuthWithGoogle(AuthCredential credential) {

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {

                    Toast.makeText(Sign_in.this, "Authentication failed..", Toast.LENGTH_SHORT).show();
                    shared_pref.reset_session();

                } else {
                    //createUserInFirebase();
                    Toast.makeText(Sign_in.this, "Login successful", Toast.LENGTH_SHORT).show();
                    shared_pref.set_session();
                    databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child(Constant.email).setValue(firebaseAuth.getCurrentUser().getEmail());
                    databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child(Constant.name).setValue(firebaseAuth.getCurrentUser().getDisplayName());
                    databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child(Constant.photo_url).setValue(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                    databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("devices").child(Constant.device).child("device_id").setValue(Constant.device_id);
                    //startActivity(new Intent(Signin.this, Signout.class));
                    //finish();
                    progressdialog.dismiss();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Sign_in.this, "Authentication failed..", Toast.LENGTH_SHORT).show();
                progressdialog.dismiss();
            }
        });
    }

}
