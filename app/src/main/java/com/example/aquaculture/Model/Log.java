package com.example.aquaculture.Model;

import com.google.firebase.database.PropertyName;

public class Log {
    private long logTime;
    private String logDetail;
    private String username;

    public Log(){}

    public Log(long logTime, String logDetail, String username){
        this.logTime = logTime;
        this.logDetail = logDetail;
        this.username = username;
    }

    @PropertyName("logDetail")
    public String getdetail() {
        return logDetail;
    }

    @PropertyName("logTime")
    public long getTime() {
        return logTime;
    }

    @PropertyName("username")
    public String getUsername() {
        return username;
    }

}
