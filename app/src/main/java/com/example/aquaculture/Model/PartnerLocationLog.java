package com.example.aquaculture.Model;

public class PartnerLocationLog extends Partner {
    /*
        Get the following private data from Partner Class
        fullname
        device
        username
     */
    private long timeIn;
    private long timeOut;
    private double latitude;
    private double longitude;

    public PartnerLocationLog(){}

    public PartnerLocationLog(String username, String fullname, String device, long timeIn, long timeOut, double latitude, double longitude) {
        super(username, fullname, device);
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(long timeIn) {
        this.timeIn = timeIn;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
