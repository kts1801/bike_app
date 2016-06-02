package com.example.lmjin_000.pedarro.Tmap.ListView;

/**
 * Created by lmjin_000 on 2016-05-24.
 */
public class Listviewitem {
    private String txt;
    private int img;

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTxt() {
        return txt;
    }

    public Listviewitem(String txt,int img){
        this.img = img;
        this.txt=txt;
    }
}
