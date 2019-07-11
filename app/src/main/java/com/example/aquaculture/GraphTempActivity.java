package com.example.aquaculture;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aquaculture.Fragment.DatePickerFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.common.graph.Graph;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class GraphTempActivity extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener, DatePickerDialog.OnDateSetListener{

    private static final String TAG = "LOG";
    private LineChart lineChart;
    private ArrayList<Entry> yValues = new ArrayList<>();
    private ArrayList<String> xValues = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("pi1-temp");
    private Long fromDate;
    private Long toDate;
    private String dateTemp;
    private EditText startDate;
    private EditText endDate;
    private Button plotGraph;
    private TextView showTemp;
    private TextView showDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_temp);
        buttonNavigationSettings();

        //getSupportActionBar().hide();

        startDate = findViewById(R.id.editTxtStartDate);
        endDate = findViewById(R.id.editTxtEndDate);
        plotGraph = findViewById(R.id.btnPlotGraph);
        lineChart = findViewById(R.id.lineChart);
        showTemp = findViewById(R.id.txtTemp);
        showDateTime = findViewById(R.id.txtDateTime);

        startDate.setFocusable(false);
        startDate.setClickable(true);
        endDate.setFocusable(false);
        endDate.setClickable(true);

        lineChart.setOnChartGestureListener(GraphTempActivity.this);
        lineChart.setOnChartValueSelectedListener(GraphTempActivity.this);

        lineChart.setDragEnabled(true);
        lineChart.setEnabled(true);
        lineChart.setPinchZoom(false);
        lineChart.setScaleYEnabled(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setExtraOffsets(5, 0, 5, 10);
        startingGraph();
    }

    private void buttonNavigationSettings(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1://btn 1 -> HomeActivity
                        Intent intent1 = new Intent(GraphTempActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.item2://btn 2 -> task
                        Intent intent2 = new Intent(GraphTempActivity.this, TaskActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.item3://btn 3 -> profile
                        Intent intent3 = new Intent(GraphTempActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });//bottom navigation
    }

    /***********************************************
     * REQUIRED TO RETURN A DATE FOR DATE LISTENER *
     ***********************************************/
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        dateTemp = DateFormat.getDateInstance().format(calendar.getTime()); // String to reflect the chosen date

        if(startDate.getText().toString().isEmpty()){
            if(!endDate.getText().toString().isEmpty() && calendar.getTimeInMillis() > toDate){
                Toast.makeText(GraphTempActivity.this, "Invalid Date: Please choose before your end date.", Toast.LENGTH_SHORT).show();
                return;
            }
            startDate.setText(dateTemp);
            fromDate = calendar.getTimeInMillis(); // Timestamp format
            Log.d(TAG, "dateTemp: " + dateTemp);
            Log.d(TAG, "fromDate: " + fromDate);
        } else {
            if(fromDate > calendar.getTimeInMillis()){
                Toast.makeText(GraphTempActivity.this, "Invalid Date: Please choose after your start date.", Toast.LENGTH_SHORT).show();
                return;
            }
            endDate.setText(dateTemp);
            toDate = calendar.getTimeInMillis() + 86399000; // Timestamp format
            Log.d(TAG, "dateTemp: " + dateTemp);
            Log.d(TAG, "toDate: " + toDate);
        }
    }
    /***********************************************
     * REQUIRED TO RETURN A DATE FOR DATE LISTENER *
     ***********************************************/

    @Override
    protected void onStart() {
        super.onStart();
        /***********************************
         * START OF THE DATE PICKING LOGIC *
         ***********************************/
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDate.setText(null);
                DialogFragment pickStartDate = new DatePickerFragment();
                pickStartDate.show(getSupportFragmentManager(), "From Date");
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startDate.getText().toString().isEmpty()){
                    Toast.makeText(GraphTempActivity.this, "Set a starting date first.", Toast.LENGTH_SHORT).show();
                } else {
                    DialogFragment pickEndDate = new DatePickerFragment();
                    pickEndDate.show(getSupportFragmentManager(), "To Date");
                }
            }
        });
        /*********************************
         * END OF THE DATE PICKING LOGIC *
         *********************************/

        plotGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startDate.getText().toString().isEmpty() || endDate.getText().toString().isEmpty()){
                    Toast.makeText(GraphTempActivity.this, "Please set the dates of data you want to graph.", Toast.LENGTH_SHORT).show();
                    return;
                }

                fromDate/=1000;
                toDate/=1000;
                Log.d(TAG, "onStart fromDate: " + fromDate);
                Log.d(TAG, "onStart toDate: " + toDate);


                Query q2 = myRef.orderByChild("time").startAt(fromDate).endAt(toDate);

                q2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        xValues.clear();
                        yValues.clear();
                        /*
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            Log.d(TAG, "Check data: " + ds.getValue().toString());
                        }
                         */
                        // Log.d(TAG, "fromDate: " + fromDate + " " + "toDate: " + toDate);
                        Log.d(TAG, "Count: " + dataSnapshot.getChildrenCount());
                        Timestamp timestamp;
                        Date date;
                        String formattedDateTime;
                        float i = 0;

                        for(DataSnapshot snaps : dataSnapshot.getChildren()){
                            String snapTemp = snaps.child("val").getValue().toString();

                            Float temp = Float.parseFloat(snapTemp) / 10;
                            yValues.add(new Entry(i, temp));
                            //Log.d(TAG, "Adding y Values");
                            //Log.d(TAG, "Value: " + snapTemp);


                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
                            Long snapTimestamp = snaps.child("time").getValue(Long.class);
                            timestamp = new Timestamp(snapTimestamp);
                            date = new Date(timestamp.getTime() * 1000);
                            formattedDateTime = dateFormat.format(date);

                            xValues.add(formattedDateTime);
                            i += 1;
                        }
                        Log.d(TAG, "Count: " + dataSnapshot.getChildrenCount());

                        LineDataSet lineDataSet = new LineDataSet(yValues, "Water Temperature");

                        //DESIGN OF THE LINES
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
                        //lineDataSet.setValueTextColor(Color.BLACK);

                        LineData lineData = new LineData(lineDataSet);
                        //lineData.setValueFormatter(new myValueFormatter());

                        lineChart.getAxisRight().setEnabled(false);
                        lineChart.setData(lineData);
                        lineChart.notifyDataSetChanged();
                        lineChart.invalidate();

                        lineChart.setVisibleXRangeMinimum(5f);
                        //lineChart.setVisibleXRangeMaximum(6f);

                        XAxis xAxis = lineChart.getXAxis();
                        xAxis.setValueFormatter(new myXValueFormatter(xValues));
                        xAxis.setDrawLabels(false);
                        //xAxis.setGranularity(2f);
                        //xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        //xAxis.setLabelRotationAngle(-45);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                startDate.setText(null);
                endDate.setText(null);
            }
        });
    }

    /*************************************************************************************************
     * FOR GESTURE CONTROLS USING GRAPH - methods for IMPLEMENTING GestureListener and ValueListener *
     *************************************************************************************************/

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.d(TAG, "onChartGestureStart: X: " + me.getX() + " Y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.d(TAG, "onChartGestureEnd: " + lastPerformedGesture);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.d(TAG, "onChartLongPressed: ");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.d(TAG, "onChartDoubleTapped: ");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.d(TAG, "onChartSingleTapped: ");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.d(TAG, "onChartFling: veloX: " + velocityX + " veloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.d(TAG, "onChartScale: ScaleX: " + scaleX + " scaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.d(TAG, "onChartTranslate: dX: " + dX + " dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.d(TAG, "onValueSelected: " + e.toString());
        Log.d(TAG, "X Value: " + e.getX());
        Log.d(TAG, "Y Value: " + e.getY());
        Log.d(TAG, "Date: " + xValues.get((int) e.getX()));

        String temp = e.getY() + " °C";
        String date = xValues.get((int) e.getX());
        showTemp.setText(temp);
        showDateTime.setText(date);
    }

    @Override
    public void onNothingSelected() {
        Log.d(TAG, "onNothingSelected: ");
    }

    public void startingGraph(){
        long today = Calendar.getInstance().getTimeInMillis();
        today /= 1000;
        Query q = myRef.orderByChild("time").startAt(today - 86400).endAt(today);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    return;
                }

                Log.d(TAG, "Count: " + dataSnapshot.getChildrenCount());
                Timestamp timestamp;
                Date date;
                String formattedDateTime;
                float i = 0;

                for(DataSnapshot snaps : dataSnapshot.getChildren()){
                    String snapTemp = snaps.child("val").getValue().toString();
                    Float temp = Float.parseFloat(snapTemp) / 10;

                    yValues.add(new Entry(i, temp));

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
                    Long snapTimestamp = snaps.child("time").getValue(Long.class);
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                    timestamp = new Timestamp(snapTimestamp);
                    date = new Date(timestamp.getTime() * 1000);
                    formattedDateTime = dateFormat.format(date);

                    xValues.add(formattedDateTime);
                    i += 1;
                }
                Log.d(TAG, "Count: " + dataSnapshot.getChildrenCount());
                LineDataSet lineDataSet = new LineDataSet(yValues, "Water Temperature");

                //DESIGN OF THE LINES
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
                //lineDataSet.setValueTextColor(Color.BLACK);

                LineData lineData = new LineData(lineDataSet);
                //lineData.setValueFormatter(new myValueFormatter());

                lineChart.getAxisRight().setEnabled(false);
                lineChart.setData(lineData);
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();

                lineChart.setVisibleXRangeMinimum(5f);
                //lineChart.setVisibleXRangeMaximum(6f);

                XAxis xAxis = lineChart.getXAxis();
                xAxis.setValueFormatter(new myXValueFormatter(xValues));
                xAxis.setDrawLabels(false);

                Entry entry = yValues.get(yValues.size() - 1);
                showTemp.setText(entry.getY() + " °C");
                showDateTime.setText(xValues.get(yValues.size() - 1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    public class myValueFormatter implements IValueFormatter {

        private DecimalFormat valFormat;

        public myValueFormatter(){
            valFormat = new DecimalFormat("##.0");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return valFormat.format(value) + " C";
        }
    }
}
