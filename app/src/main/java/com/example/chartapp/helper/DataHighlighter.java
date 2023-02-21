package com.example.chartapp.helper;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.highlight.Highlight;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DataHighlighter {
    private final String numberPattern = "[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)";
    private final List<Highlight> highlightList = new ArrayList<>();
    private final Context context;
    private String highlightType = "Custom Values";
    private float xStartNum = 0f;
    private float xEndnum = 0f;
    private Date xStartDate = new GregorianCalendar(2021, Calendar.MAY, 01).getTime();
    private Date xEndDate = new GregorianCalendar(2021, Calendar.MAY, 01).getTime();
    private final List<List<Float>> values;
    private final List<String> labels;
    private final String xLabelTypeStr;
    private float ystartvalue = 0f;
    private float yendvalue = 0f;
    private final List<Date> arrayOfDates;

    public DataHighlighter(Context context, List<String> labels, List<List<Float>> values, String label, List<Date> arrayOfDates) {
        this.context = context;
        this.labels = labels;
        this.values = values;
        this.xLabelTypeStr = label;
        this.arrayOfDates = arrayOfDates;
    }

    @SuppressLint("SetTextI18n")
    public void highlightData(Chart chart) {
        highlightList.clear();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Highlight");


        final TextView xRangeStart = new TextView(context);
        final TextView xRangeEnd = new TextView(context);
        final TextView yRangeStart = new TextView(context);
        final TextView yRangeEnd = new TextView(context);
        final TextView HighlightType = new TextView(context);
        final TextView yStartTitle = new TextView(context);
        final TextView yEndTitle = new TextView(context);
        xRangeStart.setTypeface(null, Typeface.BOLD);
        xRangeEnd.setTypeface(null, Typeface.BOLD);
        yRangeStart.setTypeface(null, Typeface.BOLD);
        yRangeEnd.setTypeface(null, Typeface.BOLD);
        HighlightType.setTypeface(null, Typeface.BOLD);
        HighlightType.setText(highlightType);
        if (highlightType.equals("Custom Values")) {
            yRangeStart.setVisibility(View.VISIBLE);
            yRangeEnd.setVisibility(View.VISIBLE);
            yStartTitle.setVisibility(View.VISIBLE);
            yEndTitle.setVisibility(View.VISIBLE);
        } else {
            yRangeStart.setVisibility(View.GONE);
            yRangeEnd.setVisibility(View.GONE);
            yStartTitle.setVisibility(View.GONE);
            yEndTitle.setVisibility(View.GONE);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM-YYYY");
        if (xLabelTypeStr.equals("NUMERIC")) {
            xStartNum = Float.parseFloat(labels.get(0));
            xEndnum = Float.parseFloat(labels.get(labels.size() - 1));
        }
        if (xLabelTypeStr.equals("DATE")) {
            xStartDate = arrayOfDates.get(0);
            xEndDate = arrayOfDates.get(arrayOfDates.size() - 1);
        }
        ystartvalue = chart.getYMin();
        yendvalue = chart.getYMax();
        HighlightType.setClickable(true);
        HighlightType.setOnClickListener(v -> {
            String[] functionsArray = {"Custom Values", "Increasing Values", "Decreasing Values"};

            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setTitle("Pick a highlighting type");
            builder1.setItems(functionsArray, (dialog, which) -> {
                HighlightType.setText(functionsArray[which]);
                highlightType = functionsArray[which];
                if (functionsArray[which].equals("Custom Values")) {
                    yRangeStart.setVisibility(View.VISIBLE);
                    yRangeEnd.setVisibility(View.VISIBLE);
                    yStartTitle.setVisibility(View.VISIBLE);
                    yEndTitle.setVisibility(View.VISIBLE);
                } else {
                    yRangeStart.setVisibility(View.GONE);
                    yRangeEnd.setVisibility(View.GONE);
                    yStartTitle.setVisibility(View.GONE);
                    yEndTitle.setVisibility(View.GONE);
                }
            });
            builder1.show();
        });
        xRangeStart.setClickable(true);
        xRangeStart.setOnClickListener(v -> {
            if (xLabelTypeStr.equals("DATE")) {
                final Calendar newCalendar = Calendar.getInstance();
                final DatePickerDialog StartTime = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMM-YYYY");
                    xStartDate = newDate.getTime();
                    String date_string = formatter1.format(newDate.getTime());
                    xRangeStart.setText(date_string);
                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                StartTime.show();
            } else {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                builder2.setTitle("Value");


                final EditText input = new EditText(context);

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

                builder2.setView(input);


                builder2.setPositiveButton("OK", (dialog, which) -> {
                    String tempText = input.getText().toString();
                    if (!tempText.equals("") && !tempText.equals("-")) {
                        xStartNum = Float.parseFloat(tempText);
                        xRangeStart.setText(String.valueOf(xStartNum));
                    }

                });
                builder2.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                builder2.show();


            }
        });
        xRangeEnd.setClickable(true);
        xRangeEnd.setOnClickListener(v -> {
            if (xLabelTypeStr.equals("DATE")) {
                final Calendar newCalendar = Calendar.getInstance();
                final DatePickerDialog StartTime = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    SimpleDateFormat formatter12 = new SimpleDateFormat("dd MMM-YYYY");
                    String date_string = formatter12.format(newDate.getTime());
                    xRangeEnd.setText(date_string);
                    xEndDate = newDate.getTime();


                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                StartTime.show();
            } else {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                builder2.setTitle("Value");


                final EditText input = new EditText(context);

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

                builder2.setView(input);


                builder2.setPositiveButton("OK", (dialog, which) -> {
                    String tempText = input.getText().toString();
                    if (!tempText.equals("") && !tempText.equals("-")) {
                        xEndnum = Float.parseFloat(tempText);
                        xRangeEnd.setText(String.valueOf(xEndnum));
                    }

                });
                builder2.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                builder2.show();


            }
        });
        yRangeStart.setClickable(true);
        yRangeStart.setOnClickListener(v -> {


            AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
            builder2.setTitle("Value");


            final EditText input = new EditText(context);

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

            builder2.setView(input);


            builder2.setPositiveButton("OK", (dialog, which) -> {
                String tempText = input.getText().toString();
                if (!tempText.equals("") && !tempText.equals("-")) {
                    ystartvalue = Float.parseFloat(tempText);
                    yRangeStart.setText(String.valueOf(ystartvalue));
                }
            });
            builder2.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder2.show();
        });
        yRangeEnd.setClickable(true);
        yRangeEnd.setOnClickListener(v -> {


            AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
            builder2.setTitle("Value");


            final EditText input = new EditText(context);

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

            builder2.setView(input);


            builder2.setPositiveButton("OK", (dialog, which) -> {
                String tempText = input.getText().toString();
                if (!tempText.equals("") && !tempText.equals("-")) {
                    yendvalue = Float.parseFloat(tempText);
                    yRangeEnd.setText(String.valueOf(yendvalue));
                }
            });
            builder2.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder2.show();
        });
        if (xLabelTypeStr.equals("NUMERIC")) {
            xRangeStart.setText(String.valueOf(xStartNum));
            xRangeEnd.setText(String.valueOf(xEndnum));
        } else {
            xRangeStart.setText(formatter.format(xStartDate));
            xRangeEnd.setText(formatter.format(xEndDate));
        }
        yRangeStart.setText(String.valueOf(ystartvalue));
        yRangeEnd.setText(String.valueOf(yendvalue));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView highlightTitle = new TextView(context);
        highlightTitle.setText("Highlight Type:");
        linearLayout.addView(highlightTitle);
        linearLayout.addView(HighlightType);
        TextView xStartTitle = new TextView(context);
        xStartTitle.setText("X Starting Value:");
        linearLayout.addView(xStartTitle);
        linearLayout.addView(xRangeStart);
        TextView xEndTitle = new TextView(context);
        xEndTitle.setText("X Ending Value:");
        linearLayout.addView(xEndTitle);
        linearLayout.addView(xRangeEnd);

        yStartTitle.setText("T Starting Value:");
        linearLayout.addView(yStartTitle);
        linearLayout.addView(yRangeStart);

        yEndTitle.setText("Y Ending Value:");
        linearLayout.addView(yEndTitle);
        linearLayout.addView(yRangeEnd);

        builder.setView(linearLayout);


        builder.setPositiveButton("OK", (dialog, which) -> {
            if (highlightType.equals("Custom Values")) {
                if (xLabelTypeStr.equals("DATE")) {

                    SimpleDateFormat formatter2 = new SimpleDateFormat("dd MMM-YYYY");


                    for (int i = 0; i < values.size(); i++) {


                        List<Float> tempList = values.get(i);
                        for (int j = 0; j < tempList.size(); j++) {
                            if ((arrayOfDates.get(j).after(xStartDate) || formatter2.format(arrayOfDates.get(j)).equals(formatter2.format(xStartDate))) && (arrayOfDates.get(j).before(xEndDate) || arrayOfDates.get(j).equals(xEndDate))) {
                                float tempValue = tempList.get(j);
                                int after = Float.compare(tempValue, ystartvalue);
                                int before = Float.compare(tempValue, yendvalue);
                                if (after >= 0 && before <= 0) {
                                    Highlight highlightValue = new Highlight(j, i, 0);
                                    highlightList.add(highlightValue);
                                }
                            }
                        }

                    }


                } else if (xLabelTypeStr.equals("NUMERIC")) {


                    for (int i = 0; i < values.size(); i++) {
                        List<Float> tempList = values.get(i);


                        for (int j = 0; j < tempList.size(); j++) {
                            if (Float.parseFloat(labels.get(j)) >= xStartNum && Float.parseFloat(labels.get(j)) <= xEndnum) {
                                float temp_value = tempList.get(j);

                                int after = Float.compare(temp_value, ystartvalue);
                                int before = Float.compare(temp_value, yendvalue);

                                if (after >= 0 && before <= 0) {
                                    Highlight highlightValue = new Highlight(j, i, 0);
                                    highlightList.add(highlightValue);
                                }
                            }
                        }

                    }

                }
            } else if (highlightType.equals("Increasing Values")) {
                float prevValue = 0f;
                if (xLabelTypeStr.equals("DATE")) {

                    SimpleDateFormat formatter13 = new SimpleDateFormat("dd MMM-YYYY");


                    for (int i = 0; i < values.size(); i++) {


                        List<Float> tempList = values.get(i);
                        for (int j = 0; j < tempList.size(); j++) {
                            float tempValue = tempList.get(j);
                            if (j == 0) {
                                prevValue = tempValue;
                            }
                            if ((arrayOfDates.get(j).after(xStartDate) || formatter13.format(arrayOfDates.get(j)).equals(formatter13.format(xStartDate))) && (arrayOfDates.get(j).before(xEndDate) || arrayOfDates.get(j).equals(xEndDate))) {

                                int after = Float.compare(tempValue, prevValue);

                                if (after > 0) {
                                    Highlight highlightValue = new Highlight(j, i, 0);
                                    highlightList.add(highlightValue);
                                }
                                prevValue = tempValue;
                            }

                        }

                    }


                } else if (xLabelTypeStr.equals("NUMERIC")) {


                    for (int i = 0; i < values.size(); i++) {
                        List<Float> tempList = values.get(i);


                        for (int j = 0; j < tempList.size(); j++) {
                            if (Float.parseFloat(labels.get(j)) >= xStartNum && Float.parseFloat(labels.get(j)) <= xEndnum) {
                                float temp_value = tempList.get(j);
                                if (j == 0) {
                                    prevValue = temp_value;
                                }
                                int after = Float.compare(temp_value, prevValue);


                                if (after > 0) {
                                    Highlight highlightValue = new Highlight(j, i, 0);
                                    highlightList.add(highlightValue);
                                }
                                prevValue = temp_value;
                            }
                        }

                    }

                }
            } else {
                float prevValue = 0f;
                if (xLabelTypeStr.equals("DATE")) {

                    SimpleDateFormat formatter3 = new SimpleDateFormat("dd MMM-YYYY");


                    for (int i = 0; i < values.size(); i++) {


                        List<Float> tempList = values.get(i);
                        for (int j = 0; j < tempList.size(); j++) {
                            float tempValue = tempList.get(j);
                            if (j == 0) {
                                prevValue = tempValue;
                            }
                            if ((arrayOfDates.get(j).after(xStartDate) || formatter3.format(arrayOfDates.get(j)).equals(formatter3.format(xStartDate))) && (arrayOfDates.get(j).before(xEndDate) || arrayOfDates.get(j).equals(xEndDate))) {

                                int after = Float.compare(tempValue, prevValue);

                                if (after < 0) {
                                    Highlight highlight_value = new Highlight(j, i, 0);
                                    highlightList.add(highlight_value);
                                }
                                prevValue = tempValue;
                            }

                        }

                    }


                } else if (xLabelTypeStr.equals("NUMERIC")) {


                    for (int i = 0; i < values.size(); i++) {
                        List<Float> tempList = values.get(i);


                        for (int j = 0; j < tempList.size(); j++) {
                            if (Float.parseFloat(labels.get(j)) >= xStartNum && Float.parseFloat(labels.get(j)) <= xEndnum) {
                                float temp_value = tempList.get(j);
                                if (j == 0) {
                                    prevValue = temp_value;
                                }
                                int after = Float.compare(temp_value, prevValue);


                                if (after < 0) {
                                    Highlight highlightValue = new Highlight(j, i, 0);
                                    highlightList.add(highlightValue);
                                }
                                prevValue = temp_value;
                            }
                        }

                    }

                }
            }
            Highlight[] highlightedValues = highlightList.toArray(new Highlight[0]);
            chart.highlightValues(highlightedValues);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


        builder.show();

    }


    @SuppressLint("SetTextI18n")
    public void highlightDataStacked(Chart chart) {
        highlightList.clear();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Highlight");


        final TextView xRangeStart = new TextView(context);
        final TextView xRangeEnd = new TextView(context);
        final TextView yRangeStart = new TextView(context);
        final TextView yRangeEnd = new TextView(context);
        final TextView highlightType = new TextView(context);
        final TextView yStartTitle = new TextView(context);
        final TextView yEndTitle = new TextView(context);
        xRangeStart.setTypeface(null, Typeface.BOLD);
        xRangeEnd.setTypeface(null, Typeface.BOLD);
        yRangeStart.setTypeface(null, Typeface.BOLD);
        yRangeEnd.setTypeface(null, Typeface.BOLD);
        highlightType.setTypeface(null, Typeface.BOLD);
        highlightType.setText(this.highlightType);
        if (this.highlightType.equals("Custom Values")) {
            yRangeStart.setVisibility(View.VISIBLE);
            yRangeEnd.setVisibility(View.VISIBLE);
            yStartTitle.setVisibility(View.VISIBLE);
            yEndTitle.setVisibility(View.VISIBLE);
        } else {
            yRangeStart.setVisibility(View.GONE);
            yRangeEnd.setVisibility(View.GONE);
            yStartTitle.setVisibility(View.GONE);
            yEndTitle.setVisibility(View.GONE);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM-YYYY");
        if (xLabelTypeStr.equals("NUMERIC")) {
            xStartNum = Float.parseFloat(labels.get(0));
            xEndnum = Float.parseFloat(labels.get(labels.size() - 1));
        }
        if (xLabelTypeStr.equals("DATE")) {
            xStartDate = arrayOfDates.get(0);
            xEndDate = arrayOfDates.get(arrayOfDates.size() - 1);
        }
        ystartvalue = chart.getYMin();
        yendvalue = chart.getYMax();
        highlightType.setClickable(true);
        highlightType.setOnClickListener(v -> {
            String[] functionsArray = {"Custom Values", "Increasing Values", "Decreasing Values"};

            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setTitle("Pick a highlighting type");
            builder1.setItems(functionsArray, (dialog, which) -> {
                highlightType.setText(functionsArray[which]);
                DataHighlighter.this.highlightType = functionsArray[which];
                if (functionsArray[which].equals("Custom Values")) {
                    yRangeStart.setVisibility(View.VISIBLE);
                    yRangeEnd.setVisibility(View.VISIBLE);
                    yStartTitle.setVisibility(View.VISIBLE);
                    yEndTitle.setVisibility(View.VISIBLE);
                } else {
                    yRangeStart.setVisibility(View.GONE);
                    yRangeEnd.setVisibility(View.GONE);
                    yStartTitle.setVisibility(View.GONE);
                    yEndTitle.setVisibility(View.GONE);
                }
            });
            builder1.show();
        });
        xRangeStart.setClickable(true);
        xRangeStart.setOnClickListener(v -> {
            if (xLabelTypeStr.equals("DATE")) {
                final Calendar newCalendar = Calendar.getInstance();
                final DatePickerDialog StartTime = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMM-YYYY");
                    xStartDate = newDate.getTime();
                    String dateString = formatter1.format(newDate.getTime());
                    xRangeStart.setText(dateString);
                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                StartTime.show();
            } else {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                builder2.setTitle("Value");


                final EditText input = new EditText(context);

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

                builder2.setView(input);


                builder2.setPositiveButton("OK", (dialog, which) -> {
                    String tempText = input.getText().toString();
                    if (!tempText.equals("") && !tempText.equals("-")) {
                        xStartNum = Float.parseFloat(tempText);
                        xRangeStart.setText(String.valueOf(xStartNum));
                    }

                });
                builder2.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                builder2.show();


            }
        });
        xRangeEnd.setClickable(true);
        xRangeEnd.setOnClickListener(v -> {
            if (xLabelTypeStr.equals("DATE")) {
                final Calendar newCalendar = Calendar.getInstance();
                final DatePickerDialog StartTime = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    SimpleDateFormat formatter12 = new SimpleDateFormat("dd MMM-YYYY");
                    String dateString = formatter12.format(newDate.getTime());
                    xRangeEnd.setText(dateString);
                    xEndDate = newDate.getTime();


                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                StartTime.show();
            } else {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                builder2.setTitle("Value");


                final EditText input = new EditText(context);

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

                builder2.setView(input);


                builder2.setPositiveButton("OK", (dialog, which) -> {
                    String tempText = input.getText().toString();
                    if (!tempText.equals("") && !tempText.equals("-")) {
                        xEndnum = Float.parseFloat(tempText);
                        xRangeEnd.setText(String.valueOf(xEndnum));
                    }

                });
                builder2.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


                builder2.show();


            }
        });
        yRangeStart.setClickable(true);
        yRangeStart.setOnClickListener(v -> {


            AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
            builder2.setTitle("Value");


            final EditText input = new EditText(context);

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

            builder2.setView(input);


            builder2.setPositiveButton("OK", (dialog, which) -> {
                String tempText = input.getText().toString();
                if (!tempText.equals("") && !tempText.equals("-")) {
                    ystartvalue = Float.parseFloat(tempText);
                    yRangeStart.setText(String.valueOf(ystartvalue));
                }
            });
            builder2.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder2.show();
        });
        yRangeEnd.setClickable(true);
        yRangeEnd.setOnClickListener(v -> {


            AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
            builder2.setTitle("Value");


            final EditText input = new EditText(context);

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

            builder2.setView(input);


            builder2.setPositiveButton("OK", (dialog, which) -> {
                String tempText = input.getText().toString();
                if (!tempText.equals("") && !tempText.equals("-")) {
                    yendvalue = Float.parseFloat(tempText);
                    yRangeEnd.setText(String.valueOf(yendvalue));
                }
            });
            builder2.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder2.show();
        });
        if (xLabelTypeStr.equals("NUMERIC")) {
            xRangeStart.setText(String.valueOf(xStartNum));
            xRangeEnd.setText(String.valueOf(xEndnum));
        } else {
            xRangeStart.setText(formatter.format(xStartDate));
            xRangeEnd.setText(formatter.format(xEndDate));
        }
        yRangeStart.setText(String.valueOf(ystartvalue));
        yRangeEnd.setText(String.valueOf(yendvalue));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView highlightTitle = new TextView(context);
        highlightTitle.setText("Highlight Type:");
        linearLayout.addView(highlightTitle);
        linearLayout.addView(highlightType);
        TextView xStartTitle = new TextView(context);
        xStartTitle.setText("X Starting Value:");
        linearLayout.addView(xStartTitle);
        linearLayout.addView(xRangeStart);
        TextView xEndTitle = new TextView(context);
        xEndTitle.setText("X Ending Value:");
        linearLayout.addView(xEndTitle);
        linearLayout.addView(xRangeEnd);

        yStartTitle.setText("T Starting Value:");
        linearLayout.addView(yStartTitle);
        linearLayout.addView(yRangeStart);

        yEndTitle.setText("Y Ending Value:");
        linearLayout.addView(yEndTitle);
        linearLayout.addView(yRangeEnd);

        builder.setView(linearLayout);
        List<List<Float>> convertedList = DataHelper.convertStackedData(values);


        builder.setPositiveButton("OK", (dialog, which) -> {
            if (DataHighlighter.this.highlightType.equals("Custom Values")) {
                if (xLabelTypeStr.equals("DATE")) {

                    SimpleDateFormat formatter3 = new SimpleDateFormat("dd MMM-YYYY");


                    for (int i = 0; i < convertedList.size(); i++) {


                        List<Float> tempList = convertedList.get(i);
                        for (int j = 0; j < tempList.size(); j++) {
                            if ((arrayOfDates.get(i).after(xStartDate) || formatter3.format(arrayOfDates.get(i)).equals(formatter3.format(xStartDate))) && (arrayOfDates.get(i).before(xEndDate) || arrayOfDates.get(i).equals(xEndDate))) {
                                float tempValue = tempList.get(j);
                                int after = Float.compare(tempValue, ystartvalue);
                                int before = Float.compare(tempValue, yendvalue);
                                if (after >= 0 && before <= 0) {
                                    Highlight highlightValue = new Highlight(i, 0, j);
                                    highlightList.add(highlightValue);
                                }
                            }
                        }

                    }


                } else if (xLabelTypeStr.equals("NUMERIC")) {


                    for (int i = 0; i < convertedList.size(); i++) {
                        List<Float> tempList = convertedList.get(i);


                        for (int j = 0; j < tempList.size(); j++) {
                            if (Float.parseFloat(labels.get(i)) >= xStartNum && Float.parseFloat(labels.get(i)) <= xEndnum) {
                                float temp_value = tempList.get(j);

                                int after = Float.compare(temp_value, ystartvalue);
                                int before = Float.compare(temp_value, yendvalue);

                                if (after >= 0 && before <= 0) {
                                    Highlight highlightValue = new Highlight(i, 0, j);
                                    highlightList.add(highlightValue);
                                }
                            }
                        }

                    }

                }
            } else if (DataHighlighter.this.highlightType.equals("Increasing Values")) {
                float prevValue = 0f;
                if (xLabelTypeStr.equals("DATE")) {

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter3 = new SimpleDateFormat("dd MMM-YYYY");


                    for (int i = 0; i < convertedList.size(); i++) {


                        List<Float> tempList = convertedList.get(i);
                        for (int j = 0; j < tempList.size(); j++) {
                            float tempValue = tempList.get(j);
                            if (j == 0) {
                                prevValue = tempValue;
                            }
                            if ((arrayOfDates.get(i).after(xStartDate) || formatter3.format(arrayOfDates.get(i)).equals(formatter3.format(xStartDate))) && (arrayOfDates.get(i).before(xEndDate) || arrayOfDates.get(i).equals(xEndDate))) {

                                int after = Float.compare(tempValue, prevValue);

                                if (after > 0) {
                                    Highlight highlightValue = new Highlight(i, 0, j);
                                    highlightList.add(highlightValue);
                                }
                                prevValue = tempValue;
                            }

                        }

                    }


                } else if (xLabelTypeStr.equals("NUMERIC")) {


                    for (int i = 0; i < convertedList.size(); i++) {
                        List<Float> tempList = convertedList.get(i);


                        for (int j = 0; j < tempList.size(); j++) {
                            if (Float.parseFloat(labels.get(i)) >= xStartNum && Float.parseFloat(labels.get(i)) <= xEndnum) {
                                float tempValue = tempList.get(j);
                                if (j == 0) {
                                    prevValue = tempValue;
                                }
                                int after = Float.compare(tempValue, prevValue);


                                if (after > 0) {
                                    Highlight highlightValue = new Highlight(i, 0, j);
                                    highlightList.add(highlightValue);
                                }
                                prevValue = tempValue;
                            }
                        }

                    }

                }
            } else {
                float prevValue = 0f;
                if (xLabelTypeStr.equals("DATE")) {

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter3 = new SimpleDateFormat("dd MMM-YYYY");


                    for (int i = 0; i < convertedList.size(); i++) {


                        List<Float> tempList = convertedList.get(i);
                        for (int j = 0; j < tempList.size(); j++) {
                            float tempValue = tempList.get(j);
                            if (j == 0) {
                                prevValue = tempValue;
                            }
                            if ((arrayOfDates.get(i).after(xStartDate) || formatter3.format(arrayOfDates.get(i)).equals(formatter3.format(xStartDate))) && (arrayOfDates.get(i).before(xEndDate) || arrayOfDates.get(i).equals(xEndDate))) {

                                int after = Float.compare(tempValue, prevValue);

                                if (after < 0) {
                                    Highlight highlightValue = new Highlight(i, 0, j);
                                    highlightList.add(highlightValue);
                                }
                                prevValue = tempValue;
                            }

                        }

                    }


                } else if (xLabelTypeStr.equals("NUMERIC")) {


                    for (int i = 0; i < convertedList.size(); i++) {
                        List<Float> tempList = convertedList.get(i);


                        for (int j = 0; j < tempList.size(); j++) {
                            if (Float.parseFloat(labels.get(i)) >= xStartNum && Float.parseFloat(labels.get(i)) <= xEndnum) {
                                float tempValue = tempList.get(j);
                                if (j == 0) {
                                    prevValue = tempValue;
                                }
                                int after = Float.compare(tempValue, prevValue);


                                if (after < 0) {
                                    Highlight highlightValue = new Highlight(i, 0, j);
                                    highlightList.add(highlightValue);
                                }
                                prevValue = tempValue;
                            }
                        }

                    }

                }
            }
            Highlight[] highlightedValues = highlightList.toArray(new Highlight[0]);
            chart.highlightValues(highlightedValues);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


        builder.show();

    }
}
