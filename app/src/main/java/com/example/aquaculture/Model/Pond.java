package com.example.aquaculture.Model;

import com.google.firebase.database.PropertyName;

public class Pond {
    private String piId;
    private String pondName;
    //private String channel1;
    //private String channel2;
    //private String channel3;
    private String location;
    //private String species;
    //private Integer depth;
    //private Integer length;
    //private Integer width;

    public Pond(){} //DO NOT DELETE EMPTY CONSTRUCTOR

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
