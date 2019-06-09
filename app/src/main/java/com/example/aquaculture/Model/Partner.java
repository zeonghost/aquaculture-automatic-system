package com.example.aquaculture.Model;

public class Partner {
    private String username;
    private String fullname;
    private String device;
    private String id;

    public Partner() {
    }

    public Partner(String username, String fullname, String device, String id) {
        this.username = username;
        this.fullname = fullname;
        this.device = device;
        this.id = id;
    }

    public Partner(String username, String fullname, String device){
        this.username = username;
        this.fullname = fullname;
        this.device = device;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
