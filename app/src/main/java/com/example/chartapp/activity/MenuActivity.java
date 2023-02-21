package com.example.chartapp.activity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.chartapp.R;
import com.example.chartapp.data.DataModel;
import com.example.chartapp.helper.DataValidator;
import com.example.chartapp.utility.FileParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVWriter;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    private String chartType = "Line Chart";
    private String path = "";
    private long downloadID;
    private Uri currFileURI = null;

    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID == id) {
                FileParser parser = new FileParser(chartType);
                parser.parseFile(path, true, MenuActivity.this, currFileURI);
            }
        }
    };

    public void init() {
        DataModel dataModel = DataModel.getInstance(MenuActivity.this);
        ImageButton toCompareButton = (ImageButton) findViewById(R.id.tocomparebutton);
        toCompareButton.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this, CompareActivity.class)));
        int entriesnumber = dataModel.numberOfRows();
        if (entriesnumber == 0) {
            LinearLayout dbItems = (LinearLayout) findViewById(R.id.databaseitemslayout);
            dbItems.removeAllViews();
            TextView messsage = new TextView(MenuActivity.this);
            messsage.setText("No charts created");
            messsage.setTextColor(Color.parseColor("#FFFFFF"));
            dbItems.addView(messsage);


        } else {
            List<ArrayList<String>> dbentries = dataModel.getAllCharts();
            LinearLayout dbItems = (LinearLayout) findViewById(R.id.databaseitemslayout);
            dbItems.removeAllViews();

            for (int i = 0; i < dbentries.size(); i++) {
                LinearLayout row = new LinearLayout(this);
                row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                TextView displayText = new TextView(MenuActivity.this);
                String charttype = dbentries.get(i).get(2);
                long date = Long.parseLong(dbentries.get(i).get(3));
                Date lastModified = new Date(date);
                android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", lastModified);

                displayText.setText(dbentries.get(i).get(1) + "\n" + charttype + "\nLast modified: " + lastModified);
                displayText.setTextColor(Color.parseColor("#FFFFFF"));
                displayText.setTextSize(15);
                ImageView displayImage = new ImageView(MenuActivity.this);
                row.setBackgroundColor(Color.parseColor("#000000"));
                switch (charttype) {
                    case "Line Chart": {
                        displayImage.setImageResource(R.mipmap.linechart);
                        break;
                    }
                    case "Spline Chart": {

                        displayImage.setImageResource(R.mipmap.splinechart);

                        break;
                    }
                    case "Area Chart": {

                        displayImage.setImageResource(R.mipmap.arealinechart);

                        break;
                    }
                    case "Area Spline Chart": {

                        displayImage.setImageResource(R.mipmap.areasplinechart);

                        break;
                    }
                    case "Bubble Chart": {

                        displayImage.setImageResource(R.mipmap.bubblechart);

                        break;
                    }
                    case "Scatterplot Chart": {

                        displayImage.setImageResource(R.mipmap.scatterchart);
                        break;
                    }
                    case "Pie Chart": {

                        displayImage.setImageResource(R.mipmap.piechart);

                        break;
                    }
                    case "Radar Chart": {

                        displayImage.setImageResource(R.mipmap.radarchart);
                        break;
                    }
                    case "Bar Chart": {
                        displayImage.setImageResource(R.mipmap.barchart);
                        break;
                    }
                    case "Horizontal Bar Chart": {
                        displayImage.setImageResource(R.mipmap.horizontalbarchart);
                        break;
                    }
                    case "Stacked Bar Chart": {
                        displayImage.setImageResource(R.mipmap.stackedbarchart);
                        break;
                    }
                    case "Horizontal Stacked Bar Chart": {
                        displayImage.setImageResource(R.mipmap.horizontalstackedbarchart);
                        break;
                    }
                    case "Candlestick Chart": {
                        displayImage.setImageResource(R.mipmap.candlestickchart);
                        break;
                    }
                }
                int dbIndex = Integer.parseInt(dbentries.get(i).get(0));
                row.setId(dbIndex);
                row.addView(displayImage);
                row.addView(displayText);
                row.setOnLongClickListener((View.OnLongClickListener) v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                    builder.setTitle("Choose an action:");
                    String[] items = {"Copy", "Delete ", "Export data", "Export chart", "Close"};
                    builder.setItems(items, (DialogInterface.OnClickListener) (dialog, which) -> {
                        switch (which) {
                            case 0: {
                                dataModel.setChartId(v.getId());
                                Cursor rs = dataModel.getData();
                                rs.moveToFirst();
                                String chartname = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_TITLE)) + "Copy";
                                String chartTypeRetrieved = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_TYPE));
                                String retrievedEnabled = rs.getString(rs.getColumnIndex(DataModel.CHART_ENABLED_SETS));
                                String retrievedColumns = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_COLLUMNS));
                                String retrievedValues = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_VALUES));
                                String retrievedSettings = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_SETTINGS));
                                String retrievedColors = rs.getString(rs.getColumnIndex(DataModel.CHARTS_COLORS));
                                String retrievedScatter = rs.getString(rs.getColumnIndex(DataModel.CHARTS_SCATTERPLOT_VALUES));
                                Date now = new Date();
                                String lastModifiedStr = String.valueOf(now.getTime());
                                if (!rs.isClosed()) {
                                    rs.close();
                                }
                                dataModel.insertChart(chartname, chartTypeRetrieved, retrievedColumns, retrievedValues, retrievedSettings, retrievedColors, retrievedScatter, retrievedEnabled, lastModifiedStr);
                                init();
                                break;
                            }

                            case 1: {
                                dataModel.deleteChart(v.getId());
                                init();
                                break;
                            }
                            case 2: {
                                if (ContextCompat.checkSelfPermission(MenuActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                                }

                                if (ContextCompat.checkSelfPermission(MenuActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                }
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(MenuActivity.this);
                                builder1.setTitle("Choose a format:");
                                String[] items1 = {"xls", "xlsx ", "csv", "Close"};
                                builder1.setItems(items1, (DialogInterface.OnClickListener) (dialog1, which1) -> {
                                    switch (which1) {
                                        case 0: {
                                            new Thread(() -> {
                                                dataModel.setChartId(v.getId());
                                                Cursor rs = dataModel.getData();
                                                rs.moveToFirst();
                                                String retrievedColumns = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_COLLUMNS));
                                                String retrievedValues = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_VALUES));
                                                Gson gson = new Gson();
                                                Type typestr = new TypeToken<ArrayList<String>>() {
                                                }.getType();
                                                Type typenestedlist = new TypeToken<ArrayList<ArrayList<Float>>>() {
                                                }.getType();
                                                List<String> datasetsNames = gson.fromJson(retrievedColumns, typestr);
                                                List<List<Float>> loadedValues = gson.fromJson(retrievedValues, typenestedlist);
                                                HSSFWorkbook workbook = new HSSFWorkbook();
                                                HSSFSheet sheet = workbook.createSheet("Sheet");
                                                int rowCount = 0;
                                                int columnCount = 0;
                                                Row row1 = sheet.createRow(rowCount++);
                                                for (String dataset : datasetsNames) {

                                                    Cell cell = row1.createCell(columnCount++);
                                                    cell.setCellValue((String) dataset);

                                                }
                                                int numberOfValues = loadedValues.get(0).size();
                                                for (int i1 = 0; i1 < numberOfValues; i1++) {
                                                    row1 = sheet.createRow(rowCount++);
                                                    columnCount = 0;
                                                    for (List<Float> selected_list : loadedValues) {
                                                        Cell cell = row1.createCell(columnCount++);
                                                        cell.setCellValue((Float) selected_list.get(i1));

                                                    }
                                                }
                                                Date now = new Date();
                                                String filename = now.getTime() + ".xls";
                                                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
                                                String mPath = path + "/" + filename;

                                                File documentFile = new File(mPath);
                                                try {
                                                    FileOutputStream outputStream = new FileOutputStream(documentFile);
                                                    workbook.write(outputStream);
                                                    outputStream.flush();
                                                    outputStream.close();

                                                } catch (Throwable e) {
                                                    Toast.makeText(MenuActivity.this, "Unable to export file!",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }).start();

                                            break;
                                        }

                                        case 1: {
                                            new Thread(() -> {
                                                dataModel.setChartId(v.getId());
                                                Cursor rs = dataModel.getData();
                                                rs.moveToFirst();
                                                String retrievedColumns = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_COLLUMNS));
                                                String retrievedValues = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_VALUES));
                                                Gson gson = new Gson();
                                                Type typestr = new TypeToken<ArrayList<String>>() {
                                                }.getType();
                                                Type typenestedlist = new TypeToken<ArrayList<ArrayList<Float>>>() {
                                                }.getType();
                                                List<String> datasetsNames = gson.fromJson(retrievedColumns, typestr);
                                                List<List<Float>> loadedValues = gson.fromJson(retrievedValues, typenestedlist);
                                                XSSFWorkbook workbook = new XSSFWorkbook();
                                                XSSFSheet sheet = workbook.createSheet("Sheet");
                                                int rowCount = 0;
                                                int columnCount = 0;
                                                Row row1 = sheet.createRow(rowCount++);
                                                for (String dataset : datasetsNames) {

                                                    Cell cell = row1.createCell(columnCount++);
                                                    cell.setCellValue((String) dataset);

                                                }
                                                int numberOfValues = loadedValues.get(0).size();
                                                for (int i1 = 0; i1 < numberOfValues; i1++) {
                                                    row1 = sheet.createRow(rowCount++);
                                                    columnCount = 0;
                                                    for (List<Float> selected_list : loadedValues) {
                                                        Cell cell = row1.createCell(columnCount++);
                                                        cell.setCellValue((Float) selected_list.get(i1));

                                                    }
                                                }
                                                Date now = new Date();
                                                String filename = now.getTime() + ".xlsx";
                                                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
                                                String mPath = path + "/" + filename;

                                                File documentFile = new File(mPath);
                                                try {
                                                    FileOutputStream outputStream = new FileOutputStream(documentFile);
                                                    workbook.write(outputStream);
                                                    outputStream.flush();
                                                    outputStream.close();

                                                } catch (Throwable e) {
                                                    Toast.makeText(MenuActivity.this, "Unable to export file!",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }).start();
                                            break;
                                        }
                                        case 2: {
                                            new Thread(() -> {
                                                dataModel.setChartId(v.getId());
                                                Cursor rs = dataModel.getData();
                                                rs.moveToFirst();
                                                String retrievedColumns = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_COLLUMNS));
                                                String retrievedValues = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_VALUES));
                                                Gson gson = new Gson();
                                                Type typestr = new TypeToken<ArrayList<String>>() {
                                                }.getType();
                                                Type typenestedlist = new TypeToken<ArrayList<ArrayList<Float>>>() {
                                                }.getType();
                                                List<String> datasetsNames = gson.fromJson(retrievedColumns, typestr);
                                                List<List<Float>> loadedValues = gson.fromJson(retrievedValues, typenestedlist);
                                                Date now = new Date();
                                                String filename = now.getTime() + ".csv";
                                                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
                                                String mPath = path + "/" + filename;
                                                File file = new File(mPath);
                                                try {
                                                    FileWriter outputfile = new FileWriter(file);
                                                    CSVWriter writer = new CSVWriter(outputfile);
                                                    String[] firrstrow = datasetsNames.toArray(new String[0]);
                                                    writer.writeNext(firrstrow);
                                                    int numberOfValues = loadedValues.get(0).size();
                                                    for (int i1 = 0; i1 < numberOfValues; i1++) {
                                                        List<String> temp_list = new ArrayList<>();
                                                        for (List<Float> selected_list : loadedValues) {
                                                            temp_list.add(String.valueOf(selected_list.get(i1)));
                                                        }
                                                        String[] newrow = temp_list.toArray(new String[0]);
                                                        writer.writeNext(newrow);
                                                    }
                                                    writer.close();


                                                } catch (Throwable e) {
                                                    Toast.makeText(MenuActivity.this, "Unable to export file!",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }).start();

                                            break;
                                        }
                                        case 3: {
                                            break;
                                        }


                                    }
                                });


                                AlertDialog dialog2 = builder1.create();
                                dialog2.show();
                                break;
                            }
                            case 3: {
                                if (ContextCompat.checkSelfPermission(MenuActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                                }

                                if (ContextCompat.checkSelfPermission(MenuActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                }
                                dataModel.setChartId(v.getId());
                                Cursor rs = dataModel.getData();
                                rs.moveToFirst();
                                String chartname = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_TITLE));
                                String chartTypeRetrieved = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_TYPE));
                                String retrievedEnabled = rs.getString(rs.getColumnIndex(DataModel.CHART_ENABLED_SETS));
                                String retrievedColumns = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_COLLUMNS));
                                String retrievedValues = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_VALUES));
                                String retrievedSettings = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_SETTINGS));
                                String retrievedColors = rs.getString(rs.getColumnIndex(DataModel.CHARTS_COLORS));
                                String retrievedScatter = rs.getString(rs.getColumnIndex(DataModel.CHARTS_SCATTERPLOT_VALUES));
                                Date now = new Date();
                                String lastModifiedStr = String.valueOf(now.getTime());
                                if (!rs.isClosed()) {
                                    rs.close();
                                }
                                List<String> export_list = new ArrayList<>();
                                export_list.add(chartname);
                                export_list.add(chartTypeRetrieved);
                                export_list.add(retrievedColumns);
                                export_list.add(retrievedValues);
                                export_list.add(retrievedSettings);
                                export_list.add(retrievedColors);
                                export_list.add(retrievedScatter);
                                export_list.add(retrievedEnabled);
                                export_list.add(lastModifiedStr);
                                now = new Date();
                                String filename = now.getTime() + ".dvf";
                                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
                                String mPath = path + "/" + filename;
                                File file = new File(mPath);
                                try {
                                    FileWriter outputfile = new FileWriter(file);
                                    CSVWriter writer = new CSVWriter(outputfile);
                                    String[] firrstrow = export_list.toArray(new String[0]);
                                    writer.writeNext(firrstrow);

                                    writer.close();


                                } catch (Throwable e) {
                                    Toast.makeText(MenuActivity.this, "Unable to export file!",
                                            Toast.LENGTH_LONG).show();
                                }
                                break;
                            }
                            case 4: {
                                break;
                            }


                        }
                    });


                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                });
                row.setOnClickListener(view -> {
                    int selectedId = view.getId();
                    Intent intent = new Intent(getBaseContext(), GenerateChartActivity.class);
                    intent.putExtra("SELECTED_ID", selectedId);
                    intent.putExtra("LOADED", true);
                    startActivity(intent);


                });
                dbItems.addView(row);


            }
        }
        ImageButton createChart = (ImageButton) findViewById(R.id.createchartbutton);
        createChart.setClickable(true);
        createChart.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
            builder.setTitle("Create a chart:");
            String[] items = {"From file", "From URL", "Close"};
            builder.setItems(items, (dialog, which) -> {
                switch (which) {
                    case 0: {
                        Intent intent = new Intent();
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "DEMO"), 1001);
                        break;
                    }

                    case 1: {
                        AlertDialog.Builder builder12 = new AlertDialog.Builder(MenuActivity.this);
                        builder12.setTitle("File URL");
                        final EditText input = new EditText(MenuActivity.this);
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder12.setView(input);

                        builder12.setPositiveButton("OK", (dialog14, which14) -> {
                            String urlstring = input.getText().toString();
                            if (DataValidator.isValidURL(urlstring)) {
                                DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                currFileURI = Uri.parse(urlstring);
                                DownloadManager.Request request = new DownloadManager.Request(currFileURI);
                                request.setTitle("My File");
                                request.setDescription("Downloading");
                                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                                AlertDialog.Builder builder3 = new AlertDialog.Builder(MenuActivity.this);
                                builder3.setTitle("Choose file format:");
                                String[] items12 = {".csv", ".xls", ".xlsx", ".dvf", "Cancel"};
                                builder3.setItems(items12, (dialog12, which12) -> {
                                    String fileextension;
                                    switch (which12) {
                                        case 0:
                                        case 3:

                                        case 1:
                                        case 2: {
                                            fileextension = items12[which12];
                                            Date now = new Date();
                                            String nameoffile = now.getTime() + fileextension;
                                            path = path + File.separator + nameoffile;
                                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nameoffile);
                                            downloadID = downloadmanager.enqueue(request);
                                            currFileURI = Uri.fromFile(new File(path));
                                            Toast.makeText(MenuActivity.this, "Getting the file...",
                                                    Toast.LENGTH_LONG).show();
                                            break;
                                        }
                                        case 4: {
                                            break;
                                        }


                                        default:
                                            break;
                                    }
                                });


                                AlertDialog dialog2 = builder3.create();
                                dialog2.show();

                            } else {
                                Toast.makeText(MenuActivity.this, "Invalid URL!",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                        builder12.setNegativeButton("Cancel", (dialog13, which13) -> dialog13.cancel());

                        builder12.show();

                        break;
                    }
                    case 2: {
                        break;
                    }


                }
            });


            AlertDialog dialog2 = builder.create();
            dialog2.show();

        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        init();
    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1001) {
            currFileURI = data.getData();
            path = currFileURI.getPath();
            FileParser parser = new FileParser(chartType);
            parser.parseFile(path, false, MenuActivity.this, currFileURI);


        }
    }
}