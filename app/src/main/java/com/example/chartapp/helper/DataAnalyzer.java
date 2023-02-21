package com.example.chartapp.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataAnalyzer {
    private DataAnalyzer() {
    }

    @SuppressLint("SetTextI18n")
    public static void openAnalysisTool(Context context, List<String> datasets, List<List<Float>> values) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Data Analysis");
        final TextView datasetsText = new TextView(context);
        datasetsText.setText("Datasets:");
        datasetsText.setTypeface(null, Typeface.BOLD);
        final TextView functionsText = new TextView(context);
        functionsText.setText("Function:");
        functionsText.setTypeface(null, Typeface.BOLD);
        final TextView typeText = new TextView(context);
        typeText.setText("Type:");
        typeText.setTypeface(null, Typeface.BOLD);
        final TextView datasetstv = new TextView(context);
        final TextView functions = new TextView(context);
        final TextView result = new TextView(context);
        final TextView type = new TextView(context);
        final Button computebutton = new Button(context);
        computebutton.setBackgroundColor(Color.parseColor("#000000"));
        computebutton.setText("Compute");
        computebutton.setLayoutParams(new LinearLayout.LayoutParams(250, 120));
        computebutton.setTextColor(Color.parseColor("#FFFFFF"));
        computebutton.setOnClickListener(view -> {
            int selectionIndex = datasets.indexOf(datasetstv.getText().toString());
            String selectionFunction = functions.getText().toString();
            List<Float> selectedValues = values.get(selectionIndex);

            switch (selectionFunction) {
                case "Minimum": {
                    result.setText("Result: " + Collections.min(selectedValues));
                    break;
                }
                case "Maximum": {
                    result.setText("Result: " + Collections.max(selectedValues));
                    break;
                }
                case "Ramge": {
                    float max_temp = Collections.max(selectedValues);
                    float min_temp = Collections.min(selectedValues);
                    float range_temp = max_temp - min_temp;
                    result.setText("Result: " + range_temp);
                    break;
                }
                case "Count": {
                    result.setText("Result: " + selectedValues.size());
                    break;
                }
                case "Sum": {
                    float sum = 0f;
                    for (Float value : selectedValues) {
                        sum = sum + value;
                    }
                    result.setText("Result: " + sum);
                    break;
                }
                case "Mean": {
                    float sum = 0f;
                    int elements_number = selectedValues.size();
                    for (Float value : selectedValues) {
                        sum = sum + value;
                    }
                    result.setText("Results: " + sum / elements_number);
                    break;
                }
                case "Median": {
                    if (selectedValues.size() <= 2) {
                        if (selectedValues.size() == 2) {
                            float sum = selectedValues.get(0) + selectedValues.get(1);
                            result.setText("Result: " + sum / 2);
                        } else if (selectedValues.size() == 1) {
                            result.setText("Result: " + selectedValues.get(0));
                        }
                    } else {
                        Collections.sort(selectedValues);
                        if (selectedValues.size() % 2 == 0) {
                            int p1 = selectedValues.size() / 2;
                            int p2 = p1 + 1;
                            result.setText("Result: " + (p1 + p2) / 2);
                        } else {
                            float p = selectedValues.size() / 2f;
                            int position = (int) Math.ceil(p);
                            result.setText("Result: " + selectedValues.get(position));
                        }
                    }
                    break;

                }
                case "Mode": {
                    final List<Float> modes = new ArrayList<>();
                    final Map<Float, Float> countMap = new HashMap<>();

                    float max = -1;

                    for (final Float n : selectedValues) {
                        float count;

                        if (countMap.containsKey(n)) {
                            count = countMap.get(n) + 1;
                        } else {
                            count = 1;
                        }

                        countMap.put(n, count);

                        if (count > max) {
                            max = count;
                        }
                    }

                    for (final Map.Entry<Float, Float> tuple : countMap.entrySet()) {
                        if (tuple.getValue() == max) {
                            modes.add(tuple.getKey());
                        }
                    }
                    StringBuilder resultsString = new StringBuilder("Results: ");
                    for (Float value : modes) {
                        resultsString.append(value).append(" ");
                    }
                    result.setText("Result: " + resultsString);
                    break;
                }
                case "Standard Deviation": {
                    float sum = 0f, standardDeviation = 0f;
                    int length = selectedValues.size();

                    for (float num : selectedValues) {
                        sum += num;
                    }

                    float mean = sum / length;

                    for (float num : selectedValues) {
                        standardDeviation += Math.pow(num - mean, 2);
                    }
                    if (type.getText().toString().equals("Sample")) {
                        result.setText("Result: " + Math.sqrt(standardDeviation / length));
                    } else {
                        result.setText("Result: " + Math.sqrt(standardDeviation / (length - 1)));
                    }
                    break;
                }
                case "Variance": {
                    float sum = 0f, standardDeviation = 0f;
                    int length = selectedValues.size();

                    for (float num : selectedValues) {
                        sum += num;
                    }

                    float mean = sum / length;

                    for (float num : selectedValues) {
                        standardDeviation += Math.pow(num - mean, 2);
                    }
                    if (type.getText().toString().equals("Sample")) {
                        result.setText("Result: " + Math.sqrt(Math.sqrt(standardDeviation / length)));
                    } else {
                        result.setText("Result: " + Math.sqrt(Math.sqrt(standardDeviation / (length - 1))));
                    }
                    break;
                }
                case "Mid Range": {
                    float min = Collections.min(selectedValues);
                    float max = Collections.max(selectedValues);
                    result.setText("Result: " + (min + max) / 2);
                    break;

                }
                case "Quartiles": {
                    if (selectedValues.size() >= 4) {
                        float[] ans = new float[3];

                        for (int quartileType = 1; quartileType < 4; quartileType++) {
                            float length = selectedValues.size() + 1;
                            float quartile;
                            float newArraySize = (length * ((float) (quartileType) * 25 / 100)) - 1;
                            Collections.sort(selectedValues);
                            if (newArraySize % 1 == 0) {
                                quartile = selectedValues.get((int) newArraySize);
                            } else {
                                int newArraySize1 = (int) (newArraySize);
                                quartile = (selectedValues.get(newArraySize1) + selectedValues.get(newArraySize1 + 1)) / 2;
                            }
                            ans[quartileType - 1] = quartile;
                        }
                        result.setText("Results: " + "Q1=" + ans[0] + " Q2= " + ans[1] + "Q3= " + ans[2]);
                    } else {
                        float sum = 0f;
                        for (float value : selectedValues) {
                            sum = sum + value;
                        }
                        result.setText("Result: " + "Q1= unknown Q2=" + sum / selectedValues.size() + " Q3=unknown");
                    }
                    break;
                }
                case "IQR": {
                    if (selectedValues.size() >= 4) {
                        float[] ans = new float[3];

                        for (int quartileType = 1; quartileType < 4; quartileType++) {
                            float length = selectedValues.size() + 1;
                            float quartile;
                            float newArraySize = (length * ((float) (quartileType) * 25 / 100)) - 1;
                            Collections.sort(selectedValues);
                            if (newArraySize % 1 == 0) {
                                quartile = selectedValues.get((int) newArraySize);
                            } else {
                                int newArraySize1 = (int) (newArraySize);
                                quartile = (selectedValues.get(newArraySize1) + selectedValues.get(newArraySize1 + 1)) / 2;
                            }
                            ans[quartileType - 1] = quartile;
                        }
                        result.setText("Result: " + (ans[2] - ans[0]));
                    } else {
                        result.setText("Result: unknown");
                    }
                    break;
                }
                case "Outliers": {
                    if (selectedValues.size() >= 4) {
                        List<Float> outliers = new ArrayList<>();

                        float[] ans = new float[3];

                        for (int quartileType = 1; quartileType < 4; quartileType++) {
                            float length = selectedValues.size() + 1;
                            float quartile;
                            float newArraySize = (length * ((float) (quartileType) * 25 / 100)) - 1;
                            Collections.sort(selectedValues);
                            if (newArraySize % 1 == 0) {
                                quartile = selectedValues.get((int) newArraySize);
                            } else {
                                int newArraySize1 = (int) (newArraySize);
                                quartile = (selectedValues.get(newArraySize1) + selectedValues.get(newArraySize1 + 1)) / 2;
                            }
                            ans[quartileType - 1] = quartile;
                        }
                        float IQR = ans[2] - ans[0];
                        float upper = ans[2] + 1.5f * IQR;
                        float lower = ans[0] - 1.5f * IQR;
                        for (float value : selectedValues) {
                            if (value > upper || value < lower)
                                outliers.add(value);
                        }
                        if (outliers.size() == 0) {
                            result.setText("Result : no outliers");
                        } else {
                            StringBuilder resultString = new StringBuilder("Result: ");
                            for (float value : outliers) {
                                resultString.append(value).append(" ");
                            }
                        }
                    } else {
                        result.setText("Result: unknown");
                    }
                    break;
                }
                case "Sum of Squares": {
                    float SS = 0;
                    float sum = 0;
                    for (float value : selectedValues) {
                        sum = sum + value;
                    }
                    float mean = sum / selectedValues.size();
                    for (float value : selectedValues) {
                        SS = SS + ((value - mean) * (value - mean));
                    }
                    result.setText("Result: " + SS);
                    break;
                }
                case "Mean Absolute Derivation": {
                    float MAD = 0;
                    float sum = 0;
                    for (float value : selectedValues) {
                        sum = sum + value;
                    }
                    float mean = sum / selectedValues.size();
                    for (float value : selectedValues) {
                        MAD = MAD + Math.abs(value - mean);
                    }
                    result.setText("Result: " + MAD / selectedValues.size());
                    break;
                }
                case "Root Mean Square": {
                    float sum = 0;
                    for (float value : selectedValues) {
                        sum = sum + value;
                    }
                    float sumSquare = sum * sum;
                    float resultTemp = sumSquare / selectedValues.size();
                    result.setText("Result: " + Math.sqrt(resultTemp));
                    break;
                }
                case "Standand Error of the Mean": {
                    float sum = 0f, standardDeviation = 0f;
                    int length = selectedValues.size();

                    for (float num : selectedValues) {
                        sum += num;
                    }

                    float mean = sum / length;
                    double sqrt_val = Math.sqrt(selectedValues.size());
                    for (float num : selectedValues) {
                        standardDeviation += Math.pow(num - mean, 2);
                    }
                    if (type.getText().toString().equals("Sample")) {
                        double std = Math.sqrt(standardDeviation / length);
                        result.setText("Result: " + std / sqrt_val);
                    } else {
                        double std = Math.sqrt(standardDeviation / (length - 1));
                        result.setText("Result: " + std / sqrt_val);
                    }

                    break;
                }
                case "Coefficient of Variation": {
                    float sum = 0f, standardDeviation = 0f;
                    int length = selectedValues.size();

                    for (float num : selectedValues) {
                        sum += num;
                    }

                    float mean = sum / length;

                    for (float num : selectedValues) {
                        standardDeviation += Math.pow(num - mean, 2);
                    }

                    if (type.getText().toString().equals("Sample")) {
                        double std = Math.sqrt(standardDeviation / length);
                        result.setText("Result+ " + std / mean);
                    } else {
                        double std = Math.sqrt(standardDeviation / (length - 1));
                        result.setText("Result: " + std / mean);
                    }
                    break;
                }
                case "Relative Standard Deviation": {
                    float sum = 0f, standardDeviation = 0f;
                    int length = selectedValues.size();

                    for (float num : selectedValues) {
                        sum += num;
                    }

                    float mean = sum / length;

                    for (float num : selectedValues) {
                        standardDeviation += Math.pow(num - mean, 2);
                    }

                    if (type.getText().toString().equals("Sample")) {
                        double std = Math.sqrt(standardDeviation / length);
                        result.setText("Result+ " + (std * 100) / mean + "%");
                    } else {
                        double std = Math.sqrt(standardDeviation / (length - 1));
                        result.setText("Result: " + (std * 100) / mean + "%");
                    }
                    break;
                }
            }
        });

        type.setText("Sample");
        datasetstv.setText(datasets.get(0));
        functions.setText("Minimum");
        datasetstv.setClickable(true);
        datasetstv.setOnClickListener(v -> {
            String[] datasetsArray = datasets.toArray(new String[0]);

            AlertDialog.Builder builder13 = new AlertDialog.Builder(context);
            builder13.setTitle("Pick a dataset");
            builder13.setItems(datasetsArray, (dialog, which) -> datasetstv.setText(datasetsArray[which]));
            builder13.show();
        });
        type.setClickable(true);
        type.setOnClickListener(v -> {
            String[] types = {"Population", "Sample"};

            AlertDialog.Builder builder12 = new AlertDialog.Builder(context);
            builder12.setTitle("Pick a type");
            builder12.setItems(types, (dialog, which) -> type.setText(types[which]));
            builder12.show();
        });
        functions.setClickable(true);
        functions.setOnClickListener(v -> {
            String[] functiosnArray = {"Minimum", "Maximum", "Range", "Count", "Sum", "Mean", "Median", "Mode", "Standard Deviation", "Variance", "Mid Range", "Quartiles", "IQR", "Outliers", "Sum of Squares", "Mean Absolute Derivation", "Root Mean Square", "Standand Error of the Mean", "Coefficient of Variation", "Relative Standard Deviation"};

            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setTitle("Pick a function");
            builder1.setItems(functiosnArray, (dialog, which) -> functions.setText(functiosnArray[which]));
            builder1.show();
        });
        result.setTypeface(null, Typeface.BOLD);
        result.setText("Result:");
        LinearLayout lay = new LinearLayout(context);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.addView(datasetsText);
        lay.addView(datasetstv);
        lay.addView(functionsText);
        lay.addView(functions);
        lay.addView(typeText);
        lay.addView(type);
        lay.addView(result);
        lay.addView(computebutton);
        builder.setView(lay);


        builder.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.cancel());
        builder.show();
    }

}
