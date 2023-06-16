package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.classes.SearchStats;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.formatter.ValueFormatter;

import com.example.myapplication.R;
import com.example.myapplication.Utilitys.Utility;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;
import java.util.Map;

// TODO: 15/06/2023 make it show in the charts 
public class StatisticsView extends AppCompatActivity {

    private BarChart chart;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private Map<String, Long> searchCounts = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statisticsview);

        chart = findViewById(R.id.chart);
        // Set chart properties
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.setMaxVisibleValueCount(50);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(true);
        chart.getDescription().setEnabled(false); // Disable chart description

        db = FirebaseFirestore.getInstance();
        getSearchStats();
    }

    private void getSearchStats() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user's id

        db.collection("searchStats")
                .whereEqualTo("userId", userId) // Filter documents by userId
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            SearchStats stat = document.toObject(SearchStats.class);

                            // Check if search term and count is not null
                            if (stat.getSearchTerm() != null && stat.getCount()!=0) {
                                // Insert or update the count for the search term in searchCounts map
                                searchCounts.put(stat.getSearchTerm(), stat.getCount());
                            }
                        }

                        // Display the search stats in the chart
                        displaySearchStats();
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    private void displaySearchStats() {
        List<String> searchTerms = new ArrayList<>();
        List<BarEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Long> entry : searchCounts.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) { // Check if key and value are not null
                searchTerms.add(entry.getKey());
                entries.add(new BarEntry(searchTerms.size() - 1, entry.getValue()));
            }
        }


        BarDataSet dataSet = new BarDataSet(entries, "Search Counts");
        dataSet.setColors(getDefaultColors()); // Set custom colors
        dataSet.setDrawValues(true); // Show values on top of bars

        BarData barData = new BarData(dataSet);
        barData.setValueTextSize(10f);
        barData.setBarWidth(0.9f); // set custom bar width

        chart.setData(barData);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // Refresh chart

        // Set X-axis labels
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(searchTerms));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Set the minimum interval for the X-axis labels
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(10f);

        // Set Y-axis properties
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0f); // Set the minimum value
        yAxis.setTextColor(Color.BLACK);
        yAxis.setTextSize(10f);
        chart.getAxisRight().setEnabled(false);

        // Customize legend
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);
    }

    private List<Integer> getDefaultColors() {
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FF5722")); // Example color 1
        colors.add(Color.parseColor("#E91E63")); // Example color 2
        colors.add(Color.parseColor("#3F51B5")); // Example color 3
        colors.add(Color.parseColor("#3F51B5"));
        colors.add(Color.parseColor("#3341B5"));
        colors.add(Color.parseColor("#AF51B5"));
        colors.add(Color.parseColor("#BF21B5"));
        colors.add(Color.parseColor("#3F31B5"));
        colors.add(Color.parseColor("#DF54B5"));
        colors.add(Color.parseColor("#3F51B5"));
        colors.add(Color.parseColor("#FF91A5"));
        colors.add(Color.parseColor("#AA51B5"));




        // Add more colors as needed
        return colors;
    }
}
