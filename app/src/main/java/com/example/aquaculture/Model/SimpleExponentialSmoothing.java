package com.example.aquaculture.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SimpleExponentialSmoothing extends Forecast{
    private float iForecast;
    private float value;
    private float error;
    private float mse;
    private float errorSq;
    private int forecastingPeriod;
    private Map<Float, Float> mseMap;
    private ArrayList<Float> valList;

    public SimpleExponentialSmoothing() {
        this.valList = new ArrayList<>();
        this.mseMap = new HashMap<>();
        this.errorSq = 0;
        this.forecastingPeriod = 0;
    }

    public void setValueList(ArrayList<Float> value){this.valList = new ArrayList<>(value);}

    public ArrayList<Float> getValueList(){return valList;}

    public Map<Float, Float> getMseMap() {return mseMap;}

    public void calculateInitialForecast(ArrayList<Float> clockNum){
        iForecast = 0;
        int size = clockNum.size();
        for(int i = 0 ; i < size ; i++){
            if(i == 6){
                iForecast = iForecast / 6;
                break;
            }
            iForecast += clockNum.get(i);
        }
    }

    public void calculateForecast(ArrayList<Float> clockNum, float alpha){
        int size = clockNum.size();
        for(int i = 0 ; i < size ; i++){
            value = clockNum.get(i);
            error = value - iForecast;
            iForecast = iForecast + (alpha * error);
            if (i > 5) {
                errorSq += (float) Math.pow(error, 2);
                forecastingPeriod++;
            }
        }
        mse = errorSq/forecastingPeriod;
        mseMap.put(mse, iForecast);
    }

    public float getBestSME(){
        this.mseMap.clear();
        calculateInitialForecast(valList);
        calculateForecast(valList, 0.1f);
//        calculateForecast(valList, 0.2f);
//        calculateForecast(valList, 0.3f);
//        calculateForecast(valList, 0.4f);
//        calculateForecast(valList, 0.5f);
//        calculateForecast(valList, 0.6f);
//        calculateForecast(valList, 0.7f);
//        calculateForecast(valList, 0.8f);
//        calculateForecast(valList, 0.9f);

        float smallestMSE = Float.MAX_VALUE;
        for(Map.Entry<Float, Float> e : mseMap.entrySet()){
            if(e.getKey() < smallestMSE){
                smallestMSE = e.getKey();
            }
        }

        this.valList.clear();
        this.errorSq = 0;
        this.forecastingPeriod = 0;
        return mseMap.get(smallestMSE).floatValue();
    }
}
