package com.example.myapplication;

import static android.content.ContentValues.TAG;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {  // check if the user is not null
            DocumentReference docRef = db.collection("users").document(user.getUid());

            // Check if permissions are granted
            if (PermissionUtils.hasFineLocationPermission(this)) { // Assuming you have a similar PermissionUtils for location
                Location location = Utility.getCurrentLocation(this);

                if (location != null) {
                    // If location is not null, update the user details in Firestore
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("lookingForWork", true);
                    updates.put("latitude", location.getLatitude());
                    updates.put("longitude", location.getLongitude());

                    docRef.update(updates)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                startActivity(new Intent(serviceActivity2.this, needworkActivity.class));
                            })
                            .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
                } else {
                    // Location is null. Handle this case
                    Utility.showToast(this,"Couldn't find your location. Please ensure location is enabled and permissions are granted.");
                }
            } else {
                // Location permission is not granted, request for it
                PermissionUtils.requestFineLocationPermission(this);
            }
        } else {
            // User is null. Handle this case
            Utility.showToast(this, "User not found. Please ensure you are logged in.");
        }
    }




}