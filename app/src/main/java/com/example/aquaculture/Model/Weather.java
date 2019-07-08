package com.example.aquaculture.Model;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Weather extends AsyncTask<String, Void, String> {
    private double temp;
    private double temp_min;
    private double temp_max;
    private String cloud;
    private String cloudDescription;
    private double evaporationRate;
    private long dateTime;

    public Weather() {}

    public double getTemp() {return temp;}

    public void setTemp(double temp) {this.temp = temp;}

    public double getTemp_min() {return temp_min;}

    public void setTemp_min(double temp_min) {this.temp_min = temp_min;}

    public double getTemp_max() {return temp_max;}

    public void setTemp_max(double temp_max) {this.temp_max = temp_max;}

    public String getCloud() {return cloud;}

    public void setCloud(String cloud) {this.cloud = cloud;}

    public String getCloudDescription() {return cloudDescription;}

    public void setCloudDescription(String cloudDescription) {this.cloudDescription = cloudDescription;}

    public double getEvaporationRate() {return evaporationRate;}

    public void setEvaporationRate(double evaporationRate) {this.evaporationRate = evaporationRate;}

    public long getDateTime(){return dateTime;}

    public void search(String cityName){
        try {
            String content = this.execute("https://openweathermap.org/data/2.5/weather?q=" + cityName +"&appid=b6907d289e10d714a6e88b30761fae22").get();

            JSONObject jsonObject = new JSONObject(content);

            String weatherMain = jsonObject.getString("main");
            JSONObject jsonMain = new JSONObject(weatherMain);

            String weatherInfo = jsonObject.getString("weather");
            JSONArray jsonWeather = new JSONArray(weatherInfo);


            for(int i = 0 ; i < jsonWeather.length() ; i++){
                JSONObject weatherData = jsonWeather.getJSONObject(i);
                this.cloud = weatherData.getString("main");
                this.cloudDescription = weatherData.getString("description");
            }

            this.dateTime = jsonObject.getLong("dt");
            this.temp = Double.valueOf(jsonMain.getString("temp"));
            this.temp_min = Double.valueOf(jsonMain.getString("temp_min"));
            this.temp_max = Double.valueOf(jsonMain.getString("temp_max"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void calculateEvaporationRate(){
        double temp_mean = (temp_max + temp_min) / 2;
        this.evaporationRate = 0.0023 * (temp_mean + 17.8) * Math.pow((temp_max - temp_min), 0.5) * 0.082;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "temp=" + temp +
                ", temp_min=" + temp_min +
                ", temp_max=" + temp_max +
                ", cloud='" + cloud + '\'' +
                ", cloudDescription='" + cloudDescription + '\'' +
                ", evaporationRate=" + evaporationRate +
                '}';
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            int data = inputStreamReader.read();
            String content = "";
            char ch;
            while (data != -1){
                ch = (char) data;
                content = content + ch;
                data = inputStreamReader.read();
            }
            return content;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
