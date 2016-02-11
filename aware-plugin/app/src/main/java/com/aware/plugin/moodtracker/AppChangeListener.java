package com.aware.plugin.moodtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.aware.providers.Applications_Provider;

public class AppChangeListener extends BroadcastReceiver {
    public void onReceive(Context c, Intent intent) {
        // Get the app on front
        Cursor cursor = c.getContentResolver().query(Applications_Provider.Applications_Foreground.CONTENT_URI, new String[] { Applications_Provider.Applications_Foreground.PACKAGE_NAME}, null, null, Applications_Provider.Applications_Foreground.TIMESTAMP + " DESC LIMIT 1");
        if (cursor != null && cursor.moveToFirst()) {
            Intent appIntent = new Intent(c, FacePhoto.class);
            Log.d(Plugin.TAG, "New app on foreground " + cursor.getString(0));
            // Put AppName as extra to intent
            appIntent.putExtra("AppName", cursor.getString(0));
            appIntent.putExtra("noPreview", cursor.getString(0));
            // Start photo capture
            c.startService(appIntent);
        }
        // close the cursor
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }
}
