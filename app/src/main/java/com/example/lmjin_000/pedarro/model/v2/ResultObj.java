package com.example.lmjin_000.pedarro.model.v2;

import java.util.List;

/**
 * Created by lmjin_000 on 2016-05-24.
 */
public class ResultObj {
    List<Properties2> properties;
    int totaltime;
    int totaldistance;

    public int getTotaltime() {
        return totaltime;
    }

    public void setTotaltime(int totaltime) {
        this.totaltime = totaltime;
    }

    public int getTotaldistance() {
        return totaldistance;
    }

    public void setTotaldistance(int totaldistance) {
        this.totaldistance = totaldistance;
    }

    public List<Properties2> getProperties() {
        return properties;
    }

    public void setProperties(List<Properties2> properties) {
        this.properties = properties;
    }
}
