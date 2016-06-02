package com.example.lmjin_000.pedarro;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lmjin_000.pedarro.Tmap.RoadFindMain;
import com.example.lmjin_000.pedarro.component.ApplicationController;
import com.example.lmjin_000.pedarro.model.Rent;
import com.example.lmjin_000.pedarro.network.NetworkService;
import com.example.lmjin_000.pedarro.station_map.BikeStationMap;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by lmjin_000 on 2015-12-03.
 */
public class Main extends AppCompatActivity {

    TextView one,using,name;
    Button menu1,menu2,menu3,menu4;
    SharedPreferences setting;
    SharedPreferences.Editor editor;
    private NetworkService networkService;

    @Override
    protected void onResume() {
        super.onResume();
        using.setText("환영합니다");
        Bikeid();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        init();
        setting = getSharedPreferences("setting", 0);
        editor= setting.edit();
        menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BikeRent.class);
                startActivity(intent);
            }
        });
        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BikeStationMap.class);
                startActivity(intent);
            }
        });
        menu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RoadFindMain.class);
                startActivity(intent);
                //Web();
            }
        });
        menu4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserEdit.class);
                startActivity(intent);
            }
        });
    }
    private void init() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/font_L.otf");
        using = (TextView)findViewById(R.id.using);
        one = (TextView)findViewById(R.id.one);
        name = (TextView)findViewById(R.id.user_name);
        menu1 = (Button)findViewById(R.id.menu1);
        menu2 = (Button)findViewById(R.id.menu2);
        menu3 = (Button)findViewById(R.id.menu3);
        menu4 = (Button)findViewById(R.id.menu4);
        one.setTypeface(tf);
    }

    public void Web(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("이용권은 홈페이지에서만\n" +
                "구매하실 수 있습니다.\n" +
                "홈페이지로 이동하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.pedalro.kr/index.do")));
                            }
                        })
                .setNegativeButton("아니요",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void Bikeid() {
        //서버 접속
        ApplicationController application = ApplicationController.getInstance();
        application.buildNetworkService("52.36.42.94", 3000);
        networkService = ApplicationController.getInstance().getNetworkService();

        name.setText(setting.getString("NAME", "")+" 회원님");

        Call<Rent> gcmCall = networkService.usingbike(setting.getString("ID", ""));//NetworkService에 등록해둔거 호출
        gcmCall.enqueue(new Callback<Rent>() {
            @Override
            public void onResponse(Response<Rent> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Rent result = response.body();
                        using.setText("대여 신청 중\n대여번호 : "+result.getNumber());
                } else {
                    int statusCode = response.code();
                    Log.i("메인", "응답코드 : " + statusCode);
                    using.setText("환영합니다");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("메인", "서버 onFailure 에러내용 : " + t.getMessage());
            }
        });
    }
}
