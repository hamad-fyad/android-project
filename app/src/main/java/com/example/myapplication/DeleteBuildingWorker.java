package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteBuildingWorker extends Worker {
    private static final String TAG = "DeleteBuildingWorker";

    public DeleteBuildingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String buildingUid = getInputData().getString("building_uid");

        // Get building
        Utility.getBuilding(new Utility.BuildingCallBack() {
            @Override
            public void onBuildingReceived(Buildings building) {
                // Check if building has been marked as sold or not sold
                if (!building.isSold()) {
                    deleteBuildingFromDatabase(buildingUid);
                }
            }

            @Override
            public void onError(Exception e) {
                // handle error
            }
        }, buildingUid);
        
        return Result.success();
    }

    private void deleteBuildingFromDatabase(String buildingUid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Buildings")
            .document(buildingUid)
            .delete()
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Building successfully deleted!");
            })
            .addOnFailureListener(e -> {
                Log.w(TAG, "Error deleting building", e);
            });
    }
}
