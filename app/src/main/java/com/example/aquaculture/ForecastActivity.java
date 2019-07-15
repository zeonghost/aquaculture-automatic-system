package com.example.aquaculture;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ForecastActivity extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener {
    private static final String TAG = "ForecastActivity";
    CardView forecastCard;
    LinearLayout forecastLayout;
    TextView forecastTime;
    TextView tempForecast;
    LineChart forecastGraph;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myForecastReference;
    private ArrayList<Entry> yValuesForecast;
    private ArrayList<String> xValuesForecast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_forecast);
        String pathToForecast = HomeActivity.transferData + "-forecast";
        firebaseDatabase = FirebaseDatabase.getInstance();
        myForecastReference = firebaseDatabase.getReference(pathToForecast);
        forecastLayout = findViewById(R.id.tempForecastLayout);
        forecastCard = findViewById(R.id.waterForecastCardView);
        forecastTime = findViewById(R.id.textViewForecastTime);
        tempForecast = findViewById(R.id.textViewTempForecast);
        forecastGraph = findViewById(R.id.lineChartWaterForecast);
        yValuesForecast = new ArrayList<>();
        xValuesForecast = new ArrayList<>();
        getForecastGraph();
        buttonNavigationSettings();
    }

    
    private void getForecastGraph(){
        forecastGraph.setOnChartGestureListener(ForecastActivity.this);
        forecastGraph.setOnChartValueSelectedListener(ForecastActivity.this);
        forecastGraph.setDragEnabled(true);
        forecastGraph.setEnabled(true);
        forecastGraph.setPinchZoom(false);
        forecastGraph.setDoubleTapToZoomEnabled(false);
        forecastGraph.setHighlightPerTapEnabled(true);
        forecastGraph.getDescription().setText("Today");
        myForecastReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float i = 0, newVal;
                double val;
                Integer initialValue;
                for(DataSnapshot snaps : dataSnapshot.getChildren()){
                    Log.d(TAG, "FORECAST: " + snaps.getValue());
                    val = snaps.getValue(Double.class);
                    initialValue = Integer.valueOf((int) (Math.round(val * 10)));
                    newVal = initialValue/10.0f;
                    yValuesForecast.add(new Entry(i, newVal));
                    i++;
                }

                LineDataSet lineDataSet = new LineDataSet(yValuesForecast, "Water Temperature Forecast");
                lineDataSet.setFillAlpha(0);
                lineDataSet.setColor(Color.CYAN);
                lineDataSet.setLineWidth(5f);
                lineDataSet.setValueTextSize(0);
                lineDataSet.setHighlightEnabled(true);
                lineDataSet.setDrawHighlightIndicators(true);
                lineDataSet.setHighLightColor(Color.BLUE);
                lineDataSet.setCircleHoleRadius(-1);
                lineDataSet.setCircleColor(Color.BLUE);
                lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

                LineData lineData = new LineData(lineDataSet);
                forecastGraph.getXAxis().setEnabled(false);
                forecastGraph.getAxisRight().setEnabled(false);
                forecastGraph.setData(lineData);
                forecastGraph.notifyDataSetChanged();
                forecastGraph.invalidate();

                XAxis xAxis = forecastGraph.getXAxis();
                setXValuesForecast();
                xAxis.setValueFormatter(new ForecastActivity.myXValueFormatter(xValuesForecast));
                xAxis.setDrawLabels(true);


                Integer position;
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH");
                position = Integer.valueOf(timeFormat.format(cal.getTime()));

                if(position == 24){
                    position = 0;
                } else {
                    position += 1;
                }

                Entry entry = yValuesForecast.get(position);
                tempForecast.setText(entry.getY() + " °C");
                forecastTime.setText(xValuesForecast.get(position));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        Log.d(TAG, "X Value: " + e.getX());
        Log.d(TAG, "Y Value: " + e.getY());
        Log.d(TAG, "Date: " + xValuesForecast.get((int) e.getX()));

        forecastTime.setText(xValuesForecast.get((int) e.getX()));
        tempForecast.setText(String.valueOf(e.getY()) + " °C");
    }

    @Override
    public void onNothingSelected() {

    }

    public class myXValueFormatter implements IAxisValueFormatter {
        private ArrayList<String> values;

        public myXValueFormatter(ArrayList<String> values) {
            this.values = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Log.d(TAG, "getFormattedValue: value index: " + value);
            if(values.size() > (int) value){
                return values.get((int) value);
            } else {
                return null;
            }
        }
    }

    private void setXValuesForecast(){
        xValuesForecast.add("12:00 AM");
        xValuesForecast.add("1:00 AM");
        xValuesForecast.add("2:00 AM");
        xValuesForecast.add("3:00 AM");
        xValuesForecast.add("4:00 AM");
        xValuesForecast.add("5:00 AM");
        xValuesForecast.add("6:00 AM");
        xValuesForecast.add("7:00 AM");
        xValuesForecast.add("8:00 AM");
        xValuesForecast.add("9:00 AM");
        xValuesForecast.add("10:00 AM");
        xValuesForecast.add("11:00 AM");
        xValuesForecast.add("12:00 PM");
        xValuesForecast.add("1:00 PM");
        xValuesForecast.add("2:00 PM");
        xValuesForecast.add("3:00 PM");
        xValuesForecast.add("4:00 PM");
        xValuesForecast.add("5:00 PM");
        xValuesForecast.add("6:00 PM");
        xValuesForecast.add("7:00 PM");
        xValuesForecast.add("8:00 PM");
        xValuesForecast.add("9:00 PM");
        xValuesForecast.add("10:00 PM");
        xValuesForecast.add("11:00 PM");
    }

    private void buttonNavigationSettings(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1://btn 1 -> HomeActivity
                        Intent intent1 = new Intent(ForecastActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.item2://btn 2 -> task
                        Intent intent2 = new Intent(ForecastActivity.this, TaskActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.item3://btn 3 -> profile
                        Intent intent3 = new Intent(ForecastActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });//bottom navigation
    }
}
