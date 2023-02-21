package com.example.chartapp.helper;

import java.util.ArrayList;
import java.util.List;

public class DataHelper {
    private DataHelper() {
    }

    public static List<List<Float>> convertStackedData(List<List<Float>> values) {

        List<List<Float>> returnList = new ArrayList<>();
        int index = 0;
        while (index < values.get(0).size()) {
            List<Float> tempList = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) {
                List<Float> floatlist = values.get(i);
                tempList.add(floatlist.get(index));

            }
            returnList.add(tempList);
            index++;
        }
        return returnList;
    }

    public static float toFloat(double value) {
        Double convert = Double.valueOf(value);
        return convert.floatValue();
    }

    public static List<Integer> removeDisabledDatasets(List<Integer> datasets, List<Integer> ids) {
        List<Integer> returnList = new ArrayList<>();
        for (int i = 0; i < datasets.size(); i++) {
            if (datasets.get(i) == 1) {
                returnList.add(ids.get(i));
            }
        }
        return returnList;
    }
}
