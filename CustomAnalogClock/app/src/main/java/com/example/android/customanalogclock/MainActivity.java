package com.example.android.customanalogclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.customanalogclock.ui.CustomAnalogClock;

public class MainActivity extends AppCompatActivity {
    public static final String AnalogClockPref = "AnalogClockPref";
    public static final String SYNC = "sync";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";
    public static final String SKILL = "skill";
    public static final String IS_ALARM = "isAlarm";
    public static final String HOUR_ALARM = "hourAlarm";
    public static final String MINUTE_ALARM = "minuteAlarm";
    public static final int REQUEST_SETTING = 0;
    public static final int REQUEST_ALARM = 1;

    private CustomAnalogClock mAnalogClock;

    private int mSkill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAnalogClock = (CustomAnalogClock) findViewById(R.id.clock);

        mSkill = mAnalogClock.getSkill();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                i.putExtra(HOUR, mAnalogClock.getHour());
                i.putExtra(MINUTE, mAnalogClock.getMinute());
                i.putExtra(SKILL, mSkill);
                startActivityForResult(i, REQUEST_SETTING);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, AlarmActivity.class);
            i.putExtra(IS_ALARM, mAnalogClock.getAlarm());
            i.putExtra(HOUR_ALARM, mAnalogClock.getHourAlarm());
            i.putExtra(MINUTE_ALARM, mAnalogClock.getMinuteAlarm());
            startActivityForResult(i, REQUEST_ALARM);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_SETTING) {
            mSkill = data.getIntExtra(SKILL, R.drawable.clock_dial);

            if (!data.hasExtra(SYNC)) {
                Time Calendar = new Time();
                Calendar.setToNow();
                float hour = data.getFloatExtra(SettingsActivity.EXTRA_HOUR, Calendar.hour);
                float minute = data.getFloatExtra(SettingsActivity.ETRA_MINUTE, Calendar.minute);
                mAnalogClock.setTime(hour, minute);
                mAnalogClock.setSkill(mSkill);
                mAnalogClock.invalidate();
            } else {
                mAnalogClock.setSync(true);
                mAnalogClock.setSkill(mSkill);
            }
        } else if (requestCode == REQUEST_ALARM) {
            mAnalogClock.setAlarm(data.getBooleanExtra(IS_ALARM, false), data.getFloatExtra(HOUR_ALARM, 0),
                    data.getFloatExtra(MINUTE_ALARM, 0));
        }
    }
}
