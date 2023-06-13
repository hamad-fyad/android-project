package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;

public class NotificationReceiverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_receiver);
        Utility.showToast(this,"you have to choose one or it will not cancel the the deleting of the building ");
        String buildingUid = getIntent().getStringExtra("building_uid");

        Button soldButton = findViewById(R.id.btn_sold);
        soldButton.setOnClickListener(v -> {
            // handle "Sold" click
            markBuildingAsSold(buildingUid);
            cancelBuildingDeletion(buildingUid);

        });

        Button notSoldButton = findViewById(R.id.btn_not_sold);
        notSoldButton.setOnClickListener(v -> {
            // handle "Not Sold" click
            markBuildingAsNotSold(buildingUid);
            cancelBuildingDeletion(buildingUid);
        });
    }

    private void markBuildingAsSold(String buildingUid) {
        Utility.getBuilding(new Utility.BuildingCallBack() {
            @Override
            public void onBuildingReceived(Buildings building) {
                building.setSold(true);
                building.setSellDate(new Date());  // Current date
                saveBuildingToDatabase(building,1, "sold");  // Save changes to database
            }

            @Override
            public void onError(Exception e) {
                // handle error
                Utility.showToast(NotificationReceiverActivity.this,"no internet check you connection please ");
            }
        }, buildingUid);
    }

    private void markBuildingAsNotSold(String buildingUid) {
        Utility.getBuilding(new Utility.BuildingCallBack() {
            @Override
            public void onBuildingReceived(Buildings building) {
                building.setSold(false);
                saveBuildingToDatabase(building,1, "notSold");  // Save changes to database
            }

            @Override
            public void onError(Exception e) {
                // handle error
            }
        }, buildingUid);
    }

    private void saveBuildingToDatabase(Buildings building, int attempt, String action) {
        if (attempt > 3) {
            Utility.showToast(this,"Failed to update the building after several attempts. Please check your internet connection.");
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Buildings")
                .document(building.getUid())
                .set(building, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Building successfully updated!");
                    Utility.showToast(this,"Thanks, everything is updated.");
                    sendBroadcast(action, building.getUid());
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating building", e);
                    Utility.showToast(this,"Error occurred. Trying again...");

                    // Retry saving to database
                    saveBuildingToDatabase(building, attempt + 1, action);
                });
    }

    private void cancelBuildingDeletion(String buildingUid) {
        WorkManager.getInstance(this).cancelAllWorkByTag(buildingUid);
    }

    private void sendBroadcast(String action, String buildingId) {
        Intent intent = new Intent();
        intent.setAction("com.example.myapplication.BUILDING_UPDATE_ACTION");
        intent.putExtra("action", action);
        intent.putExtra("buildingId", buildingId);
        this.sendBroadcast(intent);
    }
}
