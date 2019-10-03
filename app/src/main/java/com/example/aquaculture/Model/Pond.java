package com.example.aquaculture.Model;

import com.google.firebase.database.PropertyName;

public class Pond {
    private String piId;
    private String pondName;
    private String location;

    public Pond(){}

    public Pond(String piId, String pondName, String location){
        this.piId = piId;
        this.pondName = pondName;
        this.location = location;
    }

    @PropertyName("PiId")
    public String getPiId() {
        return piId;
    }

    @PropertyName("PondName")
    public String getPondName() {
        return pondName;
    }

    @PropertyName("location")
    public String getLocation() {
        return location;
    }
}
