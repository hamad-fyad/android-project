package com.example.myapplication.Utilitys;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.myapplication.classes.Buildings;
import com.example.myapplication.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Utility {
    public interface BuildingCallBack{
        void onBuildingReceived(Buildings building);
        void onError(Exception e);
    }
    public static void getBuilding(BuildingCallBack callBack, String buildingUid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Buildings")
                .document(buildingUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Buildings building = documentSnapshot.toObject(Buildings.class);
                        callBack.onBuildingReceived(building);
                    } else {
                        callBack.onError(new Exception("No such building exists"));
                    }
                })
                .addOnFailureListener(callBack::onError);
    }

    public static Location getCurrentLocation(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions not granted
            return null;
        }
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        return locationManager.getLastKnownLocation(bestProvider);
    }

    public static void showToast(Context context, String message) {
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
               User user=documentSnapshot.toObject(User.class);
                callback.onUserReceived(user);
            } else {
                Log.d(TAG, "No such document");
                callback.onError(new Exception("No such document"));
            }
        }).addOnFailureListener(callback::onError);
    }

    public static void getUser(UserCallback callback,String id ) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(id).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                   User user=documentSnapshot.toObject(User.class);
                callback.onUserReceived(user);
            } else {
                Log.d(TAG, "No such document");
                callback.onError(new Exception("No such document"));
            }
        }).addOnFailureListener(callback::onError);
    }


}
