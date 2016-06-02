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
import com.example.lmjin_000.pedarro.model.BikeTmap;
import com.example.lmjin_000.pedarro.network.NetworkService;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class FragmentB extends Fragment {
    private TextView arrive_bike1,arrive_bike2,arrive_bike1_time2,arrive_bike2_time2;
    private NetworkService networkService;
    private String station_name;

    public FragmentB(String station_name){
        this.station_name = station_name;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.fragment_b, container, false);

        init(layout);

        ApplicationController application = ApplicationController.getInstance();
        application.buildNetworkService("52.36.42.94", 3000);
        networkService = ApplicationController.getInstance().getNetworkService();

        GetArriveInfo(station_name);

        return layout;
    }

    private void init(ViewGroup layout) {
        arrive_bike1 = (TextView)layout.findViewById(R.id.arrive_bike1);
        arrive_bike2 = (TextView)layout.findViewById(R.id.arrive_bike2);
        arrive_bike1_time2 = (TextView)layout.findViewById(R.id.arrive_bike1_time2);
        arrive_bike2_time2 = (TextView)layout.findViewById(R.id.arrive_bike2_time2);
    }

    void GetArriveInfo(final String station_name){

        //poiItem.getItemName()
        Call<List<BikeTmap>> arrivestationCall = networkService.arrive_info(station_name);//NetworkService에 등록해둔거 호출
        arrivestationCall.enqueue(new Callback<List<BikeTmap>>() {
            @Override
            public void onResponse(Response<List<BikeTmap>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<BikeTmap> station_temp = response.body();
                    /*
                    int cnt = 0;
                    for(int i=0;i<station_temp.size();i++)
                    {
                        if(station_temp.get(i).getTime()<=0){
                            cnt++;
                        }
                    }*/
                    if(station_temp.size() == 1){
                        arrive_bike1.setText(station_temp.get(0).getBike()+"번 자전거");
                        arrive_bike1_time2.setText("> 최소 "+station_temp.get(0).getTime() / 60 + "분 "+ station_temp.get(0).getTime() % 60+"초 후 도착");
                    }else if(station_temp.size()>=2){
                        arrive_bike1.setText(station_temp.get(0).getBike()+"번 자전거");
                        arrive_bike1_time2.setText("> 최소 "+station_temp.get(0).getTime() / 60 + "분 "+ station_temp.get(0).getTime() % 60+"초 후 도착");
                        arrive_bike2.setText(station_temp.get(1).getBike()+"번 자전거");
                        arrive_bike2_time2.setText("> 최소 "+station_temp.get(1).getTime() / 60 + "분 "+ station_temp.get(1).getTime() % 60+"초 후 도착");
                    }
                } else {
                    int statusCode = response.code();
                    arrive_bike1.setText("도착 예정 자전거가 없습니다.");
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