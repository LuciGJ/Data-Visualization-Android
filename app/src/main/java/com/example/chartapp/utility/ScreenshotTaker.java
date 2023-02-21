package com.example.chartapp.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.chartapp.activity.CompareActivity;
import com.example.chartapp.activity.GenerateChartActivity;
import com.example.chartapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class ScreenshotTaker {
    public void takeScreenshot(Context context) {
        Activity activity=(Activity) context;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        Date now = new Date();
        String screenshotName=String.valueOf(now.getTime());

        try {

            String path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            String mPath = path + "/" + screenshotName + ".jpg";
            View v1=null;
            if(activity instanceof CompareActivity) {
                 v1 = activity.getWindow().findViewById(R.id.scrollView4);
            }
            else if(activity instanceof GenerateChartActivity)
            {
                Spinner spinner=activity.findViewById(R.id.spinner);
                spinner.setVisibility(View.GONE);
                v1 = activity.getWindow().findViewById(R.id.chart_constraint);
            }

            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            if(activity instanceof  GenerateChartActivity)
            {
                Spinner spinner=activity.findViewById(R.id.spinner);
                spinner.setVisibility(View.VISIBLE);
            }
            Toast.makeText(context, "Saving image...",
                    Toast.LENGTH_LONG).show();


        } catch (Throwable e) {

            Toast.makeText(context, "Unable to take screenshot!!",
                    Toast.LENGTH_LONG).show();
        }
    }
}
