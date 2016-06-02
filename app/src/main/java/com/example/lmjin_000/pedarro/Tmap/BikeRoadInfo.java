package com.example.lmjin_000.pedarro.Tmap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lmjin_000.pedarro.R;
import com.example.lmjin_000.pedarro.Tmap.ListView.ListviewAdapter;
import com.example.lmjin_000.pedarro.Tmap.ListView.Listviewitem;
import com.example.lmjin_000.pedarro.component.ApplicationController;
import com.example.lmjin_000.pedarro.model.v2.ResultObj;
import com.example.lmjin_000.pedarro.network.NetworkService;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapData.FindPathDataListenerCallback;
import com.skp.Tmap.TMapData.TMapPathType;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapGpsManager.onLocationChangedCallback;
import com.skp.Tmap.TMapLabelInfo;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;
import com.skp.Tmap.TMapView.TMapLogoPositon;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class BikeRoadInfo extends AppCompatActivity implements onLocationChangedCallback,View.OnClickListener
{
	private RelativeLayout contentView = null;
	static AlertDialog alertDialog = null;
	static DialogInterface.OnClickListener mListener;
	TextView txt,time,distance;
	RelativeLayout handle;
	ListView listView;
	Button changebtn;
	private NetworkService networkService;

	SharedPreferences setting;
	SharedPreferences.Editor editor;

	String startx,starty,endx,endy,startname,endname;
	int cnt=1;

	@Override
	public void onLocationChange(Location location) {
		Log.i("onLocationChange ", location.getLatitude() + " " + location.getLongitude() + " " + location.getSpeed() + " " + location.getAccuracy());
		if(m_bTrackingMode) {
			mMapView.setLocationPoint(Double.parseDouble(startx), Double.parseDouble(starty));
		}
	}

	private TMapView mMapView = null;

	private Context 		mContext;
	private ArrayList<Bitmap> mOverlayList;
	public static String mApiKey="e894e540-d408-31ca-a410-db114de2390c"; // �߱޹��� appKey

	private static final int[] mArrayMapButton = {
		R.id.btnBicycle,
		R.id.btnBicycle_Path,
	};

	private 	int 		m_nCurrentZoomLevel = 0;

	private 	boolean 	m_bTrackingMode = false;

	ArrayList<String>		mArrayID;

	ArrayList<String>		mArrayCircleID;

	ArrayList<String>		mArrayLineID;

	ArrayList<String>		mArrayPolygonID;

	ArrayList<String>       mArrayMarkerID;
	private static int 		mMarkerID;

	TMapGpsManager gps = null;


	/**
	 * onCreate()
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//액션바
		try {
			getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			//  getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);
			getSupportActionBar().setCustomView(R.layout.titlebar_bikeroadinfo);        //커스텀 액션 바
		}catch (Exception e) {
			Log.i("actionbar", e.getMessage());
		}
		setContentView(R.layout.tmapapi2);

		mContext = this;

		mMapView = new TMapView(this);
		contentView  = (RelativeLayout)findViewById(R.id.contentView);
		contentView.removeAllViews();
		contentView.addView(mMapView, new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT));

		configureMapView();

		setting = getSharedPreferences("setting", 0);
		editor= setting.edit();
		startx =setting.getString("RoadStartX", "");
		starty =setting.getString("RoadStartY", "");
		endx =setting.getString("RoadEndX", "");
		endy =setting.getString("RoadEndY", "");
		startname = setting.getString("RoadStart","");
		endname = setting.getString("RoadEnd","");
		editor.remove("RoadStartX");
		editor.remove("RoadStartY");
		editor.remove("RoadEndX");
		editor.remove("RoadEndX");
		editor.remove("RoadStart");
		editor.remove("RoadEnd");
		editor.commit();
		initView();

		//�������� �޾ƿ�
		getNavi();

		mArrayID = new ArrayList<String>();

		mArrayCircleID = new ArrayList<String>();

		mArrayLineID = new ArrayList<String>();

		mArrayPolygonID = new ArrayList<String>();

		mArrayMarkerID	= new ArrayList<String>();
		mMarkerID = 0;

		gps = new TMapGpsManager(BikeRoadInfo.this);
		gps.setMinTime(1000);
		gps.setMinDistance(5);
		gps.setProvider(gps.NETWORK_PROVIDER);
		gps.OpenGps();

		mMapView.setTMapLogoPosition(TMapLogoPositon.POSITION_BOTTOMRIGHT);
		mMapView.setCenterPoint(Double.parseDouble(starty), Double.parseDouble(startx), true);
		//mMapView.setLocationPoint(Double.parseDouble(starty), Double.parseDouble(startx));

		drawBicyclePath();

		handle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handle.performClick();
				cnt++;
				if(cnt%2==0)
					changebtn.setText("지도");
				else if(cnt%2==1)
					changebtn.setText("상세");
			}
		});
	}

	/**
	 * setSKPMapApiKey()�� ApiKey�� �Է� �Ѵ�.
	 * setSKPMapBizappId()�� mBizAppID�� �Է��Ѵ�.
	 * -> setSKPMapBizappId�� TMapTapi(TMap�� ����)�� ����Ҷ� BizAppID ���� �ؾ� �Ѵ�. TMapTapi ������� �ʴ´ٸ� setSKPMapBizappId�� ���� �ʾƵ� �ȴ�.
	 */
	private void configureMapView() {
		mMapView.setSKPMapApiKey(mApiKey);
	}

	/**
	 * initView - ��ư�� ���� �����ʸ� ����Ѵ�.
	 */

	//액션바 뒤로가기
	public boolean onSupportNavigateUp(){
		finish();
		return true;
	}

	private void initView() {
		changebtn = (Button)findViewById(R.id.changebtn);
		handle = (RelativeLayout)findViewById(R.id.handle);
		listView=(ListView)findViewById(R.id.listview);
		txt = (TextView)findViewById(R.id.description);
		time = (TextView)findViewById(R.id.time);
		distance = (TextView)findViewById(R.id.distance);

		for (int btnMapView : mArrayMapButton) {
			Button ViewButton = (Button)findViewById(btnMapView);
			ViewButton.setOnClickListener((View.OnClickListener) this);
		}

		mMapView.setOnApiKeyListener(new TMapView.OnApiKeyListenerCallback() {
			@Override
			public void SKPMapApikeySucceed() {
				Log.i("Tmap","MainActivity SKPMapApikeySucceed");
			}

			@Override
			public void SKPMapApikeyFailed(String errorMsg) {
				Log.i("Tmap", "MainActivity SKPMapApikeyFailed " + errorMsg);
			}
		});


		mMapView.setOnEnableScrollWithZoomLevelListener(new TMapView.OnEnableScrollWithZoomLevelCallback() {
			@Override
			public void onEnableScrollWithZoomLevelEvent(float zoom, TMapPoint centerPoint) {
				Log.i("Tmap", "MainActivity onEnableScrollWithZoomLevelEvent " + zoom + " " + centerPoint.getLatitude() + " " + centerPoint.getLongitude());
			}
		});

		mMapView.setOnDisableScrollWithZoomLevelListener(new TMapView.OnDisableScrollWithZoomLevelCallback() {
			@Override
			public void onDisableScrollWithZoomLevelEvent(float zoom, TMapPoint centerPoint) {
				Log.i("Tmap", "MainActivity onDisableScrollWithZoomLevelEvent " + zoom + " " + centerPoint.getLatitude() + " " + centerPoint.getLongitude());
			}
		});

		mMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
			@Override
			public boolean onPressUpEvent(ArrayList<TMapMarkerItem> markerlist,ArrayList<TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
				Log.i("Tmap", "MainActivity onPressUpEvent " + markerlist.size());
				return false;
			}

			@Override
			public boolean onPressEvent(ArrayList<TMapMarkerItem> markerlist,ArrayList<TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
				Log.i("Tmap", "MainActivity onPressEvent " + markerlist.size());

				for (int i = 0; i < markerlist.size(); i++) {
					TMapMarkerItem item = markerlist.get(i);
					Log.i("Tmap", "MainActivity onPressEvent " + item.getName() + " " + item.getTMapPoint().getLatitude() + " " + item.getTMapPoint().getLongitude());
				}
				return false;
			}
		});

		mMapView.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
			@Override
			public void onLongPressEvent(ArrayList<TMapMarkerItem> markerlist,ArrayList<TMapPOIItem> poilist, TMapPoint point) {
				Log.i("Tmap", "MainActivity onLongPressEvent " + markerlist.size());
			}
		});

		mMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
			@Override
			public void onCalloutRightButton(TMapMarkerItem markerItem) {
				String strMessage = "";
				strMessage = "ID: " + markerItem.getID() + " " + "Title " + markerItem.getCalloutTitle();
				showAlertDialog(BikeRoadInfo.this, "Callout Right Button", strMessage);
			}
		});

		mMapView.setOnClickReverseLabelListener(new TMapView.OnClickReverseLabelListenerCallback() {
			@Override
			public void onClickReverseLabelEvent(TMapLabelInfo findReverseLabel) {
				if(findReverseLabel != null) {
					Log.i("Tmap", "MainActivity setOnClickReverseLabelListener " + findReverseLabel.id + " / " + findReverseLabel.labelLat
							+ " / " + findReverseLabel.labelLon + " / " + findReverseLabel.labelName);

				}
			}
		});

		m_nCurrentZoomLevel = -1;
		m_bTrackingMode = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		gps.CloseGps();
		if(mOverlayList != null){
			mOverlayList.clear();
		}
	}

	public static void showAlertDialog(Context ctx, String title, String msg)
	{
			alertDialog = new AlertDialog.Builder(ctx)
					.setIcon(R.drawable.star_yellow)
					.setTitle(title)
					.setMessage(msg)
					.setNeutralButton("Ȯ��", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which)
						{
							alertDialog.dismiss();
							alertDialog = null;
							if(mListener != null){
								mListener.onClick(dialog, which);
								mListener = null;
							}
						}

					}).show();

		}
	/**
	 * onClick Event
	 */
	@Override
	public void onClick(View v) {

		switch(v.getId()) {
		case R.id.btnBicycle_Path     :    drawBicyclePath();  	    break;
		case R.id.btnBicycle		  :
			setBicycle();		    break;
			//case R.id.btnNaviGuide		  :     getNavi();		    break;
		}
	}

	private void getNavi() {
		//���� ����
		ApplicationController application = ApplicationController.getInstance();
		application.buildNetworkService("52.36.42.94", 3000);
		networkService = ApplicationController.getInstance().getNetworkService();

		Call<ResultObj> rentCall = networkService.roadtest(starty, startx, endy, endx);//NetworkService�� ����صа� ȣ��
		rentCall.enqueue(new Callback<ResultObj>() {
			@Override
			public void onResponse(Response<ResultObj> response, Retrofit retrofit) {
				if (response.isSuccess()) {
					ResultObj result = response.body();

					time.setText(Integer.toString(result.getTotaltime()/60));
					distance.setText(String.format("%.1f",(double)result.getTotaldistance()/1000.0));

					ArrayList<Listviewitem> data=new ArrayList<>();
					Listviewitem temp;
					String strtemp;
					int inttemp = 0;
					for(int i=0;i<result.getProperties().size();i++) {
						//����
						if(result.getProperties().get(i).getDescription().equals("")) {
							strtemp = Integer.toString(result.getProperties().get(i).getDistance())+R.string.road_info;
						}
						else {
							strtemp = result.getProperties().get(i).getDescription();
						}
						//����
						switch (result.getProperties().get(i).getIcon()){
							case 11 : inttemp = R.drawable.go;break;
							case 12 :case 16:case 17 :
								inttemp = R.drawable.goleft; break;
							case 13 :case 18:case 19 :
								inttemp = R.drawable.goright; break;
							case 200 : inttemp = R.drawable.start; break;
							case 201 : inttemp = R.drawable.stop; break;
							default: inttemp = R.drawable.tumyeong; break;
						}
						temp = new Listviewitem(strtemp,inttemp);
						data.add(temp);

						ListviewAdapter adapter=new ListviewAdapter(getApplicationContext(), R.layout.listitem, data);
						listView.setAdapter(adapter);
					}
				} else {
					int statusCode = response.code();
					Log.i("TmapTest", "�����ڵ� : " + statusCode);
				}
			}

			@Override
			public void onFailure(Throwable t) {
				Log.i("TmapTest", "onFailure : " + t.getMessage());
			}
		});
	}

	/**
	 * setZoomLevel
	 * Zoom Level�� �����Ѵ�.
	 */
	public void setZoomLevel() {
    	final String[] arrString = getResources().getStringArray(R.array.a_zoomlevel);
		AlertDialog dlg = new AlertDialog.Builder(this)
			.setIcon(R.drawable.ic_launcher)
			.setTitle("Select Zoom Level")
			.setSingleChoiceItems(R.array.a_zoomlevel, m_nCurrentZoomLevel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					m_nCurrentZoomLevel = item;
					dialog.dismiss();
					mMapView.setZoomLevel(Integer.parseInt(arrString[item]));
				}
			}).show();
    }


	/**
	 * removeMapPath
	 * ��� ǥ�ø� �����Ѵ�.
	 */

	public void drawBicyclePath() {
		TMapPoint start_point = new TMapPoint(Double.parseDouble(startx), Double.parseDouble(starty));
		TMapPoint end_point = new TMapPoint(Double.parseDouble(endx), Double.parseDouble(endy));

		TMapData tmapdata = new TMapData();
		
		tmapdata.findPathDataWithType(TMapPathType.BICYCLE_PATH, start_point, end_point, new FindPathDataListenerCallback() {
			@Override
			public void onFindPathData(TMapPolyLine polyLine) {
				mMapView.addTMapPath(polyLine);
			}
		});
	}

	public void setBicycle() {
		mMapView.setBicycleInfo(!mMapView.IsBicycleInfo());
	}

}

