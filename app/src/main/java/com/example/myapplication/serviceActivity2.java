package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class serviceActivity2 extends AppCompatActivity {
    private Button needwork,needservice;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service2);


       // needwork=findViewById(R.id.needwork);
       // needservice=findViewById(R.id.needservice);

        //  profile.setOnClickListener(v->startActivity(new Intent(service.this,profile.class)));
        //browsing.setOnClickListener(v->startActivity(new Intent(serviceActivity2.this,MainActivity.class)));

        //  profile.setOnClickListener(v->startActivity(new Intent(service.this,needwork.class)));
        //  profile.setOnClickListener(v->startActivity(new Intent(service.this,needservice.class)));
         bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            }
        });

    }
}