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
/*
    public Pond(String piId, String pondName, String location, String channel1, String channel2, String channel3, String species, Integer width, Integer length, Integer depth){
        this.piId = piId;
        this.pondName = pondName;
        this.location = location;
        this.channel1 = channel1;
        this.channel2 = channel2;
        this.channel3 = channel3;
        this.species = species;
        this.width = width;
        this.length = length;
        this.depth = depth;
    }
    */
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
