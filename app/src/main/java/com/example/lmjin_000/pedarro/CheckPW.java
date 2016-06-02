package com.example.lmjin_000.pedarro;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lmjin_000.pedarro.component.ApplicationController;
import com.example.lmjin_000.pedarro.model.registerJ;
import com.example.lmjin_000.pedarro.network.NetworkService;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by lmjin_000 on 2016-05-24.
 */
public class CheckPW extends AppCompatActivity {
    private NetworkService networkService;
    SharedPreferences setting;

    EditText id,current_pw,new_pw1,new_pw2;
    Button btn;
    ImageView current_img,new_img;

    String before_pw,new_pw;
    boolean check1=false,check2=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //액션바
        try {
            getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //  getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);
            getSupportActionBar().setCustomView(R.layout.titlebar_checkpw);        //커스텀 액션 바
        }catch (Exception e) {
            Log.i("actionbar", e.getMessage());
        }
        setContentView(R.layout.check_pw);

        init();
        //서버 설정
        ApplicationController application = ApplicationController.getInstance();
        application.buildNetworkService("52.36.42.94", 3000);
        networkService = ApplicationController.getInstance().getNetworkService();

        getBeforeUserInfo();
        
        check_before_pw();
        inputNewPw();
        check_next_pw();

        btnClick();
    }

    private void btnClick() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check1 == false){
                    Toast.makeText(getBaseContext(), "기존 비밀번호 확인에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                }else if(check2 == false){
                    Toast.makeText(getBaseContext(), "새로운 비밀번호 확인에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                }
                else if(check1==true&&check2==true){

                    registerJ register = new registerJ();
                    register.setPass(new_pw);
                    setting = getSharedPreferences("setting", 0);
                    Call<registerJ> thumbnailCall = networkService.change_pw(register,setting.getString("ID",""));
                    thumbnailCall.enqueue(new Callback<registerJ>() {
                        @Override
                        public void onResponse(Response<registerJ> response, Retrofit retrofit) {
                            if (response.isSuccess()) {
                                Toast.makeText(getBaseContext(), "비밀번호가 변경되었습니다", Toast.LENGTH_SHORT).show();
                                finish();
                                Log.i("pwpw", "버튼 눌림");

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
    }

    private void check_before_pw() {
        current_pw.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때
            }


            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                Log.i("pwpw", "원래 패스워드 : " + arg0.toString());
                if (arg0.toString().equals(before_pw)) {
                    current_img.setImageResource(R.drawable.pwcheckone);
                    check1 = true;
                    Log.i("pwpw", "원래 패스워드 일치 check1 : " + check1);
                } else {
                    check1 = false;
                    current_img.setImageResource(R.drawable.pwnoncheckone);
                    Log.i("pwpw", "원래 패스워드 불일치 check1 : " + check1);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }
        });
    }

    private void inputNewPw() {
        new_pw1.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때
            }


            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                new_pw = arg0.toString();
                Log.i("pwpw","새로운 패스워드1 : "+arg0.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }
        });
    }

    private void check_next_pw() {
        new_pw2.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때
            }


            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                Log.i("pwpw", "새로운 패스워드2 : " + arg0.toString());
                if (arg0.toString().equals(new_pw)) {
                    new_img.setImageResource(R.drawable.pwcheckone);
                    check2 = true;
                    Log.i("pwpw","새로운 패스워드 일치 check2 : "+check2);
                } else {
                    check2 = false;
                    new_img.setImageResource(R.drawable.pwnoncheckone);
                    Log.i("pwpw", "원래 패스워드 불일치 check2 : " + check2);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }
        });
    }

    private void getBeforeUserInfo() {

        setting = getSharedPreferences("setting", 0);
        //원래 정보 불러오기
        Call<registerJ> stationCall = networkService.getUserInfo(setting.getString("ID",""));//NetworkService에 등록해둔거 호출
        stationCall.enqueue(new Callback<registerJ>() {
            @Override
            public void onResponse(Response<registerJ> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    registerJ result = response.body();
                    id.setText(result.getLoginid());
                    before_pw = result.getPass();
                    Log.i("pwpw", "before_pw저장 : " + result.getPass());
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

    //액션바 뒤로가기
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void init() {
        id = (EditText)findViewById(R.id.edit_id);
        current_pw = (EditText)findViewById(R.id.current_pw);
        new_pw1 = (EditText)findViewById(R.id.new_pw1);
        new_pw2 = (EditText)findViewById(R.id.new_pw2);
        btn = (Button)findViewById(R.id.checkbtn);
        current_img = (ImageView)findViewById(R.id.current_pw_img);
        new_img = (ImageView)findViewById(R.id.new_pw_img);
    }


}
