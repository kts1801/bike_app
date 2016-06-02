package com.example.lmjin_000.pedarro.GCM;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.lmjin_000.pedarro.R;
import com.example.lmjin_000.pedarro.component.ApplicationController;
import com.example.lmjin_000.pedarro.model.Bike;
import com.example.lmjin_000.pedarro.network.NetworkService;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by lmjin_000 on 2016-03-24.
 */
public class ShowMsgActivity extends Activity {

    private NetworkService networkService;

    private EditText detail;
    private Button addOK;
    private String s_detail;
    private RatingBar rating;
    private float jumsu;
    private TextView txt;



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        String bike = intent.getStringExtra("bike");

        //투명한 액티비티 띄우는 용도
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        //다이얼로그 띄우기
        FinalDialog(title, message, bike);
    }

    private void FinalDialog(String title, String message,final String bike){
        final Dialog dialog = new Dialog(ShowMsgActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog);//이제 레이팅 바의 xml에 있는 객체들이 검색 가능하다.

        ApplicationController application = ApplicationController.getInstance();
        application.buildNetworkService("52.36.42.94", 3000);
        networkService = ApplicationController.getInstance().getNetworkService();
        
        detail = (EditText) dialog.findViewById(R.id.edit_detail);
        addOK = (Button) dialog.findViewById(R.id.btn_send);
        txt = (TextView) dialog.findViewById(R.id.txt);

        txt.setText("이용하신 "+bike+"자전거 \n상태를 평가해주세요");

        addOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_detail = detail.getText().toString();

                Bike bike1 = new Bike();
                bike1.setBikeid(bike);
                bike1.setDetail(s_detail+" ");
                bike1.setPoint(jumsu);

                Call<Bike> bikeCall = networkService.biketrouble(bike1);

                bikeCall.enqueue(new Callback<Bike>() {
                    @Override
                    public void onResponse(Response<Bike> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            Bike bike_temp = response.body();
                            Log.i("Bike", "자전거 등록 성공 / 자전거 : " + bike_temp.getBikeid());
                        } else {
                            int statusCode = response.code();
                            Log.i("Bike", "응답코드 : " + statusCode);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i("MyTag", "서버 onFailure 에러내용 : " + t.getMessage());
                    }
                });
                dialog.dismiss(); // 이후 MainActivity에서 구현해준 Dismiss 리스너가 작동함
            }
        });

        //레이팅 바 검색
        rating = (RatingBar) dialog.findViewById(R.id.ratingBar);

        //사용자가 임의로 별점을 수정할 수 있도록 허가하는 메서드
        //true일 경우는 직접 별을 클릭이나 드래그로 조작 불가가
        rating.setIsIndicator(false);

        //별 반칸에 몇점을 할당할지..별반칸당 0.5점 할당
        //별의 갯수에 상관없이 코드변환 가능하게 지정
        rating.setStepSize(0.5f);

        //레이팅 바의 변경 상태를 실시간으로 감지하는 감지자
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            //v : 4개가 채워지면 0.5점*8만큼 해서 점수가 v=4로 넘어 온다.
            public void onRatingChanged(RatingBar ratingBar, float v, boolean fromUser) {

                //채워진 별갯수에 따른 값을 할당하도록 한다.
                float st = 100f / ratingBar.getNumStars();//지금은 별이 5개니까 st = 2가 된다. 즉 10f에는 꽉차있을때 점수를 나타낸다.

                //할당 값을 소수점 한자리로 끊어 준다.
                String str = String.format("%.1f", (st * v));
                jumsu = Float.parseFloat(str);

                Log.i("rating","점수 "+str);

            }
        });

        dialog.setCancelable(false);//뒤로가기 버튼으로 못끄게..
        dialog.show();

    }
}
