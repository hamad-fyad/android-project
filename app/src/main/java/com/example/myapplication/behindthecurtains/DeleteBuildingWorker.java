package com.example.myapplication.behindthecurtains;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.myapplication.Utilitys.Utility;
import com.example.myapplication.classes.Buildings;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
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
                    deleteBuildingFromDatabase(buildingUid);
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG, "onError: "+e.toString());
            }
        }, buildingUid);
        
        return Result.success();
    }

    private void deleteBuildingFromDatabase(String buildingUid) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref=db.collection("Buildings").document(buildingUid);
      ref.get().addOnSuccessListener(task-> decrementUserCount(task.getString("useruid"))).addOnFailureListener(task->{
          Log.d(TAG, "deleteBuildingFromDatabase: Error deleting building ");
          return;
      });
        db.collection("Buildings")
            .document(buildingUid)
            .delete()
            .addOnSuccessListener(aVoid -> Log.d(TAG, "Building successfully deleted!"))
            .addOnFailureListener(e -> Log.w(TAG, "Error deleting building", e));
    }
    private void decrementUserCount(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.update("buildingcount", FieldValue.increment(-1))
                .addOnSuccessListener(aVoid -> Log.d("PostUpdateReceiver", "Count successfully decremented!"))
                .addOnFailureListener(e -> Log.w("PostUpdateReceiver", "Error decrementing count", e));
    }
}
