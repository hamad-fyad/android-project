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

public class PostUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String buildingId = intent.getStringExtra("buildingId");
        String action = intent.getStringExtra("action");

        if (action.equals("sold")) {
            updateStatisticsAndDeleteBuilding(buildingId);
        } else if (action.equals("notSold")) {
            // TODO: handle "notSold" action

        }
    }

    private void updateStatisticsAndDeleteBuilding(String buildingId) {
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
                deleteBuilding(buildingId);
            }).addOnFailureListener(e -> Log.w("PostUpdateReceiver", "Error updating statistics", e));
        }).addOnFailureListener(e -> Log.w("PostUpdateReceiver", "Error getting documents", e));
    }

    private void deleteBuilding(String buildingId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Buildings").document(buildingId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("PostUpdateReceiver", "DocumentSnapshot successfully deleted!");
                    decrementUserCount(FirebaseAuth.getInstance().getCurrentUser().getUid());
                })
                .addOnFailureListener(e -> Log.w("PostUpdateReceiver", "Error deleting document", e));
    }

    private void decrementUserCount(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.update("count", FieldValue.increment(-1))
                .addOnSuccessListener(aVoid -> Log.d("PostUpdateReceiver", "Count successfully decremented!"))
                .addOnFailureListener(e -> Log.w("PostUpdateReceiver", "Error decrementing count", e));
    }
}
