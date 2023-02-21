package com.example.chartapp.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.chartapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class EmailSender {
    public void sendEmail(Context context, String email, String chart_title)
    {
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
            Spinner spinner=activity.findViewById(R.id.spinner);
            spinner.setVisibility(View.GONE);
            String path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            String mPath = path + "/" + screenshotName + ".png";
            View v1 = activity.getWindow().findViewById(R.id.chart_constraint);

            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            spinner.setVisibility(View.VISIBLE);
            Uri uri = FileProvider.getUriForFile(context, activity.getApplicationContext().getPackageName() + ".provider", imageFile);
            Intent sendImage = new Intent(android.content.Intent.ACTION_SEND);
            sendImage.putExtra(Intent.EXTRA_EMAIL, new String[]{ email});
            sendImage.putExtra(Intent.EXTRA_SUBJECT, chart_title);
            sendImage.putExtra(Intent.EXTRA_STREAM, uri);
            sendImage.setType("text/plain");
            sendImage.setType("image/png");
            activity.startActivityForResult(Intent.createChooser(sendImage, "Choose an Email client"), 77);
        } catch (Throwable e) {

            Toast.makeText(context, "Unable to send email!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
