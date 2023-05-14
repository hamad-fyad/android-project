package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Utility {
    public static Location getCurrentLocation(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions not granted
            return null;
        }

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }
    static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public interface UserCallback {
        void onUserReceived(User user);
        void onError(Exception e);
    }

    public static void getUser(UserCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();

        firestore.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                String address = documentSnapshot.getString("address");
                String number = documentSnapshot.getString("number");
                String photoURL = documentSnapshot.getString("photoURL");
                long buildingcount = documentSnapshot.getLong("buildingcount");
                boolean lookingforwork = documentSnapshot.getBoolean("LookingForWork");
                User user = new User(name, address, email, number, photoURL, buildingcount, lookingforwork);
                callback.onUserReceived(user);
            } else {
                Log.d(TAG, "No such document");
                callback.onError(new Exception("No such document"));
            }
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error getting user data", e);
            callback.onError(e);
        });
    }

}
