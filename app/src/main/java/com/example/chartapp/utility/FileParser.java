package com.example.chartapp.utility;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.chartapp.activity.GenerateChartActivity;
import com.example.chartapp.R;
import com.example.chartapp.data.DataModel;
import com.example.chartapp.helper.DataHelper;
import com.example.chartapp.helper.DataValidator;
import com.google.gson.Gson;
import com.opencsv.CSVReader;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FileParser {
    private int position = 0;
    private String chartType;
    private boolean checked = false;
    private int activesheet = 0;
    private final List<String> datasets = new ArrayList<>();
    private final Map<String, List<Float>> values = new HashMap<>();
    private int firstRow = 0;
    private int firstColumn = 0;

    public FileParser(String chartType) {
        this.chartType = chartType;
    }

    public void parseFile(String filepath, boolean tempfile, Context context, Uri currFileURI) {
        DataModel mydb = DataModel.getInstance(context);
        File getfile = new File(filepath);
        int fileSize = Integer.parseInt(String.valueOf(getfile.length() / 1024));
        if (fileSize <= 1000) {
            String extension = filepath.substring(filepath.lastIndexOf(".") + 1);
            final TextView chartname = new TextView(context);
            chartname.setText("Name:");
            final EditText input = new EditText(context);
            final ImageButton leftButton = new ImageButton(context);
            final ImageView image = new ImageView(context);
            final TextView chartTypeTv = new TextView(context);
            chartTypeTv.setText(chartType);
            image.setImageResource(R.mipmap.linechart);

            leftButton.setImageResource(R.mipmap.leftarrow);

            leftButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
            leftButton.setClickable(true);
            leftButton.setOnClickListener(v -> {
                if (position == 1) {
                    position = 13;
                } else {
                    position = position - 1;
                }
                switch (position) {
                    case 1: {
                        chartType = "Line Chart";
                        image.setImageResource(R.mipmap.linechart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 2: {
                        chartType = "Spline Chart";
                        image.setImageResource(R.mipmap.splinechart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 3: {
                        chartType = "Area Chart";
                        image.setImageResource(R.mipmap.arealinechart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 4: {
                        chartType = "Area Spline Chart";
                        image.setImageResource(R.mipmap.areasplinechart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 5: {
                        chartType = "Bubble Chart";
                        image.setImageResource(R.mipmap.bubblechart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 6: {
                        chartType = "Scatterplot Chart";
                        image.setImageResource(R.mipmap.scatterchart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 7: {
                        chartType = "Pie Chart";
                        image.setImageResource(R.mipmap.piechart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 8: {
                        chartType = "Radar Chart";
                        image.setImageResource(R.mipmap.radarchart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 9: {
                        chartType = "Bar Chart";
                        image.setImageResource(R.mipmap.barchart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 10: {
                        chartType = "Horizontal Bar Chart";
                        image.setImageResource(R.mipmap.horizontalbarchart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 11: {
                        chartType = "Stacked Bar Chart";
                        image.setImageResource(R.mipmap.stackedbarchart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 12: {
                        chartType = "Horizontal Stacked Bar Chart";
                        image.setImageResource(R.mipmap.horizontalstackedbarchart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 13: {
                        chartType = "Candlestick Chart";
                        image.setImageResource(R.mipmap.candlestickchart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                }
            });
            final ImageButton buttonRight = new ImageButton(context);
            buttonRight.setImageResource(R.mipmap.rightarrow);
            buttonRight.setBackgroundColor(Color.parseColor("#FFFFFF"));
            buttonRight.setClickable(true);
            buttonRight.setOnClickListener(v -> {
                if (position == 13) {
                    position = 1;
                } else {
                    position = position + 1;
                }
                switch (position) {
                    case 1: {
                        chartType = "Line Chart";
                        image.setImageResource(R.mipmap.linechart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 2: {
                        chartType = "Spline Chart";
                        image.setImageResource(R.mipmap.splinechart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 3: {
                        chartType = "Area Chart";
                        image.setImageResource(R.mipmap.arealinechart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 4: {
                        chartType = "Area Spline Chart";
                        image.setImageResource(R.mipmap.areasplinechart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 5: {
                        chartType = "Bubble Chart";
                        image.setImageResource(R.mipmap.bubblechart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 6: {
                        chartType = "Scatterplot Chart";
                        image.setImageResource(R.mipmap.scatterchart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 7: {
                        chartType = "Pie Chart";
                        image.setImageResource(R.mipmap.piechart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 8: {
                        chartType = "Radar Chart";
                        image.setImageResource(R.mipmap.radarchart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 9: {
                        chartType = "Bar Chart";
                        image.setImageResource(R.mipmap.barchart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 10: {
                        chartType = "Horizontal Bar Chart";
                        image.setImageResource(R.mipmap.horizontalbarchart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 11: {
                        chartType = "Stacked Bar Chart";
                        image.setImageResource(R.mipmap.stackedbarchart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 12: {
                        chartType = "Horizontal Stacked Bar Chart";
                        image.setImageResource(R.mipmap.horizontalstackedbarchart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                    case 13: {
                        chartType = "Candlestick Chart";
                        image.setImageResource(R.mipmap.candlestickchart);
                        chartTypeTv.setText(chartType);
                        break;
                    }
                }
            });

            final TextView spreadsheetTitle = new TextView(context);
            final TextView spreadsheets = new TextView(context);
            final CheckBox datasetsCheck = new CheckBox(context);
            switch (extension) {
                case "xls":
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("New Chart");

                        spreadsheetTitle.setText("Spreadsheet");
                        datasetsCheck.setText("Use first row for dataset names");
                        datasetsCheck.setChecked(checked);
                        List<String> sheetNames = new ArrayList<>();
                        firstRow = 0;
                        firstColumn = 0;

                        InputStream myInput = context.getContentResolver().openInputStream(currFileURI);

                        activesheet = 0;
                        POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
                        HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

                        for (int i = 0; i < myWorkBook.getNumberOfSheets(); i++) {
                            sheetNames.add(myWorkBook.getSheetName(i));
                        }
                        datasetsCheck.setOnCheckedChangeListener((buttonView, isChecked) -> checked = isChecked);
                        spreadsheets.setTypeface(null, Typeface.BOLD);
                        spreadsheets.setText(sheetNames.get(activesheet));
                        spreadsheets.setClickable(true);
                        spreadsheets.setOnClickListener(v -> {
                            List<String> tempList = new ArrayList<>();
                            for (int i = 0; i < sheetNames.size(); i++) {
                                tempList.add(sheetNames.get(i));
                            }
                            tempList.add("Cancel");
                            String[] sheetsArray = tempList.toArray(new String[0]);

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                            builder1.setTitle("Spreadsheets");
                            builder1.setItems(sheetsArray, (dialog, which) -> {

                                if (!sheetsArray[which].equals("Cancel")) {
                                    spreadsheets.setText(sheetsArray[which]);
                                    activesheet = which;
                                }

                            });
                            builder1.show();
                        });

                        LinearLayout newLay = new LinearLayout(context);
                        newLay.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout row = new LinearLayout(context);
                        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        newLay.addView(chartname);
                        newLay.addView(input);
                        row.addView(leftButton);
                        row.addView(image);
                        row.addView(buttonRight);
                        newLay.addView(row);
                        newLay.addView(chartTypeTv);
                        newLay.addView(spreadsheetTitle);
                        newLay.addView(spreadsheets);
                        newLay.addView(datasetsCheck);
                        builder.setView(newLay);

                        builder.setPositiveButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
                            boolean length_set = false;
                            int length = 0;
                            HSSFSheet mySheet = myWorkBook.getSheetAt(activesheet);
                            HSSFRow myRow;
                            myRow = (HSSFRow) mySheet.getRow(0);
                            Iterator<Cell> iter = myRow.cellIterator();
                            int lastColumn = myRow.getLastCellNum();
                            int lastrow = mySheet.getLastRowNum();

                            if (checked) {
                                firstRow = 1;
                                while (iter.hasNext()) {
                                    HSSFCell myCell = (HSSFCell) iter.next();


                                    datasets.add(myCell.toString());


                                }
                            } else {

                                for (int i = firstColumn; i < lastColumn; i++) {

                                    String tempDataSet = "Dataset" + i;
                                    datasets.add(tempDataSet);


                                }

                            }


                            for (int i = firstColumn; i < lastColumn; i++) {
                                List<Float> tempValues = new ArrayList<>();
                                for (int j = firstRow; j < lastrow; j++) {
                                    myRow = mySheet.getRow(j);
                                    HSSFCell myCell = (HSSFCell) myRow.getCell(i);


                                    if (DataValidator.isNumeric(myCell.toString())) {
                                        float tempValue = DataHelper.toFloat(Double.parseDouble(myCell.toString()));
                                        tempValues.add(tempValue);
                                    }
                                }
                                if (tempValues.size() > 0) {
                                    if (!length_set)
                                        length = tempValues.size();
                                    if (tempValues.size() == length)
                                        values.put(datasets.get(i), tempValues);
                                }

                            }
                            if (values.size() > 0) {

                                Gson gson = new Gson();
                                String valuesString = gson.toJson(values);
                                Intent intent = new Intent(new ContextWrapper(context).getBaseContext(), GenerateChartActivity.class);
                                intent.putExtra("values", valuesString);
                                int entriesnumber = mydb.numberOfRows();
                                if (entriesnumber != 0) {
                                    List<ArrayList<String>> dbentries = mydb.getAllCharts();
                                    int newId = Integer.parseInt(dbentries.get(dbentries.size() - 1).get(0)) + 1;
                                    String nameOfChart = input.getText().toString();
                                    if (nameOfChart.equals("")) {
                                        String newName = "Chart" + newId;
                                        intent.putExtra("CHART_NAME", newName);
                                    } else {
                                        intent.putExtra("CHART_NAME", nameOfChart);
                                    }

                                    intent.putExtra("SELECTED_ID", newId);
                                } else {
                                    String nameOfChart = input.getText().toString();
                                    if (nameOfChart.equals("")) {
                                        String newName = "NewChart";
                                        intent.putExtra("CHART_NAME", newName);
                                    } else {
                                        intent.putExtra("CHART_NAME", nameOfChart);
                                    }
                                }


                                intent.putExtra("LOADED", false);
                                intent.putExtra("CHART_TYPE", chartType);
                                if (tempfile) {
                                    File fdelete = new File(currFileURI.getPath());
                                    if (fdelete.exists()) {
                                        fdelete.delete();
                                    }
                                }
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, "The file contains invalid data!",
                                        Toast.LENGTH_LONG).show();
                                if (tempfile) {
                                    File fdelete = new File(currFileURI.getPath());
                                    if (fdelete.exists()) {
                                        fdelete.delete();
                                    }
                                }
                            }


                        });
                        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                        builder.show();


                    } catch (Exception e) {
                        Toast.makeText(context, "Unable to open the file",
                                Toast.LENGTH_LONG).show();
                        if (tempfile) {
                            File fdelete = new File(currFileURI.getPath());
                            if (fdelete.exists()) {
                                fdelete.delete();
                            }
                        }
                    }
                    break;
                case "xlsx":
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("New Chart");

                        spreadsheetTitle.setText("Spreadsheet");
                        datasetsCheck.setText("Use first row for dataset names");
                        datasetsCheck.setChecked(checked);
                        List<String> sheetNames = new ArrayList<>();
                        firstRow = 0;
                        firstColumn = 0;

                        InputStream myInput = context.getContentResolver().openInputStream(currFileURI);

                        activesheet = 0;

                        XSSFWorkbook myWorkBook = new XSSFWorkbook(myInput);

                        for (int i = 0; i < myWorkBook.getNumberOfSheets(); i++) {
                            sheetNames.add(myWorkBook.getSheetName(i));
                        }
                        datasetsCheck.setOnCheckedChangeListener((buttonView, isChecked) -> checked = isChecked);
                        spreadsheets.setTypeface(null, Typeface.BOLD);
                        spreadsheets.setText(sheetNames.get(activesheet));
                        spreadsheets.setClickable(true);
                        spreadsheets.setOnClickListener(v -> {
                            List<String> tempList = new ArrayList<>();
                            for (int i = 0; i < sheetNames.size(); i++) {
                                tempList.add(sheetNames.get(i));
                            }
                            tempList.add("Cancel");
                            String[] sheetsArray = tempList.toArray(new String[0]);

                            AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                            builder2.setTitle("Spreadsheets");
                            builder2.setItems(sheetsArray, (dialog, which) -> {

                                if (!sheetsArray[which].equals("Cancel")) {


                                    spreadsheets.setText(sheetsArray[which]);
                                    activesheet = which;

                                }
                            });
                            builder2.show();
                        });


                        LinearLayout newLay = new LinearLayout(context);
                        newLay.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout row = new LinearLayout(context);
                        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        newLay.addView(chartname);
                        newLay.addView(input);
                        row.addView(leftButton);
                        row.addView(image);
                        row.addView(buttonRight);
                        newLay.addView(row);
                        newLay.addView(chartTypeTv);
                        newLay.addView(spreadsheetTitle);
                        newLay.addView(spreadsheets);
                        newLay.addView(datasetsCheck);
                        builder.setView(newLay);

                        builder.setPositiveButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
                            boolean lengthSet = false;
                            int length = 0;
                            XSSFSheet mySheet = myWorkBook.getSheetAt(activesheet);
                            mySheet.rowIterator();
                            XSSFRow myRow;
                            myRow = (XSSFRow) mySheet.getRow(0);
                            Iterator<Cell> iter = myRow.cellIterator();
                            if (checked) {
                                firstRow = 1;
                                while (iter.hasNext()) {
                                    XSSFCell myCell = (XSSFCell) iter.next();


                                    datasets.add(myCell.toString());


                                }
                            } else {
                                int tempNumber = 0;
                                while (iter.hasNext()) {
                                    iter.next();

                                    String tempData = "Dataset" + tempNumber;
                                    datasets.add(tempData);
                                    tempNumber++;

                                }

                            }


                            int lastcollumn = myRow.getLastCellNum();
                            int lastrow = mySheet.getLastRowNum();

                            for (int i = firstColumn; i < lastcollumn; i++) {
                                List<Float> tempValues = new ArrayList<>();
                                mySheet.getRow(i);
                                for (int j = firstRow; j < lastrow; j++) {
                                    myRow = mySheet.getRow(j);
                                    XSSFCell myCell = (XSSFCell) myRow.getCell(i);


                                    if (DataValidator.isNumeric(myCell.toString())) {
                                        float tempValue = DataHelper.toFloat(Double.parseDouble(myCell.toString()));
                                        tempValues.add(tempValue);
                                    }
                                }
                                if (tempValues.size() > 0) {
                                    if (!lengthSet)
                                        length = tempValues.size();
                                    if (tempValues.size() == length)
                                        values.put(datasets.get(i), tempValues);
                                }

                            }
                            if (values.size() > 0) {

                                Gson gson = new Gson();
                                String valuesString = gson.toJson(values);
                                Intent intent = new Intent(new ContextWrapper(context).getBaseContext(), GenerateChartActivity.class);
                                intent.putExtra("values", valuesString);
                                int entriesnumber = mydb.numberOfRows();
                                if (entriesnumber != 0) {
                                    List<ArrayList<String>> dbentries = mydb.getAllCharts();
                                    int newId = Integer.parseInt(dbentries.get(dbentries.size() - 1).get(0)) + 1;
                                    String nameOfChart = input.getText().toString();
                                    if (nameOfChart.equals("")) {
                                        String newName = "Chart" + newId;
                                        intent.putExtra("CHART_NAME", newName);
                                    } else {
                                        intent.putExtra("CHART_NAME", nameOfChart);
                                    }

                                    intent.putExtra("SELECTED_ID", newId);
                                } else {
                                    String nameOfChart = input.getText().toString();
                                    if (nameOfChart.equals("")) {
                                        String newName = "NewChart";
                                        intent.putExtra("CHART_NAME", newName);
                                    } else {
                                        intent.putExtra("CHART_NAME", nameOfChart);
                                    }
                                }


                                intent.putExtra("LOADED", false);
                                intent.putExtra("CHART_TYPE", chartType);
                                if (tempfile) {
                                    File fdelete = new File(currFileURI.getPath());
                                    if (fdelete.exists()) {
                                        fdelete.delete();
                                    }
                                }
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, "The file contains invalid data!",
                                        Toast.LENGTH_LONG).show();
                                if (tempfile) {
                                    File fdelete = new File(currFileURI.getPath());
                                    if (fdelete.exists()) {
                                        fdelete.delete();
                                    }
                                }
                            }


                        });
                        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                        builder.show();


                    } catch (Exception e) {

                        Toast.makeText(context, "Unable to open the file ",

                                Toast.LENGTH_LONG).show();
                        if (tempfile) {
                            File fdelete = new File(currFileURI.getPath());
                            if (fdelete.exists()) {
                                fdelete.delete();
                            }
                        }
                        e.printStackTrace();
                    }
                    break;
                case "csv":
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("New Chart");
                        datasetsCheck.setText("Use first row for dataset names");
                        datasetsCheck.setChecked(checked);

                        firstRow = 0;
                        firstColumn = 0;

                        InputStream myInput = context.getContentResolver().openInputStream(currFileURI);
                        InputStreamReader file = new InputStreamReader(myInput);
                        activesheet = 0;

                        CSVReader reader = new CSVReader(file);
                        List<String[]> myEntries = reader.readAll();


                        datasetsCheck.setOnCheckedChangeListener((buttonView, isChecked) -> checked = isChecked);


                        LinearLayout newLay = new LinearLayout(context);
                        newLay.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout row = new LinearLayout(context);
                        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        newLay.addView(chartname);
                        newLay.addView(input);
                        row.addView(leftButton);
                        row.addView(image);
                        row.addView(buttonRight);
                        newLay.addView(row);
                        newLay.addView(chartTypeTv);
                        newLay.addView(datasetsCheck);
                        builder.setView(newLay);

                        builder.setPositiveButton("OK", (dialog, which) -> {
                            boolean lengthSet = false;
                            int length = 0;
                            if (checked) {
                                String[] firstLine = myEntries.get(0);
                                firstRow = 1;
                                datasets.addAll(Arrays.asList(firstLine));

                            } else {
                                int tempNumber = 0;
                                String[] firstLine = myEntries.get(0);
                                for (String ignored : firstLine) {


                                    String tempDataset = "Dataset" + tempNumber;
                                    datasets.add(tempDataset);
                                    tempNumber++;

                                }

                            }

                            int index = 0;
                            while (index < datasets.size()) {
                                List<Float> tempValues = new ArrayList<>();
                                for (int i = 0; i < myEntries.size(); i++) {


                                    String[] line = myEntries.get(i);


                                    if (DataValidator.isNumeric(line[index])) {
                                        float tempValue = DataHelper.toFloat(Double.parseDouble(line[index]));
                                        tempValues.add(tempValue);
                                    }

                                }
                                if (tempValues.size() > 0) {
                                    if (!lengthSet) {
                                        length = tempValues.size();
                                        lengthSet = true;
                                    }
                                    if (tempValues.size() == length)
                                        values.put(datasets.get(index), tempValues);
                                }


                                index++;
                            }

                            if (values.size() > 0) {

                                Gson gson = new Gson();
                                String valuesString = gson.toJson(values);
                                Intent intent = new Intent(new ContextWrapper(context).getBaseContext(), GenerateChartActivity.class);
                                intent.putExtra("values", valuesString);
                                int entriesnumber = mydb.numberOfRows();
                                if (entriesnumber != 0) {
                                    List<ArrayList<String>> dbentries = mydb.getAllCharts();
                                    int newId = Integer.parseInt(dbentries.get(dbentries.size() - 1).get(0)) + 1;
                                    String nameOfChart = input.getText().toString();
                                    if (nameOfChart.equals("")) {
                                        String newName = "Chart" + newId;
                                        intent.putExtra("CHART_NAME", newName);
                                    } else {
                                        intent.putExtra("CHART_NAME", nameOfChart);
                                    }

                                    intent.putExtra("SELECTED_ID", newId);
                                } else {
                                    String nameOfChart = input.getText().toString();
                                    if (nameOfChart.equals("")) {
                                        String newName = "NewChart";
                                        intent.putExtra("CHART_NAME", newName);
                                    } else {
                                        intent.putExtra("CHART_NAME", nameOfChart);
                                    }
                                }


                                intent.putExtra("LOADED", false);
                                intent.putExtra("CHART_TYPE", chartType);
                                if (tempfile) {
                                    File fdelete = new File(currFileURI.getPath());
                                    if (fdelete.exists()) {
                                        fdelete.delete();
                                    }
                                }
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, "The file contains invalid data!",
                                        Toast.LENGTH_LONG).show();
                                if (tempfile) {
                                    File fdelete = new File(currFileURI.getPath());
                                    if (fdelete.exists()) {
                                        fdelete.delete();
                                    }
                                }
                            }


                        });
                        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                        builder.show();


                    } catch (Exception e) {


                        Toast.makeText(context, "Unable to open the file ",

                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        if (tempfile) {
                            File fdelete = new File(currFileURI.getPath());
                            if (fdelete.exists()) {
                                fdelete.delete();
                            }
                        }
                    }
                    break;
                case "dvf":

                    try {
                        InputStream myInput = context.getContentResolver().openInputStream(currFileURI);
                        InputStreamReader file = new InputStreamReader(myInput);

                        CSVReader reader = new CSVReader(file);
                        List<String[]> myEntries = reader.readAll();
                        if (myEntries.get(0).length == 9) {
                            String chartname2 = myEntries.get(0)[0];
                            String chartTypeRetrieved = myEntries.get(0)[1];
                            String retrievedColumns = myEntries.get(0)[2];
                            String retrievedValues = myEntries.get(0)[3];
                            String retrievedSettings = myEntries.get(0)[4];
                            String retrievedColors = myEntries.get(0)[5];
                            String retrievedScatter = myEntries.get(0)[6];
                            String retrievedEnabled = myEntries.get(0)[7];
                            String retrievedLastModified = myEntries.get(0)[8];

                            mydb.insertChart(chartname2, chartTypeRetrieved, retrievedColumns, retrievedValues, retrievedSettings, retrievedColors, retrievedScatter, retrievedEnabled, retrievedLastModified);
                            Activity activity = (Activity) context;
                            activity.recreate();
                        } else {
                            Toast.makeText(context, "The file is corrupted!",
                                    Toast.LENGTH_LONG).show();
                            if (tempfile) {
                                File fdelete = new File(currFileURI.getPath());
                                if (fdelete.exists()) {
                                    fdelete.delete();
                                }
                            }
                        }
                    } catch (Throwable e) {
                        Toast.makeText(context, "Unable to open file!",
                                Toast.LENGTH_LONG).show();
                        if (tempfile) {
                            File fdelete = new File(currFileURI.getPath());
                            if (fdelete.exists()) {
                                fdelete.delete();
                            }
                        }
                    }
                    break;
                default:
                    Toast.makeText(context, "Invalid file format",

                            Toast.LENGTH_LONG).show();
                    if (tempfile) {
                        File fdelete = new File(currFileURI.getPath());
                        if (fdelete.exists()) {
                            fdelete.delete();
                        }
                    }
                    break;
            }
        } else {
            Toast.makeText(context, "The file is too big!",
                    Toast.LENGTH_LONG).show();
        }


    }
}
