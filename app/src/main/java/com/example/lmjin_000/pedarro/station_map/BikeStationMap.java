package com.example.lmjin_000.pedarro.station_map;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lmjin_000.pedarro.BikeRent;
import com.example.lmjin_000.pedarro.MapApiConst;
import com.example.lmjin_000.pedarro.R;
import com.example.lmjin_000.pedarro.Tmap.RentNum;
import com.example.lmjin_000.pedarro.component.ApplicationController;
import com.example.lmjin_000.pedarro.model.BikeTmap;
import com.example.lmjin_000.pedarro.model.Rent;
import com.example.lmjin_000.pedarro.model.Station;
import com.example.lmjin_000.pedarro.model.registerJ;
import com.example.lmjin_000.pedarro.network.NetworkService;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapLayout;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class BikeStationMap extends AppCompatActivity
        implements MapView.MapViewEventListener, MapView.POIItemEventListener, MapView.CurrentLocationEventListener,View.OnClickListener {

    private AutoCompleteTextView search;
    private Button handle;
    private ImageView btn_search;
    //자동완성
    private List<String> keywords = new ArrayList<String>();
    private HashMap<String, Station> mTagItemMap = new HashMap<String, Station>();

    private MapView mMapView;
    private MapPOIItem mDefaultMarker;
    private MapPoint DEFAULT_MARKER_POINT = MapPoint.mapPointWithGeoCoord(37.315978, 126.838493);
    private NetworkService networkService;
    private boolean sliding_first = true;

    LocationManager manager;
    MapLayout mapLayout;
    private boolean gpscheck = false;
    //도착예정정보 저장
    private BikeTmap biketmap = new BikeTmap();
    SharedPreferences setting;

    double lat = 0;
    double lon = 0;
    double currentlat=37.315978, currentlng=126.838493;
    //페이지뷰
    Button btn[] = new Button[3];
    ViewPager viewPager = null;
    Handler handler = null;
    int p=0;	//페이지번호
    int v=1;	//화면 전환 뱡향

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //액션바
        try {
            getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //  getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);
            getSupportActionBar().setCustomView(R.layout.titlebar_bikestationmap);        //커스텀 액션 바
        }catch (Exception e) {
            Log.i("actionbar",e.getMessage());
        }
        setContentView(R.layout.activity_main);

        init();
        btn[0] = (Button)findViewById(R.id.btn_a);

        mapLayout = new MapLayout(this);
        mMapView = mapLayout.getMapView();


        mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
//        mMapView.setOpenAPIKeyAuthenticationResultListener(this);
        mMapView.setMapViewEventListener(this);
        mMapView.setMapType(MapView.MapType.Standard);
        mMapView.setPOIItemEventListener(this);
        mMapView.setCurrentLocationEventListener(this);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapLayout);

        mMapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());

        //서버 설정
        ApplicationController application = ApplicationController.getInstance();
        application.buildNetworkService("52.36.42.94", 3000);
        initNetworkService();

        Map_init();

        //자동완성성
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, keywords);
        search.setAdapter(adapter);

        //현재 위치 알아내기
        Button btnShowLocation = (Button) findViewById(R.id.btnShowLocation);

        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String context = Context.LOCATION_SERVICE;
                manager = (LocationManager) getSystemService(context);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    alertCheckGPS();
                } else {
                    getMyLocation();
                }
            }
        });

        //검색하였을 때
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search_keyword = search.getText().toString();
                Call<Station> stationCall = networkService.getStation(search_keyword);//NetworkService에 등록해둔거 호출
                stationCall.enqueue(new Callback<Station>() {
                    @Override
                    public void onResponse(Response<Station> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            Station station_temp = response.body();
                            MapPOIItem poiltem = mMapView.findPOIItemByName(station_temp.getName())[0];
                            mMapView.selectPOIItem(poiltem, false);
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
        });
    }

    private void alertCheckGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("GPS가 켜져 있지 않습니다.\nGPS를 켜시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveConfigGPS();
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

    private void Errordialog(String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // GPS 설정화면으로 이동
    private void moveConfigGPS() {
        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);
    }

    //나의 위치정보
    private void getMyLocation() {

        if (manager == null) {
            manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        // provider 기지국||GPS 를 통해서 받을건지 알려주는 Stirng 변수
        // minTime 최소한 얼마만의 시간이 흐른후 위치정보를 받을건지 시간간격을 설정 설정하는 변수
        // minDistance 얼마만의 거리가 떨어지면 위치정보를 받을건지 설정하는 변수
        // manager.requestLocationUpdates(provider, minTime, minDistance, listener);

        // 10초
        long minTime = 1000000000;

        // 거리는 0으로 설정
        // 그래서 시간과 거리 변수만 보면 움직이지않고 10초뒤에 다시 위치정보를 받는다
        float minDistance = 500;
        MyLocationListener listener = new MyLocationListener();

        try {
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, listener);
            Toast.makeText(getApplication(),"현재 위치로 가는중",Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){
            Toast.makeText(getApplication(),"현재 위치 가져오기 실패",Toast.LENGTH_SHORT).show();
        }

        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mMapView.setShowCurrentLocationMarker(true);
        //mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(currentlat, currentlng), 2, true);
        //다시 끄기

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            }
        }, 10000);
    }

    //현재위치
    class MyLocationListener implements LocationListener {

        // 위치정보는 아래 메서드를 통해서 전달된다.
        @Override
        public void onLocationChanged(Location location) {
            currentlat = location.getLatitude();
            currentlng = location.getLongitude();
            Toast.makeText(getApplication(),"lat : "+currentlat+" lng : "+currentlng,Toast.LENGTH_SHORT).show();
            Log.i("current", "현재위치4 : " + currentlat + "," + currentlng);
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
    private void initNetworkService() {
        // TODO: 13. ApplicationConoller 객체를 이용하여 NetworkService 가져오기
        networkService = ApplicationController.getInstance().getNetworkService();
    }
//액션바 뒤로가기
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    private void init() {
        handle = (Button)findViewById(R.id.handle);
        search = (AutoCompleteTextView)findViewById(R.id.editTextQuery);
        btn_search = (ImageView)findViewById(R.id.buttonSearch);

        btn[0] = (Button)findViewById(R.id.btn_a);
        btn[1] = (Button)findViewById(R.id.btn_b);
        btn[2] = (Button)findViewById(R.id.btn_c);
    }
    //자동완성
    private void Map_init(){

        Call<List<Station>> stationCall = networkService.allStation();//NetworkService에 등록해둔거 호출
        stationCall.enqueue(new Callback<List<Station>>() {
            @Override
            public void onResponse(Response<List<Station>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Station> station_temp = response.body();

                    for (Station station : station_temp) {
                        DEFAULT_MARKER_POINT = MapPoint.mapPointWithGeoCoord(Double.parseDouble(station.getLat()), Double.parseDouble(station.getHard()));
                        createDefaultMarker(mMapView, station.getName());
                        keywords.add(station.getName());
                        mTagItemMap.put(station.getName(), station);
                    }

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


    private void createDefaultMarker(MapView mapView, String name) {
        mDefaultMarker = new MapPOIItem();
        mDefaultMarker.setItemName(name);
        mDefaultMarker.setTag(0);
        mDefaultMarker.setMapPoint(DEFAULT_MARKER_POINT);
        mDefaultMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        mDefaultMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

        mapView.addPOIItem(mDefaultMarker);
    }

    //커스텀 벌룬
    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.balloon, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            Log.i("Connection", Integer.toString(poiItem.getTag()));
            if (poiItem == null) return null;
            Station item = mTagItemMap.get(poiItem.getItemName());

            ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText(poiItem.getItemName());
            String show = "";
            ((TextView) mCalloutBalloon.findViewById(R.id.bike))
                    .setText(show);
            createDefaultMarker(mMapView, item.getName());

            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }
////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onMapViewInitialized(MapView mapView) {

        //처음 위치 설정
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("현재위치로 이동하겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                gpscheck = true;
                                String context = Context.LOCATION_SERVICE;
                                manager = (LocationManager)getSystemService(context);
                                if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                    alertCheckGPS();
                                }
                                else {
                                    getMyLocation();
                                }
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

        Log.i("Map", "MapView had loaded. Now, MapView APIs could be called safely");

        mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(currentlat, currentlng), 2, true);

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
////////////////////////////////////////////////////////////////////////

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("current", "다시 돌아옴");
        //gps키고 돌아왔을때
        String context = Context.LOCATION_SERVICE;
        manager = (LocationManager)getSystemService(context);
        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if(gpscheck==true) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getMyLocation();
                        Log.i("current", "다시 돌아와서 현재위치 갱신 성공");
                    }
                }, 1000);
            }
        } else {
            Log.i("current", "다시 돌아와서 현재위치 갱신 실패");
        }
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

        btn[0].setSelected(true);
        btn[1].setSelected(false);
        btn[2].setSelected(false);

        //슬라이딩드로어 올리기
        if(sliding_first == true){
            handle.performClick();
            sliding_first = false;
        }
        pageViewr(mapPOIItem.getItemName());
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        biketmap.setArrivest(mapPOIItem.getItemName());

    }



    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
