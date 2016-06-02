package com.example.lmjin_000.pedarro.GCM;

/**
 * Created by lmjin_000 on 2016-05-10.
 */

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.example.lmjin_000.pedarro.R;

public class Custom_Dialog extends Dialog implements View.OnClickListener {

    private EditText detail;
    private Button addOK;
    private String s_detail;
    private RatingBar rating;
    private float jumsu;

    public Custom_Dialog(Context context) {
        super(context); // context 객체를 받는 생성자가 반드시 필요!
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog); // 커스텀 다이얼로그 레이아웃

        detail = (EditText) findViewById(R.id.edit_detail);
        addOK = (Button) findViewById(R.id.btn_send);
        rating = (RatingBar)findViewById(R.id.ratingBar);

        addOK.setOnClickListener(this);
    }

    public String getDetail() {
        return s_detail; // 사이트 주소를 반환하는 메소드
    }

    public float getJumsu() {
        return jumsu;
    }

    @Override // 터치 리스너
    public void onClick(View v) {
        // 확인 버튼을 클릭하면 입력한 값을 적절히 설정한 후 다이얼로그를 닫음
        if (v == addOK) {
            s_detail = detail.getText().toString();
            rating.setIsIndicator(false);
            rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating,
                                            boolean fromUser) {
                    jumsu = rating;
                    Log.i("rating","점수 : " +String.valueOf(rating));
                }
            });
            dismiss(); // 이후 MainActivity에서 구현해준 Dismiss 리스너가 작동함
        }
    }
}