package com.aware.plugin.moodtracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.aware.Aware;

/**
 * Created by Jilin on 2/22/2016.
 */
public class MoodHistory extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moodhistory);

        Intent aware = new Intent(this, Aware.class);
        startService(aware);
        SystemClock.sleep(500);
        LinearLayout parent = (LinearLayout) findViewById(R.id.activity_card);
        parent.addView(new ContextCard().getContextCard(getApplicationContext()));
    }
}
