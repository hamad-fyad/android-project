package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private SearchView searchView;
    private Button Add_Post;
    private TypoFixer typoFixer;
    private BottomNavigationView bottomNavigationView;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Add_Post=findViewById(R.id.add_post);
        Add_Post.setOnClickListener(v->startActivity(new Intent(MainActivity.this,AddBuildingActivity.class)));
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.browsing);
         swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        List<String> dictionary = new ArrayList<>();
        //here i used this because some words would have some problem and would be diffrent
        dictionary.addAll(Arrays.asList(
                "address","agreement","price", "apartment","yanouh","yarka","bet jen","kermail","haifa","tel aviv",
                 "assessment", "asset","balcony", "bathroom", "bedroom", "bill", "block", "building",
                "capital", "charge", "city", "client", "commission", "community", "contract", "cost", "county",
                "deposit", "design", "discount", "downtown", "equity", "estate", "eviction", "excellent", "expensive",
                "garage", "garden", "ground", "home", "house","private house","appartment","penthouse","Garden Appartment","studio",
                "housing", "improvement", "income", "inspection", "interest",
                "location",  "maintenance", "management", "market",
                "neighborhood", "offer", "office", "property", "rental","villa",
                "residential","room","service","story","street","structure","suburb","town","value","village"
        ));

        this.typoFixer = new TypoFixer(dictionary);

        // Utility.showToast(this,"click on the post to open chat with the owner");

        //todo activity when there is timestamp in the buildings in the firestore
//        PeriodicWorkRequest checkSoldStatusWork =
//                new PeriodicWorkRequest.Builder(CheckSoldStatusWorker.class, 24, TimeUnit.HOURS)
//                        // Constraints
//                        .build();
//
//        WorkManager.getInstance(this).enqueue(checkSoldStatusWork);


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
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh your data here...
                getCloseBuildings();

                // This will hide the refresh indicator
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        searchView = findViewById(R.id.searchView);
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: "+typoFixer.fixTypos(query) );
                searchBuildings(typoFixer.fixTypos(query));
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextSubmit: "+typoFixer.fixTypos(newText) );
                searchBuildings(typoFixer.fixTypos(newText));
                return false;
            }
        });

        getCloseBuildings();
    }
    private void getCloseBuildings() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference housesRef = db.collection("Buildings");
        final User[] currentuser = {new User()};
        Utility.getUser(new Utility.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                currentuser[0] = user;
                String address = currentuser[0].getAddress();
                Query query = housesRef.whereEqualTo("address", address.toLowerCase().trim()).whereEqualTo("isSold", false);
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Buildings> CloseBuildings = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Buildings house = document.toObject(Buildings.class);
                            CloseBuildings.add(house);
                        }
                        Query query2 = housesRef.whereNotEqualTo("address", address.toLowerCase().trim()).whereEqualTo("isSold", false);
                        query2.get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task1.getResult()) {
                                    Buildings house = document.toObject(Buildings.class);
                                    CloseBuildings.add(house);
                                }
                                if (!CloseBuildings.isEmpty()) {
                                    showBuildings(CloseBuildings);
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

    // TODO: 13/06/2023 add the types of buildings  
    private void searchBuildings(String searchText) {
        int maxSize = Integer.MAX_VALUE;
        int maxPrice = Integer.MAX_VALUE;
        String address = "";

        // Trim leading and trailing whitespace and replace multiple spaces with single space
        searchText = searchText.trim().replaceAll("\\s+", " ");

        String[] searchTerms = searchText.split(" ");
        for (int i = 0; i < searchTerms.length - 1; i++) {
            String term = searchTerms[i].toLowerCase();
            String value = searchTerms[i + 1];

            if (term.equals("size")) {
                try {
                    value = removeChars(value);
                    maxSize = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else if (term.equals("price")) {
                try {
                    value = removeChars(value);
                    maxPrice = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }catch (Exception e){
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

        Query query = housesRef.whereLessThanOrEqualTo("price", maxPrice).whereEqualTo("isSold", false);

        if (!address.isEmpty()) {
            query = query.whereEqualTo("address", address.toLowerCase());
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Buildings> houses = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Buildings house = document.toObject(Buildings.class);

                    // Filter by size locally
                    if (house.getSize() <= maxSize) {
                        houses.add(house);
                    }
                }
                showBuildings(houses);
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
    }

    private void showBuildings(List<Buildings> buildings) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        HouseAdapter houseAdapter = new HouseAdapter(buildings);
        recyclerView.setAdapter(houseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }


}