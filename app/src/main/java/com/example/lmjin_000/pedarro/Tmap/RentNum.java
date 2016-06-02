package com.example.lmjin_000.pedarro.Tmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lmjin_000.pedarro.R;
import com.example.lmjin_000.pedarro.component.ApplicationController;
import com.example.lmjin_000.pedarro.model.BikeTmap;
import com.example.lmjin_000.pedarro.model.Rent;
import com.example.lmjin_000.pedarro.network.NetworkService;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by lmjin_000 on 2016-04-05.
 */
public class RentNum extends Activity{
    private Button btn_cancle;
    private TextView rentnum;
    SharedPreferences setting;
    SharedPreferences.Editor editor;
    private NetworkService networkService;
    LocationManager locationManager;

    double longitude;
    double latitude;

    Tmap tmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rentnum);

        rentnum = (TextView)findViewById(R.id.number);
        btn_cancle = (Button)findViewById(R.id.btn_cancle);

        setting = getSharedPreferences("setting", 0);
        editor= setting.edit();

        String id = setting.getString("ID","");

        //서버 설정
        ApplicationController application = ApplicationController.getInstance();
        application.buildNetworkService("52.36.42.94", 3000);
        initNetworkService();


        Intent intent = getIntent();
        BikeTmap bikeTmap9 = (BikeTmap)intent.getParcelableExtra("BikeTmap");

        Call<BikeTmap> tmapCall = networkService.insertone(bikeTmap9);
        tmapCall.enqueue(new Callback<BikeTmap>() {
            @Override
            public void onResponse(Response<BikeTmap> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.i("insert", "insert됨.");
                } else {
                    int statusCode = response.code();
                    Log.i("insert", "insert안됨" + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("test", "서버 onFailure 에러내용 : " + t.getMessage());
            }
        });


        getSetnumber(id);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        MyLocationListener listener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);


        tmap = new Tmap();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Intent intent = getIntent();
                    BikeTmap bikeTmap = (BikeTmap)intent.getParcelableExtra("BikeTmap");

                    tmap.execute(bikeTmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting = getSharedPreferences("setting", 0);
                Call<Rent> rentCall = networkService.deleteRent(setting.getString("ID", ""));//NetworkService에 등록해둔거 호출
                rentCall.enqueue(new Callback<Rent>() {
                    @Override
                    public void onResponse(Response<Rent> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            Log.i("MyTag", "대여신청 db삭제");
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

                //tmap.cancel(true);
                setting = getSharedPreferences("setting", 0);
                editor= setting.edit();
                editor.putBoolean("asynctask",true);
                editor.commit();

                finish();
            }
        });
    }
    private void initNetworkService() {
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }

    private String SetZeros(String num) {
        String Zeros = "";
        String temp = num;

        for (int i = 0; i < 4 - temp.length(); i++)
            Zeros += "0";

        return Zeros + temp;
    }

    private void getSetnumber(String id){
        Rent rent = new Rent();
        rent.setUser(id);
        Call<Rent> rentCall = networkService.randomnum(rent);//NetworkService에 등록해둔거 호출
        rentCall.enqueue(new Callback<Rent>() {
            @Override
            public void onResponse(Response<Rent> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Rent rent_temp = response.body();
                    rentnum.setText(SetZeros(rent_temp.getNumber()));

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

    class MyLocationListener implements LocationListener {

        // 위치정보는 아래 메서드를 통해서 전달된다.
        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();    //경도
            latitude = location.getLatitude();         //위도
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }

    public class Tmap extends AsyncTask<BikeTmap, BikeTmap, BikeTmap>
    {
        private BikeTmap bikeTmap2 = new BikeTmap();
        private BikeTmap last_bikeTmap = new BikeTmap();

        @Override
        protected BikeTmap doInBackground(BikeTmap... bikeTmaps) {
            while (isCancelled() == false) {
                setting = getSharedPreferences("setting", 0);
                editor= setting.edit();

                if(setting.getBoolean("asynctask",false)){
                    //반납완료 됨
                    editor.remove("asynctask");
                    editor.commit();
                    break;
                }
                //현재위치 받은뒤 서버로부터 값 가져오기 입력 값  : 현재 위치 ( model 새로 만들어야 할 듯 )
                //일단 임시로 현재 위치 좌표값 +해서 계산하겠음
                bikeTmap2 = bikeTmaps[0];

                Log.i("test", "사용자 이름 : " + bikeTmap2.getUserID());
                Log.i("test", "도착 정거장 : " + bikeTmap2.getArrivest());
                Log.i("test", "tmap - x : " + longitude);
                Log.i("test", "tmap - y : " + latitude);

                String temptX = Double.toString(longitude);
                String temptY = Double.toString(latitude);

                Call<BikeTmap> tmapCall = networkService.getData(bikeTmap2, bikeTmap2.getUserID(), bikeTmap2.getArrivest(),temptX,temptY);
                tmapCall.enqueue(new Callback<BikeTmap>() {
                    @Override
                    public void onResponse(Response<BikeTmap> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            last_bikeTmap = response.body();
                        } else {
                            int statusCode = response.code();
                            Log.i("test", "아직 대여에 성공하지 못하였습니다." + statusCode);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i("test", "서버 onFailure 에러내용 : " + t.getMessage());
                    }
                });

                try {
                    Thread.sleep(15000);//60초마다
                } catch (InterruptedException ex) {}

                publishProgress(last_bikeTmap);
            }
            return last_bikeTmap;
        }

        @Override
        protected void onPreExecute(){
            // ip, port 연결 , network 연결
            ApplicationController application = ApplicationController.getInstance();
            application.buildNetworkService("52.36.42.94", 3000);
            networkService = ApplicationController.getInstance().getNetworkService();

        }

        @Override
        protected void onProgressUpdate(BikeTmap... result) {

        }

        @Override
        protected void onPostExecute(BikeTmap result) {
            //클라 화면에 글씨 띄우기
            Log.i("Arrive","도착예정정보 끝");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.i("Arrive", "도착예정정보 끝");
        }

    }
}
