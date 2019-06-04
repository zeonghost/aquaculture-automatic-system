package com.example.aquaculture.Model;

import com.google.firebase.database.PropertyName;

public class Log {
    private long time;
    private String detail;

    public Log(){}

    public Log(long time, String detail){
        this.time = time;
        this.detail = detail;
    }

    @PropertyName("log_detail")
    public String getdetail() {
        return detail;
    }

    @PropertyName("log_time")
    public long getTime() {
        return time;
    }

}
