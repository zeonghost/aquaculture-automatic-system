package com.example.aquaculture.Model;


public class pondAdd {
    public String piID;
    public String pondName;
    public String ch1n;
    public String ch2n;
    public String ch3n;
    public String location;
    public String species;
    public Integer depth;
    public Integer length;
    public Integer width;

    public pondAdd(String piId, String pondName, String location, String channel1, String channel2, String channel3, String species, Integer width, Integer length, Integer depth){
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
