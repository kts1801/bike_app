package com.example.lmjin_000.pedarro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lmjin_000.pedarro.component.ApplicationController;
import com.example.lmjin_000.pedarro.model.registerJ;
import com.example.lmjin_000.pedarro.network.NetworkService;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
/**
 * Created by lmjin_000 on 2016-05-20.
 */

public class UserEdit extends AppCompatActivity {

    private EditText edit_userId, edit_userPass, edit_userName, edit_phone1, edit_phone2, edit_phone3,edit_buy,edit_auto;
    private Button regbtn,btn_pass,btn_buy,btn_auto;
    private NetworkService networkService;
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    registerJ register = new registerJ();

    @Override
    protected void onResume() {
        super.onResume();

        //서버 설정
        ApplicationController application = ApplicationController.getInstance();
        application.buildNetworkService("52.36.42.94", 3000);
        networkService = ApplicationController.getInstance().getNetworkService();

        setting = getSharedPreferences("setting", 0);
        //원래 정보 불러오기
        Call<registerJ> stationCall = networkService.getUserInfo(setting.getString("ID",""));//NetworkService에 등록해둔거 호출
        stationCall.enqueue(new Callback<registerJ>() {
            @Override
            public void onResponse(Response<registerJ> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    registerJ result = response.body();
                    edit_userId.setText(result.getLoginid());
                    edit_userName.setText(result.getName());
                    edit_phone1.setText(result.getPhone().substring(0, 3));
                    edit_phone2.setText(result.getPhone().substring(3, 7));
                    edit_phone3.setText(result.getPhone().substring(7, 11));
                    String temp_pass = "";
                    for (int i = 0; i < result.getPass().length(); i++) {
                        temp_pass += "*";
                    }
                    edit_userPass.setText(temp_pass);
                    if (result.getBuy() == 0)
                        edit_buy.setText("없음");
                    else if (result.getBuy() == 1)
                        edit_buy.setText("구매됨");

                    setting = getSharedPreferences("setting", 0);
                    if (setting.getBoolean("Auto_Login_enabled", false) == false)
                        edit_auto.setText("OFF");
                    else if (setting.getBoolean("Auto_Login_enabled", false) == true)
                        edit_auto.setText("ON");

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //액션바
        try {
            getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //  getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);
            getSupportActionBar().setCustomView(R.layout.titlebar_edituser);        //커스텀 액션 바
        }catch (Exception e) {
            Log.i("actionbar",e.getMessage());
        }
        setContentView(R.layout.user_edit);

        init();


        //서버 설정
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

                    register.setPass(userPass);
                    register.setName(userName);
                    register.setPhone(userPhone);

                    setting = getSharedPreferences("setting", 0);
                    Call<registerJ> thumbnailCall = networkService.edit_user(register, setting.getString("ID", ""));
                    thumbnailCall.enqueue(new Callback<registerJ>() {
                        @Override
                        public void onResponse(Response<registerJ> response, Retrofit retrofit) {
                            if (response.isSuccess()) {
                                Toast.makeText(getBaseContext(), "수정되었습니다", Toast.LENGTH_SHORT).show();
                                finish();

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
            }
        });

        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.pedalro.kr/index.do")));
            }
        });

        btn_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting = getSharedPreferences("setting", 0);
                editor= setting.edit();

                if (setting.getBoolean("Auto_Login_enabled", false) == false){
                    editor.putBoolean("Auto_Login_enabled", true);
                    editor.commit();
                    Toast.makeText(getBaseContext(), "자동로그인이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    edit_auto.setText("ON");
                }
                else if (setting.getBoolean("Auto_Login_enabled", false) == true){
                    editor.putBoolean("Auto_Login_enabled", false);
                    editor.commit();
                    Toast.makeText(getBaseContext(), "자동로그인이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                    edit_auto.setText("OFF");
                }
            }
        });

        btn_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CheckPW.class);
                startActivity(intent);
            }
        });
    }

    //액션바 뒤로가기
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void init() {
        edit_userId = (EditText) findViewById(R.id.userId);
        edit_userPass = (EditText) findViewById(R.id.userPass);
        edit_userName = (EditText) findViewById(R.id.userName);
        edit_phone1 = (EditText) findViewById(R.id.userPhone1);
        edit_phone2 = (EditText) findViewById(R.id.userPhone2);
        edit_phone3 = (EditText) findViewById(R.id.userPhone3);
        edit_buy = (EditText) findViewById(R.id.buyornot);
        edit_auto = (EditText) findViewById(R.id.auto);
        regbtn = (Button) findViewById(R.id.regbtn);
        btn_auto = (Button) findViewById(R.id.btn_auto);
        btn_buy = (Button) findViewById(R.id.btn_buy);
        btn_pass = (Button) findViewById(R.id.btn_pass);
    }
}