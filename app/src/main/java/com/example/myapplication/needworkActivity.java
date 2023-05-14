package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Fetch data from Firestore
            db.collection("users")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ArrayList<User> userList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User users = document.toObject(User.class);
                                userList.add(users);
                            }

                            // Set up the adapter with the fetched data
                            WorkerAdapter adapter = new WorkerAdapter(userList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
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
        updates.put("latitude", 0);
        updates.put("longitude", 0);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());

        docRef
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }

}