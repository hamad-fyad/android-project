package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//todo make this page update once every couple of minutes
public class MainActivity extends AppCompatActivity {
    private SearchView searchView;
    private Button Add_Post;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Add_Post=findViewById(R.id.add_post);
        Add_Post.setOnClickListener(v->startActivity(new Intent(MainActivity.this,AddBuildingActivity.class)));
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.browsing);

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
        searchView = findViewById(R.id.searchView);
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBuildings(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchBuildings(newText);
                return false;
            }
        });

        // Get buildings initially without any search query
        // getBuildings(Integer.MAX_VALUE, Integer.MAX_VALUE, "");
        getCloseBuildings();
    }
    //todo fix the duplicate that are add change it not possiable
    //todo make the posts clickable
    private void getCloseBuildings() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference housesRef = db.collection("Buildings");
        final User[] currentuser = {new User()};
        Utility.getUser(new Utility.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                currentuser[0] = user;
                String address = currentuser[0].getAddress();
                Query query = housesRef.whereEqualTo("address", address.toLowerCase().trim());
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Buildings> CloseBuildings = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Buildings house = document.toObject(Buildings.class);
                            CloseBuildings.add(house);
                        }
                        Query query2 = housesRef.whereNotEqualTo("address", address.toLowerCase().trim());
                        query2.get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task1.getResult()) {
                                    Buildings house = document.toObject(Buildings.class);
                                    CloseBuildings.add(house);
                                }
                                if (!CloseBuildings.isEmpty()) {
                                    RecyclerView recyclerView = findViewById(R.id.recyclerView);
                                    HouseAdapter houseAdapter = new HouseAdapter(CloseBuildings);
                                    recyclerView.setAdapter(houseAdapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task1.getException());
                            }
                        });
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.w(TAG, "Error getting user data", e);
            }
        });
    }

    private void searchBuildings(String searchText) {
        int maxSize = Integer.MAX_VALUE;
        int maxPrice = Integer.MAX_VALUE;
        String address = "";
        //todo make another loop that ignores the white marks
        String[] searchTerms = searchText.split(" ");
        for (int i = 0; i < searchTerms.length - 1; i++) {

            String term = searchTerms[i].toLowerCase();
            String value = searchTerms[i + 1];

            if (term.equals("size")) {
                try {
                    value=removeChars(value);
                    maxSize = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if (term.equals("price")) {
                try {
                    value=removeChars(value);
                    maxPrice = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if (term.equals("address")) {
                address = value;
            }
        }
        getBuildings(maxPrice, maxSize, address);
    }

    private String removeChars(String str) {
        return str.replaceAll("[^\\d]", "");
    }

    private void getBuildings() {
        getBuildings( Integer.MAX_VALUE, Integer.MAX_VALUE, "");
    }

    private void getBuildings(int maxPrice, int maxSize, String address) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference housesRef = db.collection("Buildings");

        Query query = housesRef.whereLessThanOrEqualTo("price", maxPrice);

        if (!address.isEmpty()) {
            query = query.whereEqualTo("address", address.toLowerCase());
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Buildings> houses = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Buildings house = document.toObject(Buildings.class);

                    // Filter by size locally
                    if ( house.getSize() <= maxSize) {
                        houses.add(house);
                    }
                }
                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                HouseAdapter houseAdapter = new HouseAdapter(houses);
                recyclerView.setAdapter(houseAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });

    }


}