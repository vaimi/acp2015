package com.aware.plugin.moodtracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aware.Aware;

public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    //Plugin settings in XML @xml/preferences
    public static final String STATUS_PLUGIN_MOODTRACKER = "status_plugin_moodtracker";

    //Show context card
    public static final String STATUS_PLUGIN_MOODTRACKER_CONTEXTCARD = "status_plugin_moodtracker_contextcard";

    //ESM setting
    public static final String STATUS_PLUGIN_MOODTRACKER_ESM = "status_plugin_moodtracker_esm";

    //ESM camera preview setting
    public static final String STATUS_PLUGIN_MOODTRACKER_ESM_PREVIEW = "status_plugin_moodtracker_esm_preview";


    //Photo analysis setting
    public static final String STATUS_PLUGIN_MOODTRACKER_PHOTO = "status_plugin_moodtracker_photo";

    //Photo analysis setting
    public static final String PLUGIN_MOODTRACKER_WAIT = "plugin_moodtracker_wait";

    //Plugin settings UI elements
    private static CheckBoxPreference status;
    private static ListPreference statusContextCard;
    private static ListPreference statusEsm;
    private static ListPreference statusEsmPreview;
    private static ListPreference statusPhoto;
    private static EditTextPreference waitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        status = (CheckBoxPreference) findPreference(STATUS_PLUGIN_MOODTRACKER);
        if( Aware.getSetting(this, STATUS_PLUGIN_MOODTRACKER).length() == 0 ) {
            Aware.setSetting( this, STATUS_PLUGIN_MOODTRACKER, true ); //by default, the setting is true on install
        }
        status.setChecked(Aware.getSetting(getApplicationContext(), STATUS_PLUGIN_MOODTRACKER).equals("true"));

        statusContextCard = (ListPreference) findPreference(STATUS_PLUGIN_MOODTRACKER_CONTEXTCARD);
        String contextcardOn = Aware.getSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER_CONTEXTCARD);
        if (!(contextcardOn.equals("1") || contextcardOn.equals("0"))) {
            Aware.setSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER_CONTEXTCARD, "1");
        }
        statusContextCard.setValue(contextcardOn);

        statusEsm = (ListPreference) findPreference(STATUS_PLUGIN_MOODTRACKER_ESM);
        String esmOn = Aware.getSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER_ESM);
        if (!(esmOn.equals("1") || esmOn.equals("0"))) {
            Aware.setSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER_ESM, "1");
        }
        statusEsm.setValue(esmOn);

        statusEsmPreview = (ListPreference) findPreference(STATUS_PLUGIN_MOODTRACKER_ESM_PREVIEW);
        String previewOn = Aware.getSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER_ESM_PREVIEW);
        if (!(previewOn.equals("1") || previewOn.equals("0"))) {
            Aware.setSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER_ESM_PREVIEW, "1");
        }
        statusEsmPreview.setValue(previewOn);

        statusPhoto = (ListPreference) findPreference(STATUS_PLUGIN_MOODTRACKER_PHOTO);
        String photoOn = Aware.getSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER_PHOTO);
        if (!(photoOn.equals("1") || photoOn.equals("0"))) {
            Aware.setSetting(this, Settings.STATUS_PLUGIN_MOODTRACKER_PHOTO, "1");
        }
        statusPhoto.setValue(photoOn);


        if( Aware.getSetting(getApplicationContext(), Settings.PLUGIN_MOODTRACKER_WAIT).length() == 0 ) {
            Aware.setSetting(getApplicationContext(), Settings.PLUGIN_MOODTRACKER_WAIT, "5000");
        }
        waitTime = (EditTextPreference) findPreference(PLUGIN_MOODTRACKER_WAIT);
        waitTime.setText(Aware.getSetting(getApplicationContext(), PLUGIN_MOODTRACKER_WAIT));
        waitTime.setSummary("Wait " + Aware.getSetting(getApplicationContext(), PLUGIN_MOODTRACKER_WAIT) + " ms after app launch");

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //Log.i(Plugin.TAG, "Change of setting: " + sharedPreferences.getString(key, ""));
        Preference setting = findPreference(key);

        if( setting.getKey().equals(STATUS_PLUGIN_MOODTRACKER_CONTEXTCARD)) {
            Aware.setSetting(getApplicationContext(), key, sharedPreferences.getString(key, "0"));
        }
        if( setting.getKey().equals(STATUS_PLUGIN_MOODTRACKER_ESM)) {
            Aware.setSetting(getApplicationContext(), key, sharedPreferences.getString(key, "0"));
        }
        if( setting.getKey().equals(STATUS_PLUGIN_MOODTRACKER_ESM_PREVIEW)) {
            Aware.setSetting(getApplicationContext(), key, sharedPreferences.getString(key, "0"));
        }
        if( setting.getKey().equals(STATUS_PLUGIN_MOODTRACKER_PHOTO)) {
            Aware.setSetting(getApplicationContext(), key, sharedPreferences.getString(key, "0"));
        }
        if( setting.getKey().equals(PLUGIN_MOODTRACKER_WAIT)) {
            setting.setSummary("Wait " + sharedPreferences.getString(key, "5000") + " ms after app launch");
            Aware.setSetting(getApplicationContext(), key, sharedPreferences.getString(key, "5000"));
        }

        if( setting.getKey().equals(STATUS_PLUGIN_MOODTRACKER) ) {
            boolean is_active = sharedPreferences.getBoolean(key, false);
            Aware.setSetting(this, key, is_active);
            if( is_active ) {
                Aware.startPlugin(getApplicationContext(), "com.aware.plugin.moodtracker");
            } else {
                Aware.stopPlugin(getApplicationContext(), "com.aware.plugin.moodtracker");
            }
            status.setChecked(is_active);
        }
    }
}
