package com.example.aquaculture;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.aquaculture.Model.Weather;

public class WeatherActivityTest extends AppCompatActivity {
    private static final String TAG = "LOG";
    private EditText inputCityName;
    private Button getWeatherInfo;
    private TextView tempData;
    private TextView tempMinData;
    private TextView tempMaxData;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_test);
        inputCityName = findViewById(R.id.editTxtCityName);
        getWeatherInfo = findViewById(R.id.btnGetWeatherInfo);
        tempData = findViewById(R.id.txtViewTemp);
        tempMinData = findViewById(R.id.txtViewTempMin);
        tempMaxData = findViewById(R.id.txtViewTempMax);

        getWeatherInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityName = inputCityName.getText().toString();
                Weather weather = new Weather();
                weather.search(cityName);
                weather.calculateEvaporationRate();
                Log.d(TAG, "onClick: " + weather.toString());
                tempData.setText(String.valueOf(weather.getTemp()));
                tempMinData.setText(String.valueOf(weather.getTemp_min()));
                tempMaxData.setText(String.valueOf(weather.getTemp_max()));

            }
        });
    }
}
