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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aware.providers.Applications_Provider;
import com.aware.ui.Stream_UI;
import com.aware.utils.IContextCard;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class ContextCard implements IContextCard {
    //Empty constructor
    public ContextCard(){}

    //Static values to check later
    MyDate myDate;

    @Override
    public View getContextCard(final Context context) {
        LayoutInflater sInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View card = sInflater.inflate(R.layout.card, null);
        final LinearLayout chart = (LinearLayout) card.findViewById(R.id.chart);
        final TextView month = (TextView) card.findViewById(R.id.month);
        final TextView year = (TextView) card.findViewById(R.id.year);
        Button previousButton = (Button) card.findViewById(R.id.prev_btn);
        Button nextButton = (Button) card.findViewById(R.id.next_btn);

        //Attempt to start from current datetime (month, year)
        Calendar c = Calendar.getInstance();
        myDate = new MyDate(c.get(Calendar.MONTH), c.get(Calendar.YEAR));
        //Remember month value is 1 less than actual month value
        month.setText("" + (myDate.getMonth() + 1));
        year.setText("" + (myDate.getYear()));

        //Set button callbacks
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDate.minusOneMonth();
                refreshGraph(chart, context, myDate.getDays());
                month.setText("" + (myDate.getMonth() + 1));
                year.setText("" + (myDate.getYear()));
                Log.d("AWARE", "" + "Year:" + myDate.getYear() + " Month: " + myDate.getMonth() + " Days: " + myDate.getDays());
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDate.plusOneMonth();
                refreshGraph(chart, context, myDate.getDays());
                month.setText("" + (myDate.getMonth() + 1));
                year.setText("" + (myDate.getYear()));
                Log.d("AWARE", "" + "Year:" + myDate.getYear() + " Month: " + myDate.getMonth() + " Days: " + myDate.getDays());

            }
        });
        refreshGraph(chart, context, myDate.getDays());
        Cursor cursor = context.getContentResolver().query(Provider.Moodtracker_Data.CONTENT_URI,
                null, null, null, null);
        if (cursor == null)
            Toast.makeText(context, "null", Toast.LENGTH_SHORT).show();
        if (cursor != null && cursor.moveToFirst())
            Toast.makeText(context, "something", Toast.LENGTH_SHORT).show();
        /*Log.d("AWARE", cursor.getString(0) + ":" + cursor.getString(1));
        Toast.makeText(context, cursor.getString(0) + ";" + cursor.getString(1), Toast.LENGTH_SHORT).show();*/
        Toast.makeText(context, "done1", Toast.LENGTH_SHORT).show();

        return card;
    }



    private void refreshGraph(LinearLayout chart, Context context, int days) {
        chart.removeAllViews();
        chart.addView(drawGraph(context, days));
        chart.invalidate();
    }

    private View drawGraph(Context context, int days) {
        //TODO - Get data
        //TODO - Better to show max day as well each time
        ArrayList<String> x = new ArrayList<>();
        ArrayList<Entry> barEntries = new ArrayList<>();

        for(int i=1; i<=days; i++) {
            x.add(String.valueOf(i));
            // nextInt: [0,n)
            barEntries.add(new Entry(new Random().nextInt(7), i));
        }

        LineDataSet dataSet = new LineDataSet(barEntries, "Happiness");
        dataSet.setColor(Color.parseColor("#33B5E5"));
        dataSet.setDrawValues(false);

        LineData data = new LineData(x, dataSet);

        LineChart mChart = new LineChart(context);

        mChart.setContentDescription("");
        mChart.setDescription("");
        mChart.setMinimumHeight(200);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBorders(false);

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
}
