package com.example.aquaculture;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

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
    private LineChart lineChart;
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
    private ImageButton channel1;
    private ImageButton channel2;
    private ImageButton channel3;
    private SharedPreferences sp;
    private static long lastClickTime;
    private boolean isGraphVisible;

    private FirebaseDatabase myDatabase;
    private DatabaseReference myRef;
    private Forecast forecast;
    private SimpleExponentialSmoothing sme;
    private Weather weather;

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
        lineChart = findViewById(R.id.lineChart);
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
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        isGraphVisible = false;
        lineChart.setVisibility(View.GONE);
        forecast = new Forecast();
        sme = new SimpleExponentialSmoothing();
        weather = new Weather();

        startingTempGraph();
        basicReadWrite();
        buttomNavigation();
        logRead();
    }

    @Override
    protected void onStart() {
        super.onStart();
        graphCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isGraphVisible){
                    lineChart.setVisibility(View.GONE);
                    isGraphVisible = false;
                } else {
                    lineChart.setVisibility(View.VISIBLE);
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

        logview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toGraphActivity = new Intent (PondInfoActivity.this, LogViewActivity.class);
                startActivity(toGraphActivity);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        String counter = getSystemTime();
        if(Objects.equals(counter, "time00")){
            getForecast();
        }
        getWeatherAndEvaporationRate();
        Log.d(TAG, "onResume: CALL EVAP FUNCTIUON");
    }

    public void startingTempGraph(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("pi1-temp");
        final ArrayList<Entry> yValues = new ArrayList<>();
        lineChart.setDragEnabled(false);
        lineChart.setEnabled(true);
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setHighlightPerTapEnabled(false);
        lineChart.getDescription().setText("Past 6 Hours Time");
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
                lineDataSet.setCircleHoleRadius(0);
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
                    //Log.d(TAG, "Result111: "+ logDetail);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                    Long logTime = snaps.child("logTime").getValue(Long.class);
                    //Log.d(TAG, "Result112: "+ logTime);
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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
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
        else
        {
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
                                    //String msg = getString(R.string.msg_subscribed);
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(PondInfoActivity.this, "Subscribe ERROR", Toast.LENGTH_SHORT).show();
                                    }
                                    Toast.makeText(PondInfoActivity.this, "Subscribe Success!", Toast.LENGTH_SHORT).show();
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
                                    //String msg = getString(R.string.msg_subscribed);
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(PondInfoActivity.this, "Unsubscribe ERROR", Toast.LENGTH_SHORT).show();
                                    }
                                    Toast.makeText(PondInfoActivity.this, "Unsubscribe Success!", Toast.LENGTH_SHORT).show();
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
                //final Switch sw = (Switch) findViewById(R.id.autosw);
                final ImageButton sw = (ImageButton)findViewById(R.id.autobtr);

                if(val4 == 1){
                    sw.setImageResource(R.drawable.icons_switch_on_s);
                    tempSetting.setEnabled(false);
                    channel1.setEnabled(false);
                    channel2.setEnabled(false);
                    channel3.setEnabled(false);
                }
                else{
                    sw.setImageResource(R.drawable.icons_switch_off_s);
                    tempSetting.setEnabled(true);
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
                //final Button btr1 = (Button) findViewById(R.id.ch1);
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
                            Toast.makeText(PondInfoActivity.this, "Turned off ch 1", Toast.LENGTH_SHORT).show();
                            String log = un + " turned off ch 1";
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
                            Toast.makeText(PondInfoActivity.this, "Turned on ch 1", Toast.LENGTH_SHORT).show();
                            String log = un + " turned on ch 1";
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
                            Toast.makeText(PondInfoActivity.this, "Turned off ch 2", Toast.LENGTH_SHORT).show();
                            String log = un + " turned off ch 2";
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
                            Toast.makeText(PondInfoActivity.this, "Turned on ch 2", Toast.LENGTH_SHORT).show();
                            String log = un + " turned on ch 2";
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
                            Toast.makeText(PondInfoActivity.this, "Turned off ch 3", Toast.LENGTH_SHORT).show();
                            String log = un + " turned off ch 3";
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
                            Toast.makeText(PondInfoActivity.this, "Turned on ch 3", Toast.LENGTH_SHORT).show();
                            String log = un + " turned on ch3";
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

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
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

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                top.setText(dataSnapshot.child("high").getValue().toString());
                bottom.setText(dataSnapshot.child("low").getValue().toString());
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

                int topnum = Integer.parseInt(top.getText().toString());
                int botnum = Integer.parseInt(bottom.getText().toString());
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
                String log = un + " edit the highest Critical temp to "+ topnum+" lowest Critical temp to "+botnum+" ch2 on time to "+onTime+" off time to "+ offTime;
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
        } else if(time == 24) {
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
    }

}