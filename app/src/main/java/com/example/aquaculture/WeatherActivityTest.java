package com.example.aquaculture;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.aquaculture.Model.Weather;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;

public class WeatherActivityTest extends AppCompatActivity {
    private static final String TAG = "LOG";
    private EditText inputCityName;
    private String cityName;
    private Button getWeatherInfo;

    private TextView tempData;
    private TextView tempMinData;
    private TextView tempMaxData;
    private TextView wDesc;
    private TextView wDateTime;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    private Weather w;
    private String dateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_test);
        inputCityName = findViewById(R.id.editTxtCityName);
        getWeatherInfo = findViewById(R.id.btnGetWeatherInfo);
        tempData = findViewById(R.id.txtViewTemp);
        tempMinData = findViewById(R.id.txtViewTempMin);
        tempMaxData = findViewById(R.id.txtViewTempMax);
        wDesc = findViewById(R.id.txtViewWeatherDesc);
        wDateTime = findViewById(R.id.txtViewWeatherDate);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("pi1-evap");
        w = new Weather();
        w.search("Manila");
        w.calculateEvaporationRate();
        dateString = convertToDate(w.getDateTime());


        getWeatherInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateStr;
                cityName = inputCityName.getText().toString();
                Weather weather = new Weather();
                weather.search(cityName);
                weather.calculateEvaporationRate();
                Log.d(TAG, "onClick: " + weather.toString());
                tempData.setText(weather.getTemp() + " °C");
                tempMinData.setText(weather.getTemp_min() + " °C");
                tempMaxData.setText(weather.getTemp_max() + " °C");
                wDesc.setText(weather.getCloud() + " - " + weather.getCloudDescription());
                dateStr = convertToDate(weather.getDateTime());
                wDateTime.setText(dateStr);
            }
        });


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                if(!dataSnapshot.exists()){
                    reference.child(dateString).setValue(w.getEvaporationRate());
                }


                for(DataSnapshot snaps : dataSnapshot.getChildren()){
                    String date_in_database = snaps.getKey();
                    Log.d(TAG, "Date Keys: " + date_in_database);
                    if(Objects.equals(date_in_database, "August 28, 2019")){
                        Log.d(TAG, "TODAY IS AUG 28" );
                    }
                }
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
