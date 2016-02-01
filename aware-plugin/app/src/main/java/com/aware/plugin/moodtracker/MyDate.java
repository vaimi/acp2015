package com.aware.plugin.moodtracker;

import java.util.Calendar;

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

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public MyDate(int month, int year) {
        this.days = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        this.month = month;
        this.year = year;
    }

    public MyDate minusOneMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        cal.add(Calendar.MONTH, -1);
        days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        return new MyDate(month, year);
    }

    public MyDate plusOneMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        cal.add(Calendar.MONTH, 1);
        days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        return new MyDate(month, year);
    }
}
