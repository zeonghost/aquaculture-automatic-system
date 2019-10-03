package com.example.aquaculture;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
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

public class EvaporationActivity extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener {
    private static final String TAG = "EvaporationActivity";
    private LineChart evaporationGraph;
    private FirebaseDatabase myDatabase;
    private DatabaseReference evapRef;
    private String getData;
    private ArrayList<String> xValuesEvap;
    private TextView dateEvap;
    private TextView evapRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaporation);
        evaporationGraph = findViewById(R.id.lineChartEvaporationRate);
        dateEvap = findViewById(R.id.textViewEvapDate);
        evapRate = findViewById(R.id.textViewEvaporation);
        getData = HomeActivity.transferData;
        myDatabase = FirebaseDatabase.getInstance();

        String evapPath = getData + "-evap";
        evapRef = myDatabase.getReference(evapPath);
        xValuesEvap = new ArrayList<>();
        setXValuesEvaporation();
        buttonNavigationSettings();
        updateEvaporationGraph();
    }

    private void updateEvaporationGraph(){
        evaporationGraph.setOnChartGestureListener(EvaporationActivity.this);
        evaporationGraph.setOnChartValueSelectedListener(EvaporationActivity.this);
        evaporationGraph.setDragEnabled(true);
        evaporationGraph.setEnabled(true);
        evaporationGraph.setPinchZoom(false);
        evaporationGraph.setDoubleTapToZoomEnabled(false);
        evaporationGraph.setHighlightPerTapEnabled(true);
        final ArrayList<Entry> yValues = new ArrayList<>();
        evapRef.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String month = "";
                    float i = 0;
                    for(DataSnapshot snaps : dataSnapshot.getChildren()){
                        if(i == 0){
                            String date = snaps.getKey();
                            String dateArray[] = date.split(" ", 3);
                            month = dateArray[0] + " " + dateArray[2];
                        }
                        yValues.add(new Entry(i, snaps.getValue(Float.class)));
                        i++;
                    }
                    evaporationGraph.getDescription().setText(month);
                    LineDataSet lineDataSet = new LineDataSet(yValues, "Highest Evaporation Rate Per Day");
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
                    evaporationGraph.getXAxis().setEnabled(false);
                    evaporationGraph.getAxisRight().setEnabled(false);
                    evaporationGraph.setData(lineData);
                    evaporationGraph.notifyDataSetChanged();
                    evaporationGraph.invalidate();

                    XAxis xAxis = evaporationGraph.getXAxis();
                    xAxis.setValueFormatter(new EvaporationActivity.myXValueFormatter(xValuesEvap));
                    xAxis.setDrawLabels(true);

                    int indexSize = yValues.size();
                    Entry entry = yValues.get(indexSize - 1);
                    evapRate.setText(entry.getY() + " mm/day");
                    dateEvap.setText(xValuesEvap.get(indexSize - 1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }//set line chart

    private void buttonNavigationSettings(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1://btn 1 -> HomeActivity
                        Intent intent1 = new Intent(EvaporationActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.item2://btn 2 -> task
                        Intent intent2 = new Intent(EvaporationActivity.this, TaskActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.item3://btn 3 -> profile
                        Intent intent3 = new Intent(EvaporationActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });//bottom navigation
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        bottomNavigationView.setItemIconTintList(null);
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
        dateEvap.setText(xValuesEvap.get((int) e.getX()));
        evapRate.setText(String.valueOf(e.getY()));
    }//show text when select point in line chart

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
            if(values.size() > (int) value){
                return values.get((int) value);
            } else {
                return null;
            }
        }
    }//for line chart

    private void setXValuesEvaporation(){
        evapRef.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snaps : dataSnapshot.getChildren()){
                        String date = snaps.getKey();
                        xValuesEvap.add(date);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }//set evaporation data in x axis

    private String getSystemDate(){
        String date;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("MMMM dd, yyyy");
        date = timeFormat.format(cal.getTimeInMillis());
        return date;
    }//get system time
}
