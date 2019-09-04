package com.example.aquaculture;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.aquaculture.Model.Forecast;
import com.example.aquaculture.Model.ForecastResult;
import com.example.aquaculture.Model.SimpleExponentialSmoothing;
import com.example.aquaculture.ViewHolder.ForecastViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ForecastActivityTest extends AppCompatActivity {
    private static final String TAG = "ForecastActivityTest";
    private FirebaseDatabase myDatabase;
    private DatabaseReference myRef;
    private FirebaseRecyclerOptions<Forecast> options;
    private FirebaseRecyclerAdapter<Forecast, ForecastViewHolder> adapter;
    private RecyclerView forecastTable;
    private Map<Float, Float> mseMap;
    private Button btn;
    private Forecast forecast;
    private ArrayList<Float> initializeForecast;
    private SimpleExponentialSmoothing sme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        btn = findViewById(R.id.buttontest);

        forecastTable = findViewById(R.id.recyclerForecast);
        forecastTable.setHasFixedSize(true);
        forecastTable.setLayoutManager(new LinearLayoutManager(this));
        initializeForecast = new ArrayList<>();
        mseMap = new HashMap<>();
        forecast = new Forecast();
        sme = new SimpleExponentialSmoothing();
        myDatabase = FirebaseDatabase.getInstance();
        myRef = myDatabase.getReference("pi1-forecast-test");
//        getData();
        Query query = myRef.orderByChild("time");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snaps : dataSnapshot.getChildren()){
                    if(Objects.equals(snaps.child("time").getValue(String.class), "12:00 AM")){
                        forecast.addClock0(snaps.child("val").getValue(Float.class)/10);
                        Log.d(TAG, "CLOCK 0 SNAP " + snaps.getValue());
                        Log.d(TAG, "CLOCK 0 GET " + forecast.getClock0());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "01:00 AM")){
                        forecast.addClock1(snaps.child("val").getValue(Float.class)/10);
                        Log.d(TAG, "CLOCK 1 SNAP " + snaps.getValue());
                        Log.d(TAG, "CLOCK 1 GET " + forecast.getClock1());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "02:00 AM")){
                        forecast.addClock2(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "03:00 AM")){
                        forecast.addClock3(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "04:00 AM")){
                        forecast.addClock4(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "05:00 AM")){
                        forecast.addClock5(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "06:00 AM")){
                        forecast.addClock6(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "07:00 AM")){
                        forecast.addClock7(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "08:00 AM")){
                        forecast.addClock8(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "09:00 AM")){
                        forecast.addClock9(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "10:00 AM")){
                        forecast.addClock10(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "11:00 AM")){
                        forecast.addClock11(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "12:00 PM")){
                        forecast.addClock12(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "01:00 PM")){
                        forecast.addClock13(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "02:00 PM")){
                        forecast.addClock14(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "03:00 PM")){
                        forecast.addClock15(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "04:00 PM")){
                        forecast.addClock16(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "05:00 PM")){
                        forecast.addClock17(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "06:00 PM")){
                        forecast.addClock18(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "07:00 PM")){
                        forecast.addClock19(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "08:00 PM")){
                        forecast.addClock20(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "09:00 PM")){
                        forecast.addClock21(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "10:00 PM")){
                        forecast.addClock22(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "11:00 PM")){
                        forecast.addClock23(snaps.child("val").getValue(Float.class)/10);
//                        Log.d(TAG, "onDataChange: " + snaps.getValue());
                    }
                }
                forecast.collectClockLists();
                Log.d(TAG, "onDataChange: CLOCK 0" + forecast.getClock0());
                Log.d(TAG, "onDataChange: CLOCK 1" + forecast.getClock1());
                Log.d(TAG, "onDataChange: CLOCK 2" + forecast.getClock2());
                Log.d(TAG, "onDataChange: CLOCK 3" + forecast.getClock3());
                Log.d(TAG, "onDataChange: CLOCK 4" + forecast.getClock4());
                Log.d(TAG, "onDataChange: CLOCK 5" + forecast.getClock5());
                Log.d(TAG, "onDataChange: CLOCK 6" + forecast.getClock6());
                Log.d(TAG, "onDataChange: CLOCK 7" + forecast.getClock7());
                Log.d(TAG, "onDataChange: CLOCK 8" + forecast.getClock8());
                Log.d(TAG, "onDataChange: CLOCK 9" + forecast.getClock9());
                Log.d(TAG, "onDataChange: CLOCK 10" + forecast.getClock10());
                Log.d(TAG, "onDataChange: CLOCK 11" + forecast.getClock11());
                Log.d(TAG, "onDataChange: CLOCK 12" + forecast.getClock12());
                Log.d(TAG, "onDataChange: CLOCK 13" + forecast.getClock13());
                Log.d(TAG, "onDataChange: CLOCK 14" + forecast.getClock14());
                Log.d(TAG, "onDataChange: CLOCK 15" + forecast.getClock15());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        options = new FirebaseRecyclerOptions.Builder<Forecast>().setQuery(query, Forecast.class).build();
        adapter = new FirebaseRecyclerAdapter<Forecast, ForecastViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ForecastViewHolder holder, int position, @NonNull Forecast model) {
                String date = model.getDate();
                String time = model.getTime();
                String val = convertToCelsius(model.getVal());

                holder.timestampForecast.setText("Date: " +date + " Time: " + time);
                holder.tempForecast.setText(val);
            }

            @NonNull
            @Override
            public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(ForecastActivityTest.this).inflate(R.layout.activity_forecast_list, viewGroup, false);
                ForecastViewHolder holder = new ForecastViewHolder(view);
                return holder;
            }
        };
        forecastTable.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init_forecastNode();
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
        Log.d(TAG, "init_forecastNode: FORECAST RESULT " + result);
        refForecastNode.setValue(forecastResult);
    }

    private String convertToCelsius(Integer val){
        float f = (float) val / 10;
        return String.valueOf(f);
    }

    private void getData(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pi1-forecast-test");
        ref.orderByChild("date").startAt("Jun 01, 2019").endAt("Jun 30, 2019").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "DATA: " + dataSnapshot.getValue() );
                for(DataSnapshot s : dataSnapshot.getChildren()){
                    s.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

