package com.example.lmjin_000.pedarro.model;

/**
 * Created by lmjin_000 on 2016-03-15.
 */
public class LatLng {
    BikeTmap bikeTmap;
    double startX, startY, endX, endY;

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    public BikeTmap getBikeTmap() {
        return bikeTmap;
    }

    public void setBikeTmap(BikeTmap bikeTmap) {
        this.bikeTmap = bikeTmap;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }
}