///////////////////////////////////////////////////////////////
    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        lat = mapPointGeo.latitude; lon = mapPointGeo.longitude;
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    //페이지뷰
    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.btn_a:
                viewPager.setCurrentItem(0);
                btn[0].setSelected(true);
                btn[1].setSelected(false);
                btn[2].setSelected(false);
                break;
            case R.id.btn_b:
                viewPager.setCurrentItem(1);
                btn[0].setSelected(false);
                btn[1].setSelected(true);
                btn[2].setSelected(false);
                break;
            case R.id.btn_c:
                viewPager.setCurrentItem(2);
                btn[0].setSelected(false);
                btn[1].setSelected(false);
                btn[2].setSelected(true);
                break;
            default:
                break;
        }

    }

    void pageViewr(String station_name){
        //viewPager
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager(),station_name);

        viewPager.setAdapter(adapter);

        for(int i=0;i<btn.length; i++){
            btn[i].setOnClickListener(this);
        }

        handler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                if(p==0){
                    viewPager.setCurrentItem(1);
                    p++;
                    v= 1;
                    btn[0].setSelected(true);
                    btn[1].setSelected(false);
                    btn[2].setSelected(false);
                }if(p==1&&v==0){
                    viewPager.setCurrentItem(1);
                    p--;
                    btn[0].setSelected(false);
                    btn[1].setSelected(true);
                    btn[2].setSelected(false);
                }if(p==1&&v==1){
                    viewPager.setCurrentItem(2);
                    p++;
                    btn[0].setSelected(false);
                    btn[1].setSelected(true);
                    btn[2].setSelected(false);
                }if(p==2){
                    viewPager.setCurrentItem(1);
                    p--;
                    v=0;
                    btn[0].setSelected(false);
                    btn[1].setSelected(false);
                    btn[2].setSelected(true);
                }
            }
        };
    }
}
