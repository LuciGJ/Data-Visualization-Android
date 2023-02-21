package com.example.chartapp.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.chartapp.chart.ChartGenerator;
import com.example.chartapp.R;
import com.example.chartapp.data.DataModel;
import com.example.chartapp.helper.DataAnalyzer;
import com.example.chartapp.helper.DataHighlighter;
import com.example.chartapp.helper.DataValidator;
import com.example.chartapp.utility.EmailSender;
import com.example.chartapp.utility.ScreenshotTaker;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.highlight.Highlight;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateChartActivity extends AppCompatActivity {
    private final DataModel dataModel = DataModel.getInstance(this);
    private Dialog settingsDialog;
    private final String numberPattern = "[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)";

    public void updatechart() {
        ChartGenerator generator = new ChartGenerator(this, dataModel.getChartId());
        Chart chart = generator.updateChart();
        dataModel.setLabels(generator.getDataModel().getLabels());
        dataModel.setArrayOfDates(generator.getDataModel().getArrayOfDates());
        TextView title = (TextView) findViewById(R.id.chart_title);
        TextView xTitle = (TextView) findViewById(R.id.chart_X);
        TextView yTitle = (TextView) findViewById(R.id.chart_Y);
        title.setText(dataModel.getChartTitle());
        title.setTextColor(dataModel.getChartTitleColor());
        if (dataModel.getChartType().equals("Pie Chart") || dataModel.getChartType().equals("Radar Chart")) {
            xTitle.setVisibility(View.GONE);
            yTitle.setVisibility(View.GONE);
        } else {
            if (dataModel.isxLabelBool())
                xTitle.setVisibility(View.VISIBLE);
            else
                xTitle.setVisibility(View.GONE);
            if (dataModel.isYaxisBool())
                yTitle.setVisibility(View.VISIBLE);
            else
                yTitle.setVisibility(View.GONE);
        }
        if (dataModel.getChartType().equals("Horizontal Bar Chart") || dataModel.getChartType().equals("Horizontal Stacked Bar Chart")) {
            xTitle.setText(dataModel.getyLabelStr());
            xTitle.setTextColor(dataModel.getyColor());
            yTitle.setText(dataModel.getxLabelString());
            yTitle.setTextColor(dataModel.getxColor());
        } else {
            xTitle.setText(dataModel.getxLabelString());
            xTitle.setTextColor(dataModel.getxColor());
            yTitle.setText(dataModel.getyLabelStr());
            yTitle.setTextColor(dataModel.getyColor());
        }
        setMenu(chart);
        ConstraintLayout chart_constrain = (ConstraintLayout) findViewById(R.id.chart_constraint);
        RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lay.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        RelativeLayout chart_layout = (RelativeLayout) findViewById(R.id.chart_layout);
        chart_layout.removeAllViews();
        chart_layout.setBackgroundColor(dataModel.getBackgroundColor());
        chart_constrain.setBackgroundColor(dataModel.getBackgroundColor());

        chart_layout.addView(chart, lay);


    }


    public void setMenu(Chart chart) {
        final String[] paths = {"Highlight", "Clear Highlight", "Animate X", "Animate Y", "Animate XY", "Settings", "Save as image", "Data Analysis", "Send by email", "View values", "Exit", "Nothing"};
        Spinner menu = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(GenerateChartActivity.this,
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
        menu.setSelection(11);
        menu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (dataModel.getChartType().equals("Radar Chart") || dataModel.getChartType().equals("Pie Chart") || dataModel.getChartType().equals("Scatterplot Chart")) {
                            Toast.makeText(GenerateChartActivity.this, "Unavailable for this type of chart!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            DataHighlighter highlighter = new DataHighlighter(GenerateChartActivity.this, dataModel.getLabels(), dataModel.getValues(), dataModel.getxLabelTypeStr(), dataModel.getArrayOfDates());
                            if (dataModel.getChartType().equals("Stacked Bar Chart") || dataModel.getChartType().equals("Horizontal Stacked Bar Chart")) {

                                highlighter.highlightDataStacked(chart);
                            } else {
                                highlighter.highlightData(chart);
                            }
                        }
                        menu.setSelection(11);
                        break;
                    case 1:
                        if (dataModel.getChartType().equals("Radar Chart") || dataModel.getChartType().equals("Pie Chart") || dataModel.getChartType().equals("Candlestick Chart")) {
                            Toast.makeText(GenerateChartActivity.this, "Unavailable for this type of chart!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Highlight[] highlightedValues = new Highlight[0];
                            chart.highlightValues(highlightedValues);
                        }
                        menu.setSelection(11);
                        break;
                    case 2:
                        if (dataModel.getChartType().equals("Radar Chart") || dataModel.getChartType().equals("Pie Chart") || dataModel.getChartType().equals("Candlestick Chart")) {
                            Toast.makeText(GenerateChartActivity.this, "Unavailable for this type of chart!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            chart.animateX(3000);
                        }
                        menu.setSelection(11);
                        break;
                    case 3:
                        if (dataModel.getChartType().equals("Radar Chart") || dataModel.getChartType().equals("Pie Chart") || dataModel.getChartType().equals("Candlestick Chart")) {
                            Toast.makeText(GenerateChartActivity.this, "Unavailable for this type of chart!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            chart.animateY(3000);
                        }
                        break;
                    case 4:
                        if (dataModel.getChartType().equals("Radar Chart") || dataModel.getChartType().equals("Pie Chart") || dataModel.getChartType().equals("Candlestick Chart")) {
                            Toast.makeText(GenerateChartActivity.this, "Unavailable for this type of chart!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            chart.animateXY(3000, 3000);
                        }
                        menu.setSelection(11);
                        break;
                    case 5:
                        openSettings();
                        menu.setSelection(11);
                        break;
                    case 6:
                        ScreenshotTaker screenshottaker = new ScreenshotTaker();
                        screenshottaker.takeScreenshot(GenerateChartActivity.this);
                        menu.setSelection(11);
                        break;
                    case 7:
                        DataAnalyzer.openAnalysisTool(GenerateChartActivity.this, dataModel.getDatasets(), dataModel.getValues());
                        menu.setSelection(11);
                        break;
                    case 8:
                        AlertDialog.Builder builder = new AlertDialog.Builder(GenerateChartActivity.this);
                        builder.setTitle("Enter the receiver's email address:");
                        final EditText input = new EditText(GenerateChartActivity.this);
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);
                        builder.setPositiveButton("OK", (dialog, which) -> {
                            String email = input.getText().toString();
                            if (DataValidator.isValidEmail(email)) {
                                EmailSender sender = new EmailSender();
                                sender.sendEmail(GenerateChartActivity.this, email, dataModel.getChartTitle());

                            } else {
                                Toast.makeText(GenerateChartActivity.this, "Invalid email address!",
                                        Toast.LENGTH_LONG).show();
                                dialog.cancel();
                            }
                        });
                        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                        builder.show();
                        menu.setSelection(11);
                        break;
                    case 9:
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(GenerateChartActivity.this);
                        builder2.setTitle("Values");
                        final ScrollView scrollView = new ScrollView(GenerateChartActivity.this);
                        final LinearLayout lay = new LinearLayout(GenerateChartActivity.this);
                        LinearLayout row = new LinearLayout(GenerateChartActivity.this);
                        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        lay.setOrientation(LinearLayout.VERTICAL);
                        for (String dataSet : dataModel.getDatasets()) {
                            TextView textView = new TextView(GenerateChartActivity.this);
                            textView.setText(dataSet + " ");
                            row.addView(textView);
                        }
                        lay.addView(row);

                        for (int i = 0; i < dataModel.getValues().get(0).size(); i++) {

                            LinearLayout newRow = new LinearLayout(GenerateChartActivity.this);
                            newRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            for (int j = 0; j < dataModel.getValues().size(); j++) {
                                TextView valueTv = new TextView(GenerateChartActivity.this);
                                valueTv.setText(dataModel.getValues().get(j).get(i) + " ");
                                String valueId = j + "-" + i;
                                valueTv.setTag(valueId);
                                valueTv.setOnClickListener(v -> {
                                    AlertDialog.Builder builder111 = new AlertDialog.Builder(GenerateChartActivity.this);
                                    builder111.setTitle("Value");


                                    final EditText input2 = new EditText(GenerateChartActivity.this);

                                    input2.setInputType(InputType.TYPE_CLASS_PHONE);
                                    input2.addTextChangedListener(new TextWatcher() {

                                        public void afterTextChanged(Editable s) {

                                            String result = s.toString();
                                            if (!result.matches(numberPattern) && !result.matches("") && !result.matches("-")) {

                                                input2.setText("");

                                            }
                                        }

                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        }

                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        }
                                    });

                                    builder111.setView(input2);


                                    builder111.setPositiveButton("OK", (dialog, which) -> {
                                        String tempString = input2.getText().toString();
                                        if (!tempString.equals("") && !tempString.equals("-")) {
                                            String getId = valueTv.getTag().toString();
                                            String[] splitValues = getId.split("-");
                                            int firstIndex = Integer.parseInt(splitValues[0]);
                                            int secondIndex = Integer.parseInt(splitValues[0]);
                                            dataModel.getValues().get(firstIndex).set(secondIndex, Float.valueOf(input2.getText().toString()));
                                            dataModel.updateValuesDb();
                                            valueTv.setText(input2.getText().toString() + " ");
                                            updatechart();
                                        }
                                    });
                                    builder111.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                                    builder111.show();
                                });
                                newRow.addView(valueTv);
                            }
                            lay.addView(newRow);
                        }
                        scrollView.addView(lay);
                        builder2.setView(scrollView);
                        builder2.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                        builder2.show();
                        menu.setSelection(11);
                        break;
                    case 10:
                        Intent intent = new Intent(GenerateChartActivity.this, MenuActivity.class);
                        startActivity(intent);
                        menu.setSelection(11);
                        break;
                    case 11:
                        break;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void openSettings() {

        switch (dataModel.getChartType()) {
            case "Pie Chart": {
                final TextView title = (TextView) findViewById(R.id.chart_title);
                AlertDialog.Builder builder = new AlertDialog.Builder(GenerateChartActivity.this);
                builder.setTitle("Settings");
                TextView datasetsText, chartTypeText, chartTitleText, descriptionText, titlecolorText, bgcolorText;
                TextView charttype, charttitle, titlecolor, bgcolor, description;
                Switch titleswitch, decimalswitch, descriptiontoggle;
                LinearLayout lay = new LinearLayout(GenerateChartActivity.this);
                lay.setOrientation(LinearLayout.VERTICAL);
                TextView datasetsTitle = new TextView(GenerateChartActivity.this);
                datasetsTitle.setText("Datasets");
                datasetsTitle.setTypeface(null, Typeface.ITALIC);
                datasetsTitle.setGravity(Gravity.CENTER);
                datasetsTitle.setTextSize(15);
                datasetsText = new TextView(GenerateChartActivity.this);
                datasetsText.setText("Datasets");
                datasetsText.setClickable(true);
                datasetsText.setOnClickListener(v -> {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder1.setTitle("Datasets");
                    LinearLayout lay1 = new LinearLayout(GenerateChartActivity.this);
                    lay1.setOrientation(LinearLayout.VERTICAL);

                    for (int i = 0; i < dataModel.getDatasets().size(); i++) {
                        LinearLayout row = new LinearLayout(GenerateChartActivity.this);
                        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        TextView dataset = new TextView(GenerateChartActivity.this);
                        dataset.setId(i);
                        dataset.setText(dataModel.getDatasets().get(i));
                        dataset.setTextColor(dataModel.getArrayOfColors().get(i));
                        CheckBox check = new CheckBox(GenerateChartActivity.this);
                        check.setId(i);
                        check.setText("");
                        check.setChecked(dataModel.getEnabledDataSets().get(i) == 1);
                        check.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                    if (isChecked) {
                                        dataModel.getEnabledDataSets().set(check.getId(), 1);
                                    } else {

                                        dataModel.getEnabledDataSets().set(check.getId(), 0);
                                    }
                                    dataModel.updateEnabledDB();
                                    updatechart();
                                }
                        );
                        dataset.setClickable(true);
                        dataset.setOnLongClickListener(v114 -> {
                            int color = Color.parseColor("#e7dbd0");

                            ColorPickerDialogBuilder

                                    .with(GenerateChartActivity.this)
                                    .setTitle("Choose color")
                                    .initialColor(color)
                                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                    .density(12)
                                    .setOnColorSelectedListener(selectedColor -> {
                                        String colorcode = "onColorSelected: 0x" + Integer.toHexString(selectedColor);
                                    })
                                    .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {

                                        dataModel.getArrayOfColors().set(dataset.getId(), selectedColor);
                                        dataset.setTextColor(selectedColor);
                                        dataModel.updateColorsDB();
                                        updatechart();
                                    })
                                    .setNegativeButton("cancel", (dialog, which) -> {
                                    })
                                    .build()
                                    .show();
                            return false;
                        });
                        dataset.setOnClickListener(v113 -> {
                            AlertDialog.Builder builder1110 = new AlertDialog.Builder(GenerateChartActivity.this);
                            builder1110.setTitle("Name");


                            final EditText input = new EditText(GenerateChartActivity.this);


                            builder1110.setView(input);


                            builder1110.setPositiveButton("OK", (dialog, which) -> {
                                String tempText = input.getText().toString();
                                if (!tempText.equals("")) {


                                    dataModel.getDatasets().set(dataset.getId(), tempText);
                                    dataset.setText(tempText);
                                    dataModel.updateDatasetsDB();
                                    updatechart();

                                }
                            });
                            builder1110.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                            builder1110.show();


                        });
                        row.addView(dataset);
                        row.addView(check);
                        TextView connectedcharttv = new TextView(GenerateChartActivity.this);
                        connectedcharttv.setText("Connected chart: ");
                        row.addView(connectedcharttv);
                        TextView connectedchartselecttv = new TextView(GenerateChartActivity.this);
                        int entriesnumber = dataModel.numberOfRows();
                        if (entriesnumber == 0) {
                            connectedchartselecttv.setText("None");
                        } else {

                            if (dataModel.getConnectedCharts().get(i) != -1) {
                                List<ArrayList<String>> dbentries = dataModel.getAllCharts();
                                for (int j = 0; j < dbentries.size(); j++) {
                                    int dbIndex = Integer.parseInt(dbentries.get(j).get(0));
                                    if (dataModel.getConnectedCharts().get(i) == dbIndex) {
                                        connectedchartselecttv.setText(dbentries.get(j).get(1));
                                    }
                                }
                            } else {
                                connectedchartselecttv.setText("None");
                            }
                        }
                        connectedchartselecttv.setTypeface(null, Typeface.ITALIC);
                        connectedchartselecttv.setClickable(true);
                        connectedchartselecttv.setId(i);
                        connectedchartselecttv.setOnClickListener(v13 -> {
                            List<String> tempList = new ArrayList<>();
                            List<Integer> tempListIds = new ArrayList<>();
                            int entriesnumber1 = dataModel.numberOfRows();
                            if (entriesnumber1 != 0) {


                                List<ArrayList<String>> dbentries = dataModel.getAllCharts();
                                for (int j = 0; j < dbentries.size(); j++) {
                                    int dbIndex = Integer.parseInt(dbentries.get(j).get(0));
                                    tempListIds.add(dbIndex);
                                    tempList.add(dbentries.get(j).get(1));
                                }
                            }
                            tempList.add("None");
                            String[] chartsArray = tempList.toArray(new String[0]);

                            AlertDialog.Builder builder11 = new AlertDialog.Builder(GenerateChartActivity.this);
                            builder11.setTitle("Pick a chart");
                            builder11.setItems(chartsArray, (dialog, which) -> {
                                connectedchartselecttv.setText(chartsArray[which]);
                                if (chartsArray[which].equals("None")) {
                                    dataModel.getConnectedCharts().set(connectedchartselecttv.getId(), -1);
                                } else {
                                    dataModel.getConnectedCharts().set(connectedchartselecttv.getId(), tempListIds.get(which));
                                }
                                dataModel.updateSettingsDB();
                                updatechart();
                            });
                            builder11.show();
                        });
                        row.addView(connectedchartselecttv);
                        lay1.addView(row);
                    }
                    builder1.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
                    ScrollView scroll = new ScrollView(GenerateChartActivity.this);
                    scroll.addView(lay1);
                    builder1.setView(scroll);
                    builder1.show();
                });
                datasetsText.setTypeface(null, Typeface.BOLD);
                lay.addView(datasetsTitle);
                lay.addView(datasetsText);
                TextView titleTitle = new TextView(GenerateChartActivity.this);
                titleTitle.setText("Title");
                titleTitle.setTypeface(null, Typeface.ITALIC);
                titleTitle.setGravity(Gravity.CENTER);
                titleTitle.setTextSize(15);
                lay.addView(titleTitle);
                chartTitleText = new TextView(GenerateChartActivity.this);
                chartTitleText.setText("Title:");
                lay.addView(chartTitleText);
                charttitle = new TextView(GenerateChartActivity.this);
                charttitle.setText(dataModel.getChartTitle());
                charttitle.setTypeface(null, Typeface.BOLD);
                charttitle.setClickable(true);
                charttitle.setOnClickListener(v -> {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder2.setTitle("Title");


                    final EditText input = new EditText(GenerateChartActivity.this);

                    builder2.setView(input);


                    builder2.setPositiveButton("OK", (dialog, which) -> {


                        String tempText = input.getText().toString();
                        if (!tempText.equals("")) {

                            dataModel.setChartTitle(tempText);

                            title.setText(tempText);
                            charttitle.setText(tempText);
                            dataModel.updateTypeDB();
                        }


                    });
                    builder2.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                    builder2.show();


                });
                lay.addView(charttitle);


                titleswitch = new Switch(GenerateChartActivity.this);
                titleswitch.setText("Display Title");
                titleswitch.setChecked(dataModel.isTitleBool());
                titleswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setTitleBool(true);
                        title.setVisibility(View.VISIBLE);
                    } else {
                        dataModel.setTitleBool(false);
                        title.setVisibility(View.GONE);
                    }
                    dataModel.updateSettingsDB();
                });
                lay.addView(titleswitch);
                titlecolorText = new TextView(GenerateChartActivity.this);
                titlecolorText.setText("Title Color:");
                titlecolor = new TextView(GenerateChartActivity.this);
                titlecolor.setBackgroundColor(dataModel.getChartTitleColor());

                titlecolor.setText("    ");
                LinearLayout titleRow = new LinearLayout(GenerateChartActivity.this);
                titleRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


                titlecolor.setOnClickListener(v -> {
                    int color = Color.parseColor("#e7dbd0");

                    ColorPickerDialogBuilder

                            .with(GenerateChartActivity.this)
                            .setTitle("Choose color")
                            .initialColor(color)
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
                            .setOnColorSelectedListener(selectedColor -> {
                                String colorcode = "onColorSelected: 0x" + Integer.toHexString(selectedColor);
                            })
                            .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                                titlecolor.setBackgroundColor(selectedColor);
                                dataModel.setChartTitleColor(selectedColor);
                                dataModel.updateSettingsDB();
                                updatechart();
                            })
                            .setNegativeButton("cancel", (dialog, which) -> {
                            })
                            .build()
                            .show();


                });
                titleRow.addView(titlecolorText);
                titleRow.addView(titlecolor);
                lay.addView(titleRow);
                TextView backgroundTitle = new TextView(GenerateChartActivity.this);
                backgroundTitle.setText("Background");
                backgroundTitle.setTypeface(null, Typeface.ITALIC);
                backgroundTitle.setGravity(Gravity.CENTER);
                backgroundTitle.setTextSize(15);
                lay.addView(backgroundTitle);
                bgcolorText = new TextView(GenerateChartActivity.this);
                bgcolorText.setText("Background Color:");

                LinearLayout bgRow = new LinearLayout(GenerateChartActivity.this);
                bgRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                bgcolor = new TextView(GenerateChartActivity.this);
                bgcolor.setClickable(true);
                bgcolor.setText("    ");
                bgcolor.setBackgroundColor(dataModel.getBackgroundColor());
                bgcolor.setOnClickListener(v -> {
                    int color = Color.parseColor("#e7dbd0");

                    ColorPickerDialogBuilder

                            .with(GenerateChartActivity.this)
                            .setTitle("Choose color")
                            .initialColor(color)
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
                            .setOnColorSelectedListener(selectedColor -> {
                                String colorcode = "onColorSelected: 0x" + Integer.toHexString(selectedColor);
                            })
                            .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                                bgcolor.setBackgroundColor(selectedColor);
                                dataModel.setBackgroundColor(selectedColor);
                                dataModel.updateSettingsDB();
                                updatechart();
                            })
                            .setNegativeButton("cancel", (dialog, which) -> {
                            })
                            .build()
                            .show();


                });
                bgRow.addView(bgcolorText);
                bgRow.addView(bgcolor);
                lay.addView(bgRow);
                TextView descriptionTitle = new TextView(GenerateChartActivity.this);
                descriptionTitle.setText("Description");
                descriptionTitle.setTypeface(null, Typeface.ITALIC);
                descriptionTitle.setGravity(Gravity.CENTER);
                descriptionTitle.setTextSize(15);
                lay.addView(descriptionTitle);
                descriptionText = new TextView(GenerateChartActivity.this);
                descriptionText.setText("Description:");
                lay.addView(descriptionText);
                description = new TextView(GenerateChartActivity.this);
                description.setText(dataModel.getDescriptionStr());
                description.setClickable(true);
                description.setTypeface(null, Typeface.BOLD);
                description.setOnClickListener(v -> {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder2.setTitle("Description");


                    final EditText input = new EditText(GenerateChartActivity.this);


                    builder2.setView(input);


                    builder2.setPositiveButton("OK", (dialog, which) -> {
                        String tempText = input.getText().toString();
                        if (!tempText.equals("")) {

                            dataModel.setDescriptionStr(tempText);
                            description.setText(tempText);
                            dataModel.updateSettingsDB();
                            updatechart();
                        }

                    });

                    builder2.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                    builder2.show();


                });
                lay.addView(description);
                descriptiontoggle = new Switch(GenerateChartActivity.this);
                descriptiontoggle.setText("Display Description");
                descriptiontoggle.setChecked(dataModel.isDescriptionBool());
                descriptiontoggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setDescriptionBool(true);
                    } else {
                        dataModel.setDescriptionBool(false);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });

                builder.setNegativeButton("Back", (dialog, which) -> dialog.cancel());
                lay.addView(descriptiontoggle);

                TextView typeTitle = new TextView(GenerateChartActivity.this);
                typeTitle.setText("Type and Format");
                typeTitle.setTypeface(null, Typeface.ITALIC);
                typeTitle.setGravity(Gravity.CENTER);
                typeTitle.setTextSize(15);
                lay.addView(typeTitle);
                ScrollView scroll = new ScrollView(GenerateChartActivity.this);

                decimalswitch = new Switch(GenerateChartActivity.this);
                decimalswitch.setText("Toggle Percentage");
                decimalswitch.setChecked(dataModel.isDecimalBool());
                decimalswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setDecimalBool(true);
                    } else {
                        dataModel.setDecimalBool(true);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });
                lay.addView(decimalswitch);
                chartTypeText = new TextView(GenerateChartActivity.this);
                chartTypeText.setText("Chart Type:");
                lay.addView(chartTypeText);
                charttype = new TextView(GenerateChartActivity.this);
                charttype.setText(dataModel.getChartType());
                charttype.setTypeface(null, Typeface.BOLD);
                charttype.setClickable(true);
                charttype.setOnClickListener(v -> {
                    String[] chartsArray = {"Line Chart", "Area Chart", "Spline Chart", "Bar Chart", "Horizontal Bar Chart", "Area Spline Chart", "Scatterplot Chart", "Bubble Chart", "Radar Chart", "Stacked Bar Chart", "Horizontal Stacked Bar Chart", "Pie Chart", "Candlestick Chart"};

                    AlertDialog.Builder builder2 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder2.setTitle("Pick a chart type");
                    builder2.setItems(chartsArray, (dialog, which) -> {
                        charttype.setText(chartsArray[which]);
                        dataModel.setChartType(chartsArray[which]);
                        dataModel.updateTypeDB();
                        updatechart();
                        settingsDialog.cancel();
                        openSettings();
                    });
                    builder2.show();
                });
                lay.addView(charttype);

                scroll.addView(lay);
                builder.setView(scroll);
                settingsDialog = builder.show();
                break;
            }
            case "Radar Chart": {
                final TextView title = (TextView) findViewById(R.id.chart_title);
                AlertDialog.Builder builder = new AlertDialog.Builder(GenerateChartActivity.this);
                builder.setTitle("Settings");
                TextView datasetsText, chartTypeText, chartTitleText, descriptionText, titleColorText, bgColorText;
                TextView charttype, charttitle, titlecolor, bgcolor, description;
                Switch titleswitch, descriptiontoggle;
                LinearLayout lay = new LinearLayout(GenerateChartActivity.this);
                lay.setOrientation(LinearLayout.VERTICAL);
                TextView datasetsTitle = new TextView(GenerateChartActivity.this);
                datasetsTitle.setText("Datasets");
                datasetsTitle.setTypeface(null, Typeface.ITALIC);
                datasetsTitle.setGravity(Gravity.CENTER);
                datasetsTitle.setTextSize(15);
                datasetsText = new TextView(GenerateChartActivity.this);
                datasetsText.setText("Datasets");
                datasetsText.setClickable(true);
                datasetsText.setOnClickListener(v -> {

                    AlertDialog.Builder builder12 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder12.setTitle("Datasets");
                    LinearLayout lay12 = new LinearLayout(GenerateChartActivity.this);
                    lay12.setOrientation(LinearLayout.VERTICAL);

                    for (int i = 0; i < dataModel.getDatasets().size(); i++) {
                        LinearLayout row = new LinearLayout(GenerateChartActivity.this);
                        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        TextView dataset = new TextView(GenerateChartActivity.this);
                        dataset.setId(i);
                        dataset.setText(dataModel.getDatasets().get(i));
                        dataset.setTextColor(dataModel.getArrayOfColors().get(i));
                        CheckBox check = new CheckBox(GenerateChartActivity.this);
                        check.setId(i);
                        check.setText("");

                        check.setChecked(dataModel.getEnabledDataSets().get(i) == 1);
                        check.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                    if (isChecked) {

                                        dataModel.getEnabledDataSets().set(check.getId(), 1);
                                        dataModel.updateEnabledDB();
                                        updatechart();
                                    } else {
                                        int selected = 0;
                                        for (int n : dataModel.getEnabledDataSets()) {
                                            if (n == 1) {
                                                selected++;
                                            }
                                        }
                                        if (selected > 1) {
                                            dataModel.getEnabledDataSets().set(check.getId(), 0);
                                            dataModel.updateEnabledDB();
                                            updatechart();
                                        } else {
                                            check.setChecked(true);
                                        }
                                    }
                                }
                        );
                        dataset.setClickable(true);
                        dataset.setOnLongClickListener(v112 -> {
                            int color = Color.parseColor("#e7dbd0");

                            ColorPickerDialogBuilder

                                    .with(GenerateChartActivity.this)
                                    .setTitle("Choose color")
                                    .initialColor(color)
                                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                    .density(12)
                                    .setOnColorSelectedListener(selectedColor -> {
                                        String colorcode = "onColorSelected: 0x" + Integer.toHexString(selectedColor);
                                    })
                                    .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {

                                        dataModel.getArrayOfColors().set(dataset.getId(), selectedColor);
                                        dataset.setTextColor(selectedColor);
                                        dataModel.updateColorsDB();
                                        updatechart();
                                    })
                                    .setNegativeButton("cancel", (dialog, which) -> {
                                    })
                                    .build()
                                    .show();
                            return false;
                        });
                        dataset.setOnClickListener(v111 -> {
                            AlertDialog.Builder builder1212 = new AlertDialog.Builder(GenerateChartActivity.this);
                            builder1212.setTitle("Name");


                            final EditText input = new EditText(GenerateChartActivity.this);


                            builder1212.setView(input);


                            builder1212.setPositiveButton("OK", (dialog, which) -> {
                                String tempText = input.getText().toString();
                                if (!tempText.equals("")) {


                                    dataModel.getDatasets().set(dataset.getId(), tempText);
                                    dataset.setText(tempText);
                                    dataModel.updateDatasetsDB();
                                    updatechart();

                                }
                            });
                            builder1212.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                            builder1212.show();


                        });
                        row.addView(dataset);
                        row.addView(check);
                        lay12.addView(row);
                    }
                    builder12.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
                    ScrollView scroll = new ScrollView(GenerateChartActivity.this);
                    scroll.addView(lay12);
                    builder12.setView(scroll);
                    builder12.show();
                });
                datasetsText.setTypeface(null, Typeface.BOLD);
                lay.addView(datasetsTitle);
                lay.addView(datasetsText);


                TextView titleTitle = new TextView(GenerateChartActivity.this);
                titleTitle.setText("Title");
                titleTitle.setTypeface(null, Typeface.ITALIC);
                titleTitle.setGravity(Gravity.CENTER);
                titleTitle.setTextSize(15);
                lay.addView(titleTitle);
                chartTitleText = new TextView(GenerateChartActivity.this);
                chartTitleText.setText("Title:");
                lay.addView(chartTitleText);
                charttitle = new TextView(GenerateChartActivity.this);
                charttitle.setText(dataModel.getChartTitle());
                charttitle.setTypeface(null, Typeface.BOLD);
                charttitle.setClickable(true);
                charttitle.setOnClickListener(v -> {
                    AlertDialog.Builder builder13 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder13.setTitle("Title");


                    final EditText input = new EditText(GenerateChartActivity.this);

                    builder13.setView(input);


                    builder13.setPositiveButton("OK", (dialog, which) -> {


                        String tempText = input.getText().toString();
                        if (!tempText.equals("")) {

                            dataModel.setChartTitle(tempText);
                            title.setText(tempText);
                            charttitle.setText(tempText);
                            dataModel.updateTypeDB();
                        }


                    });
                    builder13.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                    builder13.show();


                });
                lay.addView(charttitle);


                titleswitch = new Switch(GenerateChartActivity.this);
                titleswitch.setText("Display Title");
                titleswitch.setChecked(dataModel.isTitleBool());
                titleswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setTitleBool(true);
                        title.setVisibility(View.VISIBLE);
                    } else {
                        dataModel.setTitleBool(false);
                        title.setVisibility(View.GONE);
                    }
                    dataModel.updateSettingsDB();
                });
                lay.addView(titleswitch);
                titleColorText = new TextView(GenerateChartActivity.this);
                titleColorText.setText("Title Color:");
                titlecolor = new TextView(GenerateChartActivity.this);
                titlecolor.setBackgroundColor(dataModel.getChartTitleColor());

                titlecolor.setText("    ");
                LinearLayout titleRow = new LinearLayout(GenerateChartActivity.this);
                titleRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


                titlecolor.setOnClickListener(v -> {
                    int color = Color.parseColor("#e7dbd0");

                    ColorPickerDialogBuilder

                            .with(GenerateChartActivity.this)
                            .setTitle("Choose color")
                            .initialColor(color)
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
                            .setOnColorSelectedListener(selectedColor -> {
                                String colorcode = "onColorSelected: 0x" + Integer.toHexString(selectedColor);
                            })
                            .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                                titlecolor.setBackgroundColor(selectedColor);
                                dataModel.setChartTitleColor(selectedColor);
                                dataModel.updateSettingsDB();
                                updatechart();
                            })
                            .setNegativeButton("cancel", (dialog, which) -> {
                            })
                            .build()
                            .show();


                });
                titleRow.addView(titleColorText);
                titleRow.addView(titlecolor);
                lay.addView(titleRow);
                TextView backgroundTitle = new TextView(GenerateChartActivity.this);
                backgroundTitle.setText("Background");
                backgroundTitle.setTypeface(null, Typeface.ITALIC);
                backgroundTitle.setGravity(Gravity.CENTER);
                backgroundTitle.setTextSize(15);
                lay.addView(backgroundTitle);
                bgColorText = new TextView(GenerateChartActivity.this);
                bgColorText.setText("Background Color:");

                LinearLayout bgRow = new LinearLayout(GenerateChartActivity.this);
                bgRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                bgcolor = new TextView(GenerateChartActivity.this);
                bgcolor.setClickable(true);
                bgcolor.setText("    ");
                bgcolor.setBackgroundColor(dataModel.getBackgroundColor());
                bgcolor.setOnClickListener(v -> {
                    int color = Color.parseColor("#e7dbd0");

                    ColorPickerDialogBuilder

                            .with(GenerateChartActivity.this)
                            .setTitle("Choose color")
                            .initialColor(color)
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
                            .setOnColorSelectedListener(selectedColor -> {
                                String colorcode = "onColorSelected: 0x" + Integer.toHexString(selectedColor);
                            })
                            .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                                bgcolor.setBackgroundColor(selectedColor);
                                dataModel.setBackgroundColor(selectedColor);
                                dataModel.updateSettingsDB();
                                updatechart();
                            })
                            .setNegativeButton("cancel", (dialog, which) -> {
                            })
                            .build()
                            .show();


                });
                bgRow.addView(bgColorText);
                bgRow.addView(bgcolor);
                lay.addView(bgRow);
                TextView descriptionTitle = new TextView(GenerateChartActivity.this);
                descriptionTitle.setText("Description");
                descriptionTitle.setTypeface(null, Typeface.ITALIC);
                descriptionTitle.setGravity(Gravity.CENTER);
                descriptionTitle.setTextSize(15);
                lay.addView(descriptionTitle);
                descriptionText = new TextView(GenerateChartActivity.this);
                descriptionText.setText("Description:");
                lay.addView(descriptionText);
                description = new TextView(GenerateChartActivity.this);
                description.setText(dataModel.getDescriptionStr());
                description.setClickable(true);
                description.setTypeface(null, Typeface.BOLD);
                description.setOnClickListener(v -> {
                    AlertDialog.Builder builder14 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder14.setTitle("Description");


                    final EditText input = new EditText(GenerateChartActivity.this);


                    builder14.setView(input);


                    builder14.setPositiveButton("OK", (dialog, which) -> {
                        String tempText = input.getText().toString();
                        if (!tempText.equals("")) {

                            dataModel.setDescriptionStr(tempText);
                            description.setText(tempText);
                            dataModel.updateSettingsDB();
                            updatechart();
                        }

                    });

                    builder14.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                    builder14.show();


                });
                lay.addView(description);
                descriptiontoggle = new Switch(GenerateChartActivity.this);
                descriptiontoggle.setText("Display Description");
                descriptiontoggle.setChecked(dataModel.isDescriptionBool());
                descriptiontoggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setDescriptionBool(true);
                    } else {
                        dataModel.setDescriptionBool(false);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });

                builder.setNegativeButton("Back", (dialog, which) -> dialog.cancel());
                lay.addView(descriptiontoggle);

                TextView typeTitle = new TextView(GenerateChartActivity.this);
                typeTitle.setText("Type and Format");
                typeTitle.setTypeface(null, Typeface.ITALIC);
                typeTitle.setGravity(Gravity.CENTER);
                typeTitle.setTextSize(15);
                lay.addView(typeTitle);
                ScrollView scroll = new ScrollView(GenerateChartActivity.this);


                chartTypeText = new TextView(GenerateChartActivity.this);
                chartTypeText.setText("Chart Type:");
                lay.addView(chartTypeText);
                charttype = new TextView(GenerateChartActivity.this);
                charttype.setText(dataModel.getChartType());
                charttype.setTypeface(null, Typeface.BOLD);
                charttype.setClickable(true);
                charttype.setOnClickListener(v -> {
                    String[] chartsArray = {"Line Chart", "Area Chart", "Spline Chart", "Bar Chart", "Horizontal Bar Chart", "Area Spline Chart", "Scatterplot Chart", "Bubble Chart", "Radar Chart", "Stacked Bar Chart", "Horizontal Stacked Bar Chart", "Pie Chart", "Candlestick Chart"};

                    AlertDialog.Builder builder15 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder15.setTitle("Pick a chart type");
                    builder15.setItems(chartsArray, (dialog, which) -> {
                        charttype.setText(chartsArray[which]);
                        dataModel.setChartType(chartsArray[which]);
                        dataModel.updateTypeDB();
                        updatechart();
                        settingsDialog.cancel();
                        openSettings();
                    });
                    builder15.show();
                });
                lay.addView(charttype);

                scroll.addView(lay);
                builder.setView(scroll);
                settingsDialog = builder.show();
                break;
            }
            case "Candlestick Chart": {
                final TextView title = (TextView) findViewById(R.id.chart_title);
                final TextView xTitle = (TextView) findViewById(R.id.chart_X);
                final TextView yTitle = (TextView) findViewById(R.id.chart_Y);
                AlertDialog.Builder builder = new AlertDialog.Builder(GenerateChartActivity.this);
                builder.setTitle("Settings");
                TextView datasetsText, charttypeText, charttitleText, yaxislabelText, tittitlecolorTexte, xColorText, yColorText, xStartText, xStepText, bgColorText, xLabelTypeText, xMinValueText, xMaxValueText, descriptionText, xLabelText;
                TextView charttype, charttitle, Yaxislabel, titlecolor, xcolor, ycolor, xstart, xstep, bgcolor, Xlabeltype, Xminvalue, Xmaxvalue, description, Xlabelt;
                Switch titleswitch, yaxisswitch, toggleYaxis, toggleXaxis, minXtoggle, maxXtoggle, descriptiontoggle, xlabelswitch, displayswitch;
                Xminvalue = new TextView(GenerateChartActivity.this);
                Xmaxvalue = new TextView(GenerateChartActivity.this);
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM-YYYY");
                xStartText = new TextView(GenerateChartActivity.this);
                xstart = new TextView(GenerateChartActivity.this);
                LinearLayout lay = new LinearLayout(GenerateChartActivity.this);
                lay.setOrientation(LinearLayout.VERTICAL);
                TextView datasetsTitle = new TextView(GenerateChartActivity.this);
                datasetsTitle.setText("Datasets");
                datasetsTitle.setTypeface(null, Typeface.ITALIC);
                datasetsTitle.setGravity(Gravity.CENTER);
                datasetsTitle.setTextSize(15);
                lay.addView(datasetsTitle);
                datasetsText = new TextView(GenerateChartActivity.this);
                datasetsText.setText("Datasets");
                datasetsText.setClickable(true);
                datasetsText.setOnClickListener(v -> {

                    AlertDialog.Builder builder16 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder16.setTitle("Datasets");
                    LinearLayout lay13 = new LinearLayout(GenerateChartActivity.this);
                    lay13.setOrientation(LinearLayout.VERTICAL);
                    TextView highsettitle = new TextView(GenerateChartActivity.this);
                    TextView lowsettitle = new TextView(GenerateChartActivity.this);
                    TextView opensettitle = new TextView(GenerateChartActivity.this);
                    TextView closesettitle = new TextView(GenerateChartActivity.this);
                    highsettitle.setText("High set:");
                    lowsettitle.setText("Low set:");
                    opensettitle.setText("Open set:");
                    closesettitle.setText("Close set:");
                    TextView high = new TextView(GenerateChartActivity.this);
                    high.setText(dataModel.getDatasets().get(dataModel.getCandleStickVariables().get(0)));
                    high.setTextColor(dataModel.getArrayOfColors().get(dataModel.getCandleStickVariables().get(0)));
                    TextView low = new TextView(GenerateChartActivity.this);
                    low.setText(dataModel.getDatasets().get(dataModel.getCandleStickVariables().get(1)));
                    low.setTextColor(dataModel.getArrayOfColors().get(dataModel.getCandleStickVariables().get(1)));
                    TextView open = new TextView(GenerateChartActivity.this);
                    open.setText(dataModel.getDatasets().get(dataModel.getCandleStickVariables().get(2)));
                    open.setTextColor(dataModel.getArrayOfColors().get(dataModel.getCandleStickVariables().get(2)));
                    TextView close = new TextView(GenerateChartActivity.this);
                    close.setText(dataModel.getDatasets().get(dataModel.getCandleStickVariables().get(3)));
                    close.setTextColor(dataModel.getArrayOfColors().get(dataModel.getCandleStickVariables().get(3)));
                    high.setClickable(true);
                    high.setOnClickListener(v1 -> {
                        String[] datasets_array = dataModel.getDatasets().toArray(new String[0]);

                        AlertDialog.Builder builder161 = new AlertDialog.Builder(GenerateChartActivity.this);
                        builder161.setTitle("Pick a dataset");
                        builder161.setItems(datasets_array, (dialog, which) -> {
                            high.setText(datasets_array[which]);
                            high.setTextColor(dataModel.getArrayOfColors().get(which));
                            dataModel.getCandleStickVariables().set(0, which);
                            dataModel.updateSettingsDB();
                            updatechart();
                        });
                        builder161.show();
                    });
                    low.setClickable(true);
                    low.setOnClickListener(v110 -> {
                        String[] datasetsArray = dataModel.getDatasets().toArray(new String[0]);

                        AlertDialog.Builder builder1614 = new AlertDialog.Builder(GenerateChartActivity.this);
                        builder1614.setTitle("Pick a dataset");
                        builder1614.setItems(datasetsArray, (dialog, which) -> {
                            low.setText(datasetsArray[which]);
                            low.setTextColor(dataModel.getArrayOfColors().get(which));
                            dataModel.getCandleStickVariables().set(1, which);
                            dataModel.updateSettingsDB();
                            updatechart();
                        });
                        builder1614.show();
                    });
                    open.setClickable(true);
                    open.setOnClickListener(v12 -> {
                        String[] datasetsArray = dataModel.getDatasets().toArray(new String[0]);

                        AlertDialog.Builder builder1612 = new AlertDialog.Builder(GenerateChartActivity.this);
                        builder1612.setTitle("Pick a dataset");
                        builder1612.setItems(datasetsArray, (dialog, which) -> {
                            open.setText(datasetsArray[which]);
                            open.setTextColor(dataModel.getArrayOfColors().get(which));
                            dataModel.getCandleStickVariables().set(2, which);
                            dataModel.updateSettingsDB();
                            updatechart();
                        });
                        builder1612.show();
                    });
                    close.setClickable(true);
                    close.setOnClickListener(v19 -> {
                        String[] datasetsArray = dataModel.getDatasets().toArray(new String[0]);

                        AlertDialog.Builder builder1613 = new AlertDialog.Builder(GenerateChartActivity.this);
                        builder1613.setTitle("Pick a dataset");
                        builder1613.setItems(datasetsArray, (dialog, which) -> {
                            close.setText(datasetsArray[which]);
                            close.setTextColor(dataModel.getArrayOfColors().get(which));
                            dataModel.getCandleStickVariables().set(3, which);
                            dataModel.updateSettingsDB();
                            updatechart();
                        });
                        builder1613.show();
                    });
                    lay13.addView(highsettitle);
                    lay13.addView(high);
                    lay13.addView(lowsettitle);
                    lay13.addView(low);
                    lay13.addView(opensettitle);
                    lay13.addView(open);
                    lay13.addView(closesettitle);
                    lay13.addView(close);
                    builder16.setView(lay13);
                    builder16.show();
                });
                datasetsText.setTypeface(null, Typeface.BOLD);
                lay.addView(datasetsText);
                TextView titleTitle = new TextView(GenerateChartActivity.this);
                titleTitle.setText("Title");
                titleTitle.setTypeface(null, Typeface.ITALIC);
                titleTitle.setGravity(Gravity.CENTER);
                titleTitle.setTextSize(15);
                lay.addView(titleTitle);
                charttitleText = new TextView(GenerateChartActivity.this);
                charttitleText.setText("Title:");
                lay.addView(charttitleText);
                charttitle = new TextView(GenerateChartActivity.this);
                charttitle.setText(dataModel.getChartTitle());
                charttitle.setTypeface(null, Typeface.BOLD);
                charttitle.setClickable(true);
                charttitle.setOnClickListener(v -> {
                    AlertDialog.Builder builder17 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder17.setTitle("Title");


                    final EditText input = new EditText(GenerateChartActivity.this);

                    builder17.setView(input);


                    builder17.setPositiveButton("OK", (dialog, which) -> {


                        String tempText = input.getText().toString();
                        if (!tempText.equals("")) {

                            dataModel.setChartTitle(tempText);
                            title.setText(tempText);
                            charttitle.setText(tempText);
                            dataModel.updateTitleDB();
                        }


                    });
                    builder17.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                    builder17.show();


                });
                lay.addView(charttitle);


                titleswitch = new Switch(GenerateChartActivity.this);
                titleswitch.setText("Display Title");
                titleswitch.setChecked(dataModel.isTitleBool());
                titleswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setTitleBool(true);
                        title.setVisibility(View.VISIBLE);
                    } else {
                        dataModel.setTitleBool(false);
                        title.setVisibility(View.GONE);
                    }
                    dataModel.updateSettingsDB();
                });
                lay.addView(titleswitch);
                tittitlecolorTexte = new TextView(GenerateChartActivity.this);
                tittitlecolorTexte.setText("Title Color:");
                titlecolor = new TextView(GenerateChartActivity.this);
                titlecolor.setBackgroundColor(dataModel.getChartTitleColor());

                titlecolor.setText("    ");
                LinearLayout titleRow = new LinearLayout(GenerateChartActivity.this);
                titleRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


                titlecolor.setOnClickListener(v -> {
                    int color = Color.parseColor("#e7dbd0");

                    ColorPickerDialogBuilder

                            .with(GenerateChartActivity.this)
                            .setTitle("Choose color")
                            .initialColor(color)
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
                            .setOnColorSelectedListener(selectedColor -> {
                                String colorcode = "onColorSelected: 0x" + Integer.toHexString(selectedColor);
                            })
                            .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                                titlecolor.setBackgroundColor(selectedColor);
                                dataModel.setChartTitleColor(selectedColor);
                                dataModel.updateSettingsDB();
                                updatechart();
                            })
                            .setNegativeButton("cancel", (dialog, which) -> {
                            })
                            .build()
                            .show();


                });
                titleRow.addView(tittitlecolorTexte);
                titleRow.addView(titlecolor);
                lay.addView(titleRow);
                TextView backgroundTitle = new TextView(GenerateChartActivity.this);
                backgroundTitle.setText("Background");
                backgroundTitle.setTypeface(null, Typeface.ITALIC);
                backgroundTitle.setGravity(Gravity.CENTER);
                backgroundTitle.setTextSize(15);
                lay.addView(backgroundTitle);
                bgColorText = new TextView(GenerateChartActivity.this);
                bgColorText.setText("Background Color:");

                LinearLayout bgRow = new LinearLayout(GenerateChartActivity.this);
                bgRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                bgcolor = new TextView(GenerateChartActivity.this);
                bgcolor.setClickable(true);
                bgcolor.setText("    ");
                bgcolor.setBackgroundColor(dataModel.getBackgroundColor());
                bgcolor.setOnClickListener(v -> {
                    int color = Color.parseColor("#e7dbd0");

                    ColorPickerDialogBuilder

                            .with(GenerateChartActivity.this)
                            .setTitle("Choose color")
                            .initialColor(color)
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
                            .setOnColorSelectedListener(selectedColor -> {
                                String colorcode = "onColorSelected: 0x" + Integer.toHexString(selectedColor);
                            })
                            .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                                bgcolor.setBackgroundColor(selectedColor);
                                dataModel.setBackgroundColor(selectedColor);
                                dataModel.updateSettingsDB();
                                updatechart();
                            })
                            .setNegativeButton("cancel", (dialog, which) -> {
                            })
                            .build()
                            .show();


                });
                bgRow.addView(bgColorText);
                bgRow.addView(bgcolor);
                lay.addView(bgRow);
                TextView descriptionTitle = new TextView(GenerateChartActivity.this);
                descriptionTitle.setText("Description");
                descriptionTitle.setTypeface(null, Typeface.ITALIC);
                descriptionTitle.setGravity(Gravity.CENTER);
                descriptionTitle.setTextSize(15);
                lay.addView(descriptionTitle);
                descriptionText = new TextView(GenerateChartActivity.this);
                descriptionText.setText("Description:");
                lay.addView(descriptionText);
                description = new TextView(GenerateChartActivity.this);
                description.setText(dataModel.getDescriptionStr());
                description.setClickable(true);
                description.setTypeface(null, Typeface.BOLD);
                description.setOnClickListener(v -> {
                    AlertDialog.Builder builder18 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder18.setTitle("Description");


                    final EditText input = new EditText(GenerateChartActivity.this);


                    builder18.setView(input);


                    builder18.setPositiveButton("OK", (dialog, which) -> {
                        String tempText = input.getText().toString();
                        if (!tempText.equals("")) {

                            dataModel.setDescriptionStr(tempText);
                            description.setText(tempText);
                            dataModel.updateSettingsDB();
                            updatechart();
                        }

                    });

                    builder18.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                    builder18.show();


                });
                lay.addView(description);
                descriptiontoggle = new Switch(GenerateChartActivity.this);
                descriptiontoggle.setText("Display Description");
                descriptiontoggle.setChecked(dataModel.isDescriptionBool());
                descriptiontoggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setDescriptionBool(true);
                    } else {
                        dataModel.setDescriptionBool(false);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });

                builder.setNegativeButton("Back", (dialog, which) -> dialog.cancel());
                lay.addView(descriptiontoggle);
                TextView xAxisTitle = new TextView(GenerateChartActivity.this);
                xAxisTitle.setText("X Axis");
                xAxisTitle.setTypeface(null, Typeface.ITALIC);
                xAxisTitle.setGravity(Gravity.CENTER);
                xAxisTitle.setTextSize(15);
                lay.addView(xAxisTitle);
                xLabelTypeText = new TextView(GenerateChartActivity.this);
                xLabelTypeText.setText("X Label Type:");
                lay.addView(xLabelTypeText);
                Xlabeltype = new TextView(GenerateChartActivity.this);
                Xlabeltype.setText(dataModel.getxLabelTypeStr());
                Xlabeltype.setTypeface(null, Typeface.BOLD);
                Xlabeltype.setClickable(true);
                Xlabeltype.setOnClickListener(v -> {
                    String[] typesArray = {"DATE", "NUMERIC"};

                    AlertDialog.Builder builder19 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder19.setTitle("Pick a label type");
                    builder19.setItems(typesArray, (dialog, which) -> {
                        Xlabeltype.setText(typesArray[which]);
                        dataModel.setxLabelTypeStr(typesArray[which]);
                        dataModel.updateSettingsDB();
                        updatechart();
                        if (typesArray[which].equals("NUMERIC")) {
                            Xminvalue.setText(String.valueOf(dataModel.getMinXValue()));
                            Xmaxvalue.setText(String.valueOf(dataModel.getMaxXValue()));
                            xstart.setText(String.valueOf(dataModel.getxStartNum()));

                        } else {
                            String date_string = formatter.format(dataModel.getMinimumDate().getTime());
                            Xminvalue.setText(date_string);
                            date_string = formatter.format(dataModel.getMaximumDate().getTime());
                            Xmaxvalue.setText(date_string);
                            date_string = formatter.format(dataModel.getInitialDate().getTime());
                            xstart.setText(date_string);


                        }
                    });
                    builder19.show();
                });
                lay.addView(Xlabeltype);
                xLabelText = new TextView(GenerateChartActivity.this);
                xLabelText.setText("X Label:");
                lay.addView(xLabelText);
                Xlabelt = new TextView(GenerateChartActivity.this);
                Xlabelt.setText(dataModel.getxLabelString());
                Xlabelt.setTypeface(null, Typeface.BOLD);
                Xlabelt.setClickable(true);
                Xlabelt.setOnClickListener(v -> {
                    AlertDialog.Builder builder110 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder110.setTitle("Name");


                    final EditText input = new EditText(GenerateChartActivity.this);


                    builder110.setView(input);


                    builder110.setPositiveButton("OK", (dialog, which) -> {
                        String tempText = input.getText().toString();
                        if (!tempText.equals("")) {
                            if (dataModel.getChartType().equals("Horizontal Bar Chart") || dataModel.getChartType().equals("Horizontal Stacked Bar Chart")) {
                                dataModel.setxLabelString(tempText);
                                Xlabelt.setText(tempText);
                                yTitle.setText(tempText);
                                dataModel.updateSettingsDB();
                            } else {
                                dataModel.setxLabelString(tempText);
                                Xlabelt.setText(tempText);
                                xTitle.setText(tempText);
                                dataModel.updateSettingsDB();
                            }
                        }

                    });
                    builder110.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                    builder110.show();


                });
                lay.addView(Xlabelt);

                xStartText.setText("X Starting value:");
                lay.addView(xStartText);
                xstart.setTypeface(null, Typeface.BOLD);
                xstart.setClickable(true);
                xstart.setOnClickListener(v -> {
                    if (dataModel.getxLabelTypeStr().equals("DATE")) {
                        final Calendar newCalendar = Calendar.getInstance();
                        final DatePickerDialog StartTime = new DatePickerDialog(GenerateChartActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, monthOfYear, dayOfMonth);
                            dataModel.setInitialDate(newDate.getTime());
                            SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMM-YYYY");
                            String date_string = formatter1.format(newDate.getTime());
                            xstart.setText(date_string);
                            dataModel.updateSettingsDB();
                            updatechart();
                        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                        StartTime.show();
                    } else {
                        AlertDialog.Builder builder111 = new AlertDialog.Builder(GenerateChartActivity.this);
                        builder111.setTitle("Value");


                        final EditText input = new EditText(GenerateChartActivity.this);

                        input.setInputType(InputType.TYPE_CLASS_PHONE);
                        input.addTextChangedListener(new TextWatcher() {

                            public void afterTextChanged(Editable s) {

                                String result = s.toString();
                                if (!result.matches(numberPattern) && !result.matches("") && !result.matches("-")) {

                                    input.setText("");

                                }
                            }

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                        });

                        builder111.setView(input);


                        builder111.setPositiveButton("OK", (dialog, which) -> {
                            String tempString = input.getText().toString();
                            if (!tempString.equals("") && !tempString.equals("-")) {

                                dataModel.setxStartNum(Float.parseFloat(tempString));

                                xstart.setText(tempString);
                                dataModel.updateSettingsDB();
                                updatechart();
                            }
                        });
                        builder111.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                        builder111.show();

                    }
                });
                if (dataModel.getxLabelTypeStr().equals("NUMERIC")) {
                    xstart.setText(String.valueOf(dataModel.getxStartNum()));
                } else {
                    String date_string = formatter.format(dataModel.getInitialDate().getTime());
                    xstart.setText(date_string);
                }
                lay.addView(xstart);
                xStepText = new TextView(GenerateChartActivity.this);
                xStepText.setText("X Step:");
                lay.addView(xStepText);
                xstep = new TextView(GenerateChartActivity.this);

                xstep.setText(dataModel.getIncrementX());
                xstep.setTypeface(null, Typeface.BOLD);
                xstep.setClickable(true);
                xstep.setOnClickListener(v -> {
                    AlertDialog.Builder builder112 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder112.setTitle("Value");


                    final EditText input = new EditText(GenerateChartActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_PHONE);
                    input.addTextChangedListener(new TextWatcher() {

                        public void afterTextChanged(Editable s) {

                            String result = s.toString();
                            if (!result.matches(numberPattern) && !result.matches("") && !result.matches("-")) {

                                input.setText("");

                            }
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }
                    });


                    builder112.setView(input);


                    builder112.setPositiveButton("OK", (dialog, which) -> {
                        String tempText = input.getText().toString();
                        if (!tempText.equals("") && !tempText.equals("-")) {

                            dataModel.setIncrementX(tempText);
                            xstep.setText(tempText);
                            dataModel.updateSettingsDB();
                            updatechart();
                        }
                    });
                    builder112.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                    builder112.show();


                });
                lay.addView(xstep);
                xColorText = new TextView(GenerateChartActivity.this);
                xColorText.setText("X Axis Color:");
                xcolor = new TextView(GenerateChartActivity.this);
                xcolor.setText("    ");
                xcolor.setBackgroundColor(dataModel.getxColor());
                LinearLayout xRow = new LinearLayout(GenerateChartActivity.this);
                xRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                xcolor.setClickable(true);
                xcolor.setClickable(true);
                xcolor.setBackgroundColor(dataModel.getxColor());
                xcolor.setOnClickListener(v -> {
                    int color = Color.parseColor("#e7dbd0");

                    ColorPickerDialogBuilder

                            .with(GenerateChartActivity.this)
                            .setTitle("Choose color")
                            .initialColor(color)
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
                            .setOnColorSelectedListener(selectedColor -> {
                                String colorcode = "onColorSelected: 0x" + Integer.toHexString(selectedColor);
                            })
                            .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {

                                dataModel.setxColor(selectedColor);
                                xcolor.setBackgroundColor(selectedColor);
                                dataModel.updateSettingsDB();
                                updatechart();
                            })
                            .setNegativeButton("cancel", (dialog, which) -> {
                            })
                            .build()
                            .show();


                });
                xRow.addView(xColorText);
                xRow.addView(xcolor);
                lay.addView(xRow);
                xlabelswitch = new Switch(GenerateChartActivity.this);
                xlabelswitch.setChecked(dataModel.isxLabelBool());
                xlabelswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setxLabelBool(true);
                        xTitle.setVisibility(View.VISIBLE);

                    } else {
                        dataModel.setxLabelBool(false);
                        xTitle.setVisibility(View.GONE);
                    }
                    dataModel.updateSettingsDB();
                });
                xlabelswitch.setText("Display X Label");
                lay.addView(xlabelswitch);
                toggleXaxis = new Switch(GenerateChartActivity.this);
                toggleXaxis.setChecked(dataModel.isxAxisBool());
                toggleXaxis.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setxAxisBool(true);
                    } else {
                        dataModel.setxAxisBool(false);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });
                toggleXaxis.setText("Display X Axis");
                lay.addView(toggleXaxis);
                minXtoggle = new Switch(GenerateChartActivity.this);
                minXtoggle.setText("Minimum X");
                minXtoggle.setChecked(dataModel.isxMinBool());
                minXtoggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setxMinBool(true);
                    } else {
                        dataModel.setxMinBool(false);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });
                lay.addView(minXtoggle);
                xMinValueText = new TextView(GenerateChartActivity.this);
                xMinValueText.setText("Minimum X:");
                lay.addView(xMinValueText);

                Xminvalue.setClickable(true);

                if (dataModel.getxLabelTypeStr().equals("DATE"))
                    Xminvalue.setText(formatter.format(dataModel.getMinimumDate().getTime()));
                else
                    Xminvalue.setText(String.valueOf(dataModel.getMinXValue()));

                Xminvalue.setOnClickListener(v -> {
                    if (dataModel.getxLabelTypeStr().equals("DATE")) {
                        final Calendar newCalendar = Calendar.getInstance();
                        final DatePickerDialog StartTime = new DatePickerDialog(GenerateChartActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, monthOfYear, dayOfMonth);
                            dataModel.setMinimumDate(newDate.getTime());
                            SimpleDateFormat formatter12 = new SimpleDateFormat("dd MMM-YYYY");
                            String dateString = formatter12.format(newDate.getTime());
                            Xminvalue.setText(dateString);
                            dataModel.updateSettingsDB();
                            updatechart();
                        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                        StartTime.show();
                    } else {
                        AlertDialog.Builder builder116 = new AlertDialog.Builder(GenerateChartActivity.this);
                        builder116.setTitle("Value");


                        final EditText input = new EditText(GenerateChartActivity.this);

                        input.setInputType(InputType.TYPE_CLASS_PHONE);
                        input.addTextChangedListener(new TextWatcher() {

                            public void afterTextChanged(Editable s) {

                                String result = s.toString();
                                if (!result.matches(numberPattern) && !result.matches("") && !result.matches("-")) {

                                    input.setText("");

                                }
                            }

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                        });

                        builder116.setView(input);


                        builder116.setPositiveButton("OK", (dialog, which) -> {
                            String tempText = input.getText().toString();
                            if (!tempText.equals("") && !tempText.equals("-")) {

                                dataModel.setMinXValue(Float.parseFloat(tempText));
                                Xminvalue.setText(String.valueOf(dataModel.getMinXValue()));
                                dataModel.updateSettingsDB();
                                updatechart();
                            }
                        });
                        builder116.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                        builder116.show();


                    }
                });
                Xminvalue.setTypeface(null, Typeface.BOLD);
                lay.addView(Xminvalue);
                maxXtoggle = new Switch(GenerateChartActivity.this);
                maxXtoggle.setText("Maximum X");
                maxXtoggle.setChecked(dataModel.isxMaxBool());
                maxXtoggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setxMaxBool(true);
                    } else {
                        dataModel.setxMaxBool(false);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });
                lay.addView(maxXtoggle);
                xMaxValueText = new TextView(GenerateChartActivity.this);
                xMaxValueText.setText("Maximum X:");
                lay.addView(xMaxValueText);
                if (dataModel.getxLabelTypeStr().equals("DATE"))
                    Xmaxvalue.setText(formatter.format(dataModel.getMaximumDate().getTime()));
                else
                    Xmaxvalue.setText(String.valueOf(dataModel.getMaxXValue()));
                Xmaxvalue.setTypeface(null, Typeface.BOLD);
                Xmaxvalue.setClickable(true);
                Xmaxvalue.setOnClickListener(v -> {
                    if (dataModel.getxLabelTypeStr().equals("DATE")) {
                        final Calendar newCalendar = Calendar.getInstance();
                        final DatePickerDialog StartTime = new DatePickerDialog(GenerateChartActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, monthOfYear, dayOfMonth);
                            dataModel.setMaximumDate(newDate.getTime());
                            SimpleDateFormat formatter16 = new SimpleDateFormat("dd MMM-YYYY");
                            String dateString = formatter16.format(newDate.getTime());
                            Xmaxvalue.setText(dateString);
                            dataModel.updateSettingsDB();
                            updatechart();
                        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                        StartTime.show();
                    } else {
                        AlertDialog.Builder builder134 = new AlertDialog.Builder(GenerateChartActivity.this);
                        builder134.setTitle("Value");


                        final EditText input = new EditText(GenerateChartActivity.this);

                        input.setInputType(InputType.TYPE_CLASS_PHONE);
                        input.addTextChangedListener(new TextWatcher() {

                            public void afterTextChanged(Editable s) {

                                String result = s.toString();
                                if (!result.matches(numberPattern) && !result.matches("") && !result.matches("-")) {

                                    input.setText("");

                                }
                            }

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                        });

                        builder134.setView(input);


                        builder134.setPositiveButton("OK", (dialog, which) -> {
                            String tempText = input.getText().toString();
                            if (!tempText.equals("") && !tempText.equals("-")) {

                                dataModel.setMaxXValue(Float.parseFloat(tempText));
                                Xmaxvalue.setText(tempText);
                                dataModel.updateSettingsDB();
                                updatechart();
                            }
                        });
                        builder134.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                        builder134.show();

                    }
                });
                lay.addView(Xmaxvalue);
                TextView yAxisTitle = new TextView(GenerateChartActivity.this);
                yAxisTitle.setText("Y Axis");
                yAxisTitle.setTypeface(null, Typeface.ITALIC);
                yAxisTitle.setGravity(Gravity.CENTER);
                yAxisTitle.setTextSize(15);
                lay.addView(yAxisTitle);
                yaxislabelText = new TextView(GenerateChartActivity.this);
                yaxislabelText.setText("Y Axis Label:");
                lay.addView(yaxislabelText);
                Yaxislabel = new TextView(GenerateChartActivity.this);
                Yaxislabel.setText(dataModel.getyLabelStr());
                Yaxislabel.setTypeface(null, Typeface.BOLD);
                Yaxislabel.setClickable(true);
                Yaxislabel.setOnClickListener(v -> {
                    AlertDialog.Builder builder133 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder133.setTitle("Name");


                    final EditText input = new EditText(GenerateChartActivity.this);


                    builder133.setView(input);


                    builder133.setPositiveButton("OK", (dialog, which) -> {
                        String tempText = input.getText().toString();
                        if (!tempText.equals("")) {
                            if (dataModel.getChartType().equals("Horizontal Bar Chart") || dataModel.getChartType().equals("Horizontal Stacked Bar Chart")) {
                                dataModel.setyLabelStr(tempText);
                                Yaxislabel.setText(tempText);
                                xTitle.setText(tempText);
                                dataModel.updateSettingsDB();
                            } else {
                                dataModel.setyLabelStr(tempText);
                                Yaxislabel.setText(tempText);
                                yTitle.setText(tempText);
                                dataModel.updateSettingsDB();
                            }

                        }

                    });

                    builder133.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                    builder133.show();


                });
                lay.addView(Yaxislabel);
                yColorText = new TextView(GenerateChartActivity.this);
                yColorText.setText("Y Axis Color:");
                ycolor = new TextView(GenerateChartActivity.this);
                ycolor.setText("    ");
                ycolor.setBackgroundColor(dataModel.getyColor());
                ycolor.setClickable(true);
                ycolor.setOnClickListener(v -> {
                    int color = Color.parseColor("#e7dbd0");

                    ColorPickerDialogBuilder

                            .with(GenerateChartActivity.this)
                            .setTitle("Choose color")
                            .initialColor(color)
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
                            .setOnColorSelectedListener(selectedColor -> {
                                String colorcode = "onColorSelected: 0x" + Integer.toHexString(selectedColor);
                            })
                            .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {

                                dataModel.setyColor(selectedColor);
                                ycolor.setBackgroundColor(selectedColor);
                                dataModel.updateSettingsDB();
                                updatechart();
                            })
                            .setNegativeButton("cancel", (dialog, which) -> {
                            })
                            .build()
                            .show();


                });
                LinearLayout yRow = new LinearLayout(GenerateChartActivity.this);
                yRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                yRow.addView(yColorText);
                yRow.addView(ycolor);
                lay.addView(yRow);
                yaxisswitch = new Switch(GenerateChartActivity.this);
                yaxisswitch.setText("Display Y Label");
                yaxisswitch.setChecked(dataModel.isyLabelBool());

                yaxisswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setyLabelBool(true);
                        yTitle.setVisibility(View.VISIBLE);
                    } else {
                        dataModel.setyLabelBool(false);
                        yTitle.setVisibility(View.GONE);
                    }
                    dataModel.updateSettingsDB();
                });
                lay.addView(yaxisswitch);
                toggleYaxis = new Switch(GenerateChartActivity.this);
                toggleYaxis.setText("Display Y Axis");
                toggleYaxis.setChecked(dataModel.isYaxisBool());
                toggleYaxis.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setYaxisBool(true);

                    } else {
                        dataModel.setYaxisBool(false);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });
                lay.addView(toggleYaxis);
                TextView typeTitle = new TextView(GenerateChartActivity.this);
                typeTitle.setText("Type and Format");
                typeTitle.setTypeface(null, Typeface.ITALIC);
                typeTitle.setGravity(Gravity.CENTER);
                typeTitle.setTextSize(15);
                lay.addView(typeTitle);
                ScrollView scroll = new ScrollView(GenerateChartActivity.this);

                charttypeText = new TextView(GenerateChartActivity.this);
                charttypeText.setText("Chart Type:");
                lay.addView(charttypeText);
                charttype = new TextView(GenerateChartActivity.this);
                charttype.setText(dataModel.getChartType());
                charttype.setTypeface(null, Typeface.BOLD);
                charttype.setClickable(true);
                charttype.setOnClickListener(v -> {
                    String[] chartsArray = {"Line Chart", "Area Chart", "Spline Chart", "Bar Chart", "Horizontal Bar Chart", "Area Spline Chart", "Scatterplot Chart", "Bubble Chart", "Radar Chart", "Stacked Bar Chart", "Horizontal Stacked Bar Chart", "Pie Chart", "Candlestick Chart"};

                    AlertDialog.Builder builder132 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder132.setTitle("Pick a chart type");
                    builder132.setItems(chartsArray, (dialog, which) -> {
                        charttype.setText(chartsArray[which]);
                        dataModel.setChartType(chartsArray[which]);
                        dataModel.updateTypeDB();
                        updatechart();
                        settingsDialog.cancel();
                        openSettings();

                    });
                    builder132.show();
                });
                lay.addView(charttype);
                displayswitch = new Switch(GenerateChartActivity.this);
                displayswitch.setText("Display Entire Data");
                displayswitch.setChecked(dataModel.isDisplayBool());
                displayswitch.setClickable(true);
                displayswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setDisplayBool(true);
                    } else {
                        dataModel.setDisplayBool(false);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });
                lay.addView(displayswitch);
                scroll.addView(lay);
                builder.setView(scroll);
                settingsDialog = builder.show();
                break;
            }
            default: {
                final TextView title = (TextView) findViewById(R.id.chart_title);
                final TextView xTitle = (TextView) findViewById(R.id.chart_X);
                final TextView yTitle = (TextView) findViewById(R.id.chart_Y);
                AlertDialog.Builder builder = new AlertDialog.Builder(GenerateChartActivity.this);
                builder.setTitle("Settings");
                TextView datasetsText, charttypeText, charttitleText, yaxislabelText, yminvalueText, ymaxvalueText, titlecolorText, xcolorText, ycolorText, xstartText, xstepText, bgcolorText, xlabeltypeText, xminvalueText, xmaxvalueText, descriptionText, xlabelText;
                TextView charttype, charttitle, Yaxislabel, Yminvalue, Ymaxvalue, titlecolor, xcolor, ycolor, xstart, xstep, bgcolor, Xlabeltype, Xminvalue, Xmaxvalue, description, Xlabelt;
                Switch titleswitch, yaxisswitch, decimalswitch, Yminswitch, Ymaxswitch, toggleYaxis, toggleXaxis, minXtoggle, maxXtoggle, descriptiontoggle, markerstoggle, xlabelswitch, displayswitch;
                Xminvalue = new TextView(GenerateChartActivity.this);
                Xmaxvalue = new TextView(GenerateChartActivity.this);
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM-YYYY");
                xstartText = new TextView(GenerateChartActivity.this);
                xstart = new TextView(GenerateChartActivity.this);


                LinearLayout lay = new LinearLayout(GenerateChartActivity.this);
                lay.setOrientation(LinearLayout.VERTICAL);
                TextView datasetsTitle = new TextView(GenerateChartActivity.this);
                datasetsTitle.setText("Datasets");
                datasetsTitle.setTypeface(null, Typeface.ITALIC);
                datasetsTitle.setGravity(Gravity.CENTER);
                datasetsTitle.setTextSize(15);
                lay.addView(datasetsTitle);
                datasetsText = new TextView(GenerateChartActivity.this);
                datasetsText.setText("Datasets");
                datasetsText.setClickable(true);
                if (dataModel.getChartType().equals("Scatterplot Chart") || dataModel.getChartType().equals("Bubble Chart")) {
                    datasetsText.setOnClickListener(v -> {

                        AlertDialog.Builder builder131 = new AlertDialog.Builder(GenerateChartActivity.this);
                        builder131.setTitle("Datasets");
                        LinearLayout lay15 = new LinearLayout(GenerateChartActivity.this);
                        lay15.setOrientation(LinearLayout.VERTICAL);
                        TextView dependenttitle = new TextView(GenerateChartActivity.this);
                        TextView independenttitle = new TextView(GenerateChartActivity.this);
                        dependenttitle.setText("Dependent set:");
                        independenttitle.setText("Independent set:");
                        TextView independent = new TextView(GenerateChartActivity.this);
                        independent.setText(dataModel.getDatasets().get(dataModel.getScatterVariables().get(0)));
                        independent.setTextColor(dataModel.getArrayOfColors().get(dataModel.getScatterVariables().get(0)));
                        TextView dependent = new TextView(GenerateChartActivity.this);
                        dependent.setText(dataModel.getDatasets().get(dataModel.getScatterVariables().get(1)));
                        dependent.setTextColor(dataModel.getArrayOfColors().get(dataModel.getScatterVariables().get(1)));
                        independent.setClickable(true);
                        independent.setOnClickListener(v18 -> {
                            String[] datasetsArray = dataModel.getDatasets().toArray(new String[0]);

                            AlertDialog.Builder builder130 = new AlertDialog.Builder(GenerateChartActivity.this);
                            builder130.setTitle("Pick a dataset");
                            builder130.setItems(datasetsArray, (dialog, which) -> {
                                independent.setText(datasetsArray[which]);
                                independent.setTextColor(dataModel.getArrayOfColors().get(which));
                                dataModel.getScatterVariables().set(0, which);
                                dataModel.updateScatterPlotDB();
                                updatechart();
                            });
                            builder130.show();
                        });
                        dependent.setClickable(true);
                        dependent.setOnClickListener(v17 -> {
                            String[] datasetsArray = dataModel.getDatasets().toArray(new String[0]);

                            AlertDialog.Builder builder129 = new AlertDialog.Builder(GenerateChartActivity.this);
                            builder129.setTitle("Pick a dataset");
                            builder129.setItems(datasetsArray, (dialog, which) -> {
                                dependent.setText(datasetsArray[which]);
                                dependent.setTextColor(dataModel.getArrayOfColors().get(which));
                                dataModel.getScatterVariables().set(1, which);
                                dataModel.updateScatterPlotDB();
                                updatechart();
                            });
                            builder129.show();
                        });
                        lay15.addView(independenttitle);
                        lay15.addView(independent);
                        lay15.addView(dependenttitle);
                        lay15.addView(dependent);
                        if (dataModel.getChartType().equals("Bubble Chart")) {
                            TextView size = new TextView(GenerateChartActivity.this);
                            if (dataModel.getScatterVariables().get(2) == -1) {
                                size.setText("None");
                            } else {
                                size.setText(dataModel.getDatasets().get(dataModel.getScatterVariables().get(2)));
                                independent.setTextColor(dataModel.getArrayOfColors().get(dataModel.getScatterVariables().get(2)));
                            }
                            size.setClickable(true);
                            size.setOnClickListener(v16 -> {
                                List<String> tempList = new ArrayList<>();
                                tempList.addAll(dataModel.getDatasets());
                                tempList.add("None");
                                String[] datasetsArray = tempList.toArray(new String[0]);

                                AlertDialog.Builder builder128 = new AlertDialog.Builder(GenerateChartActivity.this);
                                builder128.setTitle("Pick a dataset");
                                builder128.setItems(datasetsArray, (dialog, which) -> {
                                    size.setText(datasetsArray[which]);
                                    if (datasetsArray[which].equals("None")) {
                                        dataModel.getScatterVariables().set(2, -1);
                                        size.setTextColor(Color.BLACK);
                                    } else {
                                        dataModel.getScatterVariables().set(2, which);
                                        size.setTextColor(dataModel.getArrayOfColors().get(which));
                                    }
                                    dataModel.updateScatterPlotDB();
                                    updatechart();
                                });
                                builder128.show();
                            });
                            TextView sizetitle = new TextView(GenerateChartActivity.this);
                            sizetitle.setText("Size:");
                            lay15.addView(sizetitle);
                            lay15.addView(size);
                        }
                        builder131.setView(lay15);
                        builder131.show();
                    });

                } else {
                    datasetsText.setOnClickListener(v -> {

                        AlertDialog.Builder builder127 = new AlertDialog.Builder(GenerateChartActivity.this);
                        builder127.setTitle("Datasets");
                        LinearLayout lay14 = new LinearLayout(GenerateChartActivity.this);
                        lay14.setOrientation(LinearLayout.VERTICAL);

                        for (int i = 0; i < dataModel.getDatasets().size(); i++) {
                            LinearLayout row = new LinearLayout(GenerateChartActivity.this);
                            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            TextView dataset = new TextView(GenerateChartActivity.this);
                            dataset.setId(i);
                            dataset.setText(dataModel.getDatasets().get(i));
                            dataset.setTextColor(dataModel.getArrayOfColors().get(i));
                            CheckBox check = new CheckBox(GenerateChartActivity.this);
                            check.setId(i);
                            check.setText("");
                            check.setChecked(dataModel.getEnabledDataSets().get(i) == 1);
                            check.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                        if (isChecked) {
                                            dataModel.getEnabledDataSets().set(check.getId(), 1);
                                        } else {

                                            dataModel.getEnabledDataSets().set(check.getId(), 0);
                                        }
                                        dataModel.updateEnabledDB();
                                        updatechart();
                                    }
                            );
                            dataset.setClickable(true);
                            dataset.setOnLongClickListener(v15 -> {
                                int color = Color.parseColor("#e7dbd0");

                                ColorPickerDialogBuilder

                                        .with(GenerateChartActivity.this)
                                        .setTitle("Choose color")
                                        .initialColor(color)
                                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                        .density(12)
                                        .setOnColorSelectedListener(selectedColor -> {
                                            String colorcode = "onColorSelected: 0x" + Integer.toHexString(selectedColor);
                                        })
                                        .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {

                                            dataModel.getArrayOfColors().set(dataset.getId(), selectedColor);
                                            dataset.setTextColor(selectedColor);
                                            dataModel.updateColorsDB();
                                            updatechart();
                                        })
                                        .setNegativeButton("cancel", (dialog, which) -> {
                                        })
                                        .build()
                                        .show();
                                return false;
                            });
                            dataset.setOnClickListener(v14 -> {
                                AlertDialog.Builder builder126 = new AlertDialog.Builder(GenerateChartActivity.this);
                                builder126.setTitle("Name");


                                final EditText input = new EditText(GenerateChartActivity.this);


                                builder126.setView(input);


                                builder126.setPositiveButton("OK", (dialog, which) -> {
                                    String tempText = input.getText().toString();
                                    if (!tempText.equals("")) {


                                        dataModel.getDatasets().set(dataset.getId(), tempText);
                                        dataset.setText(tempText);
                                        dataModel.updateDatasetsDB();
                                        updatechart();

                                    }
                                });
                                builder126.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                                builder126.show();


                            });
                            row.addView(dataset);
                            row.addView(check);
                            lay14.addView(row);
                        }
                        builder127.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
                        ScrollView scroll = new ScrollView(GenerateChartActivity.this);
                        scroll.addView(lay14);
                        builder127.setView(scroll);
                        builder127.show();
                    });
                }
                datasetsText.setTypeface(null, Typeface.BOLD);
                lay.addView(datasetsText);
                TextView title_title = new TextView(GenerateChartActivity.this);
                title_title.setText("Title");
                title_title.setTypeface(null, Typeface.ITALIC);
                title_title.setGravity(Gravity.CENTER);
                title_title.setTextSize(15);
                lay.addView(title_title);
                charttitleText = new TextView(GenerateChartActivity.this);
                charttitleText.setText("Title:");
                lay.addView(charttitleText);
                charttitle = new TextView(GenerateChartActivity.this);
                charttitle.setText(dataModel.getChartTitle());
                charttitle.setTypeface(null, Typeface.BOLD);
                charttitle.setClickable(true);
                charttitle.setOnClickListener(v -> {
                    AlertDialog.Builder builder125 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder125.setTitle("Title");


                    final EditText input = new EditText(GenerateChartActivity.this);

                    builder125.setView(input);


                    builder125.setPositiveButton("OK", (dialog, which) -> {


                        String tempText = input.getText().toString();
                        if (!tempText.equals("")) {

                            dataModel.setChartTitle(tempText);
                            title.setText(tempText);
                            charttitle.setText(tempText);
                            dataModel.updateTitleDB();
                        }


                    });
                    builder125.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                    builder125.show();


                });
                lay.addView(charttitle);


                titleswitch = new Switch(GenerateChartActivity.this);
                titleswitch.setText("Display Title");
                titleswitch.setChecked(dataModel.isTitleBool());
                titleswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setTitleBool(true);
                        title.setVisibility(View.VISIBLE);
                    } else {
                        dataModel.setTitleBool(false);
                        title.setVisibility(View.GONE);
                    }
                    dataModel.updateSettingsDB();
                });
                lay.addView(titleswitch);
                titlecolorText = new TextView(GenerateChartActivity.this);
                titlecolorText.setText("Title Color:");
                titlecolor = new TextView(GenerateChartActivity.this);
                titlecolor.setBackgroundColor(dataModel.getChartTitleColor());

                titlecolor.setText("    ");
                LinearLayout title_row = new LinearLayout(GenerateChartActivity.this);
                title_row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


                titlecolor.setOnClickListener(v -> {
                    int color = Color.parseColor("#e7dbd0");

                    ColorPickerDialogBuilder

                            .with(GenerateChartActivity.this)
                            .setTitle("Choose color")
                            .initialColor(color)
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
                            .setOnColorSelectedListener(selectedColor -> {
                                String colorcode = "onColorSelected: 0x" + Integer.toHexString(selectedColor);
                            })
                            .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                                titlecolor.setBackgroundColor(selectedColor);
                                dataModel.setChartTitleColor(selectedColor);
                                dataModel.updateSettingsDB();
                                updatechart();
                            })
                            .setNegativeButton("cancel", (dialog, which) -> {
                            })
                            .build()
                            .show();


                });
                title_row.addView(titlecolorText);
                title_row.addView(titlecolor);
                lay.addView(title_row);
                TextView backgroundTitle = new TextView(GenerateChartActivity.this);
                backgroundTitle.setText("Background");
                backgroundTitle.setTypeface(null, Typeface.ITALIC);
                backgroundTitle.setGravity(Gravity.CENTER);
                backgroundTitle.setTextSize(15);
                lay.addView(backgroundTitle);
                bgcolorText = new TextView(GenerateChartActivity.this);
                bgcolorText.setText("Background Color:");

                LinearLayout bgRow = new LinearLayout(GenerateChartActivity.this);
                bgRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                bgcolor = new TextView(GenerateChartActivity.this);
                bgcolor.setClickable(true);
                bgcolor.setText("    ");
                bgcolor.setBackgroundColor(dataModel.getBackgroundColor());
                bgcolor.setOnClickListener(v -> {
                    int color = Color.parseColor("#e7dbd0");

                    ColorPickerDialogBuilder

                            .with(GenerateChartActivity.this)
                            .setTitle("Choose color")
                            .initialColor(color)
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
                            .setOnColorSelectedListener(selectedColor -> {
                                String colorcode = "onColorSelected: 0x" + Integer.toHexString(selectedColor);
                            })
                            .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                                bgcolor.setBackgroundColor(selectedColor);
                                dataModel.setBackgroundColor(selectedColor);
                                dataModel.updateSettingsDB();
                                updatechart();
                            })
                            .setNegativeButton("cancel", (dialog, which) -> {
                            })
                            .build()
                            .show();


                });
                bgRow.addView(bgcolorText);
                bgRow.addView(bgcolor);
                lay.addView(bgRow);
                TextView descriptionTitle = new TextView(GenerateChartActivity.this);
                descriptionTitle.setText("Description");
                descriptionTitle.setTypeface(null, Typeface.ITALIC);
                descriptionTitle.setGravity(Gravity.CENTER);
                descriptionTitle.setTextSize(15);
                lay.addView(descriptionTitle);
                descriptionText = new TextView(GenerateChartActivity.this);
                descriptionText.setText("Description:");
                lay.addView(descriptionText);
                description = new TextView(GenerateChartActivity.this);
                description.setText(dataModel.getDescriptionStr());
                description.setClickable(true);
                description.setTypeface(null, Typeface.BOLD);
                description.setOnClickListener(v -> {
                    AlertDialog.Builder builder124 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder124.setTitle("Description");


                    final EditText input = new EditText(GenerateChartActivity.this);


                    builder124.setView(input);


                    builder124.setPositiveButton("OK", (dialog, which) -> {
                        String tempText = input.getText().toString();
                        if (!tempText.equals("")) {

                            dataModel.setDescriptionStr(tempText);
                            description.setText(tempText);
                            dataModel.updateSettingsDB();
                            updatechart();
                        }

                    });

                    builder124.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                    builder124.show();


                });
                lay.addView(description);
                descriptiontoggle = new Switch(GenerateChartActivity.this);
                descriptiontoggle.setText("Display Description");
                descriptiontoggle.setChecked(dataModel.isDescriptionBool());
                descriptiontoggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setDescriptionBool(true);
                    } else {
                        dataModel.setDescriptionBool(false);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });

                builder.setNegativeButton("Back", (dialog, which) -> dialog.cancel());
                lay.addView(descriptiontoggle);
                TextView xAxisTitle = new TextView(GenerateChartActivity.this);
                xAxisTitle.setText("X Axis");
                xAxisTitle.setTypeface(null, Typeface.ITALIC);
                xAxisTitle.setGravity(Gravity.CENTER);
                xAxisTitle.setTextSize(15);
                lay.addView(xAxisTitle);
                xlabeltypeText = new TextView(GenerateChartActivity.this);
                xlabeltypeText.setText("X Label Type:");
                lay.addView(xlabeltypeText);
                Xlabeltype = new TextView(GenerateChartActivity.this);
                Xlabeltype.setText(dataModel.getxLabelTypeStr());
                Xlabeltype.setTypeface(null, Typeface.BOLD);
                Xlabeltype.setClickable(true);
                Xlabeltype.setOnClickListener(v -> {
                    String[] typesArray = {"DATE", "NUMERIC"};

                    AlertDialog.Builder builder123 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder123.setTitle("Pick a label type");
                    builder123.setItems(typesArray, (dialog, which) -> {
                        Xlabeltype.setText(typesArray[which]);
                        dataModel.setxLabelTypeStr(typesArray[which]);
                        dataModel.updateSettingsDB();
                        updatechart();
                        if (typesArray[which].equals("NUMERIC")) {
                            Xminvalue.setText(String.valueOf(dataModel.getMinXValue()));
                            Xmaxvalue.setText(String.valueOf(dataModel.getMaxXValue()));
                            xstart.setText(String.valueOf(dataModel.getxStartNum()));

                        } else {
                            String dateString = formatter.format(dataModel.getMinimumDate().getTime());
                            Xminvalue.setText(dateString);
                            dateString = formatter.format(dataModel.getMaximumDate().getTime());
                            Xmaxvalue.setText(dateString);
                            dateString = formatter.format(dataModel.getInitialDate().getTime());
                            xstart.setText(dateString);


                        }
                    });
                    builder123.show();
                });
                lay.addView(Xlabeltype);
                xlabelText = new TextView(GenerateChartActivity.this);
                xlabelText.setText("X Label:");
                lay.addView(xlabelText);
                Xlabelt = new TextView(GenerateChartActivity.this);
                Xlabelt.setText(dataModel.getxLabelString());
                Xlabelt.setTypeface(null, Typeface.BOLD);
                Xlabelt.setClickable(true);
                Xlabelt.setOnClickListener(v -> {
                    AlertDialog.Builder builder122 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder122.setTitle("Name");


                    final EditText input = new EditText(GenerateChartActivity.this);


                    builder122.setView(input);


                    builder122.setPositiveButton("OK", (dialog, which) -> {
                        String tempText = input.getText().toString();
                        if (!tempText.equals("")) {
                            if (dataModel.getChartType().equals("Horizontal Bar Chart") || dataModel.getChartType().equals("Horizontal Stacked Bar Chart")) {
                                dataModel.setxLabelString(tempText);
                                Xlabelt.setText(tempText);
                                yTitle.setText(tempText);
                                dataModel.updateSettingsDB();
                            } else {
                                dataModel.setxLabelString(tempText);
                                Xlabelt.setText(tempText);
                                xTitle.setText(tempText);
                                dataModel.updateSettingsDB();
                            }
                        }

                    });
                    builder122.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                    builder122.show();


                });
                lay.addView(Xlabelt);

                xstartText.setText("X Starting value:");
                lay.addView(xstartText);
                xstart.setTypeface(null, Typeface.BOLD);
                xstart.setClickable(true);
                xstart.setOnClickListener(v -> {
                    if (dataModel.getxLabelTypeStr().equals("DATE")) {
                        final Calendar newCalendar = Calendar.getInstance();
                        final DatePickerDialog StartTime = new DatePickerDialog(GenerateChartActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, monthOfYear, dayOfMonth);
                            dataModel.setInitialDate(newDate.getTime());
                            SimpleDateFormat formatter15 = new SimpleDateFormat("dd MMM-YYYY");
                            String date_string = formatter15.format(newDate.getTime());
                            xstart.setText(date_string);
                            dataModel.updateSettingsDB();
                            updatechart();
                        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                        StartTime.show();
                    } else {
                        AlertDialog.Builder builder121 = new AlertDialog.Builder(GenerateChartActivity.this);
                        builder121.setTitle("Value");


                        final EditText input = new EditText(GenerateChartActivity.this);

                        input.setInputType(InputType.TYPE_CLASS_PHONE);
                        input.addTextChangedListener(new TextWatcher() {

                            public void afterTextChanged(Editable s) {

                                String result = s.toString();
                                if (!result.matches(numberPattern) && !result.matches("") && !result.matches("-")) {

                                    input.setText("");

                                }
                            }

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                        });

                        builder121.setView(input);


                        builder121.setPositiveButton("OK", (dialog, which) -> {
                            String tempText = input.getText().toString();
                            if (!tempText.equals("") && !tempText.equals("-")) {

                                dataModel.setxStartNum(Float.parseFloat(tempText));

                                xstart.setText(tempText);
                                dataModel.updateSettingsDB();
                                updatechart();
                            }
                        });
                        builder121.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                        builder121.show();

                    }
                });
                if (dataModel.getxLabelTypeStr().equals("NUMERIC")) {
                    xstart.setText(String.valueOf(dataModel.getxStartNum()));
                } else {
                    String date_string = formatter.format(dataModel.getInitialDate().getTime());
                    xstart.setText(date_string);
                }
                lay.addView(xstart);
                xstepText = new TextView(GenerateChartActivity.this);
                xstepText.setText("X Step:");
                lay.addView(xstepText);
                xstep = new TextView(GenerateChartActivity.this);
                xstep.setText(dataModel.getIncrementX());
                xstep.setTypeface(null, Typeface.BOLD);
                xstep.setClickable(true);
                xstep.setOnClickListener(v -> {
                    AlertDialog.Builder builder120 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder120.setTitle("Value");


                    final EditText input = new EditText(GenerateChartActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_PHONE);
                    input.addTextChangedListener(new TextWatcher() {

                        public void afterTextChanged(Editable s) {

                            String result = s.toString();
                            if (!result.matches(numberPattern) && !result.matches("") && !result.matches("-")) {

                                input.setText("");

                            }
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }
                    });


                    builder120.setView(input);


                    builder120.setPositiveButton("OK", (dialog, which) -> {
                        String tempText = input.getText().toString();
                        if (!tempText.equals("") && !tempText.equals("-")) {

                            dataModel.setIncrementX(tempText);
                            xstep.setText(tempText);
                            dataModel.updateSettingsDB();
                            updatechart();
                        }
                    });
                    builder120.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                    builder120.show();


                });
                lay.addView(xstep);
                xcolorText = new TextView(GenerateChartActivity.this);
                xcolorText.setText("X Axis Color:");
                xcolor = new TextView(GenerateChartActivity.this);
                xcolor.setText("    ");
                xcolor.setBackgroundColor(dataModel.getxColor());
                LinearLayout x_row = new LinearLayout(GenerateChartActivity.this);
                x_row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                xcolor.setClickable(true);
                xcolor.setClickable(true);
                xcolor.setBackgroundColor(dataModel.getxColor());
                xcolor.setOnClickListener(v -> {
                    int color = Color.parseColor("#e7dbd0");

                    ColorPickerDialogBuilder

                            .with(GenerateChartActivity.this)
                            .setTitle("Choose color")
                            .initialColor(color)
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
                            .setOnColorSelectedListener(selectedColor -> {
                                String colorcode = "onColorSelected: 0x" + Integer.toHexString(selectedColor);
                            })
                            .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {

                                dataModel.setxColor(selectedColor);
                                xcolor.setBackgroundColor(selectedColor);
                                dataModel.updateSettingsDB();
                                updatechart();
                            })
                            .setNegativeButton("cancel", (dialog, which) -> {
                            })
                            .build()
                            .show();


                });
                x_row.addView(xcolorText);
                x_row.addView(xcolor);
                lay.addView(x_row);
                xlabelswitch = new Switch(GenerateChartActivity.this);
                xlabelswitch.setChecked(dataModel.isxLabelBool());
                xlabelswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setxLabelBool(true);
                        xTitle.setVisibility(View.VISIBLE);

                    } else {
                        dataModel.setxLabelBool(false);
                        xTitle.setVisibility(View.GONE);
                    }
                    dataModel.updateSettingsDB();
                });
                xlabelswitch.setText("Display X Label");
                lay.addView(xlabelswitch);
                toggleXaxis = new Switch(GenerateChartActivity.this);
                toggleXaxis.setChecked(dataModel.isxAxisBool());
                toggleXaxis.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setxAxisBool(true);
                    } else {
                        dataModel.setxAxisBool(false);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });
                toggleXaxis.setText("Display X Axis");
                lay.addView(toggleXaxis);
                minXtoggle = new Switch(GenerateChartActivity.this);
                minXtoggle.setText("Minimum X");
                minXtoggle.setChecked(dataModel.isxMinBool());
                minXtoggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setxMinBool(true);
                    } else {
                        dataModel.setxMinBool(false);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });
                lay.addView(minXtoggle);
                xminvalueText = new TextView(GenerateChartActivity.this);
                xminvalueText.setText("Minimum X:");
                lay.addView(xminvalueText);

                Xminvalue.setClickable(true);

                if (dataModel.getxLabelTypeStr().equals("DATE"))
                    Xminvalue.setText(formatter.format(dataModel.getMinimumDate().getTime()));
                else
                    Xminvalue.setText(String.valueOf(dataModel.getMinXValue()));

                Xminvalue.setOnClickListener(v -> {
                    if (dataModel.getxLabelTypeStr().equals("DATE")) {
                        final Calendar newCalendar = Calendar.getInstance();
                        final DatePickerDialog StartTime = new DatePickerDialog(GenerateChartActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, monthOfYear, dayOfMonth);
                            dataModel.setMinimumDate(newDate.getTime());
                            SimpleDateFormat formatter14 = new SimpleDateFormat("dd MMM-YYYY");
                            String dateString = formatter14.format(newDate.getTime());
                            Xminvalue.setText(dateString);
                            dataModel.updateSettingsDB();
                            updatechart();
                        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                        StartTime.show();
                    } else {
                        AlertDialog.Builder builder119 = new AlertDialog.Builder(GenerateChartActivity.this);
                        builder119.setTitle("Value");


                        final EditText input = new EditText(GenerateChartActivity.this);

                        input.setInputType(InputType.TYPE_CLASS_PHONE);
                        input.addTextChangedListener(new TextWatcher() {

                            public void afterTextChanged(Editable s) {

                                String result = s.toString();
                                if (!result.matches(numberPattern) && !result.matches("") && !result.matches("-")) {

                                    input.setText("");

                                }
                            }

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                        });

                        builder119.setView(input);


                        builder119.setPositiveButton("OK", (dialog, which) -> {
                            String tempText = input.getText().toString();
                            if (!tempText.equals("") && !tempText.equals("-")) {

                                dataModel.setMinXValue(Float.parseFloat(tempText));
                                Xminvalue.setText(String.valueOf(dataModel.getMinXValue()));
                                dataModel.updateSettingsDB();
                                updatechart();
                            }
                        });
                        builder119.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                        builder119.show();


                    }
                });
                Xminvalue.setTypeface(null, Typeface.BOLD);
                lay.addView(Xminvalue);
                maxXtoggle = new Switch(GenerateChartActivity.this);
                maxXtoggle.setText("Maximum X");
                maxXtoggle.setChecked(dataModel.isxMaxBool());
                maxXtoggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setxMaxBool(true);
                    } else {
                        dataModel.setxMaxBool(false);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });
                lay.addView(maxXtoggle);
                xmaxvalueText = new TextView(GenerateChartActivity.this);
                xmaxvalueText.setText("Maximum X:");
                lay.addView(xmaxvalueText);
                if (dataModel.getxLabelTypeStr().equals("DATE"))
                    Xmaxvalue.setText(formatter.format(dataModel.getMaximumDate().getTime()));
                else
                    Xmaxvalue.setText(String.valueOf(dataModel.getMaxXValue()));
                Xmaxvalue.setTypeface(null, Typeface.BOLD);
                Xmaxvalue.setClickable(true);
                Xmaxvalue.setOnClickListener(v -> {
                    if (dataModel.getxLabelTypeStr().equals("DATE")) {
                        final Calendar newCalendar = Calendar.getInstance();
                        final DatePickerDialog StartTime = new DatePickerDialog(GenerateChartActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, monthOfYear, dayOfMonth);
                            dataModel.setMaximumDate(newDate.getTime());
                            SimpleDateFormat formatter13 = new SimpleDateFormat("dd MMM-YYYY");
                            String dateString = formatter13.format(newDate.getTime());
                            Xmaxvalue.setText(dateString);
                            dataModel.updateSettingsDB();
                            updatechart();
                        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                        StartTime.show();
                    } else {
                        AlertDialog.Builder builder118 = new AlertDialog.Builder(GenerateChartActivity.this);
                        builder118.setTitle("Value");


                        final EditText input = new EditText(GenerateChartActivity.this);

                        input.setInputType(InputType.TYPE_CLASS_PHONE);
                        input.addTextChangedListener(new TextWatcher() {

                            public void afterTextChanged(Editable s) {

                                String result = s.toString();
                                if (!result.matches(numberPattern) && !result.matches("") && !result.matches("-")) {

                                    input.setText("");

                                }
                            }

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                        });

                        builder118.setView(input);


                        builder118.setPositiveButton("OK", (dialog, which) -> {
                            String tempText = input.getText().toString();
                            if (!tempText.equals("") && !tempText.equals("-")) {

                                dataModel.setMaxXValue(Float.parseFloat(tempText));
                                Xmaxvalue.setText(tempText);
                                dataModel.updateSettingsDB();
                                updatechart();
                            }
                        });
                        builder118.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                        builder118.show();

                    }
                });
                lay.addView(Xmaxvalue);
                TextView yAxisTitle = new TextView(GenerateChartActivity.this);
                yAxisTitle.setText("Y Axis");
                yAxisTitle.setTypeface(null, Typeface.ITALIC);
                yAxisTitle.setGravity(Gravity.CENTER);
                yAxisTitle.setTextSize(15);
                lay.addView(yAxisTitle);
                yaxislabelText = new TextView(GenerateChartActivity.this);
                yaxislabelText.setText("Y Axis Label:");
                lay.addView(yaxislabelText);
                Yaxislabel = new TextView(GenerateChartActivity.this);
                Yaxislabel.setText(dataModel.getyLabelStr());
                Yaxislabel.setTypeface(null, Typeface.BOLD);
                Yaxislabel.setClickable(true);
                Yaxislabel.setOnClickListener(v -> {
                    AlertDialog.Builder builder117 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder117.setTitle("Name");


                    final EditText input = new EditText(GenerateChartActivity.this);


                    builder117.setView(input);


                    builder117.setPositiveButton("OK", (dialog, which) -> {
                        String tempText = input.getText().toString();
                        if (!tempText.equals("")) {
                            if (dataModel.getChartType().equals("Horizontal Bar Chart") || dataModel.getChartType().equals("Horizontal Stacked Bar Chart")) {
                                dataModel.setyLabelStr(tempText);
                                Yaxislabel.setText(tempText);
                                xTitle.setText(tempText);
                                dataModel.updateSettingsDB();
                            } else {
                                dataModel.setyLabelStr(tempText);
                                Yaxislabel.setText(tempText);
                                yTitle.setText(tempText);
                                dataModel.updateSettingsDB();
                            }

                        }

                    });

                    builder117.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                    builder117.show();


                });
                lay.addView(Yaxislabel);
                ycolorText = new TextView(GenerateChartActivity.this);
                ycolorText.setText("Y Axis Color:");
                ycolor = new TextView(GenerateChartActivity.this);
                ycolor.setText("    ");
                ycolor.setBackgroundColor(dataModel.getyColor());
                ycolor.setClickable(true);
                ycolor.setOnClickListener(v -> {
                    int color = Color.parseColor("#e7dbd0");

                    ColorPickerDialogBuilder

                            .with(GenerateChartActivity.this)
                            .setTitle("Choose color")
                            .initialColor(color)
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
                            .setOnColorSelectedListener(selectedColor -> {
                                String colorcode = "onColorSelected: 0x" + Integer.toHexString(selectedColor);
                            })
                            .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {

                                dataModel.setyColor(selectedColor);
                                ycolor.setBackgroundColor(selectedColor);
                                dataModel.updateSettingsDB();
                                updatechart();
                            })
                            .setNegativeButton("cancel", (dialog, which) -> {
                            })
                            .build()
                            .show();


                });
                LinearLayout yRow = new LinearLayout(GenerateChartActivity.this);
                yRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                yRow.addView(ycolorText);
                yRow.addView(ycolor);
                lay.addView(yRow);
                yaxisswitch = new Switch(GenerateChartActivity.this);
                yaxisswitch.setText("Display Y Label");
                yaxisswitch.setChecked(dataModel.isyLabelBool());

                yaxisswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setyLabelBool(true);
                        yTitle.setVisibility(View.VISIBLE);
                    } else {
                        dataModel.setyLabelBool(false);
                        yTitle.setVisibility(View.GONE);
                    }
                    dataModel.updateSettingsDB();
                });
                lay.addView(yaxisswitch);
                toggleYaxis = new Switch(GenerateChartActivity.this);
                toggleYaxis.setText("Display Y Axis");
                toggleYaxis.setChecked(dataModel.isYaxisBool());
                toggleYaxis.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setYaxisBool(true);

                    } else {
                        dataModel.setYaxisBool(false);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });
                lay.addView(toggleYaxis);
                Yminswitch = new Switch(GenerateChartActivity.this);
                Yminswitch.setText("Minimum Y");
                Yminswitch.setChecked(dataModel.isyMinBool());
                Yminswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {

                        dataModel.setyMinBool(true);

                    } else {
                        dataModel.setyMinBool(false);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });
                lay.addView(Yminswitch);
                yminvalueText = new TextView(GenerateChartActivity.this);
                yminvalueText.setText("Minimum Y:");
                lay.addView(yminvalueText);
                Yminvalue = new TextView(GenerateChartActivity.this);
                Yminvalue.setText(String.valueOf(dataModel.getMinYValue()));
                Yminvalue.setTypeface(null, Typeface.BOLD);
                Yminvalue.setClickable(true);
                Yminvalue.setOnClickListener(v -> {
                    AlertDialog.Builder builder114 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder114.setTitle("Value");


                    final EditText input = new EditText(GenerateChartActivity.this);

                    input.setInputType(InputType.TYPE_CLASS_PHONE);
                    input.addTextChangedListener(new TextWatcher() {

                        public void afterTextChanged(Editable s) {

                            String result = s.toString();
                            if (!result.matches(numberPattern) && !result.matches("") && !result.matches("-")) {

                                input.setText("");

                            }
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }
                    });

                    builder114.setView(input);


                    builder114.setPositiveButton("OK", (dialog, which) -> {
                        String tempText = input.getText().toString();
                        if (!tempText.equals("") && !tempText.equals("-")) {

                            dataModel.setMinYValue(Float.parseFloat(tempText));
                            Yminvalue.setText(String.valueOf(dataModel.getMinYValue()));
                            dataModel.updateSettingsDB();
                            updatechart();
                        }
                    });
                    builder114.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                    builder114.show();


                });
                lay.addView(Yminvalue);
                Ymaxswitch = new Switch(GenerateChartActivity.this);
                Ymaxswitch.setText("Maximum Y");
                Ymaxswitch.setChecked(dataModel.isyMaxBool());
                Ymaxswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {

                        dataModel.setyMaxBool(true);

                    } else {
                        dataModel.setyMaxBool(false);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });
                lay.addView(Ymaxswitch);
                ymaxvalueText = new TextView(GenerateChartActivity.this);
                ymaxvalueText.setText("Maximum Y:");
                lay.addView(ymaxvalueText);
                Ymaxvalue = new TextView(GenerateChartActivity.this);
                Ymaxvalue.setText(String.valueOf(dataModel.getMaxYValue()));
                Ymaxvalue.setTypeface(null, Typeface.BOLD);
                Ymaxvalue.setClickable(true);
                Ymaxvalue.setOnClickListener(v -> {
                    AlertDialog.Builder builder113 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder113.setTitle("Value");


                    final EditText input = new EditText(GenerateChartActivity.this);

                    input.setInputType(InputType.TYPE_CLASS_PHONE);
                    input.addTextChangedListener(new TextWatcher() {

                        public void afterTextChanged(Editable s) {

                            String result = s.toString();
                            if (!result.matches(numberPattern) && !result.matches("") && !result.matches("-")) {

                                input.setText("");

                            }
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }
                    });

                    builder113.setView(input);


                    builder113.setPositiveButton("OK", (dialog, which) -> {
                        String tempText = input.getText().toString();
                        if (!tempText.equals("") && !tempText.equals("-")) {

                            dataModel.setMaxYValue(Float.parseFloat(tempText));
                            Ymaxvalue.setText(String.valueOf(dataModel.getMaxXValue()));
                            dataModel.updateSettingsDB();
                            updatechart();
                        }
                    });
                    builder113.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                    builder113.show();


                });
                lay.addView(Ymaxvalue);
                TextView typeTitle = new TextView(GenerateChartActivity.this);
                typeTitle.setText("Type and Format");
                typeTitle.setTypeface(null, Typeface.ITALIC);
                typeTitle.setGravity(Gravity.CENTER);
                typeTitle.setTextSize(15);
                lay.addView(typeTitle);
                ScrollView scroll = new ScrollView(GenerateChartActivity.this);
                markerstoggle = new Switch(GenerateChartActivity.this);
                markerstoggle.setText("Toggle Markers");
                markerstoggle.setChecked(dataModel.isMarkersBool());
                markerstoggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setMarkersBool(true);
                    } else {
                        dataModel.setMarkersBool(false);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });
                lay.addView(markerstoggle);
                decimalswitch = new Switch(GenerateChartActivity.this);
                decimalswitch.setText("Toggle Decimals");
                decimalswitch.setChecked(dataModel.isDecimalBool());
                decimalswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setDecimalBool(true);
                    } else {
                        dataModel.setDecimalBool(false);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });
                lay.addView(decimalswitch);
                charttypeText = new TextView(GenerateChartActivity.this);
                charttypeText.setText("Chart Type:");
                lay.addView(charttypeText);
                charttype = new TextView(GenerateChartActivity.this);
                charttype.setText(dataModel.getChartType());
                charttype.setTypeface(null, Typeface.BOLD);
                charttype.setClickable(true);
                charttype.setOnClickListener(v -> {
                    String[] chartsArray = {"Line Chart", "Area Chart", "Spline Chart", "Bar Chart", "Horizontal Bar Chart", "Area Spline Chart", "Scatterplot Chart", "Bubble Chart", "Radar Chart", "Stacked Bar Chart", "Horizontal Stacked Bar Chart", "Pie Chart", "Candlestick Chart"};

                    AlertDialog.Builder builder115 = new AlertDialog.Builder(GenerateChartActivity.this);
                    builder115.setTitle("Pick a chart type");
                    builder115.setItems(chartsArray, (dialog, which) -> {
                        charttype.setText(chartsArray[which]);
                        dataModel.setChartType(chartsArray[which]);
                        dataModel.updateTypeDB();
                        updatechart();
                        settingsDialog.cancel();
                        openSettings();
                    });
                    builder115.show();
                });
                lay.addView(charttype);
                displayswitch = new Switch(GenerateChartActivity.this);
                displayswitch.setText("Display Entire Data");
                displayswitch.setChecked(dataModel.isDisplayBool());
                displayswitch.setClickable(true);
                displayswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        dataModel.setDisplayBool(true);
                    } else {
                        dataModel.setDisplayBool(false);
                    }
                    dataModel.updateSettingsDB();
                    updatechart();
                });
                lay.addView(displayswitch);
                scroll.addView(lay);
                builder.setView(scroll);
                settingsDialog = builder.show();
                break;
            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_chart);
        String valuesString = getIntent().getStringExtra("values");
        boolean loadedChart = getIntent().getBooleanExtra("LOADED", false);
        if (!loadedChart) {
            dataModel.setChartId(getIntent().getIntExtra("SELECTED_ID", 0));
            dataModel.setChartType(getIntent().getStringExtra("CHART_TYPE"));
            dataModel.setChartTitle(getIntent().getStringExtra("CHART_NAME"));
            Type typemap = new TypeToken<HashMap<String, List<Float>>>() {
            }.getType();
            Gson gson = new Gson();
            Map<String, List<Float>> valuesList;
            valuesList = gson.fromJson(valuesString, typemap);

            for (String key : valuesList.keySet()) {
                dataModel.getDatasets().add(key);
                dataModel.getValues().add(valuesList.get(key));
            }
            for (int i = 0; i < dataModel.getDatasets().size(); i++) {
                dataModel.getEnabledDataSets().add(1);
                dataModel.getConnectedCharts().add((-1));
            }
            List<Integer> availableColors = new ArrayList<>();
            availableColors.add(Color.parseColor("#0000FF"));
            availableColors.add(Color.parseColor("#FF0000"));
            availableColors.add(Color.parseColor("#FF0000"));
            availableColors.add(Color.parseColor("#800080"));
            availableColors.add(Color.parseColor("#FFA500"));
            int color_incolorIndexex = 0;
            for (int i = 0; i < dataModel.getDatasets().size(); i++) {
                dataModel.getArrayOfColors().add(availableColors.get(color_incolorIndexex));
                if (color_incolorIndexex != 4) {
                    color_incolorIndexex++;
                } else {
                    color_incolorIndexex = 0;
                }
            }

            dataModel.saveChartDB();
            int rows_size = dataModel.numberOfRows();
            if (rows_size == 1) {
                List<ArrayList<String>> get_charts = dataModel.getAllCharts();
                dataModel.setChartId(Integer.parseInt(get_charts.get(0).get(0)));
            }
        } else {
            dataModel.setChartId(getIntent().getIntExtra("SELECTED_ID", 0));
            dataModel.loadChartDB();
        }
        updatechart();

    }
}