package com.aware.plugin.moodtracker;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jilin on 1/19/2016.
 */
public class MyDate {
    private int days;
    private int month;
    private int year;

    public int getDays() {
        return days;
    }

    //DayOfMonth, 1 to 31 if there is any
    public static int toDay(long timestamp) {
        DateFormat format = new SimpleDateFormat("dd");
        //Long l = new Long("1455193063740");
        return Integer.parseInt(format.format(new Date(timestamp)));
    }

    //The very beginning of this month, use >=
    public long getMonthStartTime() {
        return this.getTimeInMillis();
    }

    private long getTimeInMillis() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1, 0, 0, 0);
        return cal.getTimeInMillis();
    }

    //The very beginning of next month, use <
    public long getMonthEndTime() {
        MyDate myDate = new MyDate(year, month);
        myDate.plusOneMonth();
        return myDate.getTimeInMillis();
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public MyDate(int year, int month) {
        this.days = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        this.month = month;
        this.year = year;
    }

    public void minusOneMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1, 0, 0, 0);
        cal.add(Calendar.MONTH, -1);
        days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
    }

    public void plusOneMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1, 0, 0, 0);
        cal.add(Calendar.MONTH, 1);
        days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
    }
}
