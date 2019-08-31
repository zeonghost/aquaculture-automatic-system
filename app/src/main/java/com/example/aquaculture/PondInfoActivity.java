package com.example.aquaculture;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.example.aquaculture.Model.Forecast;
import com.example.aquaculture.Model.ForecastResult;
import com.example.aquaculture.Model.SimpleExponentialSmoothing;
import com.example.aquaculture.Model.Weather;
import com.githang.statusbar.StatusBarCompat;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.sql.Array;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class PondInfoActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private CardView graphCheck;
    private CardView logview;
    private CardView tempRead;
    private CardView forecastRead;
    private CardView weatherRead;
    private CardView evaporateRead;
    private LinearLayout weatherForecastLayout;
    private LineChart lineChart;
    private LineChart lineChartForecastGraph;
    private TextView piID;
    private TextView location;
    private TextView pondName;
    private TextView highTemp;
    private TextView lowTemp;
    private TextView ch2OnTimeInt;
    private TextView ch2OffTimeInt;
    private TextView time1;
    private TextView log1;
    private TextView time2;
    private TextView log2;
    private TextView time3;
    private TextView log3;
    private TextView time4;
    private TextView log4;
    private TextView forecastTemp;
    private TextView forecastWeather;
    private TextView evaporateRate;
    private ImageView tempSetting;
    private TextView channel1Text;
    private TextView channel2Text;
    private TextView channel3Text;
    private ImageButton channel1;
    private ImageButton channel2;
    private ImageButton channel3;
    private SharedPreferences sp;
    private static long lastClickTime;
    private boolean isGraphVisible;
    public static float high;
    public static float low;

    private FirebaseDatabase myDatabase;
    private DatabaseReference myRef;
    private Forecast forecast;
    private SimpleExponentialSmoothing sme;
    private Weather weather;

    private TextView tempData;
    private TextView tempMinData;
    private TextView tempMaxData;
    private TextView wDesc;
    private TextView wDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_pond_info);
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#148D7F"));

        myDatabase = FirebaseDatabase.getInstance();
        piID = findViewById(R.id.txtViewPiID);
        pondName = findViewById(R.id.txtViewPondName);
        location = findViewById(R.id.txtViewPondLocation);
        graphCheck = findViewById(R.id.graphCardView);
        lineChart = findViewById(R.id.lineChart); //line graph for water temp
        lineChartForecastGraph = findViewById(R.id.lineChartForecast); //line graph for temp forecast
        weatherForecastLayout = findViewById(R.id.weatherForecastLayout); //layout containing weather forecast
        tempData = findViewById(R.id.txtViewTemp);
        tempMinData = findViewById(R.id.txtViewTempMin);
        tempMaxData = findViewById(R.id.txtViewTempMax);
        wDesc = findViewById(R.id.txtViewWeatherDesc);
        wDateTime = findViewById(R.id.txtViewWeatherDate);
        tempRead = findViewById(R.id.cardViewTemperatureRead); //cardview for temperature reading
        forecastRead = findViewById(R.id.cardViewForecastRead); //cardview for forecast reading
        weatherRead = findViewById(R.id.cardViewWeatherRead); //cardview for weather reading
        evaporateRead = findViewById(R.id.cardViewEvaporationRead); //cardview for evap rate
        logview = findViewById(R.id.logView);
        highTemp = findViewById(R.id.txtViewHighTemp);
        lowTemp = findViewById(R.id.txtViewLowTemp);
        ch2OnTimeInt = findViewById(R.id.txtCh2OnTime);
        ch2OffTimeInt = findViewById(R.id.txtCh2OffTime);
        time1 = findViewById(R.id.T1);
        log1 = findViewById(R.id.L1);
        time2 = findViewById(R.id.T2);
        log2 = findViewById(R.id.L2);
        time3 = findViewById(R.id.T3);
        log3 = findViewById(R.id.L3);
        time4 = findViewById(R.id.T4);
        log4 = findViewById(R.id.L4);
        channel1 = findViewById(R.id.ch1btr);
        channel2 = findViewById(R.id.ch2btr);
        channel3 = findViewById(R.id.ch3btr);
        tempSetting = findViewById(R.id.imageViewSmartSetting);
        forecastTemp = findViewById(R.id.pondforetemp);
        forecastWeather = findViewById(R.id.weatherforecast);
        evaporateRate = findViewById(R.id.evapRate);
        channel1Text = findViewById(R.id.textViewChannel1);
        channel2Text = findViewById(R.id.textViewChannel2);
        channel3Text = findViewById(R.id.textViewChannel3);
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        isGraphVisible = false;
        lineChart.setVisibility(View.GONE);
        lineChartForecastGraph.setVisibility(View.GONE);
        weatherForecastLayout.setVisibility(View.GONE);
        forecast = new Forecast();
        sme = new SimpleExponentialSmoothing();
        weather = new Weather();
        basicReadWrite();
        startingTempGraph();
        buttomNavigation();
        logRead();
        setChannelNames();
        getForecastGraph();
        getCriticalLevels();
    }

    @Override
    protected void onStart() {
        super.onStart();
        graphCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isGraphVisible){
                    lineChart.setVisibility(View.GONE);
                    lineChartForecastGraph.setVisibility(View.GONE);
                    weatherForecastLayout.setVisibility(View.GONE);
                    isGraphVisible = false;
                } else {
                    lineChart.setVisibility(View.VISIBLE);
                    lineChartForecastGraph.setVisibility(View.VISIBLE);
                    weatherForecastLayout.setVisibility(View.VISIBLE);
                    isGraphVisible = true;
                }
            }
        });

        tempSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempSetDialog();
            }
        });

        lineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toGraphActivity = new Intent (PondInfoActivity.this, GraphTempActivity.class);
                startActivity(toGraphActivity);
            }
        });

        lineChartForecastGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toForecastActivity = new Intent (PondInfoActivity.this, ForecastActivity.class);
                startActivity(toForecastActivity);
            }
        });

        logview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toGraphActivity = new Intent (PondInfoActivity.this, LogViewActivity.class);
                startActivity(toGraphActivity);
            }
        });

        tempRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineChart.setVisibility(View.VISIBLE);
                lineChartForecastGraph.setVisibility(View.GONE);
                weatherForecastLayout.setVisibility(View.GONE);
            }
        });

        forecastRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineChart.setVisibility(View.GONE);
                lineChartForecastGraph.setVisibility(View.VISIBLE);
                weatherForecastLayout.setVisibility(View.GONE);
            }
        });

        weatherRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineChart.setVisibility(View.GONE);
                lineChartForecastGraph.setVisibility(View.GONE);
                weatherForecastLayout.setVisibility(View.VISIBLE);
            }
        });
        statusCheck();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String counter = getSystemTime();
        if(Objects.equals(counter, "time00")){
//            getForecast();
            Log.d(TAG, "onResume:  SHOULD HAVE BEEN CALLING THE FORECAST FUNCTION");
        }
        getWeatherAndEvaporationRate();
        Log.d(TAG, "onResume: CALL EVAP FUNCTIUON");
        statusCheck();
    }

    private void getForecastGraph(){
        DatabaseReference myForecastRef = FirebaseDatabase.getInstance().getReference("pi1-forecast");
        final ArrayList<Entry> yValues = new ArrayList<>();
        lineChartForecastGraph.setDragEnabled(false);
        lineChartForecastGraph.setEnabled(true);
        lineChartForecastGraph.setPinchZoom(false);
        lineChartForecastGraph.setDoubleTapToZoomEnabled(false);
        lineChartForecastGraph.setHighlightPerTapEnabled(false);
        lineChartForecastGraph.getDescription().setText("Water Temperature Forecast - Today");
        lineChartForecastGraph.getLegend().setEnabled(false);

        myForecastRef.addValueEventListener(new ValueEventListener() {
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
                    if((newVal > high)||(newVal < low)){
                        //forecastRead.setCardBackgroundColor(Color.parseColor("#F44336"));
                    }
                    yValues.add(new Entry(i, newVal));
                    i++;
                }

                LineDataSet lineDataSet = new LineDataSet(yValues, "Water Temperature Forecast");
                lineDataSet.setFillAlpha(0);
                lineDataSet.setColor(Color.RED);
                lineDataSet.setLineWidth(2f);
                lineDataSet.setValueTextSize(0);
                lineDataSet.setCircleHoleRadius(0.5f);
                lineDataSet.setCircleColor(Color.BLUE);
                lineDataSet.setDrawCircles(false);

                LineData lineData = new LineData(lineDataSet);
                lineChartForecastGraph.getXAxis().setEnabled(false);
                lineChartForecastGraph.getAxisRight().setEnabled(false);
                lineChartForecastGraph.setData(lineData);
                lineChartForecastGraph.notifyDataSetChanged();
                lineChartForecastGraph.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void startingTempGraph(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("pi1-temp");
        final ArrayList<Entry> yValues = new ArrayList<>();
        lineChart.setDragEnabled(false);
        lineChart.setEnabled(true);
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setHighlightPerTapEnabled(false);
        lineChart.getDescription().setText("Water Temperature - Past 6 Hours");
        lineChart.getLegend().setEnabled(false);

        long today = Calendar.getInstance().getTimeInMillis();
        today /= 1000;
        Query q = myRef.orderByChild("time").startAt(today - 21600).endAt(today);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Count Starting Graph: " + dataSnapshot.getChildrenCount());
                float i = 0;
                for(DataSnapshot snaps : dataSnapshot.getChildren()){
                    String snapTemp = snaps.child("val").getValue().toString();
                    Float temp = Float.parseFloat(snapTemp) / 10;
                    yValues.add(new Entry(i, temp));
                    i += 1;
                }
                Log.d(TAG, "Count: " + dataSnapshot.getChildrenCount());
                LineDataSet lineDataSet = new LineDataSet(yValues, "Water Temperature");

                //DESIGN OF THE LINES
                lineDataSet.setFillAlpha(0);
                lineDataSet.setColor(Color.BLUE);
                lineDataSet.setLineWidth(2f);
                lineDataSet.setValueTextSize(0);
                lineDataSet.setCircleHoleRadius(0.5f);
                lineDataSet.setCircleColor(Color.BLUE);
                lineDataSet.setDrawCircles(false);
                //lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                //lineDataSet.setValueTextColor(Color.BLACK);

                LineData lineData = new LineData(lineDataSet);
                //lineData.setValueFormatter(new myValueFormatter());
                lineChart.getXAxis().setEnabled(false);
                lineChart.getAxisRight().setEnabled(false);
                lineChart.setData(lineData);
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();

                //lineChart.setVisibleXRangeMinimum(5f);
                //lineChart.setVisibleXRangeMaximum(6f);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PondInfoActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void logRead(){
        final String getData = HomeActivity.transferData;
        String path4 = getData + "-log";

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference logPath = database.getReference(path4);

        logPath.orderByChild("logTime").limitToLast(4).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot snaps : dataSnapshot.getChildren()){
                    String logDetail = snaps.child("logDetail").getValue().toString();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                    Long logTime = snaps.child("logTime").getValue(Long.class);
                    Timestamp timestamp = new Timestamp(logTime);
                    Date date = new Date(timestamp.getTime());
                    String formattedDateTime = dateFormat.format(date);
                    if(i == 3) {
                        time1.setText(formattedDateTime);
                        log1.setText(logDetail);
                    }
                    if(i == 2){
                        time2.setText(formattedDateTime);
                        log2.setText(logDetail);
                    }
                    if(i == 1){
                        time3.setText(formattedDateTime);
                        log3.setText(logDetail);
                    }
                    if(i == 0){
                        time4.setText(formattedDateTime);
                        log4.setText(logDetail);
                    }
                    i += 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void basicReadWrite() {
        final String getData = HomeActivity.transferData;
        Log.d(TAG, "Result-transfer Data: "+ getData);
        String path1 = getData + "-detail";
        String path2 = getData + "-pond1";
        String path3 = getData + "-push";
        String path4 = getData + "-log";

        final ProgressDialog waitingDialog= new ProgressDialog(PondInfoActivity.this);
        waitingDialog.setMessage("Connecting...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(true);
        waitingDialog.show();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference loc = database.getReference(path3);
        final DatabaseReference logWrite = database.getReference(path4);

        DatabaseReference hopperRef = loc;
        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put("set", 0);
        hopperUpdates.put("warn", 0);
        hopperRef.updateChildren(hopperUpdates);

        final SharedPreferences sp = getSharedPreferences("push", Context.MODE_PRIVATE);
        String push = sp.getString("push", "");

        Switch sw1 = (Switch) findViewById(R.id.switch1);

        if(Objects.equals(push, "Y")){
            sw1.setChecked(true);
        }
        else {
            sw1.setChecked(false);
        }

        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sp.edit()
                            .putString("push", "Y")
                            .apply();
                    FirebaseMessaging.getInstance().subscribeToTopic(getData)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(PondInfoActivity.this, "Subscribe Notification Error", Toast.LENGTH_SHORT).show();
                                    }
                                    Toast.makeText(PondInfoActivity.this, "Notification On", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    sp.edit()
                            .putString("push", "N")
                            .apply();
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(getData)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(PondInfoActivity.this, "Unsubscribe Notification Error", Toast.LENGTH_SHORT).show();
                                    }
                                    Toast.makeText(PondInfoActivity.this, "Notification Off", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        final DatabaseReference myRef = database.getReference(path1);
        final DatabaseReference myRef1 = database.getReference(path2);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                piID.setText(dataSnapshot.child("piID").getValue().toString());
                pondName.setText(dataSnapshot.child("pondName").getValue().toString());
                location.setText(dataSnapshot.child("location").getValue().toString());
                waitingDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final Float tempRead = dataSnapshot.child("temp").getValue(Float.class) / 10;
                final Float highTempRead = dataSnapshot.child("high").getValue(Float.class) / 10;
                final Float lowTempRead = dataSnapshot.child("low").getValue(Float.class) / 10;
                final Integer ch2OnTimeInterval = dataSnapshot.child("gap1").getValue(Integer.class);
                final Integer ch2OffTimeInterval = dataSnapshot.child("gap2").getValue(Integer.class);
                TextView textElement = (TextView) findViewById(R.id.tempRead);
                high = highTempRead;
                low = lowTempRead;
                textElement.setText(tempRead.toString() + " °C");
                highTemp.setText(highTempRead.toString() + " °C");
                lowTemp.setText(lowTempRead.toString() + " °C");
                ch2OnTimeInt.setText(ch2OnTimeInterval.toString() + " second/s");
                ch2OffTimeInt.setText(ch2OffTimeInterval.toString() + " second/s");

                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                String u1 = sp.getString("lastname", "");
                String u2 = sp.getString("firstname", "");
                final String un = u2 + " "+ u1;

                //get current temperature data from database and display
                final Integer val4 = dataSnapshot.child("auto").getValue(Integer.class);
                final ImageButton sw = (ImageButton)findViewById(R.id.autobtr);

                if(val4 == 1){
                    sw.setImageResource(R.drawable.icons_switch_on_s);
                    tempSetting.setEnabled(false);
                    tempSetting.setImageResource(R.drawable.setting_off);
                    channel1.setEnabled(false);
                    channel2.setEnabled(false);
                    channel3.setEnabled(false);
                }
                else{
                    sw.setImageResource(R.drawable.icons_switch_off_s);
                    tempSetting.setEnabled(true);
                    tempSetting.setImageResource(R.drawable.setting);
                    channel1.setEnabled(true);
                    channel2.setEnabled(true);
                    channel3.setEnabled(true);
                }//check auto model status and change switch display

                sw.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(val4 == 1){
                            sw.setImageResource(R.drawable.icons_switch_off_s);
                            myRef1.child("auto").setValue(0);
                            Toast.makeText(PondInfoActivity.this, "Automatic Model OFF", Toast.LENGTH_SHORT).show();
                            String log = un + " turned off Automatic Model";
                            Long time = System.currentTimeMillis();
                            Map<String, Object> logPut = new HashMap<>();
                            logPut.put("logDetail", log);
                            logPut.put("logTime", time);
                            String key = logWrite.push().getKey();
                            logWrite.child(key).updateChildren(logPut);
                        }
                        else{
                            sw.setImageResource(R.drawable.icons_switch_on_s);
                            myRef1.child("auto").setValue(1);
                            Toast.makeText(PondInfoActivity.this, "Automatic Model ON", Toast.LENGTH_SHORT).show();//show message
                            String log = un + " turned on Automatic Model";
                            Long time = System.currentTimeMillis();
                            Map<String, Object> logPut = new HashMap<>();
                            logPut.put("logDetail", log);
                            logPut.put("logTime", time);
                            String key = logWrite.push().getKey();
                            logWrite.child(key).updateChildren(logPut);
                        }
                    }
                });

                final Integer val1 = dataSnapshot.child("ch1").getValue(Integer.class);
                if(val1 ==1)//1 means on
                {
                    channel1.setImageResource(R.drawable.icons_switch_on_s);
                }
                if(val1 ==0)//0 means off
                {
                    channel1.setImageResource(R.drawable.icons_switch_off_s);
                }

                channel1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(val1 == 1){
                            channel1.setImageResource(R.drawable.icons_switch_off_s);
                            myRef1.child("ch1").setValue(0);//if click then change status
                            Toast.makeText(PondInfoActivity.this, "Turned off channel 1", Toast.LENGTH_SHORT).show();
                            String log = un + " turned off channel 1";
                            Long time = System.currentTimeMillis();
                            Map<String, Object> logPut = new HashMap<>();
                            logPut.put("logDetail", log);
                            logPut.put("logTime", time);
                            String key = logWrite.push().getKey();
                            logWrite.child(key).updateChildren(logPut);
                        }
                        else{
                            channel1.setImageResource(R.drawable.icons_switch_on_s);
                            myRef1.child("ch1").setValue(1);
                            Toast.makeText(PondInfoActivity.this, "Turned on channel 1", Toast.LENGTH_SHORT).show();
                            String log = un + " turned on channel 1";
                            Long time = System.currentTimeMillis();
                            Map<String, Object> logPut = new HashMap<>();
                            logPut.put("logDetail", log);
                            logPut.put("logTime", time);
                            String key = logWrite.push().getKey();
                            logWrite.child(key).updateChildren(logPut);
                        }
                    }
                });

                final Integer val2 = dataSnapshot.child("ch2").getValue(Integer.class);
                if(val2 ==1)
                {
                    channel2.setImageResource(R.drawable.icons_switch_on_s);
                }
                if(val2 ==0)
                {
                    channel2.setImageResource(R.drawable.icons_switch_off_s);
                }

                channel2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(val2 == 1){
                            channel2.setImageResource(R.drawable.icons_switch_off_s);
                            myRef1.child("ch2").setValue(0);
                            Toast.makeText(PondInfoActivity.this, "Turned off channel 2", Toast.LENGTH_SHORT).show();
                            String log = un + " turned off channel 2";
                            Long time = System.currentTimeMillis();
                            Map<String, Object> logPut = new HashMap<>();
                            logPut.put("logDetail", log);
                            logPut.put("logTime", time);
                            String key = logWrite.push().getKey();
                            logWrite.child(key).updateChildren(logPut);
                        }
                        else{
                            channel2.setImageResource(R.drawable.icons_switch_on_s);
                            myRef1.child("ch2").setValue(1);
                            Toast.makeText(PondInfoActivity.this, "Turned on channel 2", Toast.LENGTH_SHORT).show();
                            String log = un + " turned on channel 2";
                            Long time = System.currentTimeMillis();
                            Map<String, Object> logPut = new HashMap<>();
                            logPut.put("logDetail", log);
                            logPut.put("logTime", time);
                            String key = logWrite.push().getKey();
                            logWrite.child(key).updateChildren(logPut);
                        }
                    }
                });

                final Integer val3 = dataSnapshot.child("ch3").getValue(Integer.class);
                if(val3 == 1)
                {
                    channel3.setImageResource(R.drawable.icons_switch_on_s);
                }
                if(val3 == 0)
                {
                    channel3.setImageResource(R.drawable.icons_switch_off_s);
                }

                channel3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(val3 == 1){
                            channel3.setImageResource(R.drawable.icons_switch_off_s);
                            myRef1.child("ch3").setValue(0);
                            Toast.makeText(PondInfoActivity.this, "Turned off channel 3", Toast.LENGTH_SHORT).show();
                            String log = un + " turned off channel 3";
                            Long time = System.currentTimeMillis();
                            Map<String, Object> logPut = new HashMap<>();
                            logPut.put("logDetail", log);
                            logPut.put("logTime", time);
                            String key = logWrite.push().getKey();
                            logWrite.child(key).updateChildren(logPut);
                        }
                        else{
                            channel3.setImageResource(R.drawable.icons_switch_on_s);
                            myRef1.child("ch3").setValue(1);
                            Toast.makeText(PondInfoActivity.this, "Turned on channel 3", Toast.LENGTH_SHORT).show();
                            String log = un + " turned on channel 3";
                            Long time = System.currentTimeMillis();
                            Map<String, Object> logPut = new HashMap<>();
                            logPut.put("logDetail", log);
                            logPut.put("logTime", time);
                            String key = logWrite.push().getKey();
                            logWrite.child(key).updateChildren(logPut);
                        }
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void statusCheck(){
        String getData = HomeActivity.transferData;
        String path5 = getData + "-temp";
        String path6 = getData + "-pond1";

        final TextView warnMess = findViewById(R.id.tv_warning);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference recRead = database.getReference(path5);
        final DatabaseReference tempRead = database.getReference(path6);
        final ImageButton sw = (ImageButton)findViewById(R.id.autobtr);

        tempRead.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                warnMess.setVisibility(View.GONE);
                sw.setVisibility(View.VISIBLE);
                channel1.setVisibility(View.VISIBLE);
                channel2.setVisibility(View.VISIBLE);
                channel3.setVisibility(View.VISIBLE);
                Log.d(TAG,"data changed");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recRead.orderByChild("time").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "time check: " + dataSnapshot.getValue());
                for(DataSnapshot snaps : dataSnapshot.getChildren()){
                    Log.d(TAG, "time check data: " + snaps.child("time").getValue());
                    long recTime = snaps.child("time").getValue(Long.class);
                    Log.d(TAG, "recTime: " + recTime);

                    long millis = System.currentTimeMillis();
                    long newTime;
                    newTime = millis/1000;
                    long twt = 20*60;
                    newTime = newTime - twt;
                    Log.d(TAG, "millis: " + newTime);

                    if(recTime < newTime){
                        Log.d(TAG, "The hardware should be offline.");
                        warnMess.setVisibility(View.VISIBLE);
                        sw.setVisibility(View.GONE);
                        channel1.setVisibility(View.GONE);
                        channel2.setVisibility(View.GONE);
                        channel3.setVisibility(View.GONE);
                    }
                    else{
                        warnMess.setVisibility(View.GONE);
                        sw.setVisibility(View.VISIBLE);
                        channel1.setVisibility(View.VISIBLE);
                        channel2.setVisibility(View.VISIBLE);
                        channel3.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void buttomNavigation(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1:
                        Intent intent1 = new Intent(PondInfoActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.item2:
                        Intent intent2 = new Intent(PondInfoActivity.this, TaskActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.item3:
                        Intent intent3 = new Intent(PondInfoActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        bottomNavigationView.setItemIconTintList(null);
    }

    protected void tempSetDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog, null);
        final EditText top = (EditText) textEntryView.findViewById(R.id.editTextNum1);
        final EditText bottom = (EditText)textEntryView.findViewById(R.id.editTextNum2);
        final EditText ch2OnTime = textEntryView.findViewById(R.id.editTextCh2TurnOnTime);
        final EditText ch2OffTime = textEntryView.findViewById(R.id.editTextCh2TurnOffTime);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("pi1-pond1");
        String getData = HomeActivity.transferData;
        String path4 = getData + "-log";
        final DatabaseReference logWrite = database.getReference(path4);

        AlertDialog.Builder ad1 = new AlertDialog.Builder(PondInfoActivity.this);
        ad1.setTitle("Smart Settings:");
        ad1.setIcon(android.R.drawable.ic_dialog_info);
        ad1.setView(textEntryView);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String topget = dataSnapshot.child("high").getValue().toString();
                String botget = dataSnapshot.child("low").getValue().toString();
                StringBuilder  sb1 = new StringBuilder (topget);
                sb1.insert(2, ".");
                StringBuilder  sb2 = new StringBuilder (botget);
                sb2.insert(2, ".");
                top.setText(sb1.toString());
                bottom.setText(sb2.toString());
                ch2OnTime.setText(dataSnapshot.child("gap1").getValue().toString());
                ch2OffTime.setText(dataSnapshot.child("gap2").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ad1.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                if(top.getText().toString().isEmpty() || bottom.getText().toString().isEmpty() || ch2OnTime.getText().toString().isEmpty() || ch2OffTime.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please complete all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String topget = top.getText().toString();
                String botget = bottom.getText().toString();
                String topup = top.getText().toString();
                String botup = bottom.getText().toString();
                StringBuilder  sb1 = new StringBuilder (topget);
                StringBuilder  sb2 = new StringBuilder (botget);
                StringBuilder  sb3 = new StringBuilder (topup);
                StringBuilder  sb4 = new StringBuilder (botup);

                if(topget.indexOf(".") > 0){
                    sb1.delete(topget.indexOf("."), topget.indexOf(".")+1);
                    topget =sb1.toString();
                }

                if(botget.indexOf(".") > 0){
                    sb2.delete(botget.indexOf("."), botget.indexOf(".")+1);
                    botget = sb2.toString();
                }

                if(topup.indexOf(".") < 0){
                    if(topup.length()>2){
                        sb3.insert(topup.length()-1, ".");
                        topup = sb3.toString();
                    }
                }

                if(botup.indexOf(".") < 0){
                    if(botup.length()>2){
                        sb4.insert(botup.length()-1, ".");
                        botup = sb4.toString();
                    }
                }

                if(topget.length() < 3){
                    topget = topget + "0";
                }

                if(botget.length() < 3){
                    botget = botget + "0";
                }

                int topnum = Integer.parseInt(topget);
                int botnum = Integer.parseInt(botget);
                int onTime = Integer.parseInt(ch2OnTime.getText().toString());
                int offTime = Integer.parseInt(ch2OffTime.getText().toString());

                if(topnum <= botnum){
                    Toast.makeText(getApplicationContext(), "Highest Critical Water Temperature must be greater than the Lowest Critical Water Temperature!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(onTime == 0 && offTime == 0){
                    Toast.makeText(getApplicationContext(), "ARE YOU CRAZY???!!!", Toast.LENGTH_LONG).show();
                    return;
                }

                if(onTime == 0){
                    final AlertDialog.Builder channel2ErrorDialog = new AlertDialog.Builder(PondInfoActivity.this);
                    channel2ErrorDialog.setTitle("Warning!").setMessage("Channel 2 is always OFF when automatic mode is enabled!");
                    channel2ErrorDialog.setPositiveButton("Understood!",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    channel2ErrorDialog.show();
                }

                if(offTime == 0){
                    final AlertDialog.Builder channel2ErrorDialog = new AlertDialog.Builder(PondInfoActivity.this);
                    channel2ErrorDialog.setTitle("Warning!").setMessage("Channel 2 is always ON when automatic mode is enabled!");
                    channel2ErrorDialog.setPositiveButton("Understood!",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    channel2ErrorDialog.show();
                }

                Map<String, Object> passUpdate = new HashMap<>();
                passUpdate.put("high", topnum);
                passUpdate.put("low", botnum);
                passUpdate.put("gap1", onTime);
                passUpdate.put("gap2", offTime);
                myRef.updateChildren(passUpdate);
                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                String u1 = sp.getString("lastname", "");
                String u2 = sp.getString("firstname", "");
                final String un = u2 + " "+ u1;
                String log = un + " changed the highest critical temperature to "+ topup +" ℃, lowest critical temperature to " + botup + " ℃, channel 2 on-time to "+onTime+" seconds, and off-time to "+ offTime+" seconds.";
                Long time = System.currentTimeMillis();
                Map<String, Object> logPut = new HashMap<>();
                logPut.put("logDetail", log);
                logPut.put("logTime", time);
                String key = logWrite.push().getKey();
                logWrite.child(key).updateChildren(logPut);
            }
        });
        ad1.show();
    }

    private void getForecast(){
        myRef = myDatabase.getReference("pi1-forecast-test");
        Query query = myRef.orderByChild("time");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snaps : dataSnapshot.getChildren()){
                    if(Objects.equals(snaps.child("time").getValue(String.class), "12:00 AM")){
                        forecast.addClock0(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "1:00 AM")){
                        forecast.addClock1(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "2:00 AM")){
                        forecast.addClock2(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "3:00 AM")){
                        forecast.addClock3(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "4:00 AM")){
                        forecast.addClock4(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "5:00 AM")){
                        forecast.addClock5(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "6:00 AM")){
                        forecast.addClock6(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "7:00 AM")){
                        forecast.addClock7(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "8:00 AM")){
                        forecast.addClock8(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "9:00 AM")){
                        forecast.addClock9(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "10:00 AM")){
                        forecast.addClock10(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "11:00 AM")){
                        forecast.addClock11(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "12:00 PM")){
                        forecast.addClock12(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "1:00 PM")){
                        forecast.addClock13(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "2:00 PM")){
                        forecast.addClock14(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "3:00 PM")){
                        forecast.addClock15(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "4:00 PM")){
                        forecast.addClock16(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "5:00 PM")){
                        forecast.addClock17(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "6:00 PM")){
                        forecast.addClock18(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "7:00 PM")){
                        forecast.addClock19(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "8:00 PM")){
                        forecast.addClock20(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "9:00 PM")){
                        forecast.addClock21(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "10:00 PM")){
                        forecast.addClock22(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "11:00 PM")){
                        forecast.addClock23(snaps.child("val").getValue(Float.class)/10);
                    }
                }
                forecast.collectClockLists();
                init_forecastNode();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getCriticalLevels(){
        DatabaseReference refPiPondNode = myDatabase.getReference("pi1-pond1");
        refPiPondNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "getCriticalLevels: " + dataSnapshot.child("low").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void init_forecastNode(){
        DatabaseReference refForecastNode = myDatabase.getReference("pi1-forecast");
        ForecastResult forecastResult = new ForecastResult();
        ArrayList<Float> result = new ArrayList<>();
        int size = forecast.getAllClock().size();
        for(int i = 0 ; i < size ; i++){
            sme.setValueList(forecast.getClockIndex(i));
            result.add(sme.getBestSME());
        }
        forecastResult.update(result);
        refForecastNode.setValue(forecastResult);
    }

    private String getSystemTime(){
        String forecastTime;
        Integer time;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH");
        time = Integer.valueOf(timeFormat.format(cal.getTime()));
        time += 1;
        if(time < 10){
            forecastTime = "time0" + (time);
        } else if(time >= 23) {
            forecastTime = "time00";
        } else {
            forecastTime = "time" + (time);
        }
        Log.d(TAG, "getSystemTime: timeFormat " + timeFormat.format(cal.getTime()));
        Log.d(TAG, "getSystemTime: time " + time);
        Log.d(TAG, "getSystemTime:  forecastTime " + forecastTime);

        DatabaseReference forecastRef = myDatabase.getReference("pi1-forecast");
        forecastRef.child(forecastTime).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                Integer initialValue = Integer.valueOf((int) Math.round(dataSnapshot.getValue(Double.class) * 10));
                String tempValue = (double) initialValue/10.0 + " °C";
                forecastTemp.setText(tempValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return forecastTime;
    }

    private void getWeatherAndEvaporationRate(){
        weather.search("Manila");
        Integer initialWeatherValue = Integer.valueOf((int) (Math.round(weather.getTemp() * 10)));
        String weatherValue = (double) initialWeatherValue/10.0 + " °C";
        forecastWeather.setText(weatherValue);
        weather.calculateEvaporationRate();
        Log.d(TAG, "getWeatherAndEvaporationRate: EVAP " + weather.getEvaporationRate());
        Integer initialEvapRate = Integer.valueOf((int) (Math.round(weather.getEvaporationRate() * 1000)));
        String evapRate = String.valueOf((double) initialEvapRate/1000.0);
        evaporateRate.setText(evapRate);

        tempData.setText(weather.getTemp() + " °C");
        tempMinData.setText(weather.getTemp_min() + " °C");
        tempMaxData.setText(weather.getTemp_max() + " °C");
        wDesc.setText(weather.getCloud() + " - " + weather.getCloudDescription());
        String dateStr = convertToDate(weather.getDateTime());
        wDateTime.setText(dateStr);
    }

    private void setChannelNames(){
        String piID = HomeActivity.transferData;
        Log.d(TAG, "setChannelNames: device " + piID);
        String path = piID + "-detail";
        Log.d(TAG, "setChannelNames: path " + path);
        final DatabaseReference detailRef = myDatabase.getReference(path);
        detailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: channelName " + dataSnapshot.getValue());
                String ch1Name = dataSnapshot.child("ch1n").getValue(String.class);
                String ch2Name = dataSnapshot.child("ch2n").getValue(String.class);
                String ch3Name = dataSnapshot.child("ch3n").getValue(String.class);

                channel1Text.setText(ch1Name);
                channel2Text.setText(ch2Name);
                channel3Text.setText(ch3Name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String convertToDate(long ts){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ts * 1000);
        String dateString = DateFormat.format("MMMM dd, yyyy - h:mm a", calendar).toString();
        return dateString;
    }
}