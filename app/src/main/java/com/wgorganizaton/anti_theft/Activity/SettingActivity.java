package com.wgorganizaton.anti_theft.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.wgorganizaton.anti_theft.Authentication.SignInActivity;
import com.wgorganizaton.anti_theft.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.profile_img)
    CircularImageView Profile_Img;
    @BindView(R.id.name)
    TextView F_Name;
    @BindView(R.id.phone_no)
    TextView Phone_No;
    @BindView(R.id.edit_profile)
    Button Edit_profile;
    @BindView(R.id.setting_email)
    TextView Setting_Email;
    @BindView(R.id.change_pass)
    TextView Change_Pass;
    @BindView(R.id.sign_out)
    TextView SignOut;


    //    VARIABLES
    private String userId;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, UserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getSupportActionBar().setTitle(getString(R.string.settings));

        ButterKnife.bind(this);

        FirebaseCasting();

        RetrieveDeviceInfo();

//        CLICK LISTENERS
        Profile_Img.setOnClickListener(this);
        Edit_profile.setOnClickListener(this);
        Change_Pass.setOnClickListener(this);
        SignOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_img:
                Toast.makeText(this, "working", Toast.LENGTH_SHORT).show();
                break;
            case R.id.edit_profile:
                Toast.makeText(this, "working", Toast.LENGTH_SHORT).show();
                break;
            case R.id.change_pass:
                Toast.makeText(this, "working", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sign_out:
                UserSignOut();
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

    private void RetrieveDeviceInfo() {
        UserRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String img = dataSnapshot.child("profile_img").getValue().toString();
                    String fName = dataSnapshot.child("f_name").getValue().toString();
                    String phone = dataSnapshot.child("phone_no").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();

                    Picasso.get().load(img).into(Profile_Img);
                    F_Name.setText(fName);
                    Phone_No.setText(phone);
                    Setting_Email.setText(email);

                } else {
                    Toast.makeText(SettingActivity.this, "Please Upload Information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UserSignOut() {

        Intent intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
        mAuth.signOut();
    }
}