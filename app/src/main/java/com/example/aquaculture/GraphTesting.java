package com.example.aquaculture;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MotionEvent;

import com.example.aquaculture.Model.Weather;
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

import java.util.ArrayList;
import java.util.Calendar;

public class GraphTesting extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener {
    private LineChart lineChartEvaporation;
    private FirebaseDatabase myDatabase;
    private DatabaseReference myEvapReference;
    private DatabaseReference myForecastReference;
    private ArrayList<Entry> yValuesEvap;
    private ArrayList<Entry> yValuesForecast;
    private ArrayList<String> xValuesForecast;
    private double depth;
    private Weather w;
    private double iEvap;
    private LineChart lineChartForecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_testing);
        lineChartEvaporation = findViewById(R.id.lineEvaporation);
        lineChartForecast = findViewById(R.id.lineForecast);
        yValuesEvap = new ArrayList<>();
        yValuesForecast = new ArrayList<>();
        xValuesForecast = new ArrayList<>();
        myDatabase = FirebaseDatabase.getInstance();
        myEvapReference = myDatabase.getReference("pi1-detail");
        myForecastReference = myDatabase.getReference("pi1-forecast");
        w = new Weather();
        getEvaporationRate();
        getEvaporationGraph();
        getForecastGraph();
    }

    private void getForecastGraph(){
        lineChartForecast.setOnChartGestureListener(GraphTesting.this);
        lineChartForecast.setOnChartValueSelectedListener(GraphTesting.this);
        lineChartForecast.setDragEnabled(true);
        lineChartForecast.setEnabled(true);
        lineChartForecast.setPinchZoom(false);
        lineChartForecast.setDoubleTapToZoomEnabled(false);
        lineChartForecast.setHighlightPerTapEnabled(true);
        myForecastReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float i = 0;
                double val;
                for(DataSnapshot snaps : dataSnapshot.getChildren()){
                    val = snaps.getValue(Double.class);
                    yValuesForecast.add(new Entry(i, (float) val));
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
                lineChartForecast.getXAxis().setEnabled(true);
                lineChartForecast.getAxisRight().setEnabled(false);
                lineChartForecast.setData(lineData);
                lineChartForecast.notifyDataSetChanged();
                lineChartForecast.invalidate();

                XAxis xAxis = lineChartForecast.getXAxis();
                setXValuesForecast();
                xAxis.setValueFormatter(new GraphTesting.myXValueFormatter(xValuesForecast));
                xAxis.setDrawLabels(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getEvaporationGraph(){
        lineChartEvaporation.setOnChartGestureListener(GraphTesting.this);
        lineChartEvaporation.setOnChartValueSelectedListener(GraphTesting.this);
        lineChartEvaporation.setDragEnabled(false);
        lineChartEvaporation.setEnabled(true);
        lineChartEvaporation.setPinchZoom(false);
        lineChartEvaporation.setDoubleTapToZoomEnabled(false);
        lineChartEvaporation.setHighlightPerTapEnabled(false);
        myEvapReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final double evapRate = convertMilliToMeter(iEvap * 30);
                float count = 0;
                depth = dataSnapshot.child("depth").getValue(Double.class);
                while (depth > 0.459d){
                    depth -= evapRate;
                    yValuesEvap.add(new Entry(count, (float)depth));
                    count++;
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
        String dateString = DateFormat.format("MMMM dd, yyyy - h:mm a", calendar).toString();
        return dateString;
    }

    private void getEvaporationRate(){
        w.search("Manila");
        w.calculateEvaporationRate();
        Integer initialEvapRate = Integer.valueOf((int) (Math.round(w.getEvaporationRate() * 1000)));
        iEvap = (double) initialEvapRate/1000;
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
}
