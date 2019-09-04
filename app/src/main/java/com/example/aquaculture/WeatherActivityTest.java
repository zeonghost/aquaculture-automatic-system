package com.example.aquaculture;

import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.aquaculture.Model.User;
import com.example.aquaculture.Model.Weather;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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
import java.util.EventListener;

public class WeatherActivityTest extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener
{
    private static final String TAG = "LOG";
    private EditText inputCityName;
    private String cityName;
    private Button getWeatherInfo;

    private TextView tempData;
    private TextView tempMinData;
    private TextView tempMaxData;
    private TextView wDesc;
    private TextView wDateTime;

    private FirebaseDatabase myDatabase;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myEvapReference;
    private LineChart lineChartEvapGraph;
    private Weather EvapRate;
    private double evaporationRate;
    private DatabaseReference ref;

    private ArrayList<Entry> yValuesEvap;
    private ArrayList<String> xValuesEvap;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_test);

        inputCityName = findViewById(R.id.editTxtCityName);
        getWeatherInfo = findViewById(R.id.btnGetWeatherInfo);
        tempData = findViewById(R.id.txtViewTemp);
        tempMinData = findViewById(R.id.txtViewTempMin);
        tempMaxData = findViewById(R.id.txtViewTempMax);
        wDesc = findViewById(R.id.txtViewWeatherDesc);
        wDateTime = findViewById(R.id.txtViewWeatherDate);
        myDatabase = FirebaseDatabase.getInstance();
        getEvapGraph();
        yValuesEvap = new ArrayList<>();
        xValuesEvap = new ArrayList<>();
        EvapRate = new Weather();
        EvapRate.search("Manila");
        EvapRate.calculateEvaporationRate();
        evaporationRate = EvapRate.getEvaporationRate();
        final String dateString = convertDate(EvapRate.getDateTime());
        wDateTime.setText(dateString);

        //String key = ref.push().getKey();

        ref = myDatabase.getReference("pi1-evap");
//      ref.child(dateString).child("value").setValue(evaporationRate);
//    ref.addValueEventListener(new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//            Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
//            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                String date = snapshot.getKey();
//                if (date.equals(dateString)) {
//                    double container = snapshot.child(date).getValue(Double.class);
//                    Log.d(TAG, "CONTAINER " + container);
//                    Log.d(TAG, "CONTAINER " + evaporationRate);
//                    if (container < evaporationRate) {
//                        Log.d(TAG, "container is less than evaporation rate ");
//                        snapshot.getRef().child(date).setValue(evaporationRate);
//                    }
//                }
//            }
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//        }
//
//
//    });


    }
    private String convertDate(long ts)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ts * 1000);
        String dateString = DateFormat.format("MMMM dd, yyyy", calendar).toString();
        return dateString;
    }

    private void getEvapGraph (){
        String pathToEvaporation = HomeActivity.transferData + "pi1-evap";
        myEvapReference = myDatabase.getReference(pathToEvaporation);
        lineChartEvapGraph= findViewById(R.id.evapGraph);
//        lineChartEvapGraph.setOnChartGestureListener(WeatherActivityTest.this);
//        lineChartEvapGraph.setOnChartValueSelectedListener(WeatherActivityTest.this);
        lineChartEvapGraph.setDragEnabled(true);
        lineChartEvapGraph.setScaleEnabled(false);
        lineChartEvapGraph.setEnabled(true);
        lineChartEvapGraph.setPinchZoom(false);
        lineChartEvapGraph.setDoubleTapToZoomEnabled(false);
        lineChartEvapGraph.setHighlightPerTapEnabled(true);
        lineChartEvapGraph.getDescription().setText("Today");
        lineChartEvapGraph.getLegend().setEnabled(false);

        myEvapReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                float i = 0, newVal;
                double val;
                Integer initialValue;
                for(DataSnapshot snaps : dataSnapshot.getChildren())
                {
                    Log.d(TAG, "EVAPORATION RATE: " + snaps.getValue());
                    val = snaps.getValue(Double.class);
                    initialValue = Integer.valueOf((int) (Math.round(val * 10)));
                    newVal = initialValue/10.0f;
                    yValuesEvap.add(new Entry(i, newVal));
                    i++;
                }
                LineDataSet lineDataSet = new LineDataSet(yValuesEvap, "Water Temperature Forecast");
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
                lineChartEvapGraph.getXAxis().setEnabled(false);
                lineChartEvapGraph.getAxisRight().setEnabled(false);
                lineChartEvapGraph.setData(lineData);
                lineChartEvapGraph.notifyDataSetChanged();
                lineChartEvapGraph.invalidate();

                final ArrayList<Entry>yValuesEvap = new ArrayList<>();

                ref.orderByKey().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        Log.d(TAG, "DATASNAPSHOT " + dataSnapshot.getValue());
                        float i=0;
                        float value;
                        for( DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            Log.d(TAG, "EVAPORATION RATES: " + snapshot.getValue());
                            value= snapshot.getValue(Float.class);
                            yValuesEvap.add(new Entry(i, value));
                            i++;
                        }
                        Log.d(TAG, "Y VALUES ARRAYLIST " + yValuesEvap);
                        LineDataSet set1= new LineDataSet(yValuesEvap, "Data Set");
                        set1.setFillAlpha(100);

  ArrayList<ILineDataSet> dataSets= new ArrayList<>();
    dataSets.add(set1);

    LineData data= new LineData(dataSets);
                        lineChartEvapGraph.setData(data);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


//                yValuesEvap.add();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

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

    }

    @Override
    public void onNothingSelected() {

    }
}
//    ArrayList<Entry>yValues = new ArrayList<>();
//
//    yValues.add(new Entry(0, 60f));
//    yValues.add(new Entry(1, 30f));
//    yValues.add(new Entry(2, 60f));
//    yValues.add(new Entry(3, 40f));
//    yValues.add(new Entry(4, 70f));
//    yValues.add(new Entry(5, 60f));
//    yValues.add(new Entry(6, 80f));
//
//    LineDataSet set1= new LineDataSet(yValues, "Data Set");
//    set1.setFillAlpha(100);
//
//    ArrayList<ILineDataSet> dataSets= new ArrayList<>();
//    dataSets.add(set1);
//
//    LineData data= new LineData(dataSets);
//
//    lineChartEvapGraph.setData(data);