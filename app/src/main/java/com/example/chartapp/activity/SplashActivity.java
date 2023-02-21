package com.example.chartapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chartapp.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash_screen);
        int SPLASH_DISPLAY_LENGTH = 1000;
        new Handler().postDelayed(() -> {

            Intent mainIntent = new Intent(SplashActivity.this, TutorialActivity.class);
            SplashActivity.this.startActivity(mainIntent);
            SplashActivity.this.finish();
        }, SPLASH_DISPLAY_LENGTH);
    }

}