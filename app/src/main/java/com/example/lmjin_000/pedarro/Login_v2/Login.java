package com.example.lmjin_000.pedarro.Login_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lmjin_000.pedarro.Main;
import com.example.lmjin_000.pedarro.R;
import com.example.lmjin_000.pedarro.component.ApplicationController;
import com.example.lmjin_000.pedarro.model.registerJ;
import com.example.lmjin_000.pedarro.network.NetworkService;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class Login extends AppCompatActivity {

    EditText editCar, editPasswd;
    CheckBox autoLogin;
    String loginId, pass;
    Button btnLogin,btnJoin;
    SharedPreferences setting;
    SharedPreferences.Editor editor;
    private NetworkService networkService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ApplicationController application = ApplicationController.getInstance();
        application.buildNetworkService("52.36.42.94", 3000);
        networkService = ApplicationController.getInstance().getNetworkService();
        
        TextView text_car  = (TextView) findViewById(R.id.text_car);

        TextView text_passwd  = (TextView) findViewById(R.id.text_passwd);

        TextView btn_login  = (TextView) findViewById(R.id.btn_login);

        setting = getSharedPreferences("setting", 0);
        editor= setting.edit();

        editCar = (EditText) findViewById(R.id.edit_car);
        editPasswd = (EditText) findViewById(R.id.edit_passwd);
        autoLogin = (CheckBox) findViewById(R.id.auto_login);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnJoin = (Button)findViewById(R.id.btn_join);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, register.class);
                startActivity(intent);
            }
        });
        
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginId = editCar.getText().toString();
                pass = editPasswd.getText().toString();

                Call<registerJ> thumbnailCall = networkService.getNameLogin(loginId,pass);

                thumbnailCall.enqueue(new Callback<registerJ>() {
                    @Override
                    public void onResponse(Response<registerJ> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            registerJ login_temp = response.body();
                            editor.putString("ID", loginId);
                            editor.putString("PW", pass);
                            editor.putString("NAME", login_temp.getName());
                            editor.putString("token",login_temp.getRegisterid());
                            editor.commit();
                            
                            Intent intent = new Intent(Login.this, Main.class);
                            startActivity(intent);
                            Toast.makeText(getBaseContext(), "로그인되었습니다", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            int statusCode = response.code();
                            Log.i("MyTag", "응답코드 : " + statusCode);
                            Toast.makeText(getBaseContext(), "비밀번호 틀렸습니다", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i("MyTag", "서버 onFailure 에러내용 : " + t.getMessage());
                    }
                });
                
            }
        });
        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("Auto_Login_enabled", true);
                    editor.commit();
                } else {
                    editor.putBoolean("Auto_Login_enabled", false);
                    editor.commit();
                }
            }
        });
    }
}