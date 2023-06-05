package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

public class PostUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String buildingId = intent.getStringExtra("buildingId");
        String action = intent.getStringExtra("action");

        if (action.equals("sold")) {
            deleteBuilding(buildingId);
        } else if (action.equals("notSold")) {
            // TODO: 31/05/2023 make the statistics here if not sold or if sold check it
            // You can update the timestamp here if you want
        }
    }

    private void deleteBuilding(String buildingId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Buildings").document(buildingId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("PostUpdateReceiver", "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w("PostUpdateReceiver", "Error deleting document", e));
    }
}
