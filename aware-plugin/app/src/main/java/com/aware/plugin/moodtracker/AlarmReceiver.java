package com.aware.plugin.moodtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by zhanna on 15/02/16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent activity = new Intent(context, EsmQuestionnaire.class);
        context.startActivity(activity);

    }
}
