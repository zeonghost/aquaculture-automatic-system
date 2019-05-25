package com.example.aquaculture;

import android.app.ActivityOptions;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.FirebaseApp;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PondInfoActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private CardView graphCheck;
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        basicReadWrite();
        setContentView(R.layout.activity_pond_info);
        setTitle("Pond Details");

        graphCheck = findViewById(R.id.cardView);
        lineChart = findViewById(R.id.lineChart);
        startingGraph();

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

    public void basicReadWrite() {
        String getData = HomeActivity.transferData;
        Log.d(TAG, "Result-transfer Data: "+ getData);
        String path1 = getData + "-detail/location";
        String path2 = getData + "-pond1";

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(path1);
        final DatabaseReference myRef1 = database.getReference(path2);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                TextView textElement = (TextView) findViewById(R.id.locate);
                textElement.setText(value);
                //get data from db
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            } 
        });

        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Float tempRead = dataSnapshot.child("temp").getValue(Float.class) / 10;
                TextView textElement = (TextView) findViewById(R.id.tempRead);
                textElement.setText(tempRead.toString() + " °C");
                //get current temperature data from database and display

                final Integer val4 = dataSnapshot.child("auto").getValue(Integer.class);
                final Switch sw = (Switch) findViewById(R.id.autosw);
                if(val4 == 1){
                    sw.setChecked(true);
                }
                else{
                    sw.setChecked(false);
                }//check auto model status and change switch display
                sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            myRef1.child("auto").setValue(1);
                            Toast.makeText(PondInfoActivity.this, "Automatic Model ON", Toast.LENGTH_SHORT).show();//show message
                        } else {
                            myRef1.child("auto").setValue(0);
                            Toast.makeText(PondInfoActivity.this, "Automatic Model OFF", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final Integer val1 = dataSnapshot.child("ch1").getValue(Integer.class);
                final Button btr1 = (Button) findViewById(R.id.ch1);
                if(val1 ==1)//1 means on
                    {
                        btr1.setBackgroundColor(Color.RED);//change color
                    }
                if(val1 ==0)//0 means off
                    {
                        btr1.setBackgroundColor(Color.GREEN);
                    }
                btr1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick (View v){
                        if (val1 == 1) {
                            myRef1.child("ch1").setValue(0);//if click then change status
                            Toast.makeText(PondInfoActivity.this, "Turned off ch 1", Toast.LENGTH_SHORT).show();
                        }
                        if (val1 == 0) {
                            myRef1.child("ch1").setValue(1);
                            Toast.makeText(PondInfoActivity.this, "Turned on ch 1", Toast.LENGTH_SHORT).show();
                        }
                    }
                    });

                final Integer val2 = dataSnapshot.child("ch2").getValue(Integer.class);
                final Button btr2 = (Button) findViewById(R.id.ch2);
                if(val2 ==1)
                    {
                        btr2.setBackgroundColor(Color.RED);
                    }
                if(val2 ==0)
                    {
                        btr2.setBackgroundColor(Color.GREEN);
                    }
                btr2.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick (View v){
                        if (val2 == 1) {
                            myRef1.child("ch2").setValue(0);
                            Toast.makeText(PondInfoActivity.this, "Turned off ch 2", Toast.LENGTH_SHORT).show();
                        }
                        if (val2 == 0) {
                            myRef1.child("ch2").setValue(1);
                            Toast.makeText(PondInfoActivity.this, "Turned on ch 2", Toast.LENGTH_SHORT).show();
                        }
                    }
                    });

                final Integer val3 = dataSnapshot.child("ch3").getValue(Integer.class);
                final Button btr3 = (Button) findViewById(R.id.ch3);
                if(val3 == 1)
                {
                    btr3.setBackgroundColor(Color.RED);
                }
                if(val3 == 0)
                {
                    btr3.setBackgroundColor(Color.GREEN);
                }
                btr3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(val3 == 1)
                        {
                            myRef1.child("ch3").setValue(0);
                            Toast.makeText(PondInfoActivity.this, "Turned off ch 3", Toast.LENGTH_SHORT).show();
                        }
                        if(val3 == 0)
                        {
                            myRef1.child("ch3").setValue(1);
                            Toast.makeText(PondInfoActivity.this, "Turned on ch 3", Toast.LENGTH_SHORT).show();
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
    }

    public void startingGraph(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("pi1-temp");
        final ArrayList<Entry> yValues = new ArrayList<>();
        lineChart.setDragEnabled(false);
        lineChart.setEnabled(true);
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setHighlightPerTapEnabled(false);
        lineChart.getDescription().setText("Within 12 Hours Time");
        lineChart.getLegend().setEnabled(false);

        long today = Calendar.getInstance().getTimeInMillis();
        today /= 1000;
        Query q = myRef.orderByChild("time").startAt(today - 43200).endAt(today);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Count: " + dataSnapshot.getChildrenCount());
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
                lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
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

            }
        });
    }
}
