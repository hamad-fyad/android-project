package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_account_button);
        progressBar = findViewById(R.id.Progress_bar);
        TextView createAccountTextView = findViewById(R.id.create_account_text_view_btn);
        TextView resetPassword = findViewById(R.id.ResetPass);
        loginButton.setOnClickListener(v -> loginUser());
        resetPassword.setOnClickListener(v->startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class)));
        createAccountTextView.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        boolean isValidated = validateData(email, password);
        if (!isValidated) {
            return;
        }
        loginAccountInFirebase(email, password);
    }

    private void loginAccountInFirebase(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            changeInProgress(false);
            if (task.isSuccessful()) {
                //login is success
                if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                    //go mainactivity
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else if(!firebaseAuth.getCurrentUser().isEmailVerified()) {
                    Utility.showToast(LoginActivity.this, "Email not verified , Please verify your email");
                }


            } else {
                Utility.showToast(LoginActivity.this, "you are account is not  with us ");
                Intent signup=new Intent(LoginActivity.this,RegisterActivity.class);
                signup.putExtra("email",emailEditText.getText().toString());
                signup.putExtra("password",passwordEditText.getText().toString());
                startActivity(signup);
                //login failed
            }
        });
    }

    private void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateData(String email, String password) {
        // validate the data that the user has inputted.
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email is invalid ");
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordEditText.setError("Password length is invalid ");
            return false;
        }
        return true;
    }
}
