package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.fragment.app.FragmentActivity;

import com.example.myapplication.Utilitys.Utility;
import com.example.myapplication.classes.User;
import com.google.android.gms.maps.model.Circle;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityMapsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;
// TODO: 16/06/2023 fix the map code in whatsapp 

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Circle circle;
    private  Location location1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = db.collection("users").document(user.getUid());

        // Check if permissions are granted
     location1 = Utility.getCurrentLocation(this);
        Map<String, Object> updates = new HashMap<>();

        if (location1 != null) {
            double latitude = location1.getLatitude();
            double longitude = location1.getLongitude();

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


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference doc = firestore.collection("users");
        Query query = doc.whereEqualTo("lookingForWork", true)
                .whereNotEqualTo("uid", user.getUid());

        Map<String, Marker> markers = new HashMap<>();

        query.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            for (DocumentChange docChange : value.getDocumentChanges()) {
                User changedUser = docChange.getDocument().toObject(User.class);
                String uid = changedUser.getUid();
                LatLng location = new LatLng(changedUser.getLatitude(), changedUser.getLongitude());
                switch (docChange.getType()) {
                    case ADDED:
                        Log.d(TAG, "onMapReady: "+location1.getLatitude()+" "+location1.getLongitude());
                        double distance = haversine(location1.getLatitude(), location1.getLongitude(), changedUser.getLatitude(), changedUser.getLongitude());
                        if (distance <= 5) {
                            // If yes, add a marker for this user
                            Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(changedUser.getName()));
                            marker.setTag(changedUser);
                            markers.put(uid, marker);
                        }
                        break;

                    case MODIFIED:
                        markers.get(uid).setPosition(location);
                        markers.get(uid).setTitle(changedUser.getName());
                        markers.get(uid).setTag(changedUser);
                        break;
                    case REMOVED:
                        markers.get(uid).remove();
                        markers.remove(uid);
                        break;
                }
            }

            Utility.getUser(new Utility.UserCallback() {
                @SuppressLint("PotentialBehaviorOverride")
                @Override
                public void onUserReceived(User user12) {
                    LatLng current = new LatLng(user12.getLatitude(), user12.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(current).title("you are here "));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                    CircleOptions circleOptions = new CircleOptions()
                            .center(current)
                            .radius(5000) // Radius in meters (5 km)
                            .strokeWidth(2)
                            .strokeColor(Color.BLUE)
                            .fillColor(Color.parseColor("#80ADD8E6")); // Transparent blue color for the fill

                    circle = mMap.addCircle(circleOptions);

                    mMap.setOnMarkerClickListener(marker -> {
                        User clickedUser = (User) marker.getTag();
                        if (clickedUser != null) {
                            String clickedUserId = clickedUser.getUid();
                            DocumentReference clickedUserDoc = firestore.collection("users").document(clickedUserId);
                            clickedUserDoc.update("interestedUsers", FieldValue.arrayUnion(user12.getUid()))
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
        });
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
    //this function is for finding the dis between two users i used here the haversine formula
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

