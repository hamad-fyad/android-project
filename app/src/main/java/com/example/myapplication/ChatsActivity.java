package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private FirebaseFirestore db;
    private String currentUserId;
    private List<String> chatIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        db = FirebaseFirestore.getInstance();
        currentUserId = getIntent().getStringExtra("currentUserId");
        recyclerView = findViewById(R.id.recyclerView);
        chatAdapter = new ChatAdapter(chatIds);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.profile:
                    // Start ProfileActivity
                    Intent profileIntent = new Intent(ChatsActivity.this, ProfileActivity.class);
                    startActivity(profileIntent);
                    return true;
                case R.id.browsing:
                    // Start BrowsingActivity
                    Intent browsingIntent = new Intent(ChatsActivity.this, MainActivity.class);
                    startActivity(browsingIntent);
                    return true;
                case R.id.service:
                    // Start ServiceActivity
                    Intent serviceIntent = new Intent(ChatsActivity.this, serviceActivity2.class);
                    startActivity(serviceIntent);
                    return true;
            }
            return false;
        });
        listenForChats();
    }

    private void listenForChats() {
        db.collection("users").document(currentUserId).collection("chat")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error listening for chats", e);
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                String docId = dc.getDocument().getId();
                                Log.d(TAG, "Document added with ID: " + docId);
                                chatIds.add(docId);
                            }
                            else if (dc.getType() == DocumentChange.Type.REMOVED) {
                                String docId = dc.getDocument().getId();
                                Log.d(TAG, "Document removed with ID: " + docId);
                                chatIds.remove(docId);
                            }
                        }
                    } else {
                        Log.d(TAG, "No chat documents found");
                    }

                    Log.d(TAG, "chatIds list now has " + chatIds.size() + " items");
                    chatAdapter.notifyDataSetChanged();
                });
    }
}