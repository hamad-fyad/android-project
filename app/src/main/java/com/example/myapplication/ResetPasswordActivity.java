package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText Email;
    private TextView Back_to_login;
    private Button Reset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Email=findViewById(R.id.email_edit_text);
        Back_to_login=findViewById(R.id.back_login);
        Reset=findViewById(R.id.reset_pass);
        Back_to_login.setOnClickListener(v->startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class)));

    }
 

}