package com.aware.plugin.moodtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class AppChangeListener extends BroadcastReceiver {
    public void onReceive(Context c, Intent intent) {
        //final Context c_final = c;
        //String trigger = intent.getExtras().getString("Applications_Foreground.package_name");
        //Log.d(Plugin.TAG, "Opened " + trigger);
        c.startService(new Intent(c, FacePhoto.class));
    }
}
