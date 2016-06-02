package com.example.lmjin_000.pedarro.station_map;

/**
 * Created by lmjin_000 on 2016-05-21.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lmjin_000.pedarro.R;
import com.example.lmjin_000.pedarro.component.ApplicationController;
import com.example.lmjin_000.pedarro.model.Bike;
import com.example.lmjin_000.pedarro.model.BikeTmap;
import com.example.lmjin_000.pedarro.model.Station;
import com.example.lmjin_000.pedarro.network.NetworkService;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class FragmentA extends Fragment {
    TextView station, rent_bike, park_bike, soon_bike, soon_bike_minute, recommend_bike, jumsu;
    private NetworkService networkService;
    private String station_name;

    public FragmentA(String station_name){
        this.station_name = station_name;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.fragment_a, container, false);

        init(layout);

        ApplicationController application = ApplicationController.getInstance();
        application.buildNetworkService("52.36.42.94", 3000);
        networkService = ApplicationController.getInstance().getNetworkService();

        getStationInfo(station_name);
        GetArriveInfo(station_name);
        GetRecommend(station_name);

        return layout;
    }

    private void init(ViewGroup layout) {
        station = (TextView)layout.findViewById(R.id.station);
        rent_bike = (TextView)layout.findViewById(R.id.rent_bike);
        park_bike = (TextView)layout.findViewById(R.id.park_bike);
        soon_bike = (TextView)layout.findViewById(R.id.soon_bike);
        soon_bike_minute = (TextView)layout.findViewById(R.id.soon_bike_minute);
        recommend_bike = (TextView)layout.findViewById(R.id.recommend_bike);
        jumsu = (TextView)layout.findViewById(R.id.jumsu);
    }

    public void getStationInfo(final String station_name) {
        Call<Station> stationCall = networkService.getStation(station_name);//NetworkService에 등록해둔거 호출
        stationCall.enqueue(new Callback<Station>() {
            @Override
            public void onResponse(Response<Station> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Station station_temp = response.body();
                    station.setText(station_temp.getName());
                    rent_bike.setText("대여 가능 : "+Integer.toString(station_temp.getPark()));
                    park_bike.setText("반납 가능 : "+Integer.toString(station_temp.getAll() - station_temp.getPark()));
                } else {
                    int statusCode = response.code();
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "서버 onFailure 에러내용 : " + t.getMessage());
            }
        });
    }

    void GetArriveInfo(final String station_name){

        Call<List<BikeTmap>> arrivestationCall = networkService.arrive_info(station_name);//NetworkService에 등록해둔거 호출
        arrivestationCall.enqueue(new Callback<List<BikeTmap>>() {
            @Override
            public void onResponse(Response<List<BikeTmap>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<BikeTmap> station_temp = response.body();
                    if(station_temp.size() >= 1) {
                        soon_bike.setText("도착 예정 자전거 : " + Integer.toString(station_temp.size())+"대");
                        if(station_temp.get(0).getTime()/60>0)
                            soon_bike_minute.setText("(최소 " + Integer.toString(station_temp.get(0).getTime()/60)+"분 후)");
                        else
                            soon_bike_minute.setText("(최소 " + Integer.toString(station_temp.get(0).getTime()%60)+"초 후)");
                    }
                } else {
                    int statusCode = response.code();
                    soon_bike.setText("도착 예정 자전거 : 0대");
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "서버 onFailure 에러내용 : " + t.getMessage());
            }
        });
    }
    //자전거 추천
    void GetRecommend(String station_name){

        //poiItem.getItemName()
        Call<List<Bike>> arrivestationCall = networkService.recommend_info(station_name);//NetworkService에 등록해둔거 호출
        arrivestationCall.enqueue(new Callback<List<Bike>>() {
            @Override
            public void onResponse(Response<List<Bike>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Bike> bike_temp = response.body();

                    int su = 0;
                    for(int i=0;i<bike_temp.size();i++){
                        if(bike_temp.get(i).getPoint()>=70)
                            su++;
                    }

                    recommend_bike.setText("추천 자전거 개수 : " + Integer.toString(su)+"대");
                    jumsu.setText("(70점 이상)");
                } else {
                    int statusCode = response.code();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "서버 onFailure 에러내용 : " + t.getMessage());
            }
        });
    }
}