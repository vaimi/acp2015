package com.aware.plugin.moodtracker;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by zhanna on 15/02/16.
 */
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Niels", "Three seconds have passed! Zhanna no longer cries :-)");


        Intent intent_esmQuestionnaire = new Intent(context, EsmQuestionnaire.class);
        intent_esmQuestionnaire.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent_esmQuestionnaire);
    }
}
