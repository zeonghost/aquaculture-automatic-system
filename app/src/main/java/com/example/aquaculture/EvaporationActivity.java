package com.example.aquaculture;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EvaporationActivity extends AppCompatActivity {
    private LineChart evapLineChart;
    private FirebaseDatabase myDatabase;
    private DatabaseReference evapRef;
    private String getData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaporation);
        evapLineChart = findViewById(R.id.lineChartEvaporationRate);
        getData = HomeActivity.transferData;
        myDatabase = FirebaseDatabase.getInstance();


        String evapPath = getData + "-evap";
        evapRef = myDatabase.getReference(evapPath);
    }
}
