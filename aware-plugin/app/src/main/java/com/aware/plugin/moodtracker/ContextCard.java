package com.aware.plugin.moodtracker;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aware.Aware;
import com.aware.providers.Applications_Provider;
import com.aware.ui.Stream_UI;
import com.aware.utils.IContextCard;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ContextCard implements IContextCard {
    //Empty constructor
    public ContextCard(){}

    //Static values to check later
    MyDate myDate;

    @Override
    public View getContextCard(final Context context) {
        if (!Aware.getSetting(context, Settings.STATUS_PLUGIN_MOODTRACKER_CONTEXTCARD).equals("true")) {
            return null;
        }

        LayoutInflater sInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View card = sInflater.inflate(R.layout.card, null);
        final LinearLayout chart = (LinearLayout) card.findViewById(R.id.chart);
        final TextView month = (TextView) card.findViewById(R.id.month);
        final TextView year = (TextView) card.findViewById(R.id.year);
        Button previousButton = (Button) card.findViewById(R.id.prev_btn);
        Button nextButton = (Button) card.findViewById(R.id.next_btn);

        //Attempt to start from current datetime (month, year)
        Calendar c = Calendar.getInstance();
        myDate = new MyDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH));
        //Remember month value is 1 less than actual month value
        month.setText("" + (myDate.getMonth() + 1));
        year.setText("" + (myDate.getYear()));
        Log.d("AWARE", "" + "Year:" + myDate.getYear() + " Month: " + myDate.getMonth() + " Days: " + myDate.getDays());

        //Set button callbacks
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDate.minusOneMonth();
                refreshGraph(chart, context, myDate);
                month.setText("" + (myDate.getMonth() + 1));
                year.setText("" + (myDate.getYear()));
                Log.d("AWARE", "" + "Year:" + myDate.getYear() + " Month: " + myDate.getMonth() + " Days: " + myDate.getDays());
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDate.plusOneMonth();
                refreshGraph(chart, context, myDate);
                month.setText("" + (myDate.getMonth() + 1));
                year.setText("" + (myDate.getYear()));
                Log.d("AWARE", "" + "Year:" + myDate.getYear() + " Month: " + myDate.getMonth() + " Days: " + myDate.getDays());

            }
        });
        refreshGraph(chart, context, myDate);

        //Toast.makeText(context, "done7", Toast.LENGTH_SHORT).show();

        return card;
    }



    private void refreshGraph(LinearLayout chart, Context context, MyDate myDate) {
        chart.removeAllViews();
        chart.addView(drawGraph(context, myDate));
        chart.invalidate();
    }
    //convert it into labels as:
    /*very happy: (6/7 - 1]
    happy:  (5/7 - 6/7]
    slightly happy: (4/7 - 5/7]
    neutral:  [3/7 - 4/7]
    slightly sad: [2/7 - 3/7)
    sad:  [1/7 - 2/7)
    very sad: [0 - 1/7)*/
    private View drawGraph(Context context, MyDate myDate) {
        //TODO - Better to show max day as well each time
        ArrayList<String> x = new ArrayList<>();
        ArrayList<Entry> barEntries = new ArrayList<>();

        for(int i=1; i<=myDate.getDays(); i++) {
            x.add(String.valueOf(i));
        }

        // nextInt: [0,n)
        //barEntries.add(new Entry(new Random().nextInt(7), i));

        //Get data from database, only need data from this month, also filter records with -1 as happiness value. 0 - 1
        /*Cursor cursor = context.getContentResolver().query(Provider.Moodtracker_Data.CONTENT_URI,
                new String[] { Provider.Moodtracker_Data.TIMESTAMP, Provider.Moodtracker_Data.HAPPINESS_VALUE },
                Provider.Moodtracker_Data.HAPPINESS_VALUE  + " != -1 and " + Provider.Moodtracker_Data.TIMESTAMP + " >= " + myDate.getMonthStartTime()
                    + " and " + Provider.Moodtracker_Data.TIMESTAMP + " < " + myDate.getMonthEndTime(), null, null);*/
        //Get data of ESM values
        Cursor cursor = context.getContentResolver().query(Provider.Moodtracker_Data.CONTENT_URI,
                new String[] {Provider.Moodtracker_Data.TIMESTAMP, Provider.Moodtracker_Data.HAPPINESS_VALUE},
                    Provider.Moodtracker_Data.TRIGGER + " == \"ESMHAPPINESS\" and "
                    + Provider.Moodtracker_Data.TIMESTAMP + " >= " + myDate.getMonthStartTime() + " and "
                    + Provider.Moodtracker_Data.TIMESTAMP + " < " + myDate.getMonthEndTime(), null, null);

        //Process data first. 0, 20, 40, 60, 80, 100, 120
        HashMap<Integer, HappinessObject> mDayHappiness = new HashMap<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //Log.d("AWARE", "" + cursor.getLong(0) + " " + cursor.getDouble(1));
                long timestamp = cursor.getLong(0);
                double happiness = (double)cursor.getDouble(1)/20;

                int day = MyDate.toDay(timestamp);
                //Log.d("AWARE", "" + day);
                HappinessObject happinessObject;

                if (mDayHappiness.containsKey(day))   //Have already some value, need to average them
                    happinessObject = mDayHappiness.get(day);
                else
                    happinessObject = new HappinessObject();

                happinessObject.addValue(happiness);
                mDayHappiness.put(day, happinessObject);

            } while(cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed())
            cursor.close();

        //Add data to the right day buffer
        for (Map.Entry<Integer, HappinessObject> entry: mDayHappiness.entrySet()) {
            Integer day = entry.getKey();
            Double happiness = entry.getValue().getValue();
            //Log.d("AWARE", "ESM happiness value:" + Float.parseFloat("" + happiness) + " Day:" + day);
            //second param of Entry starting from 0, and our day variable counts from 1
            barEntries.add(new Entry(Float.parseFloat("" + happiness), day - 1));
        }

        LineDataSet dataSet = new LineDataSet(barEntries, "Happiness");
        dataSet.setColor(Color.parseColor("#33B5E5"));
        dataSet.setDrawValues(false);

        LineData data = new LineData(x, dataSet);

        LineChart mChart = new LineChart(context);

        mChart.getLegend().setEnabled(false);
        mChart.setContentDescription("");
        mChart.setDescription("");
        //Dynamically stretch the chart to a reasonable height for viewing's sake
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        //Usual good percentage of screen height
        int niceHeight = (int) Math.round(metrics.heightPixels * 0.3);
        mChart.setMinimumHeight((niceHeight > 200) ? niceHeight : 200);
        mChart.setMinimumWidth((int)Math.round(metrics.widthPixels * 0.8));
        
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBorders(false);

        //Modify the legend so that it won't eat too much space from chart
        Legend legend = mChart.getLegend();
        legend.setEnabled(false);

        //TODO - MarkerView if user wants to see detailed info of certain data point

        YAxis left = mChart.getAxisLeft();
        left.setDrawLabels(true);
        left.setDrawGridLines(true);
        left.setDrawAxisLine(true);
        left.setLabelCount(7, true);
        left.setAxisMaxValue(6);
        left.setAxisMinValue(0);
        left.setValueFormatter(new MyYAxisValueFormatter());

        YAxis right = mChart.getAxisRight();
        right.setDrawAxisLine(false);
        right.setDrawLabels(false);
        right.setDrawGridLines(false);

        LimitLine ll = new LimitLine(3, "");
        ll.setLineWidth(4f);
        ll.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        left.addLimitLine(ll);
        left.setDrawLimitLinesBehindData(true);

        XAxis bottom = mChart.getXAxis();
        bottom.setPosition(XAxis.XAxisPosition.BOTTOM);
        bottom.setSpaceBetweenLabels(0);
        bottom.setDrawGridLines(false);
        bottom.setDrawAxisLine(true);

        mChart.setData(data);
        mChart.invalidate();
        mChart.animateX(1000);

        return mChart;
    }

    private class HappinessObject {
        private int records;
        private double value;

        public HappinessObject() {
            this.records = 0;
            this.value = -1;
        }

        public Double getValue() {
            return this.value;
        }

        public void addValue(double newValue){
            if (this.value == .1)
                this.value = newValue;
            else
                average(newValue);
            records++;
        }

        private void average(double newValue) {
            this.value = ((records * value) + newValue) * 1.0 / (records + 1);
        }
    }
}
