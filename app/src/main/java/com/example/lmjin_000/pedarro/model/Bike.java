package com.example.lmjin_000.pedarro.model;

/**
 * Created by lmjin_000 on 2016-05-10.
 */
public class Bike {
    String Bikeid;
    float Point;
    String Detail;
    String Station;

    public String getStation() {
        return Station;
    }

    public void setStation(String station) {
        Station = station;
    }

    public String getBikeid() {
        return Bikeid;
    }

    public void setBikeid(String bikeid) {
        Bikeid = bikeid;
    }

    public float getPoint() {
        return Point;
    }

    public void setPoint(float point) {
        Point = point;
    }

    public String getDetail() {
        return Detail;
    }

    public void setDetail(String detail) {
        Detail = detail;
    }
}
