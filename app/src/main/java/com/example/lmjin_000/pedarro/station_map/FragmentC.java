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
import com.example.lmjin_000.pedarro.network.NetworkService;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class FragmentC extends Fragment {
    private TextView recommend_bike1,recommend_bike2,recommend_bike1_rate,recommend_bike2_rate,recommend_bike1_detail,recommend_bike2_detail;
    private NetworkService networkService;
    private String station_name;

    public FragmentC(String station_name){
        this.station_name = station_name;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.fragment_c, container, false);

        init(layout);

        ApplicationController application = ApplicationController.getInstance();
        application.buildNetworkService("52.36.42.94", 3000);
        networkService = ApplicationController.getInstance().getNetworkService();

        GetRecommend(station_name);

        return layout;
    }

    private void init(ViewGroup layout) {
        recommend_bike1 = (TextView)layout.findViewById(R.id.recommend_bike1);
        recommend_bike2 = (TextView)layout.findViewById(R.id.recommend_bike2);
        recommend_bike1_rate = (TextView)layout.findViewById(R.id.recommend_bike1_rate);
        recommend_bike2_rate = (TextView)layout.findViewById(R.id.recommend_bike2_rate);
        recommend_bike1_detail = (TextView)layout.findViewById(R.id.recommend_bike1_detail);
        recommend_bike2_detail = (TextView)layout.findViewById(R.id.recommend_bike2_detail);
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
                    if (bike_temp.size() == 1) {
                        recommend_bike1.setText(bike_temp.get(0).getBikeid()+"번 자전거");
                        recommend_bike1_rate.setText(Float.toString(bike_temp.get(0).getPoint()) + "점");
                        recommend_bike1_detail.setText(bike_temp.get(0).getDetail());
                    } else if (bike_temp.size() >= 2) {
                        recommend_bike1.setText(bike_temp.get(0).getBikeid()+"번 자전거");
                        recommend_bike2.setText(bike_temp.get(1).getBikeid()+"번 자전거");

                        recommend_bike1_rate.setText(Float.toString(bike_temp.get(0).getPoint()) + "점");
                        recommend_bike2_rate.setText(Float.toString(bike_temp.get(1).getPoint()) + "점");

                        recommend_bike1_detail.setText(bike_temp.get(0).getDetail());
                        recommend_bike2_detail.setText(bike_temp.get(1).getDetail());
                    }
                } else {
                    int statusCode = response.code();
                    recommend_bike1.setText("보관된 자전거가 없습니다.");
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "서버 onFailure 에러내용 : " + t.getMessage());
            }
        });
    }

}