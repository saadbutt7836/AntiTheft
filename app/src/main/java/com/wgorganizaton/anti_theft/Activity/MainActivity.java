package com.wgorganizaton.anti_theft.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.wgorganizaton.anti_theft.Authentication.SignInActivity;
import com.wgorganizaton.anti_theft.R;
import com.wgorganizaton.anti_theft.Utility.TimeStamp;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
//import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.device_info)
    LinearLayout Device_info;
    @BindView(R.id.subscribe)
    LinearLayout Subscribe;
    @BindView(R.id.feature)
    LinearLayout Feature;
    @BindView(R.id.setting)
    LinearLayout Settings;


    //    VARIABLES
    private String userId = null;
    private Double timestamp = null;

    //FIREBASE
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(getString(R.string.dashboard));
        ButterKnife.bind(this);


        CheckUserLogin();

        FirebaseCasting();

//        RetrieveUserInfo();

//        CLICK LISTENERS
        Device_info.setOnClickListener(this);
        Subscribe.setOnClickListener(this);
        Feature.setOnClickListener(this);
        Settings.setOnClickListener(this);

//        CLICKABLE FALSE
//        Device_info.setClickable(false);
//        Subscribe.setClickable(false);
//        Feature.setClickable(false);
//        Settings.setClickable(false);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.device_info:
                startActivity(new Intent(this, DeviceInfoActivity.class));
                break;
            case R.id.subscribe:
                startActivity(new Intent(this, SubscriptionActivity.class));
                break;
            case R.id.feature:
                Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }
    }

    //    ==================================================== CALLING METHODS ======================================================
    private void FirebaseCasting() {
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        UserRef = mDatabase.child("Users");
    }


    private void CheckUserLogin() {
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
        }
    }

    private void RetrieveUserInfo() {
        UserRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    timestamp = Double.valueOf(dataSnapshot.child("timestamp").getValue().toString());


                    if (TimeStamp.getTimeStamp(timestamp) > 15) {
                        Toast.makeText(MainActivity.this, "Buy Subscription", Toast.LENGTH_SHORT).show();
                        Dialog();
                    } else {
                        Toast.makeText(MainActivity.this, "free trial or paid", Toast.LENGTH_SHORT).show();

                        Device_info.setClickable(true);
                        Subscribe.setClickable(true);
                        Feature.setClickable(true);
                        Settings.setClickable(true);

                    }

                } else {
                    Toast.makeText(MainActivity.this, "Please Upload Information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Dialog() {
// Create Alert using Builder
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                .setCancelable(false)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle(getString(R.string.trial_title))
                .setMessage(getString(R.string.trial_desc))
                .addButton("Upgrade", getResources().getColor(R.color.White), getResources().getColor(R.color.WineRed), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {

                    Intent intent = new Intent(MainActivity.this, SubscriptionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                    dialog.dismiss();
                });

// Show the alert
        builder.show();
    }
}