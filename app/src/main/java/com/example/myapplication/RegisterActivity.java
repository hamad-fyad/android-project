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

import java.util.regex.Pattern;
//handler for authentication for signing up
public class RegisterActivity extends AppCompatActivity {
EditText emailEditText,passwordEditTest,confirmPasswordEditText;
Button createAccountBtn;
ProgressBar progressBar;
TextView loginBtnTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText=findViewById(R.id.email_edit_text);
        passwordEditTest=findViewById(R.id.password_edit_text);
        confirmPasswordEditText=findViewById(R.id.confirm_Password_edit_text);
        createAccountBtn=findViewById(R.id.create_account_button);
        progressBar=findViewById(R.id.Progress_bar);
        loginBtnTextView=findViewById(R.id.login_text_view_btn);

        createAccountBtn.setOnClickListener(v-> CreateAccount());
        loginBtnTextView.setOnClickListener(v-> finish());
    }
    void CreateAccount(){
        String email= emailEditText.getText().toString();
        String password= passwordEditTest.getText().toString();
        String confirmPassword= confirmPasswordEditText.getText().toString();
        boolean isvalidated= validateData(email,password,confirmPassword);
        if (!isvalidated){
            return ;
        }
        createAccountInFirebase(email,password);



    }
    void createAccountInFirebase(String email,String password){
    changeInProgress(true);
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if (task.isSuccessful()){
                            //creating acc done
                            Utility.showToast(RegisterActivity.this, "Successfully create account,check email to verify");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }else {
                            //failure
                            Utility.showToast(RegisterActivity.this, task.getException().getLocalizedMessage());

                        }
                    }
                });
    }
    void changeInProgress(boolean InProgress){
        if(InProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }
    boolean validateData(String email,String password,String confirmPassword){
        // validate the data that the user has inputted.
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid ");
            return  false;
        }
        if(password.length()<6){
            passwordEditTest.setError("Password length is invalid ");
            return false;
        }
        if(!password.equals(confirmPassword)){
            confirmPasswordEditText.setError(" password doesn't match ");
            return  false ;
        }
    return  true;

    }
}