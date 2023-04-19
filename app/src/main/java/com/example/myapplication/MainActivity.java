package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Button logout;
    private Button Add_Post;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Add_Post=findViewById(R.id.add_post);
Add_Post.setOnClickListener(v->startActivity(new Intent(MainActivity.this,AddBuildingActivity.class)));
    bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profile:
                        // Start ProfileActivity
                        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(profileIntent);
                        return true;
                    case R.id.browsing:
                        // Start BrowsingActivity
                        Utility.showToast(MainActivity.this,"you are already in browsing page");
                        return true;
                    case R.id.service:
                        // Start ServiceActivity
                        Intent serviceIntent = new Intent(MainActivity.this, serviceActivity2.class);
                        startActivity(serviceIntent);
                        return true;
                }
                return false;
            }
        });
    }



}