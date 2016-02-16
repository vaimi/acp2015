package com.aware.plugin.moodtracker;

import android.app.Activity;
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

/**
 * Created by vaimi on 3.12.2015.
 */
public class EsmQuestionnaire extends Activity {

    private SeekBar seekBar;
    private int moodValue;
    private int stepSize = 20;
    private Button asklaterbtn;
    private Button submitbtn;
    private SharedPreferences prefs;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static Boolean DELAYED = false;
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
                remindLater();
            }
        });

    }

    private void remindLater(){
        if (Plugin.DEBUG) Log.d("delay", "pressed");
        DELAYED = true;
        editor = prefs.edit();
        editor.putBoolean("Delayed", DELAYED);
        editor.commit();
        scheduleReminder();
        finish();
    }

    private void scheduleReminder() {
        Scheduler.removeSchedule(getApplicationContext(), "schedule_reminder");
        // start ESMQuestionnaire activity in 5 min
        Scheduler.Schedule schedule = new Scheduler.Schedule("schedule_reminder");
        long time = Calendar.getInstance().getTimeInMillis();
        long timeToRemind = time + 300000;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeToRemind);
        try {
            schedule.setTimer(c)
                    .setActionType(Scheduler.ACTION_TYPE_ACTIVITY)
                    .setActionClass("com.aware.plugin.moodtracker/com.aware.plugin.moodtracker.EsmQuestionnaire");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Scheduler.saveSchedule(getApplicationContext(), schedule);
        if (Plugin.DEBUG) Log.d(Plugin.TAG, "Esm Rescheduled");
    }



}
