package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class CheckSoldStatusWorker extends Worker {
    private static final String TAG = "CheckSoldStatusWorker";
    public CheckSoldStatusWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    @NonNull
    @Override
    public Result doWork() {
        checkPostsDaily();
        return Result.success();
    }
    private void checkPostsDaily() {
        Calendar threeMonthsAgo = Calendar.getInstance();
        threeMonthsAgo.add(Calendar.MONTH, -3);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Buildings")
                .whereLessThan("timestamp", threeMonthsAgo.getTime())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()&&task.getResult().size()>0) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Buildings building = document.toObject(Buildings.class);
                            sendNotification(building);
                        }
                    } else {
                        Log.w(TAG, "Error checking posts.", task.getException());
                    }
                });
    }

    private void sendNotification(Buildings building) {
        String channelId = "com.example.myapplication";
        String channelName = "CheckSoldStatus";
        scheduleBuildingDeletion(building.getUid());
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        Intent resultIntent = new Intent(getApplicationContext(), NotificationReceiverActivity.class);
        resultIntent.putExtra("building_uid", building.getUid());
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Check Sold Status")
                .setContentText("Your post for " + building.getTypeofbuilding() + " has been published for 3 months. Is it sold?\n"+"the building will be deleted in week if you don't respond")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);
        int notificationId = building.getUid().hashCode();
        notificationManager.notify(notificationId, builder.build());
    }
    private void scheduleBuildingDeletion(String buildingUid) {
        // Schedule deletion in 1 week
        OneTimeWorkRequest deletionRequest = new OneTimeWorkRequest.Builder(DeleteBuildingWorker.class)
                .setInputData(new Data.Builder().putString("building_uid", buildingUid).build())
                .setInitialDelay(7, TimeUnit.DAYS)
                .build();
        WorkManager.getInstance(getApplicationContext()).enqueue(deletionRequest);
    }
}
