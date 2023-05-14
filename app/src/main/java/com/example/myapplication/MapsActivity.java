package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityMapsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
final int R1=6371;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        // Add a marker in Sydney and move the camera
//        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
//        CollectionReference doc=firestore.collection("users");
//                Query query=doc.whereEqualTo("LookingForWork",true);
//                query.get().addOnCompleteListener(task -> {
//                    if (task.isSuccessful()){
//                        ArrayList<User> workers=new ArrayList<>();
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            workers.add(document.toObject(User.class));
//                        }
//                        LatLng []worker=new LatLng[workers.size()];
//                        int i=0;
//                        for (User user : workers) {
//                            worker[i]=new LatLng(user.getLatitude(),user.getLongitude());
//                        }
//
//
//                    }
//                    else {
//                        Log.w(TAG, "Error getting documents.", task.getException());
//                   Utility.getUser(new Utility.UserCallback() {
//                       @Override
//                       public void onUserReceived(User user) {
//                           LatLng sydney = new LatLng(32, 151);
//                       }
//
//                       @Override
//                       public void onError(Exception e) {
//
//                       }
//                   });
//                    }
//                });

        LatLng sydney = new LatLng(32.98449634833544, 35.24621557788863);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Reset the values in Firestore when the app goes into the background
        Map<String, Object> updates = new HashMap<>();
        updates.put("lookingforservice", false);
        updates.put("latitude", null);
        updates.put("longitude", null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());

        docRef
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }

}

