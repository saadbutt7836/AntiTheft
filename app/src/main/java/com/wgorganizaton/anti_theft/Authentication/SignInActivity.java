package com.wgorganizaton.anti_theft.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wgorganizaton.anti_theft.Activity.MainActivity;
import com.wgorganizaton.anti_theft.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    //    WIDGET CASTING
    @BindView(R.id.new_user)
    TextView New_User;
    @BindView(R.id.sign_in_email)
    EditText Email;
    @BindView(R.id.sign_in_pass)
    EditText Password;
    @BindView(R.id.forgot_pass)
    TextView Forgot_Pass;
    @BindView(R.id.sign_in)
    Button Sign_In;


    //    VARIABLES
    private String email = null,
            password = null;


    //    FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);


        mAuth = FirebaseAuth.getInstance();


        New_User.setOnClickListener(this);
        Forgot_Pass.setOnClickListener(this);
        Sign_In.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_user:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            case R.id.forgot_pass:
                startActivity(new Intent(SignInActivity.this, ForgotPassActivity.class));
                break;
            case R.id.sign_in:
                email = Email.getText().toString().trim();
                password = Password.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    SignIn();
                } else {
                    Toast.makeText(this, "Wrong Information", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //    =============================================================== METHODS CALLING ====================================================================


    private void SignIn() {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();

                            if (user.isEmailVerified()) {
                                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                finish();
                            } else {
                                user.sendEmailVerification();

                                String message = "A verification email sent to your email Id. First verify then signIn please";
                                Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
                               
                                mAuth.signOut();
                            }
                        } else {
                            Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}