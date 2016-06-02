package com.example.lmjin_000.pedarro;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.lmjin_000.pedarro.Login_v2.Login;

/**
 * Created by lmjin_000 on 2015-11-09.
 */
public class Splash extends Activity {
    Handler handler = new Handler(); //Handler객체를 이용해 2~3초 뒤에 무언가 작동하는 기능을 수행할 수 있습니다.
    //Handler은 AutoImport가 되지 않는데 이건 Handler 클래스가 여러 종류가 있을때 발생합니다. Alt + Enter를 누르고 android.os.Handler 클래스를 선택해주세요.

    SharedPreferences setting;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        setting = getSharedPreferences("setting", 0);
        editor= setting.edit();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(setting.getBoolean("Auto_Login_enabled", false)){
                    Intent intent = new Intent(getApplicationContext(), Main.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 1000);
    }
}
