package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

// TODO: 15/06/2023 make it show in the charts 
public class StatisticsView extends AppCompatActivity {

    private BarChart chart;

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

        showAverageSellTimeGraph();
    }

    public void showAverageSellTimeGraph() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference buildingsRef = db.collection("Buildings");

        Query query = buildingsRef.whereEqualTo("sold", true).whereEqualTo("useruid", userId);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                if (documents.isEmpty()) {
                    Log.d(TAG, "No sold buildings found.");
                } else {
                    List<Date> sellDates = new ArrayList<>();
                    List<Date> postCreatedDates = new ArrayList<>();

                    for (DocumentSnapshot document : documents) {
                        Date sellDate = document.getDate("sellDate");
                        Date postCreatedDate = document.getDate("postCreatedDate");
                        if (sellDate != null && postCreatedDate != null) {
                            sellDates.add(sellDate);
                            postCreatedDates.add(postCreatedDate);
                        }
                    }

                    // Calculate average sell time for each month from the day the post was created
                    List<Float> avgSellTimes = calculateAverageSellTimes(sellDates, postCreatedDates);

                    // Create chart entries
                    List<BarEntry> entries = new ArrayList<>();
                    for (int i = 0; i < avgSellTimes.size(); i++) {
                        float avgSellTime = avgSellTimes.get(i);
                        entries.add(new BarEntry(i, avgSellTime));
                    }

                    BarDataSet dataSet = new BarDataSet(entries, "Average Sell Time");
                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS); // Set custom colors
                    dataSet.setDrawValues(true); // Show values on top of bars

                    BarData barData = new BarData(dataSet);
                    barData.setValueTextSize(10f);
                    barData.setBarWidth(0.9f); // set custom bar width

                    chart.setData(barData);
                    chart.setFitBars(true); // make the x-axis fit exactly all bars
                    chart.invalidate(); // Refresh chart

                    // Set X-axis labels
                    String[] months = getMonthNames();
                    XAxis xAxis = chart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setGranularity(1f); // Set the minimum interval for the X-axis labels
                    xAxis.setCenterAxisLabels(true);
                    xAxis.setAxisMinimum(0f);
                    xAxis.setAxisMaximum(0 + chart.getBarData().getGroupWidth(0.4f, 0.02f) * 12);
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
            } else {
                Log.w(TAG, "Error getting sold buildings.", task.getException());
            }
        });
    }



    private List<Float> calculateAverageSellTimes(List<Date> sellDates, List<Date> postCreatedDates) {
        List<Float> avgSellTimes = new ArrayList<>();

        // Group sell dates and post created dates by month
        List<List<Date>> sellDatesByMonth = groupDatesByMonth(sellDates);
        List<List<Date>> postCreatedDatesByMonth = groupDatesByMonth(postCreatedDates);

        // Calculate average sell time for each month
        long sellTimeSum = 0;
        int totalCount = 0;

        int numMonths = Math.min(sellDatesByMonth.size(), postCreatedDatesByMonth.size());

        for (int i = 0; i < numMonths; i++) {
            List<Date> sellDatesOfMonth = sellDatesByMonth.get(i);
            List<Date> postCreatedDatesOfMonth = postCreatedDatesByMonth.get(i);

            if (sellDatesOfMonth.isEmpty() || postCreatedDatesOfMonth.isEmpty()) {
                avgSellTimes.add(0f);// no data for this month, so the average is 0
                continue;
            }

            long sellTimeOfMonth = calculateTotalSellTime(sellDatesOfMonth, postCreatedDatesOfMonth);
            sellTimeSum += sellTimeOfMonth;

            totalCount += sellDatesOfMonth.size();

            float avgSellTime = (float) sellTimeSum / totalCount;
            avgSellTimes.add((float) totalCount);
        }

        return avgSellTimes;
    }


    private List<List<Date>> groupDatesByMonth(List<Date> dates) {
        List<List<Date>> datesByMonth = new ArrayList<>();

        // Initialize the list with 12 empty lists for each month
        for (int i = 1; i <= 12; i++) {
            datesByMonth.add(new ArrayList<>());
        }

        for (Date date : dates) {
            int month = getMonthFromDate(date);
            datesByMonth.get(month).add(date);
        }
        return datesByMonth;
    }


    private int getMonthFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    private long calculateTotalSellTime(List<Date> sellDates, List<Date> postCreatedDates) {
        long sellTimeSum = 0;
        for (int i = 0; i < sellDates.size(); i++) {
            Date sellDate = sellDates.get(i);
            Date postCreatedDate = postCreatedDates.get(i);
            long sellTime = sellDate.getTime() - postCreatedDate.getTime();
            sellTimeSum += sellTime;
        }
        return sellTimeSum;
    }

    private String[] getMonthNames() {
        String[] fullMonthNames = new DateFormatSymbols().getMonths();
        String[] shortMonthNames = new String[fullMonthNames.length];
        for (int i = 0; i < fullMonthNames.length; i++) {
            if (fullMonthNames[i].length() >= 3) {
                shortMonthNames[i] = fullMonthNames[i].substring(0, 3);
            } else {
                shortMonthNames[i] = fullMonthNames[i];
            }
        }
        return shortMonthNames;
    }

    private List<Integer> getDefaultColors() {
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FF5722")); // Example color 1
        colors.add(Color.parseColor("#E91E63")); // Example color 2
        colors.add(Color.parseColor("#3F51B5")); // Example color 3
        // Add more colors as needed
        return colors;
    }
}
