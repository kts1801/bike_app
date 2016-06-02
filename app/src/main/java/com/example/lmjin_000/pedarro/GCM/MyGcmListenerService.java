package com.example.lmjin_000.pedarro.GCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.lmjin_000.pedarro.Main;
import com.example.lmjin_000.pedarro.R;
import com.example.lmjin_000.pedarro.component.ApplicationController;
import com.example.lmjin_000.pedarro.model.BikeTmap;
import com.example.lmjin_000.pedarro.network.NetworkService;
import com.google.android.gms.gcm.GcmListenerService;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by lmjin_000 on 2016-03-23.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    private NetworkService networkService;
    SharedPreferences setting;
    SharedPreferences.Editor editor;
    Handler handler = new Handler();
    String bikeid = null;
    /**
     *
     * @param from SenderID 값을 받아온다.
     * @param data Set형태로 GCM으로 받은 데이터 payload이다.
     */
    @Override
        public void onMessageReceived(String from, Bundle data) {

        //서버 접속
        ApplicationController application = ApplicationController.getInstance();
        application.buildNetworkService("52.36.42.94", 3000);
        networkService = ApplicationController.getInstance().getNetworkService();

        Bikeid();

        setting = getSharedPreferences("setting", 0);
        editor= setting.edit();
        editor.putBoolean("asynctask",true);
        editor.commit();

        final String title = data.getString("title");
        final String message = data.getString("message");

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Title: " + title);
        Log.d(TAG, "Message: " + message);

        // GCM으로 받은 메세지를 디바이스에 알려주는 sendNotification()을 호출한다.
        sendNotification(title, message);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Bike: " + bikeid);

                Intent intent = new Intent(getApplicationContext(), ShowMsgActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("title", title);
                intent.putExtra("message", message);
                intent.putExtra("bike", bikeid);
                getApplicationContext().startActivity(intent);

            }
        }, 500);

    }


    /**
     * 실제 디바에스에 GCM으로부터 받은 메세지를 알려주는 함수이다. 디바이스 Notification Center에 나타난다.
     * @param title
     * @param message
     */
    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, Main.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public void Bikeid() {

        setting = getSharedPreferences("setting", 0);

        Call<BikeTmap> gcmCall = networkService.getBikeid(setting.getString("ID",""),1);//NetworkService에 등록해둔거 호출
        gcmCall.enqueue(new Callback<BikeTmap>() {
            @Override
            public void onResponse(Response<BikeTmap> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    BikeTmap result = response.body();

                    bikeid = result.getBike();
                    Log.d(TAG, "Bike1: " + bikeid);
                    Log.d(TAG, "Bike2: " + result.getBike());
                } else {
                    int statusCode = response.code();
                    Log.i(TAG, "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "서버 onFailure 에러내용 : " + t.getMessage());
            }
        });
    }
}