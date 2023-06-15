package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.adapters.HouseAdapter;
import com.example.myapplication.classes.Buildings;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyPostsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.profile:
                    // Start ProfileActivity
                    Intent profileIntent = new Intent(MyPostsActivity.this, ProfileActivity.class);
                    startActivity(profileIntent);
                    return true;
                case R.id.browsing:
                    // Start BrowsingActivity
                    Intent BrowsingIntent = new Intent(MyPostsActivity.this, MainActivity.class);
                    startActivity(BrowsingIntent);
                    return true;
                case R.id.service:
                    // Start ServiceActivity
                    Intent serviceIntent = new Intent(MyPostsActivity.this, serviceActivity2.class);
                    startActivity(serviceIntent);
                    return true;
            }
            return false;
        });
        getBuildings();
    }
    private void getBuildings() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference housesRef = db.collection("Buildings");

        Query query = housesRef.whereEqualTo("useruid", user.getUid());


        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Buildings> houses = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Buildings house = document.toObject(Buildings.class);
                        houses.add(house);
                }
                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                HouseAdapter houseAdapter = new HouseAdapter(houses);
                recyclerView.setAdapter(houseAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(MyPostsActivity.this));
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });

    }

}