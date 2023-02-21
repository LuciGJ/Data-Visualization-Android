package com.example.chartapp.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chartapp.R;

public class TutorialActivity extends AppCompatActivity {
    private int imageindex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_screen);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        if (pref.getBoolean("tutorial", false)) {
            Intent i = new Intent(TutorialActivity.this, MenuActivity.class);
            startActivity(i);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(TutorialActivity.this);
        builder.setTitle("Hello! Would you like to follow a quick tutorial?");
        String[] items = {"Yes", "No "};
        builder.setItems(items, (dialog, which) -> {
            switch (which) {
                case 0: {
                    break;
                }

                case 1: {
                    editor.putBoolean("tutorial", true);
                    editor.apply();
                    Intent i = new Intent(TutorialActivity.this, MenuActivity.class);
                    startActivity(i);
                    break;

                }


            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
        ImageView tutorialImg = (ImageView) findViewById(R.id.tutorialimage);
        tutorialImg.setClickable(true);
        imageindex = 1;
        tutorialImg.setOnClickListener(view -> {
            imageindex++;
            switch (imageindex) {
                case 2: {
                    tutorialImg.setImageResource(R.mipmap.tutorial2);
                    break;
                }
                case 3: {
                    tutorialImg.setImageResource(R.mipmap.tutorial3);
                    break;
                }
                case 4: {
                    tutorialImg.setImageResource(R.mipmap.tutorial4);
                    break;
                }
                case 5: {
                    tutorialImg.setImageResource(R.mipmap.tutorial5);
                    break;
                }
                case 6: {
                    editor.putBoolean("tutorial", true);
                    editor.commit();
                    Intent i = new Intent(TutorialActivity.this, MenuActivity.class);
                    startActivity(i);
                }
            }
        });
    }
}
