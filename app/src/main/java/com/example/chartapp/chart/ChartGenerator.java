package com.example.chartapp.chart;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chartapp.data.DataModel;
import com.example.chartapp.helper.DataHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChartGenerator implements OnChartValueSelectedListener {

    private final DataModel dataModel;

    public ChartGenerator(Context context, int chartId) {
        dataModel = DataModel.getInstance(context);
        dataModel.setContext(context);
        dataModel.setChartId(chartId);
        dataModel.loadChartDB();
    }

    public DataModel getDataModel() {
        return dataModel;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        final int index = (int) e.getX();


        List<Integer> selectedIds = DataHelper.removeDisabledDatasets(dataModel.getEnabledDataSets(), dataModel.getConnectedCharts());
        if (dataModel.numberOfRows() != 0) {
            if (selectedIds.get(index) != -1) {
                for (int j = 0; j < dataModel.getAllCharts().size(); j++) {
                    int dbIndex = Integer.parseInt(dataModel.getAllCharts().get(j).get(0));
                    if (selectedIds.get(index) == dbIndex) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(dataModel.getContext());
                        builder.setCancelable(true);
                        int previousId = dataModel.getChartId();
                        dataModel.setChartId(selectedIds.get(index));
                        dataModel.loadChartDB();
                        Chart chart = updateChart();
                        TextView chartTitleTv = new TextView(dataModel.getContext());
                        TextView xLabelTv = new TextView(dataModel.getContext());
                        TextView yLabelTv = new TextView(dataModel.getContext());

                        if (dataModel.isTitleBool()) {

                            chartTitleTv.setText(dataModel.getChartTitle());
                            chartTitleTv.setTextColor(dataModel.getChartTitleColor());
                            chartTitleTv.setVisibility(View.VISIBLE);
                        } else {
                            chartTitleTv.setVisibility(View.GONE);
                        }


                        if (dataModel.getChartType().equals("Horizontal Bar Chart") || dataModel.getChartType().equals("Horizontal Stacked Bar Chart")) {

                            xLabelTv.setVisibility(View.GONE);
                            chartTitleTv.setVisibility(View.GONE);
                            yLabelTv.setVisibility(View.GONE);
                            yLabelTv.setRotation(90);
                            if (dataModel.isxAxisBool() && dataModel.isxLabelBool()) {

                                xLabelTv.setText(dataModel.getyLabelStr());
                                xLabelTv.setTextColor(dataModel.getyColor());
                                xLabelTv.setVisibility(View.VISIBLE);
                            }

                            if (dataModel.isYaxisBool() && dataModel.isyLabelBool()) {

                                yLabelTv.setText(dataModel.getxLabelString());
                                yLabelTv.setTextColor(dataModel.getxColor());
                                yLabelTv.setVisibility(View.VISIBLE);
                            }
                        } else {
                            yLabelTv.setVisibility(View.GONE);
                            yLabelTv.setRotation(90);
                            if (dataModel.isxAxisBool() && dataModel.isxLabelBool()) {

                                xLabelTv.setText(dataModel.getxLabelString());
                                xLabelTv.setTextColor(dataModel.getxColor());
                                xLabelTv.setVisibility(View.VISIBLE);
                            }

                            if (dataModel.isYaxisBool() && dataModel.isyLabelBool()) {

                                yLabelTv.setText(dataModel.getyLabelStr());
                                yLabelTv.setTextColor(dataModel.getyColor());
                                yLabelTv.setVisibility(View.VISIBLE);
                            }

                        }
                        LinearLayout linearLayout = new LinearLayout(dataModel.getContext());
                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout row = new LinearLayout(dataModel.getContext());
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
                        builder.setView(linearLayout);
                        dataModel.setChartId(previousId);
                        dataModel.loadChartDB();
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }
        }
    }

    @Override
    public void onNothingSelected() {
    }

    private ValueFormatter getValueFormatter() {
        if (dataModel.getxLabelTypeStr().equals("NUMERIC")) {
            float startX = dataModel.getxStartNum();
            float increment = Float.parseFloat(dataModel.getIncrementX());
            for (int i = 0; i < dataModel.getValues().get(0).size(); i++) {
                dataModel.getLabels().add(String.valueOf(startX));
                startX = startX + increment;

            }
            if (!dataModel.isDecimalBool()) {
                return new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        if ((int) value >= 0 && value < dataModel.getLabels().size()) {
                            return dataModel.getLabels().get((int) value);
                        }

                        return "";

                    }
                };
            } else {
                return new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        if ((int) value >= 0 && value < dataModel.getLabels().size()) {
                            float f = Float.parseFloat(dataModel.getLabels().get((int) value));
                            int i = (int) f;
                            return String.valueOf(i);
                        }

                        return "";

                    }
                };
            }

        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM-YYYY");
            Calendar cStart = Calendar.getInstance();
            String strdate;
            cStart.setTime(dataModel.getInitialDate());
            Date startingX = dataModel.getInitialDate();
            for (int i = 0; i < dataModel.getValues().get(0).size(); i++) {

                strdate = formatter.format(startingX);
                dataModel.getLabels().add(strdate);
                dataModel.getArrayOfDates().add(startingX);
                cStart.add(Calendar.DAY_OF_MONTH, Integer.parseInt(dataModel.getIncrementX()));
                startingX = cStart.getTime();
            }
            return new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    if ((int) value >= 0 && value < dataModel.getLabels().size()) {
                        return dataModel.getLabels().get((int) value);
                    }

                    return "";

                }
            };
        }
    }

    public int getStart() {
        int start = 0;
        if (dataModel.isxMinBool()) {
            if (dataModel.getxLabelTypeStr().equals("NUMERIC")) {
                for (int i = 0; i < dataModel.getLabels().size(); i++) {
                    if (Float.parseFloat(dataModel.getLabels().get(i)) >= dataModel.getMinXValue()) {

                        start = i;
                        break;
                    }
                }
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM-YYYY");
                for (int i = 0; i < dataModel.getLabels().size(); i++) {
                    if (dataModel.getArrayOfDates().get(i).after(dataModel.getMinimumDate()) || dataModel.getLabels().get(i).equals(formatter.format(dataModel.getMinimumDate()))) {
                        start = i;
                        break;
                    }
                }
            }

        }
        return start;
    }

    public int getStop() {
        int stop = dataModel.getValues().get(0).size();
        if (dataModel.isxMaxBool()) {
            if (dataModel.getxLabelTypeStr().equals("NUMERIC")) {
                for (int i = 0; i < dataModel.getLabels().size(); i++) {
                    if (Float.parseFloat(dataModel.getLabels().get(i)) >= dataModel.getMaxXValue()) {
                        stop = i;

                        break;
                    }
                }

            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM-YYYY");
                for (int i = 0; i < dataModel.getLabels().size(); i++) {
                    if (dataModel.getArrayOfDates().get(i).after(dataModel.getMaximumDate()) || dataModel.getLabels().get(i).equals(formatter.format(dataModel.getMaximumDate()))) {
                        stop = i;
                        break;
                    }
                }
            }


        }
        return stop;
    }

    private Chart generateBubbleChart() {

        int start, stop;
        BubbleChart chart;
        chart = new BubbleChart(dataModel.getContext());
        chart.setScaleMinima(2f, 1f);
        chart.clear();

        start = 0;
        stop = dataModel.getValues().get(0).size();

        List<IBubbleDataSet> dataSets = new ArrayList<>();
        List<BubbleEntry> lineValues = new ArrayList<>();
        List<Float> tempList = dataModel.getValues().get(dataModel.getScatterVariables().get(0));
        List<Float> tempList2 = dataModel.getValues().get(dataModel.getScatterVariables().get(1));
        List<Float> tempList3 = new ArrayList<>();
        if (dataModel.getScatterVariables().get(2) == -1) {
            for (int i = 0; i < dataModel.getValues().get(0).size(); i++) {
                tempList3.add(0.2f);
            }
        } else {
            tempList3 = dataModel.getValues().get(dataModel.getScatterVariables().get(2));
        }

        for (int j = start; j < stop; j++) {


            lineValues.add(new BubbleEntry(tempList.get(j), tempList2.get(j), tempList3.get(j)));


        }
        lineValues.sort(new EntryXComparator());

        BubbleDataSet set = new BubbleDataSet(lineValues, dataModel.getDatasets().get(dataModel.getScatterVariables().get(0)) + "/" + dataModel.getDatasets().get(dataModel.getScatterVariables().get(1)));


        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(dataModel.getArrayOfColors().get(dataModel.getScatterVariables().get(0)));
        set.setDrawValues(dataModel.isMarkersBool());


        dataSets.add(set);


        BubbleData data = new BubbleData(dataSets);

        chart.setData(data);
        YAxis myYaxis = chart.getAxisLeft();


        myYaxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        XAxis myXaxis = chart.getXAxis();
        myXaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        myXaxis.setDrawGridLines(false);

        myXaxis.setGranularity(1f);
        if (dataModel.isDisplayBool()) {
            chart.setVisibleXRange(dataModel.getValues().get(0).size(), dataModel.getValues().get(0).size());
        }


        YAxis yAxis = chart.getAxisLeft();
        yAxis.setEnabled(dataModel.isYaxisBool());
        if (dataModel.isyMinBool()) {
            myYaxis.setAxisMinimum(dataModel.getMinYValue());
        }
        if (dataModel.isyMaxBool()) {
            myYaxis.setAxisMaximum(dataModel.getMaxYValue());
        }
        yAxis.setTextColor(dataModel.getyColor());
        yAxis.setGridColor(dataModel.getyColor());
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);

        myXaxis.setValueFormatter(getValueFormatter());
        myXaxis.setAxisMinimum(getStart());
        myXaxis.setAxisMaximum(getStop());

        myXaxis.setLabelCount(dataModel.getLabels().size());
        myXaxis.setEnabled(dataModel.isxAxisBool());
        myXaxis.setGridColor(dataModel.getxColor());
        myXaxis.setTextColor(dataModel.getxColor());
        myXaxis.setDrawLabels(dataModel.isxAxisBool());
        myXaxis.setLabelRotationAngle(90);
        Description chart_description = chart.getDescription();
        chart_description.setEnabled(dataModel.isDescriptionBool());
        chart_description.setText(dataModel.getDescriptionStr());
        chart_description.setTextColor(dataModel.getxColor());
        chart.invalidate();
        return chart;
    }

    public Chart generateCandlestickChart() {

        int start, stop;
        CandleStickChart chart;
        chart = new CandleStickChart(dataModel.getContext());
        chart.setScaleMinima(2f, 1f);
        chart.clear();

        start = 0;
        stop = dataModel.getValues().get(0).size();

        List<ICandleDataSet> dataSets = new ArrayList<>();
        List<CandleEntry> linevalues = new ArrayList<>();
        List<Float> tempList = dataModel.getValues().get(dataModel.getCandleStickVariables().get(0));
        List<Float> tempList2 = dataModel.getValues().get(dataModel.getCandleStickVariables().get(1));
        List<Float> tempList3 = dataModel.getValues().get(dataModel.getCandleStickVariables().get(2));
        List<Float> tempList4 = dataModel.getValues().get(dataModel.getCandleStickVariables().get(3));
        List<Integer> colors = new ArrayList<>();
        for (int j = start; j < stop; j++) {
            CandleEntry entry = new CandleEntry(j, tempList.get(j), tempList2.get(j), tempList3.get(j), tempList4.get(j));
            if (tempList3.get(j) > tempList4.get(j)) {
                colors.add(Color.RED);
            } else {
                colors.add(Color.GREEN);
            }
            linevalues.add(entry);
        }
        linevalues.sort(new EntryXComparator());

        CandleDataSet set = new CandleDataSet(linevalues, dataModel.getChartTitle());

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColors(colors);
        set.setShadowColor(Color.BLACK);
        set.setShadowWidth(0.8f);
        set.setDecreasingColor(Color.RED);
        set.setIncreasingColor(Color.GREEN);
        set.setNeutralColor(Color.GRAY);
        set.setShowCandleBar(true);
        dataSets.add(set);

        CandleData data = new CandleData(set);

        chart.setData(data);
        YAxis myYaxis = chart.getAxisLeft();


        myYaxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        XAxis myXaxis = chart.getXAxis();
        myXaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        myXaxis.setDrawGridLines(false);

        myXaxis.setGranularity(1f);

        if (dataModel.isDisplayBool()) {
            chart.setVisibleXRange(dataModel.getValues().get(0).size(), dataModel.getValues().get(0).size());
        }


        YAxis yAxis = chart.getAxisLeft();
        yAxis.setEnabled(dataModel.isYaxisBool());
        if (dataModel.isyMinBool()) {
            myYaxis.setAxisMinimum(dataModel.getMinYValue());
        }
        if (dataModel.isyMaxBool()) {
            myYaxis.setAxisMaximum(dataModel.getMaxYValue());
        }
        yAxis.setTextColor(dataModel.getyColor());
        yAxis.setGridColor(dataModel.getyColor());
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);

        myXaxis.setValueFormatter(getValueFormatter());
        myXaxis.setAxisMinimum(getStart());
        myXaxis.setAxisMaximum(getStop());

        myXaxis.setLabelCount(dataModel.getLabels().size());
        myXaxis.setEnabled(dataModel.isxAxisBool());
        myXaxis.setGridColor(dataModel.getxColor());
        myXaxis.setTextColor(dataModel.getxColor());
        myXaxis.setDrawLabels(dataModel.isxAxisBool());
        myXaxis.setLabelRotationAngle(90);
        Description chart_description = chart.getDescription();
        chart_description.setEnabled(dataModel.isDescriptionBool());
        chart_description.setText(dataModel.getDescriptionStr());
        chart_description.setTextColor(dataModel.getxColor());
        chart.getLegend().setEnabled(false);
        chart.invalidate();
        return chart;
    }

    public Chart generateScatterPlotChart() {

        int start, stop;
        ScatterChart chart;
        chart = new ScatterChart(dataModel.getContext());
        chart.setScaleMinima(2f, 1f);
        chart.clear();

        start = 0;
        stop = dataModel.getValues().get(0).size();

        List<IScatterDataSet> dataSets = new ArrayList<>();
        List<Entry> linevalues = new ArrayList<>();
        List<Float> tempList = dataModel.getValues().get(dataModel.getScatterVariables().get(0));
        List<Float> tempList2 = dataModel.getValues().get(dataModel.getScatterVariables().get(1));

        for (int j = start; j < stop; j++) {


            linevalues.add(new Entry(tempList.get(j), tempList2.get(j)));


        }
        linevalues.sort(new EntryXComparator());

        ScatterDataSet set = new ScatterDataSet(linevalues, dataModel.getDatasets().get(dataModel.getScatterVariables().get(0)) + "/" + dataModel.getDatasets().get(dataModel.getScatterVariables().get(1)));


        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(dataModel.getArrayOfColors().get(dataModel.getScatterVariables().get(0)));
        set.setDrawValues(dataModel.isMarkersBool());


        dataSets.add(set);


        ScatterData data = new ScatterData(set);

        chart.setData(data);
        YAxis myYaxis = chart.getAxisLeft();


        myYaxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        XAxis myXaxis = chart.getXAxis();
        myXaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        myXaxis.setDrawGridLines(false);

        myXaxis.setGranularity(1f);

        if (dataModel.isDisplayBool()) {
            chart.setVisibleXRange(dataModel.getValues().get(0).size(), dataModel.getValues().get(0).size());
        }


        YAxis yAxis = chart.getAxisLeft();
        yAxis.setEnabled(dataModel.isYaxisBool());
        if (dataModel.isyMinBool()) {
            myYaxis.setAxisMinimum(dataModel.getMinYValue());
        }
        if (dataModel.isyMaxBool()) {
            myYaxis.setAxisMaximum(dataModel.getMaxYValue());
        }
        yAxis.setTextColor(dataModel.getyColor());
        yAxis.setGridColor(dataModel.getyColor());
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);

        myXaxis.setValueFormatter(getValueFormatter());
        myXaxis.setAxisMinimum(getStart());
        myXaxis.setAxisMaximum(getStop());

        myXaxis.setLabelCount(dataModel.getLabels().size());
        myXaxis.setEnabled(dataModel.isxAxisBool());
        myXaxis.setGridColor(dataModel.getxColor());
        myXaxis.setTextColor(dataModel.getxColor());
        myXaxis.setDrawLabels(dataModel.isxAxisBool());
        myXaxis.setLabelRotationAngle(90);
        Description chart_description = chart.getDescription();
        chart_description.setEnabled(dataModel.isDescriptionBool());
        chart_description.setText(dataModel.getDescriptionStr());
        chart_description.setTextColor(dataModel.getxColor());
        chart.invalidate();
        return chart;
    }

    public Chart generateBarChart() {

        int start, stop;
        BarChart chart;

        chart = new BarChart(dataModel.getContext());
        chart.setScaleMinima(2f, 1f);

        chart.clear();

        start = 0;
        stop = dataModel.getValues().get(0).size();

        List<IBarDataSet> dataSets = new ArrayList<>();
        for (int i = 0; i < dataModel.getValues().size(); i++) {
            if (dataModel.getEnabledDataSets().get(i) == 1) {
                List<BarEntry> linevalues = new ArrayList<>();
                List<Float> tempList = dataModel.getValues().get(i);

                for (int j = start; j < stop; j++) {


                    linevalues.add(new BarEntry(j, tempList.get(j)));


                }
                linevalues.sort(new EntryXComparator());

                BarDataSet set = new BarDataSet(linevalues, dataModel.getDatasets().get(i));


                set.setAxisDependency(YAxis.AxisDependency.LEFT);
                set.setColor(dataModel.getArrayOfColors().get(i));
                set.setDrawValues(dataModel.isMarkersBool());


                dataSets.add(set);

            }
        }
        int count = 0;
        for (int i = 0; i < dataModel.getEnabledDataSets().size(); i++) {
            if (dataModel.getEnabledDataSets().get(i) == 1) {
                count++;
            }
        }
        BarData data = new BarData(dataSets);
        float groupSpace = 0.06f;
        float barSpace = 0.01f;
        float barWidth = 1f;
        if (count != 0) {
            barWidth = ((1.00f - groupSpace) / count) - barSpace;
        }

        data.setBarWidth(barWidth);
        chart.setData(data);
        YAxis myYaxis = chart.getAxisLeft();


        myYaxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        XAxis myXaxis = chart.getXAxis();
        myXaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        myXaxis.setDrawGridLines(false);

        myXaxis.setGranularity(1f);

        if (dataModel.isDisplayBool()) {
            chart.setVisibleXRange(dataModel.getValues().get(0).size(), dataModel.getValues().get(0).size());
        }


        YAxis yAxis = chart.getAxisLeft();
        yAxis.setEnabled(dataModel.isYaxisBool());
        if (dataModel.isyMinBool()) {
            myYaxis.setAxisMinimum(dataModel.getMinYValue());
        }
        if (dataModel.isyMaxBool()) {
            myYaxis.setAxisMaximum(dataModel.getMaxYValue());
        }
        yAxis.setTextColor(dataModel.getyColor());
        yAxis.setGridColor(dataModel.getyColor());
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);

        myXaxis.setValueFormatter(getValueFormatter());
        myXaxis.setAxisMinimum(getStart());
        myXaxis.setAxisMaximum(getStop());
        if (count > 1) {
            myXaxis.setAxisMinimum(0);

            myXaxis.setCenterAxisLabels(true);

            chart.groupBars(0f, groupSpace, barSpace);
        }
        myXaxis.setLabelCount(dataModel.getLabels().size());
        myXaxis.setEnabled(dataModel.isxAxisBool());
        myXaxis.setGridColor(dataModel.getxColor());
        myXaxis.setTextColor(dataModel.getxColor());
        myXaxis.setDrawLabels(dataModel.isxAxisBool());
        myXaxis.setLabelRotationAngle(90);
        Description chartDescription = chart.getDescription();
        chartDescription.setEnabled(dataModel.isDescriptionBool());
        chartDescription.setText(dataModel.getDescriptionStr());
        chartDescription.setTextColor(dataModel.getxColor());
        chart.invalidate();
        return chart;
    }

    public Chart generateStackedBarChart() {


        BarChart chart;
        chart = new BarChart(dataModel.getContext());
        chart.setScaleMinima(2f, 1f);
        chart.clear();


        List<BarEntry> linevalues = new ArrayList<>();
        List<IBarDataSet> dataSets = new ArrayList<>();
        List<List<Float>> convertedList = DataHelper.convertStackedData(dataModel.getValues());
        List<Integer> colors = new ArrayList<>();
        List<String> dataSetStr = new ArrayList<>();
        for (int i = 0; i < convertedList.size(); i++) {
            List<Float> tempList = convertedList.get(i);
            List<Float> stackedValues = new ArrayList<>();
            for (int j = 0; j < tempList.size(); j++) {
                if (dataModel.getEnabledDataSets().get(j) == 1) {

                    stackedValues.add(tempList.get(j));

                }
            }

            float[] stack = new float[stackedValues.size()];
            int indexfloat = 0;
            for (Float f : stackedValues)
                stack[indexfloat++] = f;

            linevalues.add(new BarEntry(i, stack));
        }
        linevalues.sort(new EntryXComparator());
        for (int i = 0; i < dataModel.getEnabledDataSets().size(); i++) {
            if (dataModel.getEnabledDataSets().get(i) == 1) {
                colors.add(dataModel.getArrayOfColors().get(i));
                dataSetStr.add(dataModel.getDatasets().get(i));
            }
        }
        BarDataSet set = new BarDataSet(linevalues, "");


        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColors(colors);
        String[] selectedDatasets = dataSetStr.toArray(new String[0]);

        set.setStackLabels(selectedDatasets);
        set.setDrawValues(dataModel.isMarkersBool());


        dataSets.add(set);
        int count = 0;
        for (int i = 0; i < dataModel.getEnabledDataSets().size(); i++) {
            if (dataModel.getEnabledDataSets().get(i) == 1) {
                count++;
            }
        }
        BarData data = new BarData(dataSets);
        float groupSpace = 0.06f;
        float barSpace = 0.01f;
        float barWidth = 1f;
        if (count != 0) {
            barWidth = ((1.00f - groupSpace) / count) - barSpace;
        }

        data.setBarWidth(barWidth);
        chart.setData(data);
        YAxis myYaxis = chart.getAxisLeft();


        myYaxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        XAxis myXaxis = chart.getXAxis();
        myXaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        myXaxis.setDrawGridLines(false);

        myXaxis.setGranularity(1f);

        if (dataModel.isDisplayBool()) {
            chart.setVisibleXRange(dataModel.getValues().get(0).size(), dataModel.getValues().get(0).size());
        }

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setEnabled(dataModel.isYaxisBool());
        if (dataModel.isyMinBool()) {
            myYaxis.setAxisMinimum(dataModel.getMinYValue());
        }
        if (dataModel.isyMaxBool()) {
            myYaxis.setAxisMaximum(dataModel.getMaxYValue());
        }
        yAxis.setTextColor(dataModel.getyColor());
        yAxis.setGridColor(dataModel.getyColor());
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);

        myXaxis.setValueFormatter(getValueFormatter());
        myXaxis.setAxisMinimum(getStart());
        myXaxis.setAxisMaximum(getStop());

        myXaxis.setLabelCount(dataModel.getLabels().size());
        myXaxis.setEnabled(dataModel.isxAxisBool());
        myXaxis.setGridColor(dataModel.getxColor());
        myXaxis.setTextColor(dataModel.getxColor());
        myXaxis.setDrawLabels(dataModel.isxAxisBool());
        myXaxis.setLabelRotationAngle(90);
        Description chartDescription = chart.getDescription();
        chartDescription.setEnabled(dataModel.isDescriptionBool());
        chartDescription.setText(dataModel.getDescriptionStr());
        chartDescription.setTextColor(dataModel.getxColor());
        chart.invalidate();
        return chart;
    }

    public Chart generateHorizontalStackedBarchart() {

        HorizontalBarChart chart;

        chart = new HorizontalBarChart(dataModel.getContext());
        chart.setScaleMinima(2f, 1f);
        chart.clear();
        List<BarEntry> linevalues = new ArrayList<>();
        List<IBarDataSet> dataSets = new ArrayList<>();
        List<List<Float>> convertedList = DataHelper.convertStackedData(dataModel.getValues());
        List<Integer> colors = new ArrayList<>();
        List<String> dataSetStr = new ArrayList<>();
        for (int i = 0; i < convertedList.size(); i++) {
            List<Float> tempList = convertedList.get(i);
            List<Float> stackedValues = new ArrayList<>();
            for (int j = 0; j < tempList.size(); j++) {
                if (dataModel.getEnabledDataSets().get(j) == 1) {

                    stackedValues.add(tempList.get(j));

                }
            }

            float[] stack = new float[stackedValues.size()];
            int indexfloat = 0;
            for (Float f : stackedValues)
                stack[indexfloat++] = f;

            linevalues.add(new BarEntry(i, stack));
        }
        linevalues.sort(new EntryXComparator());
        for (int i = 0; i < dataModel.getEnabledDataSets().size(); i++) {
            if (dataModel.getEnabledDataSets().get(i) == 1) {
                colors.add(dataModel.getArrayOfColors().get(i));
                dataSetStr.add(dataModel.getDatasets().get(i));
            }
        }
        BarDataSet set = new BarDataSet(linevalues, "");


        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColors(colors);
        String[] selected_datasets = dataSetStr.toArray(new String[0]);

        set.setStackLabels(selected_datasets);
        set.setDrawValues(dataModel.isMarkersBool());


        dataSets.add(set);
        int count = 0;
        for (int i = 0; i < dataModel.getEnabledDataSets().size(); i++) {
            if (dataModel.getEnabledDataSets().get(i) == 1) {
                count++;
            }
        }
        BarData data = new BarData(dataSets);
        float groupSpace = 0.06f;
        float barSpace = 0.01f;
        float barWidth = 1f;
        if (count != 0) {
            barWidth = ((1.00f - groupSpace) / count) - barSpace;
        }

        data.setBarWidth(barWidth);
        chart.setData(data);
        YAxis myYaxis = chart.getAxisLeft();


        myYaxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        XAxis myXaxis = chart.getXAxis();
        myXaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        myXaxis.setDrawGridLines(false);

        myXaxis.setGranularity(1f);

        if (dataModel.isDisplayBool()) {
            chart.setVisibleXRange(dataModel.getValues().get(0).size(), dataModel.getValues().get(0).size());
        }


        YAxis yAxis = chart.getAxisLeft();
        yAxis.setEnabled(dataModel.isYaxisBool());
        if (dataModel.isyMinBool()) {
            myYaxis.setAxisMinimum(dataModel.getMinYValue());
        }
        if (dataModel.isyMaxBool()) {
            myYaxis.setAxisMaximum(dataModel.getMaxYValue());
        }
        yAxis.setTextColor(dataModel.getyColor());
        yAxis.setGridColor(dataModel.getyColor());
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);

        myXaxis.setValueFormatter(getValueFormatter());
        myXaxis.setAxisMinimum(getStart());
        myXaxis.setAxisMaximum(getStop());

        myXaxis.setLabelCount(dataModel.getLabels().size());
        myXaxis.setEnabled(dataModel.isxAxisBool());
        myXaxis.setGridColor(dataModel.getxColor());
        myXaxis.setTextColor(dataModel.getxColor());
        myXaxis.setDrawLabels(dataModel.isxAxisBool());
        Description chartDescription = chart.getDescription();
        chartDescription.setEnabled(dataModel.isDescriptionBool());
        chartDescription.setText(dataModel.getDescriptionStr());
        chartDescription.setTextColor(dataModel.getxColor());
        chart.invalidate();
        return chart;
    }

    public Chart generateHorizontalBarChart() {

        int start, stop;
        HorizontalBarChart chart;
        chart = new HorizontalBarChart(dataModel.getContext());
        chart.setScaleMinima(2f, 1f);
        chart.clear();

        start = 0;
        stop = dataModel.getValues().get(0).size();

        List<IBarDataSet> dataSets = new ArrayList<>();
        for (int i = 0; i < dataModel.getValues().size(); i++) {
            if (dataModel.getEnabledDataSets().get(i) == 1) {
                List<BarEntry> linevalues = new ArrayList<>();
                List<Float> tempList = dataModel.getValues().get(i);

                for (int j = start; j < stop; j++) {


                    linevalues.add(new BarEntry(j, tempList.get(j)));


                }
                linevalues.sort(new EntryXComparator());

                BarDataSet set = new BarDataSet(linevalues, dataModel.getDatasets().get(i));


                set.setAxisDependency(YAxis.AxisDependency.LEFT);
                set.setColor(dataModel.getArrayOfColors().get(i));
                set.setDrawValues(dataModel.isMarkersBool());


                dataSets.add(set);

            }
        }

        int count = 0;
        for (int i = 0; i < dataModel.getEnabledDataSets().size(); i++) {
            if (dataModel.getEnabledDataSets().get(i) == 1) {
                count++;
            }
        }
        BarData data = new BarData(dataSets);

        chart.setData(data);
        YAxis myYaxis = chart.getAxisLeft();
        float groupSpace = 0.06f;
        float barSpace = 0.01f;


        myYaxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        XAxis myXaxis = chart.getXAxis();
        myXaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        myXaxis.setDrawGridLines(false);

        myXaxis.setGranularity(1f);

        if (dataModel.isDisplayBool()) {
            chart.setVisibleXRange(dataModel.getValues().get(0).size(), dataModel.getValues().get(0).size());
        }


        YAxis yAxis = chart.getAxisLeft();
        yAxis.setEnabled(dataModel.isYaxisBool());
        if (dataModel.isyMinBool()) {
            myYaxis.setAxisMinimum(dataModel.getMinYValue());
        }
        if (dataModel.isyMaxBool()) {
            myYaxis.setAxisMaximum(dataModel.getMaxYValue());
        }
        yAxis.setTextColor(dataModel.getyColor());
        yAxis.setGridColor(dataModel.getyColor());
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);

        myXaxis.setValueFormatter(getValueFormatter());
        myXaxis.setAxisMinimum(getStart());
        myXaxis.setAxisMaximum(getStop());

        if (count > 1) {
            myXaxis.setAxisMinimum(0);

            myXaxis.setCenterAxisLabels(true);

            chart.groupBars(0f, groupSpace, barSpace);
        }

        myXaxis.setLabelCount(dataModel.getLabels().size());
        myXaxis.setEnabled(dataModel.isxAxisBool());
        myXaxis.setGridColor(dataModel.getxColor());
        myXaxis.setTextColor(dataModel.getxColor());
        myXaxis.setDrawLabels(dataModel.isxAxisBool());
        Description chartDescription = chart.getDescription();
        chartDescription.setEnabled(dataModel.isDescriptionBool());
        chartDescription.setText(dataModel.getDescriptionStr());
        chartDescription.setTextColor(dataModel.getxColor());
        chart.invalidate();
        return chart;
    }

    public Chart generateRadarChart() {
        RadarChart chart;
        chart = new RadarChart(dataModel.getContext());

        chart.clear();
        List<IRadarDataSet> dataSets = new ArrayList<>();
        List<RadarEntry> tempValues = new ArrayList<>();
        for (int i = 0; i < dataModel.getValues().size(); i++) {
            if (dataModel.getEnabledDataSets().get(i) == 1) {

                List<Float> tempList = dataModel.getValues().get(i);
                RadarEntry entry = new RadarEntry(tempList.get(0));
                tempValues.add(entry);
                tempValues.sort(new EntryXComparator());
                RadarDataSet set = new RadarDataSet(tempValues, dataModel.getDatasets().get(i));
                set.setColor(Color.BLACK);
                set.setAxisDependency(YAxis.AxisDependency.LEFT);
                dataSets.add(set);
                dataModel.getLabels().add(dataModel.getDatasets().get(i));
            }
        }

        RadarData data = new RadarData(dataSets);

        chart.setData(data);
        XAxis axis = chart.getXAxis();
        axis.setLabelCount(dataModel.getLabels().size());
        ValueFormatter formatter = new IndexAxisValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                if (value < dataModel.getLabels().size())
                    return dataModel.getLabels().get((int) value);
                return "";
            }
        };
        axis.setValueFormatter(formatter);
        Description chartDescription = chart.getDescription();
        chartDescription.setEnabled(dataModel.isDescriptionBool());
        chartDescription.setText(dataModel.getDescriptionStr());
        chartDescription.setTextColor(Color.BLACK);
        chart.getLegend().setEnabled(false);
        chart.invalidate();
        return chart;
    }

    public Chart generatePieChart() {


        PieChart chart;
        chart = new PieChart(dataModel.getContext());


        chart.clear();
        List<Integer> tempColors = new ArrayList<>();
        List<PieEntry> tempValues = new ArrayList<>();
        float xentry = 0;
        for (int i = 0; i < dataModel.getValues().size(); i++) {
            if (dataModel.getEnabledDataSets().get(i) == 1) {
                List<Float> tempList = dataModel.getValues().get(i);
                PieEntry entry = new PieEntry(tempList.get(0), dataModel.getDatasets().get(i));
                entry.setX(xentry);
                tempValues.add(entry);
                tempColors.add(dataModel.getArrayOfColors().get(i));
                xentry++;
            }
        }


        PieDataSet dataSets = new PieDataSet(tempValues, "");

        dataSets.setColors(tempColors);
        PieData data = new PieData(dataSets);

        chart.setData(data);

        Description chartDescription = chart.getDescription();
        chartDescription.setEnabled(dataModel.isDescriptionBool());
        chartDescription.setText(dataModel.getDescriptionStr());
        chartDescription.setTextColor(dataModel.getxColor());
        chart.setUsePercentValues(dataModel.isDecimalBool());
        chart.setOnChartValueSelectedListener(this);
        chart.invalidate();
        return chart;
    }

    public Chart generateLinechart() {
        int start, stop;
        LineChart chart;
        chart = new LineChart(dataModel.getContext());
        chart.setScaleMinima(2f, 1f);

        chart.clear();

        start = 0;
        stop = dataModel.getValues().get(0).size();

        List<ILineDataSet> dataSets = new ArrayList<>();
        for (int i = 0; i < dataModel.getValues().size(); i++) {
            if (dataModel.getEnabledDataSets().get(i) == 1) {
                List<Entry> linevalues = new ArrayList<>();
                List<Float> tempList = dataModel.getValues().get(i);

                for (int j = start; j < stop; j++) {


                    linevalues.add(new Entry(j, tempList.get(j)));


                }
                linevalues.sort(new EntryXComparator());

                LineDataSet set = new LineDataSet(linevalues, dataModel.getDatasets().get(i));

                if (dataModel.getChartType().equals("Area Chart")) {
                    set.setFillAlpha(100);
                    set.setDrawFilled(true);
                    set.setFillColor(dataModel.getArrayOfColors().get(i));
                }
                if (dataModel.getChartType().equals("Spline Chart")) {
                    set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                }
                if (dataModel.getChartType().equals("Area Spline Chart")) {
                    set.setFillAlpha(100);
                    set.setDrawFilled(true);
                    set.setFillColor(dataModel.getArrayOfColors().get(i));
                    set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                }
                set.setAxisDependency(YAxis.AxisDependency.LEFT);
                set.setColor(dataModel.getArrayOfColors().get(i));
                set.setDrawValues(dataModel.isMarkersBool());
                set.setDrawCircles(dataModel.isMarkersBool());


                dataSets.add(set);

            }
        }


        LineData data = new LineData(dataSets);

        chart.setData(data);
        YAxis myYaxis = chart.getAxisLeft();


        myYaxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        XAxis myXaxis = chart.getXAxis();
        myXaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        myXaxis.setDrawGridLines(false);

        myXaxis.setGranularity(1f);

        if (dataModel.isDisplayBool()) {
            chart.setVisibleXRange(dataModel.getValues().get(0).size(), dataModel.getValues().get(0).size());
        }


        YAxis yAxis = chart.getAxisLeft();
        yAxis.setEnabled(dataModel.isYaxisBool());
        if (dataModel.isyMinBool()) {
            myYaxis.setAxisMinimum(dataModel.getMinYValue());
        }
        if (dataModel.isyMaxBool()) {
            myYaxis.setAxisMaximum(dataModel.getMaxYValue());
        }
        yAxis.setTextColor(dataModel.getyColor());
        yAxis.setGridColor(dataModel.getyColor());
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);

        myXaxis.setValueFormatter(getValueFormatter());
        myXaxis.setAxisMinimum(getStart());
        myXaxis.setAxisMaximum(getStop());

        myXaxis.setLabelCount(dataModel.getLabels().size());
        myXaxis.setEnabled(dataModel.isxAxisBool());
        myXaxis.setGridColor(dataModel.getxColor());
        myXaxis.setTextColor(dataModel.getxColor());
        myXaxis.setDrawLabels(dataModel.isxAxisBool());
        myXaxis.setLabelRotationAngle(90);
        Description chart_description = chart.getDescription();
        chart_description.setEnabled(dataModel.isDescriptionBool());
        chart_description.setText(dataModel.getDescriptionStr());
        chart_description.setTextColor(dataModel.getxColor());
        chart.invalidate();
        return chart;
    }


    public Chart updateChart() {
        Chart chart = null;

        switch (dataModel.getChartType()) {
            case ("Bar Chart"): {
                chart = generateBarChart();
                break;

            }
            case ("Line Chart"):
            case "Area Chart":
            case "Spline Chart":
            case "Area Spline Chart": {

                chart = generateLinechart();

                break;
            }
            case ("Scatterplot Chart"): {
                chart = generateScatterPlotChart();
                break;
            }
            case ("Bubble Chart"): {
                chart = generateBubbleChart();
                break;
            }
            case "Pie Chart": {
                chart = generatePieChart();
                break;
            }
            case "Horizontal Bar Chart": {
                chart = generateHorizontalBarChart();
                break;
            }
            case "Stacked Bar Chart": {
                chart = generateStackedBarChart();
                break;
            }
            case "Horizontal Stacked Bar Chart": {
                chart = generateHorizontalStackedBarchart();
                break;
            }
            case "Radar Chart": {
                chart = generateRadarChart();
                break;
            }
            case ("Candlestick Chart"): {
                chart = generateCandlestickChart();
                break;

            }

            default:
                break;
        }
        return chart;
    }
}
