package com.example.aquaculture.Model;

import com.google.firebase.database.PropertyName;

public class Log {
    private long logTime;
    private String logDetail;

    public Log(){}

    public Log(long logTime, String logDetail){
        this.logTime = logTime;
        this.logDetail = logDetail;
    }

    @PropertyName("logDetail")
    public String getdetail() {
        return logDetail;
    }

    @PropertyName("logTime")
    public long getTime() {
        return logTime;
    }

}
