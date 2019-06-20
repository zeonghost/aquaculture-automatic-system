package com.example.aquaculture.Model;

public class Temperature {
    long time;
    Integer val;

    public Temperature() {}

    public Temperature(long time, Integer val) {
        this.time = time;
        this.val = val;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }
}
