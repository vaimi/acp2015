package com.aware.plugin.moodtracker;

import android.content.Intent;

import com.aware.Applications;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.utils.Aware_Plugin;

import android.content.IntentFilter;
import android.net.Uri;

import java.util.jar.Manifest;

public class Plugin extends Aware_Plugin {
    private static AppChangeListener acl = new AppChangeListener();

    @Override
    public void onCreate() {
        super.onCreate();



        TAG = "AWARE::"+getResources().getString(R.string.app_name);
        DEBUG = Aware.getSetting(this, Aware_Preferences.DEBUG_FLAG).equals("true");

        //Initialize our plugin's settings
        if( Aware.getSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER).length() == 0 ) {
            Aware.setSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER, true);
        }
        if( Aware.getSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER_CONTEXTCARD).length() == 0 ) {
            Aware.setSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER_CONTEXTCARD, true);
        }
        if( Aware.getSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER_ESM).length() == 0 ) {
            Aware.setSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER_ESM, true);
        }
        if( Aware.getSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER_ESM_PREVIEW).length() == 0 ) {
            Aware.setSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER_ESM_PREVIEW, true);
        }
        if( Aware.getSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER_PHOTO).length() == 0 ) {
            Aware.setSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER_PHOTO, true);
        }
        if( Aware.getSetting(getApplicationContext(), Settings.PLUGIN_MOODTRACKER_WAIT).length() == 0 ) {
            Aware.setSetting(getApplicationContext(), Settings.PLUGIN_MOODTRACKER_WAIT, 5000);
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

        Aware.setSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER, false);

        // Unregister app change listener
        unregisterReceiver(acl);

        //Deactivate any sensors/plugins you activated here
        //e.g., Aware.setSetting(this, Aware_Preferences.STATUS_ACCELEROMETER, false);
        Aware.setSetting(this, Aware_Preferences.STATUS_APPLICATIONS, false);

        //Stop plugin
        Aware.stopPlugin(this, "com.aware.plugin.moodtracker");
    }
}
