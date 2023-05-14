package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText, confirmPasswordEditText, nameEditText, addressEditText;
    private  Button createAccountBtn;
    private ProgressBar progressBar;
    private TextView loginBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_Password_edit_text);
        createAccountBtn = findViewById(R.id.create_account_button);
        progressBar = findViewById(R.id.Progress_bar);
        loginBtnTextView = findViewById(R.id.login_text_view_btn);
        nameEditText = findViewById(R.id.name);
        addressEditText = findViewById(R.id.address);

        createAccountBtn.setOnClickListener((v) -> createAccount());
        loginBtnTextView.setOnClickListener(v -> finish());


    }

    private void createAccount() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String address = addressEditText.getText().toString();
        boolean isvalidated = validateData(email, password, confirmPassword,name,address);

        if (!isvalidated) {
            return;
        }

        createUserInFirebaseAuth(email, password, name, address);
    }

    private void createUserInFirebaseAuth(String email, String password, String name, String address) {
        changeInProgress(true);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    changeInProgress(false);

                    if (task.isSuccessful()) {
                        //creating acc done
                        Utility.showToast(RegisterActivity.this, "Successfully created account, check email to verify");
                        sendEmailVerification();
                        saveUserToFirestore(name, address, emailEditText.getText().toString());
                    } else {
                        //failure
                            Utility.showToast(RegisterActivity.this, task.getException().getLocalizedMessage());
                    }
                });
    }

    private void sendEmailVerification() {
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
    }

    private void saveUserToFirestore(String name, String address,String email) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //todo change to the user class maybe will add the number in reqister (ask the user)
       // User users= new User(name,address,email.trim(),number,"",0,false);
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email.trim());
        user.put("address", address);
        user.put("number", "change the number");


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