package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.classes.SearchStats;
import com.example.myapplication.classes.User;
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

public class StatisticsView extends AppCompatActivity {

    private BarChart chart;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private long SellTimeSum,SoldCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statisticsview);
        chart = findViewById(R.id.chart);
        getAvgTime();
    }
    private void getAvgTime() {
      Utility.getUser(new Utility.UserCallback() {
          @Override
          public void onUserReceived(User user) {
              SellTimeSum=user.getSellTimeSum();
              SoldCount=user.getSoldCount();
              displaySearchStats();
          }
          @Override
          public void onError(Exception e) {
              Log.d(TAG, "onError: error getting the document ");
          }
      });
    }
    private void displaySearchStats() {
        // Calculate average sell time
        // Set chart properties
        float avgSellTime=0;
        if (SoldCount!=0 && SellTimeSum!=0) {
            avgSellTime = (float) SellTimeSum / SoldCount;
        }else {
            Utility.showToast(this,"you haven't sold anything yet");
            return;
        }
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.setMaxVisibleValueCount(50);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(true);
        chart.getDescription().setEnabled(false); // Disable chart description
        // Create a list of bar entries
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, SoldCount)); // Entry for SoldCount
        entries.add(new BarEntry(1, avgSellTime)); // Entry for avgSellTime

        // Create a BarDataSet with entries
        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(getDefaultColors()); // Set custom colors
        dataSet.setDrawValues(true); // Show values on top of bars

        BarData barData = new BarData(dataSet);
        barData.setValueTextSize(10f);
        barData.setBarWidth(0.9f); // set custom bar width

        chart.setData(barData);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // Refresh chart

        // Set X-axis labels
        String[] labels = new String[]{"Sold Count", "Average Sell Time"};
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Set the minimum interval for the X-axis labels
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(10f);

        // Set Y-axis properties
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
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
        colors.add(Color.parseColor("#FF5722"));
        colors.add(Color.parseColor("#E91E63"));
        colors.add(Color.parseColor("#3F51B5"));
        colors.add(Color.parseColor("#3F51B5"));
        colors.add(Color.parseColor("#3341B5"));
        colors.add(Color.parseColor("#AF51B5"));
        colors.add(Color.parseColor("#BF21B5"));
        colors.add(Color.parseColor("#3F31B5"));
        colors.add(Color.parseColor("#DF54B5"));
        colors.add(Color.parseColor("#3F51B5"));
        colors.add(Color.parseColor("#FF91A5"));
        colors.add(Color.parseColor("#AA51B5"));
        return colors;
    }
}
