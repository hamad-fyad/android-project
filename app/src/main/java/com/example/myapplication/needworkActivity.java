package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


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
    //todo add the gps location and change the user looking for work to true
    // TODO: 13/05/2023 fix the users that are  check who true need work fetch the data for the location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needwork);

        RecyclerView recyclerView = findViewById(R.id.worker_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        // Runnable that fetches data
        Runnable fetchData = () -> {
            if (currentUser != null) {
                // Fetch the current user's document from Firestore
                db.collection("users").document(currentUser.getUid())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Get the interestedUsers array from the current user's document
                                    List<String> interestedUsers = (List<String>) document.get("interestedUsers");
                                    if (interestedUsers != null) {
                                        ArrayList<User> userList = new ArrayList<>();
                                        // Fetch each interested user's document from Firestore
                                        for (String userId : interestedUsers) {
                                            db.collection("users").document(userId)
                                                    .get()
                                                    .addOnCompleteListener(userTask -> {
                                                        if (userTask.isSuccessful()) {
                                                            DocumentSnapshot userDocument = userTask.getResult();
                                                            if (userDocument.exists()) {
                                                                User user = userDocument.toObject(User.class);
                                                                userList.add(user);
                                                                // Update the RecyclerView when a new user is added
                                                                runOnUiThread(() -> {
                                                                    WorkerAdapter adapter = new WorkerAdapter(userList,user1 -> {
                                                                        // Display a toast with the user's name when an item is clicked
                                                                        Toast.makeText(needworkActivity.this, "Clicked on user: " + user1.getName(), Toast.LENGTH_SHORT).show();
                                                                    });
                                                                    recyclerView.setAdapter(adapter);
                                                                });
                                                            }
                                                        } else {
                                                            Log.w(TAG, "Error getting user document.", userTask.getException());
                                                        }
                                                    });
                                        }
                                    } else {
                                        // Make a message no interested users in the meantime
                                        //runOnUiThread makes sure that its run on the main thread
                                        runOnUiThread(() -> Utility.showToast(needworkActivity.this, "No interested users at the moment. Please try again later."));
                                    }
                                }
                            } else {
                                Log.w(TAG, "Error getting user document.", task.getException());
                            }
                            // After refreshing the data, indicate that the refreshing has finished
                            swipeRefreshLayout.setRefreshing(false);
                        });
            }
        };

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            fetchData.run();
        });

        // Trigger initial refresh manually
        fetchData.run();
    }


    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Reset the values in Firestore when the app goes into the background
        Map<String, Object> updates = new HashMap<>();
        updates.put("LookingForWork", false); // or whatever the default value is
        updates.put("latitude", -1);
        updates.put("longitude", -1);
        updates.put("interestedUsers",null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
            assert user != null;
            DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }

}