package com.example.chartapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chartapp.chart.ChartGenerator;
import com.example.chartapp.R;
import com.example.chartapp.data.DataModel;
import com.example.chartapp.utility.ScreenshotTaker;
import com.github.mikephil.charting.charts.Chart;

import java.util.ArrayList;
import java.util.List;

public class CompareActivity extends AppCompatActivity {
    private final DataModel dataModel = DataModel.getInstance(this);
    private final List<Integer> selectedItems = new ArrayList<>();

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CompareActivity.this, MenuActivity.class));

    }

    public void selectCharts() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CompareActivity.this);
        builder.setTitle("Charts");
        LinearLayout lay = new LinearLayout(CompareActivity.this);
        lay.setOrientation(LinearLayout.VERTICAL);
        int entriesnumber = dataModel.numberOfRows();
        if (entriesnumber == 0) {


            TextView messsage = new TextView(CompareActivity.this);
            messsage.setText("No charts created");
            lay.addView(messsage);

        } else {
            List<ArrayList<String>> dbentries = dataModel.getAllCharts();
            for (int i = 0; i < dbentries.size(); i++) {
                CheckBox tempCheck = new CheckBox(CompareActivity.this);
                tempCheck.setText(dbentries.get(i).get(1) + " - " + dbentries.get(i).get(2));
                int dbindex = Integer.parseInt(dbentries.get(i).get(0));
                tempCheck.setId(dbindex);
                tempCheck.setChecked(selectedItems.contains(dbindex));

                tempCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            if (isChecked) {
                                selectedItems.add(tempCheck.getId());


                            } else {
                                selectedItems.remove(Integer.valueOf(tempCheck.getId()));


                            }
                        }
                );
                lay.addView(tempCheck);
            }

        }
        builder.setPositiveButton(
                "Done",
                (DialogInterface.OnClickListener) (dialog, id) -> {
                    LinearLayout compareCharts = (LinearLayout) findViewById(R.id.compare_linear);
                    compareCharts.removeAllViews();
                    if (selectedItems.size() != 0) {


                        for (Integer i : selectedItems) {
                            dataModel.setChartId(i);
                            dataModel.loadChartDB();
                            updatechart();
                        }
                        LinearLayout row = new LinearLayout(CompareActivity.this);
                        row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
                        row.setGravity(Gravity.CENTER);
                        row.setBackgroundColor(Color.WHITE);
                        compareCharts.addView(row);
                    }
                    dialog.cancel();
                });
        builder.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
        ScrollView scroll = new ScrollView(CompareActivity.this);
        scroll.addView(lay);
        builder.setView(scroll);
        AlertDialog alert1 = builder.create();
        alert1.show();
    }


    public void updatechart() {
        dataModel.getLabels().clear();
        dataModel.getArrayOfDates().clear();
        ChartGenerator generator = new ChartGenerator(this, dataModel.getChartId());
        Chart chart = generator.updateChart();
        TextView chartTitleTv = new TextView(CompareActivity.this);
        TextView x_labeltv = new TextView(CompareActivity.this);
        TextView y_labeltv = new TextView(CompareActivity.this);

        if (dataModel.isTitleBool()) {

            chartTitleTv.setText(dataModel.getChartTitle());
            chartTitleTv.setTextColor(dataModel.getChartTitleColor());
            chartTitleTv.setVisibility(View.VISIBLE);
        } else {
            chartTitleTv.setVisibility(View.GONE);
        }


        if (dataModel.getChartType().equals("Horizontal Bar Chart") || dataModel.getChartType().equals("Horizontal Stacked Bar Chart")) {

            x_labeltv.setVisibility(View.GONE);
            chartTitleTv.setVisibility(View.GONE);
            y_labeltv.setVisibility(View.GONE);
            y_labeltv.setRotation(90);
            if (dataModel.isxAxisBool() && dataModel.isxLabelBool()) {

                x_labeltv.setText(dataModel.getyLabelStr());

                x_labeltv.setTextColor(dataModel.getyColor());
                x_labeltv.setVisibility(View.VISIBLE);
            }

            if (dataModel.isYaxisBool() && dataModel.isyLabelBool()) {

                y_labeltv.setText(dataModel.getxLabelString());
                y_labeltv.setTextColor(dataModel.getxColor());
                y_labeltv.setVisibility(View.VISIBLE);
            }
        } else {
            y_labeltv.setVisibility(View.GONE);
            y_labeltv.setRotation(90);
            if (dataModel.isxAxisBool() && dataModel.isxLabelBool()) {

                x_labeltv.setText(dataModel.getxLabelString());
                x_labeltv.setTextColor(dataModel.getxColor());
                x_labeltv.setVisibility(View.VISIBLE);
            }

            if (dataModel.isYaxisBool() && dataModel.isyLabelBool()) {

                y_labeltv.setText(dataModel.getyLabelStr());
                y_labeltv.setTextColor(dataModel.getyColor());
                y_labeltv.setVisibility(View.VISIBLE);
            }

        }
        LinearLayout linearLayout = new LinearLayout(CompareActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout row = new LinearLayout(this);
        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 800));
        chart.setLayoutParams(new LinearLayout.LayoutParams(800, 700));
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 900));
        row.setGravity(Gravity.CENTER);
        chartTitleTv.setGravity(Gravity.CENTER);
        row.setBackgroundColor(dataModel.getBackgroundColor());

        row.addView(chart);
        linearLayout.addView(chartTitleTv);
        linearLayout.addView(row);

        linearLayout.setBackgroundColor(dataModel.getBackgroundColor());
        LinearLayout compareCharts = (LinearLayout) findViewById(R.id.compare_linear);
        compareCharts.addView(linearLayout);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        selectCharts();
        final String[] paths = {"Change selected charts", "Save as image", "Exit", "Nothing"};
        Spinner menu = (Spinner) findViewById(R.id.comparesettings_button);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CompareActivity.this,
                android.R.layout.simple_spinner_item, paths) {
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return super.getDropDownView(position, convertView, parent);
            }

            public int getCount() {
                return paths.length - 1;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menu.setAdapter(adapter);
        menu.setSelection(3);
        menu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {

                        selectCharts();
                        menu.setSelection(3);
                        break;
                    }

                    case 1: {
                        ScreenshotTaker screenshottaker = new ScreenshotTaker();
                        screenshottaker.takeScreenshot(CompareActivity.this);
                        menu.setSelection(3);
                        break;
                    }
                    case 2: {
                        Intent intent = new Intent(CompareActivity.this, MenuActivity.class);
                        startActivity(intent);
                    }
                    case 3: {
                        break;
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
}