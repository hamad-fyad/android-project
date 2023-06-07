package com.example.myapplication;



import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;


import com.google.android.material.bottomnavigation.BottomNavigationView;


public class serviceActivity2 extends AppCompatActivity {
    private Button needwork,needservice;
    private BottomNavigationView bottomNavigationView;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service2);


       needwork=findViewById(R.id.needwork);
       needservice=findViewById(R.id.needservice);


        needwork.setOnClickListener(v->need_work());
       needservice.setOnClickListener(v->need_service());
         bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.service);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.profile:
                    // Start ProfileActivity
                    Intent profileIntent = new Intent(serviceActivity2.this, ProfileActivity.class);
                    startActivity(profileIntent);
                    return true;
                case R.id.browsing:
                    // Start BrowsingActivity
                    Intent browsingIntent = new Intent(serviceActivity2.this, MainActivity.class);
                    startActivity(browsingIntent);
                    return true;
                case R.id.service:
                    // Start ServiceActivity
                    Utility.showToast(serviceActivity2.this,"you are already in service page");
                    return true;
            }
            return false;
        });

    }

    private void need_service() {
        // Check if permissions are granted
        if (!PermissionUtils.hasFineLocationPermission(this)) {
            PermissionUtils.requestFineLocationPermission(this);
        }
        else {
            startActivity(new Intent(serviceActivity2.this, MapsActivity.class));
        }
    }



    private void need_work() {
        if (!PermissionUtils.hasFineLocationPermission(this)) {
            PermissionUtils.requestFineLocationPermission(this);
        }
        else {
            startActivity(new Intent(serviceActivity2.this, needworkActivity.class));
        }
    }




}