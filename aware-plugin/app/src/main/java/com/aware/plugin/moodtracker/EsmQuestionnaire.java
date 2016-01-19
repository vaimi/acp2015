package com.aware.plugin.moodtracker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;

/**
 * Created by vaimi on 3.12.2015.
 */
public class EsmQuestionnaire extends Activity {

    private SeekBar seekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.esmscreen);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        }


        }
}
