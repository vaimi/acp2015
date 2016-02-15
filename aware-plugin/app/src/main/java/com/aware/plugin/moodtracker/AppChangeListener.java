package com.aware.plugin.moodtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aware.Aware;

/**
 *  Class to listen new apps on foreground broadcasts by AWARE framework
 */
public class AppChangeListener extends BroadcastReceiver {
    private Context context = null;

    /**
     * Listener for broadcasts. Checks the trigger from database and launches camera service
     * to take the photo.
     * @param c Context
     * @param intent Intent
     */
    public void onReceive(Context c, Intent intent) {
        this.context = c;

        // Check that photo analysis is checked
        if (!Aware.getSetting(c, Settings.STATUS_PLUGIN_MOODTRACKER_PHOTO).equals("true")) {
            return;
        }

        String lastApp = CommonMethods.getLastApp(context);
        if (lastApp != null) {
            if (Plugin.DEBUG) Log.d(Plugin.TAG, "New app on foreground " + lastApp);
            Intent appIntent = new Intent(context, FacePhoto.class);
            // Put AppName as extra to intent
            appIntent.putExtra("AppName", lastApp);
            // Start photo capture
            context.startService(appIntent);
        } else {
            Log.e(Plugin.TAG, "Unable to fetch last app");
        }
    }

}
