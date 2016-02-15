package com.aware.plugin.moodtracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.aware.Applications;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.ESM;
import com.aware.utils.Aware_Plugin;
import com.aware.utils.Scheduler;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.jar.Manifest;

public class Plugin extends Aware_Plugin {
    private static AppChangeListener acl = new AppChangeListener();
    private SharedPreferences prefs;

    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    public void onCreate() {
        super.onCreate();

        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean delayed = prefs.getBoolean("Delayed", false);


        TAG = "AWARE::"+getResources().getString(R.string.app_name);
        DEBUG = Aware.getSetting(this, Aware_Preferences.DEBUG_FLAG).equals("true");

        //Initialize our plugin's settings
        if( Aware.getSetting(this, Settings.STATUS_PLUGIN_TEMPLATE).length() == 0 ) {
            Aware.setSetting(this, Settings.STATUS_PLUGIN_TEMPLATE, true);
        }

        //Activate programmatically any sensors/plugins you need here
        //e.g., Aware.setSetting(this, Aware_Preferences.STATUS_ACCELEROMETER,true);
        //NOTE: if using plugin with dashboard, you can specify the sensors you'll use there.


        Aware.setSetting(this, Aware_Preferences.STATUS_APPLICATIONS, true);
        Aware.startSensor(this, Aware_Preferences.STATUS_APPLICATIONS);

        IntentFilter broadcastFilter = new IntentFilter();
        broadcastFilter.addAction(Applications.ACTION_AWARE_APPLICATIONS_FOREGROUND);
        registerReceiver(acl, broadcastFilter);

        //Any active plugin/sensor shares its overall context using broadcasts
        CONTEXT_PRODUCER = new ContextProducer() {
            @Override
            public void onContext() {
                //Broadcast your context here
            }
        };

        //Add permissions you need (Support for Android M) e.g.,
        //REQUIRED_PERMISSIONS.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //REQUIRED_PERMISSIONS.add(Manifest.permission.CAMERA);

        //To sync data to the server, you'll need to set this variables from your ContentProvider
        DATABASE_TABLES = Provider.DATABASE_TABLES;
        TABLES_FIELDS = Provider.TABLES_FIELDS;
        CONTEXT_URIS = new Uri[]{ Provider.Moodtracker_Data.CONTENT_URI };

        //Activate plugin
        Aware.startPlugin(this, "com.aware.plugin.moodtracker");

        if (delayed){
            // start ESMQuestionnaire activity in 5 min
            Scheduler.Schedule schedule = new Scheduler.Schedule("schedule_id");
            long time = Calendar.getInstance().getTimeInMillis();
            long timeToRemind = time + 300000;
            Calendar c = null;

            c.setTimeInMillis(timeToRemind);
            try {
                schedule.setTimer(c)
                        .setActionType(Scheduler.ACTION_TYPE_ACTIVITY)
                        .setActionClass("com.aware.plugin.moodtracker/com.aware.plugin.moodtracker.EsmQuestionnaire");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Scheduler.saveSchedule(getApplicationContext(), schedule);

        } else {
            try{

                Scheduler.Schedule schedule = new Scheduler.Schedule("schedule_id");
                schedule.addHour(9) //0-23
                        .addHour(13)
                        .addHour(17)
                        .addHour(21)
                        .setActionType(Scheduler.ACTION_TYPE_ACTIVITY)
                        .setActionClass("com.aware.plugin.moodtracker/com.aware.plugin.moodtracker.EsmQuestionnaire");

                Scheduler.saveSchedule(getApplicationContext(), schedule);

                //to remove
                //Scheduler.removeSchedule(getApplicationContext(), "schedule_id");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //This function gets called every 5 minutes by AWARE to make sure this plugin is still running.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Check if the user has toggled the debug messages
        DEBUG = Aware.getSetting(this, Aware_Preferences.DEBUG_FLAG).equals("true");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Aware.setSetting(this, Settings.STATUS_PLUGIN_TEMPLATE, false);

        //Deactivate any sensors/plugins you activated here
        //e.g., Aware.setSetting(this, Aware_Preferences.STATUS_ACCELEROMETER, false);
        Aware.setSetting(this, Aware_Preferences.STATUS_APPLICATIONS, false);

        //Stop plugin
        Aware.stopPlugin(this, "com.aware.plugin.moodtracker");
    }
}
