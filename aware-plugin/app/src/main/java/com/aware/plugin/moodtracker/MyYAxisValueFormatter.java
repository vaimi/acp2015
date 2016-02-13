package com.aware.plugin.moodtracker;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

/**
 * Created by Jilin on 1/25/2016.
 */
public class MyYAxisValueFormatter implements YAxisValueFormatter {
    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        //Assuming there will be only labels: 0, 1, 2, 3, 4, 5, 6
        if (value == 0)
            return "Very sad";
        else if (value == 1)    //Sad
            return "";
        else if (value == 2)    //Slightly sad
            return "";
        else if (value == 3)
            return "Neutral";
        else if (value == 4)    //Slightly happy
            return "";
        else if (value == 5)    //Happy
            return "";
        else if (value == 6)
            return "Very happy";
        else
            return "";
    }
}
