package com.wgorganizaton.anti_theft.Authentication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.wgorganizaton.anti_theft.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgotPassActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.forgot_email)
    EditText Forgot_Email;
    @BindView(R.id.forgot_done)
    Button Forgot_Done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        ButterKnife.bind(this);

        Forgot_Done.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgot_done:
                String email = Forgot_Email.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(this, "Email Required", Toast.LENGTH_SHORT).show();
                } else {
                    ForgotPassword(email);
                }
                break;
        }
    }

    //    ===================================================== CALLING METHODS ============================================================


    private void ForgotPassword(String email) {

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassActivity.this, "Please Check Your Email", Toast.LENGTH_SHORT).show();
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(ForgotPassActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}