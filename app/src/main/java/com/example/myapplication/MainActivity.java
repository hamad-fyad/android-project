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

import com.example.myapplication.CHAT.ChatRoomActivity;
import com.example.myapplication.Utilitys.TypoFixer;
import com.example.myapplication.Utilitys.Utility;
import com.example.myapplication.adapters.HouseAdapter;
import com.example.myapplication.behindthecurtains.CheckSoldStatusWorker;
import com.example.myapplication.classes.Buildings;
import com.example.myapplication.classes.SearchStats;
import com.example.myapplication.classes.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    private SearchView searchView;
    private Button Add_Post;
    private TypoFixer typoFixer;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private BottomNavigationView bottomNavigationView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Buildings> posts=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Add_Post=findViewById(R.id.add_post);
        Add_Post.setOnClickListener(v->startActivity(new Intent(MainActivity.this,AddBuildingActivity.class)));
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.browsing);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        this.typoFixer = new TypoFixer();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TypoFixer typoFixer = new TypoFixer(); // Create a new instance of TypoFixer
        db.collection("Words")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Object dictionaryValue = document.get("dictionary");
                            if (dictionaryValue instanceof String) {
                                String dictionary = (String) dictionaryValue;
                                typoFixer.addWords(Collections.singletonList(dictionary));
                            } else if (dictionaryValue instanceof List) {
                                List<String> dictionaryList = (List<String>) dictionaryValue;
                                typoFixer.addWords(dictionaryList);
                            }
                        }
                    } else {
                        Log.d(TAG, "onComplete: failed", task.getException());
                    }
                });
        // Utility.showToast(this,"click on the post to open chat with the owner");
        PeriodicWorkRequest checkSoldStatusWork =
                new PeriodicWorkRequest.Builder(CheckSoldStatusWorker.class,24 , TimeUnit.HOURS)
                        .build();
        WorkManager.getInstance(this).enqueue(checkSoldStatusWork);
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
                getTopSearchedBuildings();
                // This will hide the refresh indicator
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        searchView = findViewById(R.id.searchView);
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()){
                    getTopSearchedBuildings();
                }
                    String fixedQuery = typoFixer.fixTypos(query.toLowerCase());
                    Log.d(TAG, "onQueryTextSubmit: " + fixedQuery);
                    // Check if the same search term has been submitted before
                isPreviousSearchTerm(fixedQuery, exists -> {
                    if (exists) {
                        // Your search term exists, perform your desired operations here
                        incrementSearchTermCount(fixedQuery);
                    } else {
                        // Your search term doesn't exist, perform your desired operations here
                        addNewSearchTerm(fixedQuery);
                    }
                });
                searchBuildings(fixedQuery);
                    return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() > 5){
                    Log.d(TAG, "onQueryTextSubmit: "+typoFixer.fixTypos(newText));
                    posts.clear();
                    showBuildings(posts);
                    searchBuildings(typoFixer.fixTypos(newText));
                }else if (newText.isEmpty()||newText.length()==0) {
                    getTopSearchedBuildings();
                }else {
                    getBuildings();
                }
                return false;
            }
        });
        posts.clear();
        showBuildings(posts);
        getTopSearchedBuildings();
        getBuildings();
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
                Query query = housesRef.whereEqualTo("address", address.toLowerCase().trim());
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Buildings> closeBuildings = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Buildings house = document.toObject(Buildings.class);
                            if (!house.isSold())
                                closeBuildings.add(house);
                        }
                        Query query2 = housesRef.whereNotEqualTo("address", address.toLowerCase().trim());
                        query2.get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task1.getResult()) {
                                    Buildings house = document.toObject(Buildings.class);
                                    if (!house.isSold()){
                                        closeBuildings.add(house);
                                    }
                                }
                                if (!closeBuildings.isEmpty()) {
                                    posts.clear();
                                    showBuildings(posts);
                                    showBuildings(closeBuildings);
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
        // Retrieve and show the top searched buildings
        getTopSearchedBuildings();
    }
    private void getTopSearchedBuildings() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference searchStatsRef = db.collection("searchStats");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        posts.clear();
        showBuildings(posts);
        Log.d(TAG, "getTopSearchedBuildings: "+posts.size());
        if (currentUser != null) {
            // Retrieve the top 1 most searched terms
            Query query = searchStatsRef
                    .whereEqualTo("userId", currentUser.getUid())
                    .orderBy("count", Query.Direction.DESCENDING)
                    .limit(1);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if(task.getResult().size()>0){
                    List<Buildings> topSearchedBuildings = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String search = document.getString("searchTerm");
                        Log.d(TAG, "getTopSearchedBuildings: "+search+" "+document.getLong("count"));
                        searchBuildings(search);
                    }
                    }else {
                            getBuildings();
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            });
        } else {
            Log.w(TAG, "Error: user not signed in.");
        }
    }

    private void searchBuildings(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return;
        }
        int maxSize = Integer.MAX_VALUE;
        int maxPrice = Integer.MAX_VALUE;
        String address = "", typeofbuilding = "", type = "";
        // Trim leading and trailing whitespace and replace multiple spaces with single space
        searchText = searchText.trim().replaceAll("\\s+", " ");
        String[] searchTerms = searchText.split(" ");
        for (int i = 0; i < searchTerms.length - 1; i++) {
            String term = searchTerms[i].toLowerCase();
            String value = searchTerms[i + 1];
            switch (term) {
                case "size":
                    value = removeChars(value);
                    maxSize = Integer.parseInt(value);
                    break;
                case "price":
                    value = removeChars(value);
                    maxPrice = Integer.parseInt(value);
                    break;
                case "address":
                    address = value;
                    break;
                case "type":
                    type = value;
                    break;
            }
        }
        getBuildings(maxPrice, maxSize, address, type);
    }
    private String removeChars(String str) {
        return str.replaceAll("[^\\d]", "");
    }
    private void getBuildings() {
        getBuildings( Integer.MAX_VALUE, Integer.MAX_VALUE,"","");
    }
    private void getBuildings(int maxPrice, int maxSize, String address,String type) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference housesRef = db.collection("Buildings");
        Query query = housesRef.whereLessThanOrEqualTo("price", maxPrice) ;
        if (!address.isEmpty()) {
            query = query.whereEqualTo("address", address.toLowerCase());
        }
        if (!type.isEmpty()) {
            query = query.whereEqualTo("type", type.toLowerCase());
        }
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Buildings> houses = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Buildings house = document.toObject(Buildings.class);
                    // Filter by size locally
                    if(house.getSize() <= maxSize && !house.isSold()){
                        houses.add(house);
                    }
                }
                posts=houses;
                showBuildings(posts);
                if(posts.size()>=0){
                    getRestoftheBuildings(posts);
                    Log.d(TAG, "getBuildings: "+posts.size());
                }
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
    }
    private void getRestoftheBuildings(List<Buildings> posts) {
        db.collection("Buildings").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Buildings house = document.toObject(Buildings.class);
                    if(!posts.contains(house)&&!house.isSold()){
                        posts.add(house);
                    }
                    }
                Log.d(TAG, "getRestoftheBuildings: "+posts.size());
                Log.d(TAG, "getRestoftheBuildings: "+posts.get(0).getAddress());
                showBuildings(posts);
            }
            else {
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
    private void isPreviousSearchTerm(String searchTerm, OnSearchTermCheckListener listener) {
        // Check if the search term exists in the searchStats collection
        Query query = db.collection("searchStats").whereEqualTo("searchTerm", searchTerm).whereEqualTo("userId",FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                listener.onComplete(true);
            } else {
                listener.onComplete(false);
            }
        });
    }
    public interface OnSearchTermCheckListener {
        void onComplete(boolean exists);
    }
    private void incrementSearchTermCount(String searchTerm) {
        // Get the document reference for the existing search term
        db.collection("searchStats")
                .whereEqualTo("searchTerm", searchTerm).whereEqualTo("userId",FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String documentId = documentSnapshot.getId();
                        long count = documentSnapshot.getLong("count");
                        // Increment the count of the existing search term by 1
                        db.collection("searchStats")
                                .document(documentId)
                                .update("count", count + 1)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Search term count incremented"))
                                .addOnFailureListener(e -> Log.w(TAG, "Error incrementing search term count", e));
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error retrieving search term document", e));
    }
    private void addNewSearchTerm(String searchTerm) {
        // Assume you have user ID here, get it from current logged-in user
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        long timestamp = System.currentTimeMillis() / 1000;
        SearchStats stats = new SearchStats(userId, searchTerm, timestamp, 1);
        db.collection("searchStats")
                .add(stats)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "New search term added"))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding new search term", e));
    }
}