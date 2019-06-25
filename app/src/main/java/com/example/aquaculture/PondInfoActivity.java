package com.example.aquaculture;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

//import com.example.aquaculture.Model.Loga;
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
    private CardView tempSet;
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
    private Switch channel1;
    private Switch channel2;
    private Switch channel3;
    private SharedPreferences sp;
    private static long lastClickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_pond_info);
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#148D7F"));

        piID = findViewById(R.id.txtViewPiID);
        pondName = findViewById(R.id.txtViewPondName);
        location = findViewById(R.id.txtViewPondLocation);
        graphCheck = findViewById(R.id.cardView);
        lineChart = findViewById(R.id.lineChart);
        tempSet = findViewById(R.id.cardViewTempSet);
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
        channel1 = findViewById(R.id.ch1s);
        channel2 = findViewById(R.id.ch2s);
        channel3 = findViewById(R.id.ch3s);
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        startingGraph();
        basicReadWrite();
        buttomNavigation();
        logRead();
    }

    @Override
    protected void onStart() {
        super.onStart();
        lineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toGraphActivity = new Intent (PondInfoActivity.this, GraphTempActivity.class);
                startActivity(toGraphActivity);
            }
        });

        tempSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempSetDialog();
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

    /*******************
     * FUNCTIONS
     *******************/

    public void startingGraph(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("pi1-temp");
        final ArrayList<Entry> yValues = new ArrayList<>();
        lineChart.setDragEnabled(false);
        lineChart.setEnabled(true);
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setHighlightPerTapEnabled(false);
        lineChart.getDescription().setText("Within 6 Hours Time");
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
                lineDataSet.setCircleHoleRadius(-1);
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
                final Switch sw = (Switch) findViewById(R.id.autosw);

                if(val4 == 1){
                    sw.setChecked(true);
                    tempSet.setEnabled(false);
                    channel1.setEnabled(false);
                    channel2.setEnabled(false);
                    channel3.setEnabled(false);
                }
                else{
                    sw.setChecked(false);
                    tempSet.setEnabled(true);
                    channel1.setEnabled(true);
                    channel2.setEnabled(true);
                    channel3.setEnabled(true);
                }//check auto model status and change switch display

                sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(!isFastDoubleClick()){
                            if (isChecked) {
                                myRef1.child("auto").setValue(1);
                                Toast.makeText(PondInfoActivity.this, "Automatic Model ON", Toast.LENGTH_SHORT).show();//show message
                                String log = un + " turned on Automatic Model";
                                Long time = System.currentTimeMillis();
                                Map<String, Object> logPut = new HashMap<>();
                                logPut.put("logDetail", log);
                                logPut.put("logTime", time);
                                String key = logWrite.push().getKey();
                                logWrite.child(key).updateChildren(logPut);
                            } else {
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
                        }
                        else{
                            Toast.makeText(PondInfoActivity.this, "Operation Too Fast", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                final Integer val1 = dataSnapshot.child("ch1").getValue(Integer.class);
                //final Button btr1 = (Button) findViewById(R.id.ch1);
                if(val1 ==1)//1 means on
                {
                    channel1.setChecked(true);//change color
                }
                if(val1 ==0)//0 means off
                {
                    channel1.setChecked(false);
                }

                channel1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(!isFastDoubleClick()) {
                            if (isChecked) {
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
                            else{
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
                        }
                        else{
                            Toast.makeText(PondInfoActivity.this, "Operation Too Fast", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final Integer val2 = dataSnapshot.child("ch2").getValue(Integer.class);
                if(val2 ==1)
                {
                    channel2.setChecked(true);
                }
                if(val2 ==0)
                {
                    channel2.setChecked(false);
                }
                channel2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(!isFastDoubleClick()) {
                            if (isChecked) {
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
                        else{
                            Toast.makeText(PondInfoActivity.this, "Operation Too Fast", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final Integer val3 = dataSnapshot.child("ch3").getValue(Integer.class);
                if(val3 == 1)
                {
                    channel3.setChecked(true);
                }
                if(val3 == 0)
                {
                    channel3.setChecked(false);
                }
                channel3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(!isFastDoubleClick()) {
                            if (isChecked) {
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
                        else{
                            Toast.makeText(PondInfoActivity.this, "Operation Too Fast", Toast.LENGTH_SHORT).show();
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
                        if(Objects.equals(sp.getString("role",""), "Partner")){
                            Intent intent0 = new Intent(PondInfoActivity.this, PartnerLogActivity.class);
                            startActivity(intent0);
                        } else {
                            Intent intent1 = new Intent(PondInfoActivity.this, HomeActivity.class);
                            startActivity(intent1);
                        }
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
                String un = sp.getString("username", "");
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
}
