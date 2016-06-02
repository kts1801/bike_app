package com.example.lmjin_000.pedarro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by lmjin_000 on 2016-05-25.
 */
public class Empty extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Intent intent1 = getIntent();

        Intent intent = new Intent(getApplicationContext(), BikeRent.class);
        intent.putExtra("StationName", intent1.getStringExtra("StationName"));

        startActivity(intent);
        finish();
    }
}
