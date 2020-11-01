package com.yellow.widgetdemo.widgetsUI;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import com.yellow.widgetdemo.R;
import com.yellow.widgetdemo.widgets.ArcProgressBar;

/**
 * Created by Freeman on 16-1-20.
 */
public class ArcProgressBarActivity extends Activity {

    private ArcProgressBar mArcProgressBar;
    private SeekBar mSeekBar;
    private Switch mSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arcprogressbar_activity_layout);
        mArcProgressBar = findViewById(R.id.arc_progress_bar);
        mSeekBar = findViewById(R.id.my_seek_bar);
        mSwitch = findViewById(R.id.anim_switch);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mArcProgressBar.setProgress(seekBar.getProgress() / 100.f);
            }
        });

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mArcProgressBar.needProgressAnim(isChecked);
            }
        });
    }
}

