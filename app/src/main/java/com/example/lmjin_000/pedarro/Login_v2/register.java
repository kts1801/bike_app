package com.example.lmjin_000.pedarro.Login_v2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lmjin_000.pedarro.GCM.QuickstartPreferences;
import com.example.lmjin_000.pedarro.GCM.RegistrationIntentService;
import com.example.lmjin_000.pedarro.R;
import com.example.lmjin_000.pedarro.component.ApplicationController;
import com.example.lmjin_000.pedarro.model.registerJ;
import com.example.lmjin_000.pedarro.network.NetworkService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class register extends AppCompatActivity {

    //GCM
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "GCM";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String token;

    Handler handler = new Handler();
    private EditText edit_userId, edit_userPass, edit_userName, edit_phone1, edit_phone2, edit_phone3;
    private Button regbtn;
    registerJ register = new registerJ();
    //1)번 부분
    private NetworkService networkService;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //액션바
        try {
            getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //  getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);
            getSupportActionBar().setCustomView(R.layout.titlebar_register);        //커스텀 액션 바
        }catch (Exception e) {
            Log.i("actionbar", e.getMessage());
        }
        setContentView(R.layout.activity_register);

        //gcm
        registBroadcastReceiver();

        init();

        ApplicationController application = ApplicationController.getInstance();
        application.buildNetworkService("52.36.42.94", 3000);
        networkService = ApplicationController.getInstance().getNetworkService();

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userPass = edit_userPass.getText().toString();
                String userName = edit_userName.getText().toString();
                String userPhone = edit_phone1.getText().toString()
                        + edit_phone2.getText().toString()
                        + edit_phone3.getText().toString();

                int phoneLength = edit_phone1.length() + edit_phone2.length() + edit_phone3.length();
                if (phoneLength < 11) {
                    Toast.makeText(getApplicationContext(), "휴대폰 번호를 제대로 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                getInstanceIdToken();

                String userId = edit_userId.getText().toString();

                register.setLoginid(userId);
                register.setPass(userPass);
                register.setName(userName);
                register.setPhone(userPhone);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        register.setRegisterid(token);
                        Log.i(TAG, "회원가입 저장 token :" + token);

                        Call<registerJ> thumbnailCall = networkService.post_register(register);

                        thumbnailCall.enqueue(new Callback<registerJ>() {
                            @Override
                            public void onResponse(Response<registerJ> response, Retrofit retrofit) {
                                if (response.isSuccess()) {
                                    flag = true;

                                    Toast.makeText(getBaseContext(), "가입해주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
                                    finish();

                                } else {
                                    flag = false;
                                    int statusCode = response.code();
                                    Log.i("MyTag", "응답코드 : " + statusCode);
                                    Toast.makeText(getBaseContext(), "이미 회원정보가 존재합니다.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.i("MyTag", "서버 onFailure 에러내용 : " + t.getMessage());
                            }
                        });
                    }
                }, 100);

            }

            }
        });
    }
    //액션바 뒤로가기
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    //EditText, Button, TextView 초기화 함수
    private void init() {
        edit_userId = (EditText) findViewById(R.id.userId);
        edit_userPass = (EditText) findViewById(R.id.userPass);
        edit_userName = (EditText) findViewById(R.id.userName);
        edit_phone1 = (EditText) findViewById(R.id.userPhone1);
        edit_phone2 = (EditText) findViewById(R.id.userPhone2);
        edit_phone3 = (EditText) findViewById(R.id.userPhone3);
        regbtn = (Button) findViewById(R.id.regbtn);

    }

    /**
     * Instance ID를 이용하여 디바이스 토큰을 가져오는 RegistrationIntentService를 실행한다.
     */
    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * LocalBroadcast 리시버를 정의한다. 토큰을 획득하기 위한 READY, GENERATING, COMPLETE 액션에 따라 UI에 변화를 준다.
     */
    public void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();


                if(action.equals(QuickstartPreferences.REGISTRATION_READY)){
                    // 액션이 READY일 경우
                    Log.i(TAG,"Ready");
                } else if(action.equals(QuickstartPreferences.REGISTRATION_GENERATING)){
                    // 액션이 GENERATING일 경우
                    Log.i(TAG,"generating");
                } else if(action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)){
                    // 액션이 COMPLETE일 경우
                    token = intent.getStringExtra("token");
                    Log.i(TAG,"complete / token :"+token);
                }

            }
        };
    }

    /**
     * 앱이 실행되어 화면에 나타날때 LocalBoardcastManager에 액션을 정의하여 등록한다.
     */
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

    }

    /**
     * 앱이 화면에서 사라지면 등록된 LocalBoardcast를 모두 삭제한다.
     */
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    /**
     * Google Play Service를 사용할 수 있는 환경이지를 체크한다.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}