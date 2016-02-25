package com.aware.plugin.moodtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.aware.Aware;

import java.util.TreeMap;

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

        // Workaround to filer esm entries
        /*Cursor cursor = context.getContentResolver()
                .query(Provider.Moodtracker_Data.CONTENT_URI, new String[] {"timestamp"}, "trigger='ESMHAPPINESS'", null, "timestamp DESC LIMIT 1");
        if (cursor != null && cursor.moveToFirst()) {
            long now = System.currentTimeMillis();
            if ((now - cursor.getLong(0)) < 3600000) {
                if (Plugin.DEBUG) Log.d(Plugin.TAG, "ESM was shown < hour ago. Aborting");
                return;
            }
        }*/

        Intent esmIntent = new Intent(context, EsmQuestionnaire.class);
        esmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(esmIntent);
    }
}