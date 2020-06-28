package com.wgorganizaton.anti_theft.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wgorganizaton.anti_theft.R;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

public class DeviceInfoActivity extends AppCompatActivity implements View.OnClickListener {


    @BindView(R.id.imei_txt)
    TextView Imei_Text;
    @BindView(R.id.sim_op_name)
    TextView Sim_Op_Name;
    @BindView(R.id.sim_country_iso)
    TextView Sim_Country__Iso;
    @BindView(R.id.ip_address)
    TextView Ip_Address;
    @BindView(R.id.mac_address)
    TextView Mac_Address;
    @BindView(R.id.upload_info)
    Button Upload_Info;


    //CONSTANTS
    private static final int REQUEST_PHONE_CODE = 100;

    //    VARIABLES
    private String userId = null,
            ip = null,
            mac = null;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, DeviceRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.device_info));

        ButterKnife.bind(this);

        FirebaseCasting();

        RetrieveDeviceInfo();

        Upload_Info.setOnClickListener(this);


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_info:
//                GetDeviceInformation();
                PhonePermission();

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

                    UploadInfoToDatabase(manager.getDeviceId(), manager.getSimOperatorName(), manager.getSimCountryIso());

                } else {
                    Toast.makeText(DeviceInfoActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    //    ==================================================== CALLING METHODS ======================================================
    private void FirebaseCasting() {
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DeviceRef = mDatabase.child("Device-Info");
    }


    private void PhonePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(READ_SMS) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(READ_PHONE_NUMBERS) ==
                    PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {


                TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                UploadInfoToDatabase(manager.getDeviceId(), manager.getSimOperatorName(), manager.getSimCountryIso());

            } else {
                String[] permission = {READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE};
                requestPermissions(permission, REQUEST_PHONE_CODE);
            }
        } else {

            TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            UploadInfoToDatabase(manager.getDeviceId(), manager.getSimOperatorName(), manager.getSimCountryIso());
        }
    }

    public static String getMobileIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    private void RetrieveDeviceInfo() {
        DeviceRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String imei = dataSnapshot.child("imei").getValue().toString();
                    String simOperatorName = dataSnapshot.child("op_name").getValue().toString();
                    String simCountryIso = dataSnapshot.child("op_iso").getValue().toString();
                    String ip = dataSnapshot.child("ip_address").getValue().toString();
                    String mac = dataSnapshot.child("mac_address").getValue().toString();

                    Imei_Text.setText(getString(R.string.imei) + " " + imei);
                    Sim_Op_Name.setText(getString(R.string.op_name) + " " + simOperatorName);
                    Sim_Country__Iso.setText(getString(R.string.op_code) + " " + simCountryIso);
                    Ip_Address.setText(getString(R.string.ip_add) + ": " + ip);
                    Mac_Address.setText(getString(R.string.mac_add) + ": " + mac);


                } else {
                    Toast.makeText(DeviceInfoActivity.this, "Please Upload Information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UploadInfoToDatabase(String imei, String simOperatorName, String simCountryIso) {
        System.out.println("voice: " + getMobileIPAddress());
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();

        String ip = Formatter.formatIpAddress(wInfo.getIpAddress());
        String mac = wInfo.getMacAddress();

        Map<String, Object> Map = new HashMap<>();

        Map.put("imei", imei);
        Map.put("op_name", simOperatorName);
        Map.put("op_iso", simCountryIso);
        Map.put("ip_address", ip);
        Map.put("mac_address", mac);

        DeviceRef.child(userId).updateChildren(Map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(DeviceInfoActivity.this, "Information Updated Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DeviceInfoActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}