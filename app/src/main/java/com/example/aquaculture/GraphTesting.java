package com.example.aquaculture;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;

import com.example.aquaculture.Model.Weather;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class GraphTesting extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener {
    private static final String TAG = "GraphTesting";
    private LineChart lineChartEvaporation;
    private FirebaseDatabase myDatabase;
    private DatabaseReference myReference;
    private ArrayList<Entry> yValuesEvap;
    private double depth;
    private Weather w;
    private double iEvap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_testing);
        lineChartEvaporation = findViewById(R.id.lineEvaporation);
        yValuesEvap = new ArrayList<>();
        myDatabase = FirebaseDatabase.getInstance();
        myReference = myDatabase.getReference("pi1-detail");
        w = new Weather();
        getEvaporationRate();
        getEvaporationGraph();
    }

    private void getEvaporationGraph(){
        lineChartEvaporation.setOnChartGestureListener(GraphTesting.this);
        lineChartEvaporation.setOnChartValueSelectedListener(GraphTesting.this);
        lineChartEvaporation.setDragEnabled(false);
        lineChartEvaporation.setEnabled(true);
        lineChartEvaporation.setPinchZoom(false);
        lineChartEvaporation.setDoubleTapToZoomEnabled(false);
        lineChartEvaporation.setHighlightPerTapEnabled(false);
        myReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //final double evapRate = convertMilliToMeter(0.011 * 30);
                final double evapRate = convertMilliToMeter(iEvap * 30);
                float count = 0;
                depth = dataSnapshot.child("depth").getValue(Double.class);
                Log.d(TAG, "onDataChange: DEPTH " + dataSnapshot.child("depth").getValue(Double.class));
                Log.d(TAG, "onDataChange: Evap Rate per Day: " + w.getEvaporationRate());
                Log.d(TAG, "onDataChange: Evap Rate Per Month : " + evapRate);


                while (depth > 0.459d){
                    Log.d(TAG, "onDataChange: Depth level " + depth);
                    depth -= evapRate;
                    yValuesEvap.add(new Entry(count, (float)depth));
                    count++;
                    Log.d(TAG, "onDataChange: count " + count);
                }
                LineDataSet lineDataSet = new LineDataSet(yValuesEvap, "Water Evaporation Rate");
                lineDataSet.setFillAlpha(0);
                lineDataSet.setColor(Color.BLUE);
                lineDataSet.setLineWidth(2f);
                lineDataSet.setValueTextSize(0);
                lineDataSet.setCircleHoleRadius(0);
                lineDataSet.setCircleColor(Color.BLUE);
                lineDataSet.setDrawCircles(false);

                LineData lineData = new LineData(lineDataSet);
                lineChartEvaporation.getXAxis().setEnabled(false);
                lineChartEvaporation.getAxisRight().setEnabled(false);
                lineChartEvaporation.setData(lineData);
                lineChartEvaporation.notifyDataSetChanged();
                lineChartEvaporation.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private double convertMilliToMeter(double evap){
        return evap / 1000;
    }

    private String getDateToday(){
        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis();
        String dateString = DateFormat.format("MMMM dd, yyyy - h:mm a", calendar).toString();
        return dateString;
    }

    private void getEvaporationRate(){
        w.search("Manila");
        w.calculateEvaporationRate();
        Log.d(TAG, "getWeatherAndEvaporationRate: EVAP " + w.getEvaporationRate());
        Integer initialEvapRate = Integer.valueOf((int) (Math.round(w.getEvaporationRate() * 1000)));
        iEvap = (double) initialEvapRate/1000;
        Log.d(TAG, "getWeatherAndEvaporationRate: iEVAP " + iEvap);
        //String evapRate = String.valueOf((double) initialEvapRate/1000.0);
        //evaporateRate.setText(evapRate);
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
