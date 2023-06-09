package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.myapplication.Utilitys.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Email=findViewById(R.id.email_edit_text);
        TextView back_to_login = findViewById(R.id.back_login);
        Button reset = findViewById(R.id.reset_pass);
        back_to_login.setOnClickListener(v->startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class)));
        reset.setOnClickListener(v->ResetPassword());
    }
    private void ResetPassword() {
        String email = Email.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Utility.showToast(ResetPasswordActivity.this, "Enter your email first");
            return;
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SignInMethodQueryResult result = task.getResult();
                if (result != null && !result.getSignInMethods().isEmpty()) {

                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Utility.showToast(ResetPasswordActivity.this, "Email sent");
                            startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Utility.showToast(ResetPasswordActivity.this, "Failed to send email");
                        }
                    });
                } else {
                    Utility.showToast(ResetPasswordActivity.this, "You don't have an account with us");
                    startActivity(new Intent(ResetPasswordActivity.this, RegisterActivity.class));
                    finish();
                }
            } else {
                // An error occurred while fetching the sign-in methods for the email address.
                Utility.showToast(ResetPasswordActivity.this, "An error occurred while checking your account status");
            }
        });
    }


}