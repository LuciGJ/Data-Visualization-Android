package com.example.chartapp.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DataModel extends SQLiteOpenHelper {
    private List<Integer> arrayOfColors = new ArrayList<>();
    private List<Integer> enabledDataSets = new ArrayList<>();
    private List<String> datasets = new ArrayList<>();
    private List<List<Float>> values = new ArrayList<>();
    private String descriptionStr = "Description";
    private boolean displayBool;
    private boolean xAxisBool = true;
    private boolean yaxisBool = true;
    private boolean decimalBool = true;
    private boolean descriptionBool = true;
    private boolean markersBool = true;
    private boolean xMinBool;
    private boolean yMinBool;
    private boolean xMaxBool;
    private boolean yMaxBool;
    private List<Integer> scatterVariables = Arrays.asList(0, 0, -1);
    private List<String> labels = new ArrayList<>();
    private List<Date> arrayOfDates = new ArrayList<>();
    private List<Integer> connectedCharts = new ArrayList<>();
    private float xStartNum;
    private int xColor = Color.parseColor("#000000");
    private int yColor = Color.parseColor("#000000");
    private float minXValue;
    private float maxXValue;
    private float minYValue;
    private float maxYValue;
    private String chartType = "Line Chart";
    private String incrementX = "1";
    private String xLabelTypeStr = "DATE";
    private Date minimumDate = new GregorianCalendar(2021, Calendar.MAY, 01).getTime();
    private Date maximumDate = new GregorianCalendar(2021, Calendar.MAY, 01).getTime();
    private Date initialDate = new GregorianCalendar(2021, Calendar.MAY, 01).getTime();
    private Context context;
    private boolean titleBool;
    private int chartTitleColor;
    private int backgroundColor;
    private String xLabelString = "X";
    private boolean xLabelBool = true;
    private String yLabelStr = "Y";
    private boolean yLabelBool = true;
    private String chartTitle;
    private List<String> settingsList = new ArrayList<>();
    private int chartId = 0;
    private List<Integer> candleStickVariables = Arrays.asList(0, 0, 0, 0, 0);
    public static final String DATABASE_NAME = "Charts.db";
    public static final String CHARTS_TABLE_NAME = "charts";
    public static final String CHARTS_TABLE_TITLE = "title";
    public static final String CHARTS_TABLE_TYPE = "type";
    public static final String CHARTS_TABLE_COLLUMNS = "collumns";
    public static final String CHARTS_TABLE_VALUES = "valueslist";
    public static final String CHARTS_TABLE_SETTINGS = "settings";
    public static final String CHARTS_COLORS = "colors";
    public static final String CHARTS_SCATTERPLOT_VALUES = "scatterplot_values";
    public static final String CHART_ENABLED_SETS = "enabledsets";
    public static final String CHARTS_LAST_MODIFIED = "last_modified";


    public static DataModel getInstance(Context context) {
        return new DataModel(context);
    }

    private DataModel(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table charts " +
                        "(id integer primary key, title text,type text,collumns text,valueslist text,settings text, colors text,scatterplot_values text, enabledsets text, last_modified text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS charts");
        onCreate(db);
    }

    public void insertChart(String title, String type, String columns, String values, String settings, String colors, String scatterplot_values, String enableddatasets, String last_modified) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("type", type);
        contentValues.put("collumns", columns);
        contentValues.put("valueslist", values);
        contentValues.put("settings", settings);
        contentValues.put("colors", colors);
        contentValues.put("scatterplot_values", scatterplot_values);
        contentValues.put("enabledsets", enableddatasets);
        contentValues.put("last_modified", last_modified);
        db.insert("charts", null, contentValues);
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from charts where id=" + chartId + "", null);
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, CHARTS_TABLE_NAME);
    }

    public void updateTitle(Integer id, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        db.update("charts", contentValues, "id = ? ", new String[]{Integer.toString(id)});
    }

    public void updateType(Integer id, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", type);
        db.update("charts", contentValues, "id = ? ", new String[]{Integer.toString(id)});
    }

    public void updateValues(Integer id, String values) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("valueslist", values);
        db.update("charts", contentValues, "id = ? ", new String[]{Integer.toString(id)});
    }

    public void updateSettings(Integer id, String settings) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("settings", settings);
        db.update("charts", contentValues, "id = ? ", new String[]{Integer.toString(id)});
    }

    public void updateColors(Integer id, String colors) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("colors", colors);
        db.update("charts", contentValues, "id = ? ", new String[]{Integer.toString(id)});
    }

    public void updateScatterplot(Integer id, String values) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("scatterplot_values", values);
        db.update("charts", contentValues, "id = ? ", new String[]{Integer.toString(id)});
    }

    public void updateEnabled(Integer id, String enabledsets) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("enabledsets", enabledsets);
        db.update("charts", contentValues, "id = ? ", new String[]{Integer.toString(id)});
    }

    public void updateDatasets(Integer id, String datasets) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("collumns", datasets);
        db.update("charts", contentValues, "id = ? ", new String[]{Integer.toString(id)});
    }

    public void deleteChart(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("charts",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public ArrayList<ArrayList<String>> getAllCharts() {
        ArrayList<ArrayList<String>> returnList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor res = db.rawQuery("select * from charts", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            int tempId = res.getInt(res.getColumnIndex("id"));
            ArrayList<String> tempList = new ArrayList<>();

            tempList.add(String.valueOf(tempId));
            tempList.add(res.getString(res.getColumnIndex(CHARTS_TABLE_TITLE)));
            tempList.add(res.getString(res.getColumnIndex(CHARTS_TABLE_TYPE)));
            tempList.add(res.getString(res.getColumnIndex(CHARTS_LAST_MODIFIED)));
            returnList.add(tempList);

            res.moveToNext();
        }
        return returnList;
    }

    private void loadSettings() {
        titleBool = Boolean.parseBoolean(settingsList.get(0));
        chartTitleColor = Integer.parseInt(settingsList.get(1));
        backgroundColor = Integer.parseInt(settingsList.get(2));
        descriptionStr = settingsList.get(3);
        descriptionBool = Boolean.parseBoolean(settingsList.get(4));
        xLabelTypeStr = settingsList.get(5);
        xLabelString = settingsList.get(6);
        xStartNum = Float.parseFloat(settingsList.get(7));
        initialDate = new Date(Long.parseLong(settingsList.get(8)));
        incrementX = settingsList.get(9);
        xColor = Integer.parseInt(settingsList.get(10));
        xLabelBool = Boolean.parseBoolean(settingsList.get(11));
        xAxisBool = Boolean.parseBoolean(settingsList.get(12));
        xMinBool = Boolean.parseBoolean(settingsList.get(13));
        minXValue = Float.parseFloat(settingsList.get(14));
        minimumDate = new Date(Long.parseLong(settingsList.get(15)));
        xMaxBool = Boolean.parseBoolean(settingsList.get(16));
        maxXValue = Float.parseFloat(settingsList.get(17));
        maximumDate = new Date(Long.parseLong(settingsList.get(18)));
        yLabelStr = settingsList.get(19);
        yColor = Integer.parseInt(settingsList.get(20));
        yLabelBool = Boolean.parseBoolean(settingsList.get(21));
        yaxisBool = Boolean.getBoolean(settingsList.get(22));
        yMinBool = Boolean.parseBoolean(settingsList.get(23));
        minYValue = Float.parseFloat(settingsList.get(24));
        yMaxBool = Boolean.parseBoolean(settingsList.get(25));
        maxYValue = Float.parseFloat(settingsList.get(26));
        markersBool = Boolean.parseBoolean(settingsList.get(27));
        decimalBool = Boolean.parseBoolean(settingsList.get(28));
        displayBool = Boolean.parseBoolean(settingsList.get(29));
        String retrieved_connected_charts = settingsList.get(30);
        Gson gson = new Gson();
        Type typeint = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        connectedCharts = gson.fromJson(retrieved_connected_charts, typeint);
        String retrieved_candlestick = settingsList.get(31);
        candleStickVariables = gson.fromJson(retrieved_candlestick, typeint);


    }

    public void loadChartDB() {
        DataModel myDb = this;
        Cursor rs = myDb.getData();
        rs.moveToFirst();
        chartTitle = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_TITLE));
        chartType = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_TYPE));
        String retrievedColumns = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_COLLUMNS));
        String retrievedValues = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_VALUES));
        String retrievedSettings = rs.getString(rs.getColumnIndex(DataModel.CHARTS_TABLE_SETTINGS));
        String retrievedColors = rs.getString(rs.getColumnIndex(DataModel.CHARTS_COLORS));
        String retrievedScatterplotValues = rs.getString(rs.getColumnIndex(DataModel.CHARTS_SCATTERPLOT_VALUES));
        String retrievedEnabled = rs.getString(rs.getColumnIndex(DataModel.CHART_ENABLED_SETS));
        if (!rs.isClosed()) {
            rs.close();
        }

        Gson gson = new Gson();
        Type typeStr = new TypeToken<ArrayList<String>>() {
        }.getType();
        Type typeInt = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        Type typeNestedList = new TypeToken<ArrayList<ArrayList<Float>>>() {
        }.getType();


        datasets = gson.fromJson(retrievedColumns, typeStr);
        arrayOfColors = gson.fromJson(retrievedColors, typeInt);
        scatterVariables = gson.fromJson(retrievedScatterplotValues, typeInt);
        settingsList = gson.fromJson(retrievedSettings, typeStr);
        values = gson.fromJson(retrievedValues, typeNestedList);
        enabledDataSets = gson.fromJson(retrievedEnabled, typeInt);
        loadSettings();

    }

    public void updateSettings() {
        settingsList.clear();
        settingsList.add(String.valueOf(titleBool));
        settingsList.add(String.valueOf(chartTitleColor));
        settingsList.add(String.valueOf(backgroundColor));
        settingsList.add(descriptionStr);
        settingsList.add(String.valueOf(descriptionBool));
        settingsList.add(xLabelTypeStr);
        settingsList.add(xLabelString);
        settingsList.add(String.valueOf(xStartNum));
        settingsList.add(String.valueOf(initialDate.getTime()));
        settingsList.add(incrementX);
        settingsList.add(String.valueOf(xColor));
        settingsList.add(String.valueOf(xLabelBool));
        settingsList.add(String.valueOf(xAxisBool));
        settingsList.add(String.valueOf(xMinBool));
        settingsList.add(String.valueOf(minXValue));
        settingsList.add(String.valueOf(minimumDate.getTime()));
        settingsList.add(String.valueOf(xMaxBool));
        settingsList.add(String.valueOf(maxXValue));
        settingsList.add(String.valueOf(maximumDate.getTime()));
        settingsList.add(yLabelStr);
        settingsList.add(String.valueOf(yColor));
        settingsList.add(String.valueOf(yLabelBool));
        settingsList.add(String.valueOf(yaxisBool));
        settingsList.add(String.valueOf(yMinBool));
        settingsList.add(String.valueOf(minYValue));
        settingsList.add(String.valueOf(yMaxBool));
        settingsList.add(String.valueOf(maxYValue));
        settingsList.add(String.valueOf(markersBool));
        settingsList.add(String.valueOf(decimalBool));
        settingsList.add(String.valueOf(displayBool));
        Gson gson = new Gson();
        String connectedcharts_str = gson.toJson(connectedCharts);
        settingsList.add(connectedcharts_str);
        String candlestickvariables_str = gson.toJson(candleStickVariables);
        settingsList.add(candlestickvariables_str);

    }


    public void saveChartDB() {
        updateSettings();
        Gson gson = new Gson();
        String valuesStr = gson.toJson(values);
        String datasetsStr = gson.toJson(datasets);
        String settingsStr = gson.toJson(settingsList);
        String colors = gson.toJson(arrayOfColors);
        String scatterplotValues = gson.toJson(scatterVariables);
        String dataSetEnabledStr = gson.toJson(enabledDataSets);
        Date now = new Date();
        String last_modified_str = String.valueOf(now.getTime());
        this.insertChart(chartTitle, chartType, datasetsStr, valuesStr, settingsStr, colors, scatterplotValues, dataSetEnabledStr, last_modified_str);
    }

    public void updateSettingsDB() {
        updateSettings();
        Gson gson = new Gson();
        String settingsStr = gson.toJson(settingsList);
        updateSettings(chartId, settingsStr);
    }

    public void updateTitleDB() {
        updateTitle(chartId, chartTitle);
    }

    public void updateValuesDb() {
        Gson gson = new Gson();
        String valuesString = gson.toJson(values);
        updateValues(chartId, valuesString);
    }

    public void updateTypeDB() {

        updateType(chartId, chartType);
    }

    public void updateColorsDB() {

        Gson gson = new Gson();
        String colorsString = gson.toJson(arrayOfColors);
        updateColors(chartId, colorsString);
    }

    public void updateScatterPlotDB() {

        Gson gson = new Gson();
        String scatterString = gson.toJson(scatterVariables);
        updateScatterplot(chartId, scatterString);
    }

    public void updateEnabledDB() {

        Gson gson = new Gson();
        String enabledStr = gson.toJson(enabledDataSets);
        updateEnabled(chartId, enabledStr);
    }

    public void updateDatasetsDB() {

        Gson gson = new Gson();
        String datasetsStr = gson.toJson(datasets);
        updateDatasets(chartId, datasetsStr);
    }

    public List<Integer> getArrayOfColors() {
        return arrayOfColors;
    }

    public List<Integer> getEnabledDataSets() {
        return enabledDataSets;
    }

    public List<String> getDatasets() {
        return datasets;
    }

    public List<List<Float>> getValues() {
        return values;
    }

    public String getDescriptionStr() {
        return descriptionStr;
    }

    public void setDescriptionStr(String descriptionStr) {
        this.descriptionStr = descriptionStr;
    }

    public boolean isDisplayBool() {
        return displayBool;
    }

    public void setDisplayBool(boolean displayBool) {
        this.displayBool = displayBool;
    }

    public boolean isxAxisBool() {
        return xAxisBool;
    }

    public void setxAxisBool(boolean xAxisBool) {
        this.xAxisBool = xAxisBool;
    }

    public boolean isYaxisBool() {
        return yaxisBool;
    }

    public void setYaxisBool(boolean yaxisBool) {
        this.yaxisBool = yaxisBool;
    }

    public boolean isDecimalBool() {
        return decimalBool;
    }

    public void setDecimalBool(boolean decimalBool) {
        this.decimalBool = decimalBool;
    }

    public boolean isDescriptionBool() {
        return descriptionBool;
    }

    public void setDescriptionBool(boolean descriptionBool) {
        this.descriptionBool = descriptionBool;
    }

    public boolean isMarkersBool() {
        return markersBool;
    }

    public void setMarkersBool(boolean markersBool) {
        this.markersBool = markersBool;
    }

    public boolean isxMinBool() {
        return xMinBool;
    }

    public void setxMinBool(boolean xMinBool) {
        this.xMinBool = xMinBool;
    }

    public boolean isyMinBool() {
        return yMinBool;
    }

    public void setyMinBool(boolean yMinBool) {
        this.yMinBool = yMinBool;
    }

    public boolean isxMaxBool() {
        return xMaxBool;
    }

    public void setxMaxBool(boolean xMaxBool) {
        this.xMaxBool = xMaxBool;
    }

    public boolean isyMaxBool() {
        return yMaxBool;
    }

    public void setyMaxBool(boolean yMaxBool) {
        this.yMaxBool = yMaxBool;
    }

    public List<Integer> getScatterVariables() {
        return scatterVariables;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Date> getArrayOfDates() {
        return arrayOfDates;
    }

    public void setArrayOfDates(List<Date> arrayOfDates) {
        this.arrayOfDates = arrayOfDates;
    }

    public List<Integer> getConnectedCharts() {
        return connectedCharts;
    }

    public float getxStartNum() {
        return xStartNum;
    }

    public void setxStartNum(float xStartNum) {
        this.xStartNum = xStartNum;
    }

    public int getxColor() {
        return xColor;
    }

    public void setxColor(int xColor) {
        this.xColor = xColor;
    }

    public int getyColor() {
        return yColor;
    }

    public void setyColor(int yColor) {
        this.yColor = yColor;
    }

    public float getMinXValue() {
        return minXValue;
    }

    public void setMinXValue(float minXValue) {
        this.minXValue = minXValue;
    }

    public float getMaxXValue() {
        return maxXValue;
    }

    public void setMaxXValue(float maxXValue) {
        this.maxXValue = maxXValue;
    }

    public float getMinYValue() {
        return minYValue;
    }

    public void setMinYValue(float minYValue) {
        this.minYValue = minYValue;
    }

    public float getMaxYValue() {
        return maxYValue;
    }

    public void setMaxYValue(float maxYValue) {
        this.maxYValue = maxYValue;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public String getIncrementX() {
        return incrementX;
    }

    public void setIncrementX(String incrementX) {
        this.incrementX = incrementX;
    }

    public String getxLabelTypeStr() {
        return xLabelTypeStr;
    }

    public void setxLabelTypeStr(String xLabelTypeStr) {
        this.xLabelTypeStr = xLabelTypeStr;
    }

    public Date getMinimumDate() {
        return minimumDate;
    }

    public void setMinimumDate(Date minimumDate) {
        this.minimumDate = minimumDate;
    }

    public Date getMaximumDate() {
        return maximumDate;
    }

    public void setMaximumDate(Date maximumDate) {
        this.maximumDate = maximumDate;
    }

    public Date getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isTitleBool() {
        return titleBool;
    }

    public void setTitleBool(boolean titleBool) {
        this.titleBool = titleBool;
    }

    public int getChartTitleColor() {
        return chartTitleColor;
    }

    public void setChartTitleColor(int chartTitleColor) {
        this.chartTitleColor = chartTitleColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getxLabelString() {
        return xLabelString;
    }

    public void setxLabelString(String xLabelString) {
        this.xLabelString = xLabelString;
    }

    public boolean isxLabelBool() {
        return xLabelBool;
    }

    public void setxLabelBool(boolean xLabelBool) {
        this.xLabelBool = xLabelBool;
    }

    public String getyLabelStr() {
        return yLabelStr;
    }

    public void setyLabelStr(String yLabelStr) {
        this.yLabelStr = yLabelStr;
    }

    public boolean isyLabelBool() {
        return yLabelBool;
    }

    public void setyLabelBool(boolean yLabelBool) {
        this.yLabelBool = yLabelBool;
    }

    public String getChartTitle() {
        return chartTitle;
    }

    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
    }

    public int getChartId() {
        return chartId;
    }

    public void setChartId(int chartId) {
        this.chartId = chartId;
    }

    public List<Integer> getCandleStickVariables() {
        return candleStickVariables;
    }

}
