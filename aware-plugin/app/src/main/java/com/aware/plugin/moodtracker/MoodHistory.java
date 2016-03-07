package com.aware.plugin.moodtracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aware.Aware;

/**
 * Created by Jilin on 2/22/2016.
 */
public class MoodHistory extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent aware = new Intent(this, Aware.class);
        startService(aware);
        Intent plugin = new Intent(this, Plugin.class);
        startService(plugin);
        SystemClock.sleep(500);
        setContentView(R.layout.activity_moodhistory);
        LinearLayout parent = (LinearLayout) findViewById(R.id.activity_card);
        if (!Aware.getSetting(getApplicationContext(), Settings.STATUS_PLUGIN_MOODTRACKER_CONTEXTCARD).equals("1")) {
            TextView notEnabled = new TextView(this);
            notEnabled.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            notEnabled.setText("Not enabled");
            parent.addView(notEnabled);
        } else  {
            parent.addView(new ContextCard().getContextCard(getApplicationContext()));
        }
    }
}
