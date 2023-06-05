package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class NotificationReceiverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_receiver);

        String buildingUid = getIntent().getStringExtra("building_uid");
Utility.getBuilding(new Utility.BuildingCallBack() {
    @Override
    public void onBuildingReceived(Buildings building) {

    }

    @Override
    public void onError(Exception e) {

    }
},buildingUid);
        Button soldButton = findViewById(R.id.btn_sold);
        soldButton.setOnClickListener(v -> {
            // handle "Sold" click
            markBuildingAsSold(buildingUid);
        });

        Button notSoldButton = findViewById(R.id.btn_not_sold);
        notSoldButton.setOnClickListener(v -> {
            // handle "Not Sold" click
            markBuildingAsNotSold(buildingUid);
        });
    }

    private void markBuildingAsSold(String buildingUid) {
        // TODO: Implement the logic to mark the building as sold in your database
    }

    private void markBuildingAsNotSold(String buildingUid) {
        // TODO: Implement the logic to mark the building as not sold in your database
    }
}
