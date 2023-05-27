package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText, confirmPasswordEditText, nameEditText, addressEditText,numberEditText;
    private  Button createAccountBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_Password_edit_text);
        createAccountBtn = findViewById(R.id.create_account_button);
        progressBar = findViewById(R.id.Progress_bar);
        TextView loginBtnTextView = findViewById(R.id.login_text_view_btn);
        nameEditText = findViewById(R.id.name);
        addressEditText = findViewById(R.id.address);
        numberEditText = findViewById(R.id.number);
        String email, password;
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("email") && intent.hasExtra("password")) {
            email = intent.getStringExtra("email");
            password = intent.getStringExtra("password");
            emailEditText.setText(email);
            passwordEditText.setText(password);
        }
        createAccountBtn.setOnClickListener((v) -> createAccount());
        loginBtnTextView.setOnClickListener(v -> finish());


    }

    private void createAccount() {
        String  email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String number = numberEditText.getText().toString().trim();

        boolean isDataValidated = validateData(email, password, confirmPassword, name, address);

        if (!isDataValidated) {
            return;
        }

        createUserInFirebaseAuth(email, password, name, address, number);
    }


    private void createUserInFirebaseAuth(String email, String password, String name, String address,String number) {
        changeInProgress(true);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    changeInProgress(false);

                    if (task.isSuccessful()) {
                        //creating acc done
                        Utility.showToast(RegisterActivity.this, "Successfully created account, check email to verify");
                        sendEmailVerification();
                        saveUserToFirestore(name, address, email, number);
                    } else {
                        //failure
                            Utility.showToast(RegisterActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage());
                    }
                });
    }

    private void sendEmailVerification() {
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
    }

    private void saveUserToFirestore(String name, String address,String email,String number) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //todo change to the user class maybe will add the number in register (ask the user) change but need to check if it works check
        User user= new User(name,address,email.trim(),number,uid);



        FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .set(user)
                .addOnSuccessListener(documentReference -> {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Utility.showToast(RegisterActivity.this, e.getLocalizedMessage());
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                });
    }

    private void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }
    private  boolean validateData(String email, String password, String confirmPassword, String name, String address) {
        // Validate the data that the user has inputted.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password length is invalid");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords doesn't match");
            return false;
        }
        if (name.trim().isEmpty()) {
            nameEditText.setError("Name is required");
            return false;
        }
        if (address.trim().isEmpty()) {
            addressEditText.setError("Address is required");
            return false;
        }
        return true;
    }

}