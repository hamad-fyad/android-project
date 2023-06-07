package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class needworkActivity extends AppCompatActivity {
    private TextView slide;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needwork);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            DocumentReference docRef = db.collection("users").document(currentUser.getUid());

            // Check if permissions are granted
            if (PermissionUtils.hasFineLocationPermission(this)) {
                Location location = Utility.getCurrentLocation(this);

                if (location != null) {
                    // If location is not null, update the user details in Firestore
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("lookingForWork", true);
                    updates.put("latitude", location.getLatitude());
                    updates.put("longitude", location.getLongitude());

                    docRef.update(updates)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                            .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
                } else {
                    // Location is null. Handle this case
                    Utility.showToast(this, "Couldn't find your location. Please ensure location is enabled and permissions are granted.");
                }
            } else {
                // Location permission is not granted, request it
                PermissionUtils.requestFineLocationPermission(this);
            }
        }

        recyclerView = findViewById(R.id.worker_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        slide = findViewById(R.id.slide);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.service);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.profile:
                    // Start ProfileActivity
                    Intent profileIntent = new Intent(needworkActivity.this, ProfileActivity.class);
                    startActivity(profileIntent);
                    return true;
                case R.id.browsing:
                    // Start BrowsingActivity
                    Intent browsingIntent = new Intent(needworkActivity.this, MainActivity.class);
                    startActivity(browsingIntent);
                    return true;
                case R.id.service:
                    // Start ServiceActivity
                    Intent serve = new Intent(needworkActivity.this, serviceActivity2.class);
                    startActivity(serve);
                    return true;
            }
            return false;
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            listenForInterestedUsers();
        });

        // Trigger initial refresh manually
        listenForInterestedUsers();
    }

    private void listenForInterestedUsers() {
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .addSnapshotListener((snapshot, e) -> {
                        if (e != null) {
                            Log.e(TAG, "Error listening for interested users", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            List<String> interestedUsers = snapshot.toObject(User.class).getInterestedUsers();
                            if (interestedUsers != null) {
                                ArrayList<User> userList = new ArrayList<>();
                                for (String userId : interestedUsers) {
                                    db.collection("users").document(userId)
                                            .get()
                                            .addOnCompleteListener(userTask -> {
                                                if (userTask.isSuccessful()) {
                                                    DocumentSnapshot userDocument = userTask.getResult();
                                                    if (userDocument.exists()) {
                                                        User user = userDocument.toObject(User.class);
                                                        userList.add(user);
                                                        Log.w(TAG, "listenForInterestedUsers: " + user.toString());
                                                        // Update the RecyclerView when a new user is added
                                                        runOnUiThread(() -> {
                                                            WorkerAdapter adapter = new WorkerAdapter(userList);
                                                            slide.setVisibility(View.GONE);
                                                            recyclerView.setAdapter(adapter);
                                                        });
                                                    }
                                                } else {
                                                    Log.w(TAG, "Error getting user document.", userTask.getException());
                                                }
                                            });
                                }
                            } else {
                                runOnUiThread(() -> {
                                    if (!isFinishing()) {
                                        Utility.showToast(needworkActivity.this, "No interested users at the moment. Please try again later.");
                                    }
                                });
                            }
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Reset the values in Firestore when the app goes into the background
        Map<String, Object> updates = new HashMap<>();
        updates.put("lookingForWork", false);
        updates.put("lookingforservice", false); // or whatever the default value is
        updates.put("latitude", -1);
        updates.put("longitude", -1);
        updates.put("interestedUsers", null);

        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }
}
