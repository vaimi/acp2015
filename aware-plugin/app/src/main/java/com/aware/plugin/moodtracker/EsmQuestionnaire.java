package com.aware.plugin.moodtracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
                Log.d("Seek bar", moodValue + "");

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
                Log.d("submit", "pressed");
                editor = prefs.edit();
                editor.putBoolean("Delayed", DELAYED);
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
        Log.d("delay", "pressed");
//        DELAYED = true;
//        editor = prefs.edit();
//        editor.putBoolean("Delayed", DELAYED);
//        editor.commit();


        Intent myIntent = new Intent(getApplicationContext(), MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 300000, pendingIntent);

        finish();
    }


}
