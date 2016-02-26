package com.aware.plugin.moodtracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.utils.Scheduler;

import org.json.JSONException;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by vaimi on 3.12.2015.
 */


public class EsmQuestionnaire extends Activity {

    private Boolean submitted = false;
    private SeekBar seekBar;
    private int moodValue;
    private int stepSize = 20;
    private Button asklaterbtn;
    private Button submitbtn;
    private static AlarmManager alarmManager;
    private SharedPreferences prefs;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.esmscreen);

        asklaterbtn = (Button) findViewById(R.id.asklaterbtn);
        submitbtn = (Button) findViewById(R.id.submitbtn);

        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.incrementProgressBy(20);
        seekBar.setMax(120);
        seekBar.setProgress(60);

        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                moodValue = seekBar.getProgress();
                //Log.d("Seek bar", moodValue + "");

                progress = ((int) Math.round(progress / stepSize)) * stepSize;
                seekBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodValue = seekBar.getProgress();

                ContentValues new_data = new ContentValues();
                new_data.put(Provider.Moodtracker_Data.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
                new_data.put(Provider.Moodtracker_Data.TIMESTAMP, System.currentTimeMillis());
                new_data.put(Provider.Moodtracker_Data.HAPPINESS_VALUE, moodValue);
                new_data.put(Provider.Moodtracker_Data.TRIGGER, "ESMHAPPINESS");
                //Insert the data to the ContentProvider
                getApplicationContext()
                        .getContentResolver()
                        .insert(Provider.Moodtracker_Data.CONTENT_URI, new_data);

                submitted = true;
                Intent myIntent = new Intent(getApplicationContext(), EsmListener.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
                if (Plugin.DEBUG) Log.d("submit", "pressed");
                Intent cameraIntent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(cameraIntent);
                //editor.putBoolean("Delayed", DELAYED);
                finish();

            }
        });

        asklaterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (!submitted) scheduleReminder();
    }

    private void scheduleReminder() {
        Log.d(Plugin.TAG, "Esm Rescheduled");
        Intent myIntent = new Intent(getApplicationContext(), EsmListener.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 15 * 60 * 1000, pendingIntent);
        /*Scheduler.removeSchedule(getApplicationContext(), "schedule_reminder");
        // start ESMQuestionnaire activity in 5 min
        Scheduler.Schedule schedule = new com.aware.plugin.moodtracker.Scheduler.Schedule("schedule_reminder");
        long time = System.currentTimeMillis();
        long timeToRemind = time + 15*60*1000;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeToRemind);
        try {
            schedule.setTimer(c)
                    .setActionType(Scheduler.ACTION_TYPE_BROADCAST)
                    .setActionClass("com.aware.plugin.moodtracker.esm.launch");
                    //.setActionClass("com.aware.plugin.moodtracker/com.aware.plugin.moodtracker.EsmQuestionnaire");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Scheduler.saveSchedule(getApplicationContext(), schedule);*/
    }


}
