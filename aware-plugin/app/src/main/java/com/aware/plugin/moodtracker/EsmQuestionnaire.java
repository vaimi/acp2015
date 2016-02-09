package com.aware.plugin.moodtracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

/**
 * Created by vaimi on 3.12.2015.
 */
public class EsmQuestionnaire extends Activity {

    private SeekBar seekBar;
    private int moodValue;
    private int stepSize = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.esmscreen);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.incrementProgressBy(20);
        seekBar.setMax(120);
        seekBar.setProgress(60);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                moodValue = seekBar.getProgress();
                Log.d("Seek bar", moodValue + "");

                progress = ((int) Math.round(progress/stepSize))*stepSize;
                seekBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });


        }
}
