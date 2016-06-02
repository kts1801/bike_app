package com.example.lmjin_000.pedarro.Tmap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lmjin_000.pedarro.R;
import com.example.lmjin_000.pedarro.RoadStationFind;

/**
 * Created by lmjin_000 on 2016-05-24.
 */
public class RoadFindMain extends AppCompatActivity {
    EditText startStation, endStation;
    Button resultbtn,startbtn, endbtn;

    SharedPreferences setting;

    @Override
    protected void onResume() {
        super.onResume();

        setting = getSharedPreferences("setting", 0);
        startStation.setText(setting.getString("RoadStart", "검색 도구를 통해 검색하세요"));
        endStation.setText(setting.getString("RoadEnd", "검색 도구를 통해 검색하세요"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //액션바
        try {
            getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //  getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);
            getSupportActionBar().setCustomView(R.layout.titlebar_roadfindmain);        //커스텀 액션 바
        }catch (Exception e) {
            Log.i("actionbar", e.getMessage());
        }
        setContentView(R.layout.road_main);

        init();
        setting = getSharedPreferences("setting", 0);

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RoadStationFind.class);
                intent.putExtra("Type", "출발");
                startActivity(intent);
            }
        });
        startStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RoadStationFind.class);
                intent.putExtra("Type", "출발");
                startActivity(intent);
            }
        });
        endbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RoadStationFind.class);
                intent.putExtra("Type", "도착");
                startActivity(intent);
            }
        });
        endStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RoadStationFind.class);
                intent.putExtra("Type", "도착");
                startActivity(intent);
            }
        });
        resultbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!setting.getString("RoadStart", "출발 대여소").equals("출발 대여소") &&
                        !setting.getString("RoadEnd", "도착 대여소").equals("도착 대여소"))//설정된게 없을 때
                {
                    Intent intent = new Intent(getApplicationContext(), BikeRoadInfo.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "출발, 도착 대여소를 설정해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
//액션바 뒤로가기
public boolean onSupportNavigateUp(){
    finish();
    return true;
}
    private void init() {
        startStation = (EditText)findViewById(R.id.startStation);
        endStation = (EditText)findViewById(R.id.endStation);
        startbtn = (Button)findViewById(R.id.startStation_btn);
        endbtn = (Button)findViewById(R.id.endStation_btn);
        resultbtn = (Button)findViewById(R.id.result_btn);
    }
}
