package com.example.android.customanalogclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.android.customanalogclock.service.RingtonePlayingService;
import com.example.android.customanalogclock.ui.CustomAnalogClock;

public class AlarmActivity extends AppCompatActivity {
    boolean isAlarm;
    float hourAlarm;
    float minuteAlarm;

    private TimePicker mTimePicker;
    private Button mSetAlarmButton;
    private Button mCancelAlarmButton;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTimePicker = (TimePicker) findViewById(R.id.timepicker);
        mSetAlarmButton = (Button) findViewById(R.id.alarm_on_button);
        mCancelAlarmButton = (Button) findViewById(R.id.alarm_off_button);
        mTextView = (TextView) findViewById(R.id.textview);

        getDataFromIntent();

        mSetAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAlarm = true;
                setAlarmText("Alarm set to " + (int) hourAlarm + ":" +
                        (int) minuteAlarm);
            }
        });

        mCancelAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAlarm = false;
                setAlarmText("Alarm is not set");
                Intent i = new Intent(AlarmActivity.this, RingtonePlayingService.class);
                i.putExtra(CustomAnalogClock.RINGTONE_STATUS, "stop");
                AlarmActivity.this.startService(i);
            }
        });

        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hourAlarm = (float) hourOfDay;
                minuteAlarm = (float) minute;
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.action_approve:
                Intent i = new Intent();
                i.putExtra(MainActivity.IS_ALARM, isAlarm);
                i.putExtra(MainActivity.HOUR_ALARM, hourAlarm);
                i.putExtra(MainActivity.MINUTE_ALARM, minuteAlarm);
                this.setResult(Activity.RESULT_OK, i);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    private void getDataFromIntent() {
        Intent localIntent = getIntent();
        isAlarm = localIntent.getBooleanExtra(MainActivity.IS_ALARM, false);
        hourAlarm = localIntent.getFloatExtra(MainActivity.HOUR_ALARM, 0);
        minuteAlarm = localIntent.getFloatExtra(MainActivity.MINUTE_ALARM, 0);
        if (isAlarm) {
            setAlarmText("Alarm set to " + (int)hourAlarm + ":" + (int)minuteAlarm);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mTimePicker.setHour((int) hourAlarm);
                mTimePicker.setMinute((int) minuteAlarm);
            } else {
                mTimePicker.setCurrentHour((int) hourAlarm);
                mTimePicker.setCurrentMinute((int) minuteAlarm);
            }
        } else {
            setAlarmText("Alarm is not set");
        }
    }

    private void setAlarmText(String message) {
        mTextView.setText(message);
    }
}
