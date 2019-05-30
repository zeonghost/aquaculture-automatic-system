package com.example.aquaculture.Model;


public class Smart {
    private int ch1;
    private int ch2;
    private int ch3;

    public Smart() {
        this.ch1 = 0;
        this.ch2 = 0;
        this.ch3 = 0;
    }

    public void turnOnChannel1(float criticalLowTemp, float criticalHighTemp, float waterTemp){
        if(waterTemp >= criticalHighTemp || waterTemp <= criticalLowTemp){
            this.ch1 = 1;
        } else {
            this.ch1 = 0;
        }
    }

    public void turnOnChannel2(int channel2_stat){
        if(channel2_stat == 0){
            this.ch2 = 1;
        } else {
            this.ch2 = 0;
        }
    }

    public int getCh1() {
        return ch1;
    }
}
