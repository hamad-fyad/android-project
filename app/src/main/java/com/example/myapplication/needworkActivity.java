package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class needworkActivity extends AppCompatActivity {
    //todo add the gps location and change the user looking for work to true
    // TODO: 13/05/2023 fix the users that are  cheak who true need work fetch the data for the location
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needwork);

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.worker_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firestore and Auth
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

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
                                                            WorkerAdapter adapter = new WorkerAdapter(userList);
                                                            recyclerView.setAdapter(adapter);
                                                        }
                                                    } else {
                                                        Log.w(TAG, "Error getting user document.", userTask.getException());
                                                    }
                                                });
                                    }
                                }else {
                                    //todo make a meesage no intersted users in the mean time
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting user document.", task.getException());
                        }
                    });
        }
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
        DocumentReference docRef = db.collection("users").document(user.getUid());

        docRef
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }

}