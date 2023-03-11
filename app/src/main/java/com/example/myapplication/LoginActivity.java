package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.LocusId;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText emailEditText,passwordEditTest;
    Button loginBtn;
    ProgressBar progressBar;
    TextView createAccountBtnTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText=findViewById(R.id.email_edit_text);
        passwordEditTest=findViewById(R.id.password_edit_text);
        loginBtn=findViewById(R.id.login_account_button);
        progressBar=findViewById(R.id.Progress_bar);
        createAccountBtnTextView=findViewById(R.id.create_account_text_view_btn);

        loginBtn.setOnClickListener(v-> loginUser());
        createAccountBtnTextView.setOnClickListener(v-> startActivity(new Intent(LoginActivity.this,RegisterActivity.class)));
    }
   void loginUser(){
       String email= emailEditText.getText().toString();
       String password= passwordEditTest.getText().toString();
       boolean isvalidated= validateData(email,password);
       if (!isvalidated){
           return ;
       }
       loginAccountInFirebase(email,password);

   }
   void loginAccountInFirebase(String email,String password){
       FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
       changeInProgress(true);
       firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               changeInProgress(false);
               if (task.isSuccessful()){
                   //login is success
                   if(firebaseAuth.getCurrentUser().isEmailVerified()){
                       //go mainactivity
                       startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                   }else {
                       Utility.showToast(LoginActivity.this,"Email not verified , Please verify your email");
                   }
               }else {
                   //login failed
                   Utility.showToast(LoginActivity.this,task.getException().getLocalizedMessage());

               }
           }
       });


   }
    void changeInProgress(boolean InProgress){
        if(InProgress){
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }
    boolean validateData(String email,String password){
        // validate the data that the user has inputted.
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid ");
            return  false;
        }
        if(password.length()<6){
            passwordEditTest.setError("Password length is invalid ");
            return false;
        }

        return  true;

    }
}