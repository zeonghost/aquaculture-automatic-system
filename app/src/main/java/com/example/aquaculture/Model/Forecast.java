package com.example.aquaculture.Model;

import java.util.ArrayList;

public class Forecast {
    private String date;
    private String time;
    private Integer val;
    private ArrayList<Float> clock_0 = new ArrayList<>();
    private ArrayList<Float> clock_1 = new ArrayList<>();
    private ArrayList<Float> clock_2 = new ArrayList<>();
    private ArrayList<Float> clock_3 = new ArrayList<>();
    private ArrayList<Float> clock_4 = new ArrayList<>();
    private ArrayList<Float> clock_5 = new ArrayList<>();
    private ArrayList<Float> clock_6 = new ArrayList<>();
    private ArrayList<Float> clock_7 = new ArrayList<>();
    private ArrayList<Float> clock_8 = new ArrayList<>();
    private ArrayList<Float> clock_9 = new ArrayList<>();
    private ArrayList<Float> clock_10 = new ArrayList<>();
    private ArrayList<Float> clock_11 = new ArrayList<>();
    private ArrayList<Float> clock_12 = new ArrayList<>();
    private ArrayList<Float> clock_13 = new ArrayList<>();
    private ArrayList<Float> clock_14 = new ArrayList<>();
    private ArrayList<Float> clock_15 = new ArrayList<>();
    private ArrayList<Float> clock_16 = new ArrayList<>();
    private ArrayList<Float> clock_17 = new ArrayList<>();
    private ArrayList<Float> clock_18 = new ArrayList<>();
    private ArrayList<Float> clock_19 = new ArrayList<>();
    private ArrayList<Float> clock_20 = new ArrayList<>();
    private ArrayList<Float> clock_21 = new ArrayList<>();
    private ArrayList<Float> clock_22 = new ArrayList<>();
    private ArrayList<Float> clock_23 = new ArrayList<>();
    private ArrayList<ArrayList<Float>> allClock = new ArrayList<>();

    public Forecast(String date, String time, Integer val) {
        this.date = date;
        this.time = time;
        this.val = val;
    }

    public Forecast() {}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    public void addClock0(float value){clock_0.add(value);}

    public void addClock1(float value){clock_1.add(value);}

    public void addClock2(float value){clock_2.add(value);}

    public void addClock3(float value){clock_3.add(value);}

    public void addClock4(float value){clock_4.add(value);}

    public void addClock5(float value){clock_5.add(value);}

    public void addClock6(float value){clock_6.add(value);}

    public void addClock7(float value){clock_7.add(value);}

    public void addClock8(float value){clock_8.add(value);}

    public void addClock9(float value){clock_9.add(value);}

    public void addClock10(float value){clock_10.add(value);}

    public void addClock11(float value){clock_11.add(value);}

    public void addClock12(float value){clock_12.add(value);}

    public void addClock13(float value){clock_13.add(value);}

    public void addClock14(float value){clock_14.add(value);}

    public void addClock15(float value){clock_15.add(value);}

    public void addClock16(float value){clock_16.add(value);}

    public void addClock17(float value){clock_17.add(value);}

    public void addClock18(float value){clock_18.add(value);}

    public void addClock19(float value){clock_19.add(value);}

    public void addClock20(float value){clock_20.add(value);}

    public void addClock21(float value){clock_21.add(value);}

    public void addClock22(float value){clock_22.add(value);}

    public void addClock23(float value){clock_23.add(value);}

    public ArrayList<Float> getClock0(){return clock_0;}

    public ArrayList<Float> getClock1(){return clock_1;}

    public ArrayList<Float> getClock2(){return clock_2;}

    public ArrayList<Float> getClock3(){return clock_3;}

    public ArrayList<Float> getClock4(){return clock_4;}

    public ArrayList<Float> getClock5(){return clock_5;}

    public ArrayList<Float> getClock6(){return clock_6;}

    public ArrayList<Float> getClock7(){return clock_7;}

    public ArrayList<Float> getClock8(){return clock_8;}

    public ArrayList<Float> getClock9(){return clock_9;}

    public ArrayList<Float> getClock10(){return clock_10;}

    public ArrayList<Float> getClock11(){return clock_11;}

    public ArrayList<Float> getClock12(){return clock_12;}

    public ArrayList<Float> getClock13(){return clock_13;}

    public ArrayList<Float> getClock14(){return clock_14;}

    public ArrayList<Float> getClock15(){return clock_15;}

    public ArrayList<Float> getClock16(){return clock_16;}

    public ArrayList<Float> getClock17(){return clock_17;}

    public ArrayList<Float> getClock18(){return clock_18;}

    public ArrayList<Float> getClock19(){return clock_19;}

    public ArrayList<Float> getClock20(){return clock_20;}

    public ArrayList<Float> getClock21(){return clock_21;}

    public ArrayList<Float> getClock22(){return clock_22;}

    public ArrayList<Float> getClock23(){return clock_23;}

    public void collectClockLists(){
        allClock.add(clock_0);
        allClock.add(clock_1);
        allClock.add(clock_2);
        allClock.add(clock_3);
        allClock.add(clock_4);
        allClock.add(clock_5);
        allClock.add(clock_6);
        allClock.add(clock_7);
        allClock.add(clock_8);
        allClock.add(clock_9);
        allClock.add(clock_10);
        allClock.add(clock_11);
        allClock.add(clock_12);
        allClock.add(clock_13);
        allClock.add(clock_14);
        allClock.add(clock_15);
        allClock.add(clock_16);
        allClock.add(clock_17);
        allClock.add(clock_18);
        allClock.add(clock_19);
        allClock.add(clock_20);
        allClock.add(clock_21);
        allClock.add(clock_22);
        allClock.add(clock_23);
    }

    public ArrayList<ArrayList<Float>> getAllClock(){return allClock;}

    public ArrayList<Float> getClockIndex(int index){return allClock.get(index);}
}
