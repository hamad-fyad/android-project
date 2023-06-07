package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityMapsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
   private ArrayList<User> workers=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = db.collection("users").document(user.getUid());

        // Check if permissions are granted
        Location location = Utility.getCurrentLocation(this);
        Map<String, Object> updates = new HashMap<>();

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            updates.put("lookingforservice", true);
            updates.put("latitude", latitude);
            updates.put("longitude", longitude);
        } else {
            Log.w(TAG, "No location available");
            updates.put("lookingforservice", true);
            updates.put("latitude", -1); //use default values when location is not available
            updates.put("longitude", -1);
        }

        docRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference doc = firestore.collection("users");
        Query query = doc.whereEqualTo("lookingForWork", true)
                .whereNotEqualTo("uid", user.getUid());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.w(TAG, "onMapReady task : "+task.getResult().size());
                for (QueryDocumentSnapshot document : task.getResult()) {
                    workers.add(document.toObject(User.class));
                }


                Utility.getUser(new Utility.UserCallback() {
                    @Override
                    public void onUserReceived(User user) {
                        LatLng current = new LatLng(user.getLatitude(), user.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(current).title("you are here "));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
                        Utility.showToast(MapsActivity.this,""+workers.size());
                        Log.w(TAG, "onUserReceived: "+workers.size() );
                        double userLat = user.getLatitude();
                        double userLon = user.getLongitude();


                        for (User user1 : workers) {
                            Utility.showToast(MapsActivity.this,"hello");
                            if (haversine(userLat, userLon, user1.getLatitude(), user1.getLongitude()) <= 50) {
                                LatLng userLocation = new LatLng(user1.getLatitude(), user1.getLongitude());
                                Marker marker = mMap.addMarker(new MarkerOptions().position(userLocation).title(user1.getName()));
                                // Store the user object in the marker
                                marker.setTag(user1);
                            }
                        }

                        // Set up the OnMarkerClickListener
                        mMap.setOnMarkerClickListener(marker -> {
                            User clickedUser = (User) marker.getTag();
                            if (clickedUser != null) {
                                String clickedUserId = clickedUser.getUid();  // Replace getUserId() with the correct method in your User class
                                DocumentReference clickedUserDoc = firestore.collection("users").document(clickedUserId);
                                clickedUserDoc.update("interestedUsers", FieldValue.arrayUnion(user.getUid()))
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                                        .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
                            }
                            return false;
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("didn't get the current users ", e.getMessage());
                    }
                });
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
        //here for testing because it doesn't show in the emulator
//        LatLng sydney = new LatLng(32.98449634833544, 35.24621557788863);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> updates = new HashMap<>();
        updates.put("lookingforservice", false);
        updates.put("latitude", -1);
        updates.put("longitude", -1);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());

        docRef
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> updates = new HashMap<>();
        updates.put("lookingforservice", false);
        updates.put("latitude", -1);
        updates.put("longitude", -1);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());

        docRef
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c;
    }

}

