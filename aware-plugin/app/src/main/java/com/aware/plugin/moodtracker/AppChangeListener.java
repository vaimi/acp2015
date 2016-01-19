package com.aware.plugin.moodtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class AppChangeListener extends BroadcastReceiver {
    public void onReceive(Context c, Intent intent) {
        //final Context c_final = c;
        //Object o = intent.getExtras().get("Applications.ACTION_AWARE_APPLICATIONS_FOREGROUND");
        Log.d(Plugin.TAG, "New app on foreground");
        c.startService(new Intent(c, FacePhoto.class));

    }
}
