package com.example.lmjin_000.pedarro.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lmjin_000 on 2016-03-10.
 * mySQL에 있는 디비정보와 같아야함(이름과 형이 무조건 같아야한다)
 * 이건 MYSQL에서 TMAP 테이블에 해당하는 정보
 * 서버와 통신을 하면 디비 정보를 JSON형식으로 받아온다.
 */
public class BikeTmap implements Parcelable {
    String Bike;
    String UserID;
    String Arrivest;
    int Time;


    public String getBike() {
        return Bike;
    }

    public void setBike(String bike) {
        Bike = bike;
    }

    public String getArrivest() {
        return Arrivest;
    }

    public void setArrivest(String arrivest) {
        Arrivest = arrivest;
    }

    public int getTime() {
        return Time;
    }

    public void setTime(int time) {
        Time = time;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
//////

    public BikeTmap(){ }

    protected BikeTmap(Parcel in) { //parcel 데이터로 전달된 것을 객체형으로 만드는 생성자
        Bike = in.readString();
        UserID= in.readString();
        Arrivest= in.readString();
        Time = in.readInt();
    }

    public static final Creator<BikeTmap> CREATOR = new Creator<BikeTmap>() { //parcel 데이터를 객체로 다시 만들어 주는 단계.
        @Override
        public BikeTmap createFromParcel(Parcel in) {
            return new BikeTmap(in);
        }

        @Override
        public BikeTmap[] newArray(int size) { return new BikeTmap[size];}
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {   //객체를 parcel 데이터로 만드는 단계
        dest.writeString(Bike);
        dest.writeString(UserID);
        dest.writeString(Arrivest);
        dest.writeInt(Time);
    }
}
