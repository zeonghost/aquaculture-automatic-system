package com.example.aquaculture.Model;


public class pondAdd {
    public String piID;
    public String pondName;
    public String ch1n;
    public String ch2n;
    public String ch3n;
    public String location;
    public String species;
    public Float depth;
    public Float length;
    public Float width;

    public pondAdd(String piId, String pondName, String location, String channel1, String channel2, String channel3, String species, Float width, Float length, Float depth){
        this.piID = piId;
        this.pondName = pondName;
        this.location = location;
        this.ch1n = channel1;
        this.ch2n = channel2;
        this.ch3n = channel3;
        this.species = species;
        this.width = width;
        this.length = length;
        this.depth = depth;
    }
}
