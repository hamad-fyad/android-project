package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

// TODO: 13/06/2023 see where the action intent  
public class PostUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String buildingId = intent.getStringExtra("buildingId");
        String action = intent.getStringExtra("action");

        if (action.equals("sold")) {
            updateStatisticsAndMarkBuildingAsSold(buildingId);
        } else if (action.equals("notSold")) {
            // TODO: handle "notSold" action

        }
    }

    private void updateStatisticsAndMarkBuildingAsSold(String buildingId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = db.collection("Users").document(userId);
        DocumentReference buildingRef = db.collection("Buildings").document(buildingId);

        Task<DocumentSnapshot> getUserTask = userRef.get();
        Task<DocumentSnapshot> getBuildingTask = buildingRef.get();

        Tasks.whenAllSuccess(getUserTask, getBuildingTask).addOnSuccessListener(results -> {
            DocumentSnapshot userSnapshot = (DocumentSnapshot) results.get(0);
            DocumentSnapshot buildingSnapshot = (DocumentSnapshot) results.get(1);

            long sellTimeSum = userSnapshot.getLong("sellTimeSum");
            long soldCount = userSnapshot.getLong("soldCount");
            Timestamp listedTimestamp = buildingSnapshot.getTimestamp("listedTimestamp");
            Timestamp soldTimestamp = Timestamp.now();
            long sellTime = soldTimestamp.getSeconds() - listedTimestamp.getSeconds();

            sellTimeSum += sellTime;
            soldCount++;

            userRef.update(
                    "sellTimeSum", sellTimeSum,
                    "soldCount", soldCount
            ).addOnSuccessListener(aVoid -> {
                Log.d("PostUpdateReceiver", "Statistics successfully updated!");
                markBuildingAsSold(buildingId); // Updating the building's sold status
            }).addOnFailureListener(e -> Log.w("PostUpdateReceiver", "Error updating statistics", e));
        }).addOnFailureListener(e -> Log.w("PostUpdateReceiver", "Error getting documents", e));
    }

    private void markBuildingAsSold(String buildingId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Buildings").document(buildingId)
                .update("sold", true)
                .addOnSuccessListener(aVoid -> {
                    Log.d("PostUpdateReceiver", "Building successfully marked as sold!");
                    decrementUserCount(FirebaseAuth.getInstance().getCurrentUser().getUid());
                })
                .addOnFailureListener(e -> Log.w("PostUpdateReceiver", "Error marking building as sold", e));
    }


    private void decrementUserCount(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.update("count", FieldValue.increment(-1))
                .addOnSuccessListener(aVoid -> Log.d("PostUpdateReceiver", "Count successfully decremented!"))
                .addOnFailureListener(e -> Log.w("PostUpdateReceiver", "Error decrementing count", e));
    }
}
