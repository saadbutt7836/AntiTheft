package com.wgorganizaton.anti_theft.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wgorganizaton.anti_theft.Authentication.SignInActivity;
import com.wgorganizaton.anti_theft.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.device_info)
    LinearLayout Device_info;
    @BindView(R.id.subscribe)
    LinearLayout Subscribe;
    @BindView(R.id.feature)
    LinearLayout Feature;
    @BindView(R.id.setting)
    LinearLayout Settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(getString(R.string.dashboard));
        ButterKnife.bind(this);

        CheckUserLogin();

//        CLICK LISTENERS
        Device_info.setOnClickListener(this);
        Subscribe.setOnClickListener(this);
        Feature.setOnClickListener(this);
        Settings.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.device_info:
                startActivity(new Intent(this, DeviceInfoActivity.class));
                break;
            case R.id.subscribe:
                Toast.makeText(this, "working", Toast.LENGTH_SHORT).show();
                break;
            case R.id.feature:
                Toast.makeText(this, "working", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;

        }
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
}