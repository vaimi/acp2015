package com.aware.plugin.moodtracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

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
    private static CheckBoxPreference statusContextCard;
    private static CheckBoxPreference statusEsm;
    private static CheckBoxPreference statusEsmPreview;
    private static CheckBoxPreference statusPhoto;
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

        statusContextCard = (CheckBoxPreference) findPreference(STATUS_PLUGIN_MOODTRACKER_CONTEXTCARD);

        statusEsm = (CheckBoxPreference) findPreference(STATUS_PLUGIN_MOODTRACKER_ESM);

        statusEsmPreview = (CheckBoxPreference) findPreference(STATUS_PLUGIN_MOODTRACKER_ESM_PREVIEW);

        statusPhoto = (CheckBoxPreference) findPreference(STATUS_PLUGIN_MOODTRACKER_PHOTO);

        waitTime = (EditTextPreference) findPreference(PLUGIN_MOODTRACKER_WAIT);
        waitTime.setText(Aware.getSetting(getApplicationContext(), PLUGIN_MOODTRACKER_WAIT));
        waitTime.setSummary("Wait " + Aware.getSetting(getApplicationContext(), PLUGIN_MOODTRACKER_WAIT) + " ms after app launch");

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference setting = findPreference(key);

        if( setting.getKey().equals(STATUS_PLUGIN_MOODTRACKER_CONTEXTCARD)) {
            boolean is_active = sharedPreferences.getBoolean(key, false);
            Aware.setSetting(getApplicationContext(), key, is_active);
            statusContextCard.setChecked(is_active);
        }
        if( setting.getKey().equals(STATUS_PLUGIN_MOODTRACKER_ESM)) {
            boolean is_active = sharedPreferences.getBoolean(key, false);
            Aware.setSetting(getApplicationContext(), key, is_active);
            statusEsm.setChecked(is_active);
        }
        if( setting.getKey().equals(STATUS_PLUGIN_MOODTRACKER_ESM_PREVIEW)) {
            boolean is_active = sharedPreferences.getBoolean(key, false);
            Aware.setSetting(getApplicationContext(), key, is_active);
            statusEsmPreview.setChecked(is_active);
        }
        if( setting.getKey().equals(STATUS_PLUGIN_MOODTRACKER_PHOTO)) {
            boolean is_active = sharedPreferences.getBoolean(key, false);
            Aware.setSetting(getApplicationContext(), key, is_active);
            statusPhoto.setChecked(is_active);
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
