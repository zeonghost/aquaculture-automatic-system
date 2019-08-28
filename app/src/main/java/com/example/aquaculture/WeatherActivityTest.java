package com.example.aquaculture;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.aquaculture.Model.User;
import com.example.aquaculture.Model.Weather;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EventListener;

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

    private FirebaseDatabase myDatabase;
    private LineChart lineChartForecastGraph;
    private Weather EvapRate;
    private double evaporationRate;
    private DatabaseReference ref;
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
        myDatabase = FirebaseDatabase.getInstance();

        EvapRate = new Weather();
        EvapRate.search("Manila");
        EvapRate.calculateEvaporationRate();
        evaporationRate = EvapRate.getEvaporationRate();
        final String dateString = convertDate(EvapRate.getDateTime());
        wDateTime.setText(dateString);

        //String key = ref.push().getKey();

        ref = myDatabase.getReference("pi1-evap");
//        ref.child(dateString).child("value").setValue(evaporationRate);
    ref.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
    {
        Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
    for(DataSnapshot snapshot: dataSnapshot.getChildren())
    {
        snapshot.getKey();
        String date=snapshot.getKey();
        if(date.equals(dateString))
        {
            double container = snapshot.child("value").getValue(Double.class);
            Log.d(TAG, "CONTAINER "+ container);
            Log.d(TAG, "CONTAINER "+ evaporationRate);
            if (container < evaporationRate)
            {
                Log.d(TAG, "container is less than evaporation rate ");
                snapshot.getRef().child("value").setValue(evaporationRate);
            }
        }
    }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});


//private void getEvapGrap()
//{
//    DatabaseReference myEvapRef = FirebaseDatabase.getInstance().getReference("pi1-evap");
//    final ArrayList<Entry> yValues = new ArrayList<>();
//    lineChartForecastGraph.setDragEnabled(false);
//    lineChartForecastGraph.setEnabled(true);
//    lineChartForecastGraph.setPinchZoom(false);
//    lineChartForecastGraph.setDoubleTapToZoomEnabled(false);
//    lineChartForecastGraph.setHighlightPerTapEnabled(false);
//    lineChartForecastGraph.getDescription().setText("Evaporation");
//    lineChartForecastGraph.getLegend().setEnabled(false);
//    myEvapRef.addValueEventListener(new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//        }
//    }alue)
//}

    }


    private String convertDate(long ts){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ts * 1000);
        String dateString = DateFormat.format("MMMM dd, yyyy", calendar).toString();
        return dateString;
    }
}
