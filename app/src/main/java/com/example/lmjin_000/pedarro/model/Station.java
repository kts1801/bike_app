package com.example.lmjin_000.pedarro.model;

/**
 * Created by lmjin_000 on 2016-03-17.
 */
public class Station {
    public String Name;
    public String Lat;
    public String Hard;
    public int All;
    public int Park;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getHard() {
        return Hard;
    }

    public void setHard(String hard) {
        Hard = hard;
    }

    public int getAll() {
        return All;
    }

    public void setAll(int all) {
        All = all;
    }

    public int getPark() {
        return Park;
    }

    public void setPark(int park) {
        Park = park;
    }
}
