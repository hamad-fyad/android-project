package com.example.myapplication;

import static android.content.ContentValues.TAG;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = db.collection("users").document(user.getUid());

        // Check if permissions are granted
        Location location = Utility.getCurrentLocation(this);
        Map<String, Object> updates = null;
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            updates = new HashMap<>();
            updates.put("lookingforservice", true);
            updates.put("latitude", latitude);
            updates.put("longitude", longitude);
        } else {
            Log.w(TAG, "No location available");
        }

        if (updates != null) {
            docRef.update(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                            startActivity(new Intent(serviceActivity2.this, MapsActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        } else {
            // Handle the case when location is null
            // You may want to display an error message to the user
        }
    }


    private void need_work() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = db.collection("users").document(user.getUid());
//todo when making the other pages make sure to change the ==null because of the emulator
        // Check if permissions are granted
        Location location = Utility.getCurrentLocation(this);
        Map<String, Object> updates = null;
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            updates = new HashMap<>();
            updates.put("LookingForWork", true);
            updates.put("latitude", latitude);
            updates.put("longitude", longitude);
        } else {
            Log.w(TAG, "No location available");
        }

        if (updates != null) {
            docRef.update(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                            startActivity(new Intent(serviceActivity2.this, needworkActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        } else {
            // Handle the case when location is null
            // You may want to display an error message to the user
        }
    }



}