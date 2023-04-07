package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText emailEditText, passwordEditText, confirmPasswordEditText, nameEditText, addressEditText;
    Button createAccountBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;

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

        createAccountBtn.setOnClickListener(v -> createAccount());
        loginBtnTextView.setOnClickListener(v -> finish());
    }

    void createAccount() {
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

    void createUserInFirebaseAuth(String email, String password, String name, String address) {
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

    void sendEmailVerification() {
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
    }

    void saveUserToFirestore(String name, String address,String email) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }
    boolean validateData(String email, String password, String confirmPassword, String name, String address) {
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
            confirmPasswordEditText.setError("Passwords don't match");
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