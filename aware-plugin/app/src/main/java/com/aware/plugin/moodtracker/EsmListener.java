package com.aware.plugin.moodtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aware.Aware;

/**
 * Created by Mikko on 18.2.2016.
 */
public class EsmListener extends BroadcastReceiver {
    private Context context;

    public void onReceive(Context c, Intent intent) {
        this.context = c;

        if (!Aware.getSetting(c, Settings.STATUS_PLUGIN_MOODTRACKER_ESM).equals("1")) {
            return;
        }
        Intent esmIntent = new Intent(context, EsmQuestionnaire.class);
        esmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(esmIntent);
    }
}