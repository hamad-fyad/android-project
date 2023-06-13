package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class StatisticsView extends AppCompatActivity {

    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statisticsview);

        chart = findViewById(R.id.chart);

        showAverageSellTimeGraph();
    }

    public void showAverageSellTimeGraph() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = db.collection("Users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<Long> sellTimes = (List<Long>) document.get("sellTimes");
                    long sellTimeSum = 0;
                    List<Entry> entries = new ArrayList<>();

                    for (int i = 0; i < sellTimes.size(); i++) {
                        sellTimeSum += sellTimes.get(i);
                        float avgSellTime = (float) sellTimeSum / (i + 1);
                        entries.add(new Entry(i, avgSellTime));
                    }

                    LineDataSet dataSet = new LineDataSet(entries, "Average Sell Time");
                    LineData lineData = new LineData(dataSet);

                    chart.setData(lineData);
                    chart.invalidate(); // refresh chart
                }
            }
        });
    }
}
