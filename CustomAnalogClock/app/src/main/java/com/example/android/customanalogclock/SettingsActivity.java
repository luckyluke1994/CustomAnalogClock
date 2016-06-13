package com.example.android.customanalogclock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity implements TimePickerFragment.SetTimeComplete{
    public static final String EXTRA_HOUR = "hour";
    public static final String ETRA_MINUTE = "minute";
    public static final int REQUEST_SKILL = 1;

    private TextView mTextView;
    private Button mButton;
    private Button syncButton;
    private Button changeSkillButton;
    private ImageView mImageView;

    private Resources mResources;

    private float mHour;
    private float mMinute;
    private int mSkill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mResources = getResources();

        mTextView = (TextView) findViewById(R.id.time_textview);
        mImageView = (ImageView) findViewById(R.id.skill_imageView);
        mButton = (Button) findViewById(R.id.change_button);
        syncButton = (Button) findViewById(R.id.sync_button);
        changeSkillButton = (Button) findViewById(R.id.change_skillButton);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra(MainActivity.SYNC, true);
                i.putExtra(MainActivity.SKILL, mSkill);
                SettingsActivity.this.setResult(Activity.RESULT_OK, i);
                SettingsActivity.this.finish();
            }
        });

        changeSkillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, ChooseSkillActivity.class);
                startActivityForResult(i, REQUEST_SKILL);
            }
        });

        getData();
        loadSkill();

        mTextView.setText(Utils.formatTime(mHour, mMinute));
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePicker = new TimePickerFragment();
                timePicker.setTime(mHour, mMinute);
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
//                if (null != NavUtils.getParentActivityName(this)) {
//                    NavUtils.navigateUpFromSameTask(this);
//                }
//                Intent i = new Intent();
//                i.putExtra(EXTRA_HOUR, mHour);
//                i.putExtra(ETRA_MINUTE, mMinute);
//                i.putExtra(MainActivity.SKILL, mSkill);
//                SettingsActivity.this.setResult(Activity.RESULT_OK, i);
//                SettingsActivity.this.finish();
//                return true;
            case R.id.action_approve:
                Intent i = new Intent();
                i.putExtra(EXTRA_HOUR, mHour);
                i.putExtra(ETRA_MINUTE, mMinute);
                i.putExtra(MainActivity.SKILL, mSkill);
                SettingsActivity.this.setResult(Activity.RESULT_OK, i);
                SettingsActivity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_SKILL) {
            mSkill = data.getIntExtra(MainActivity.SKILL, R.drawable.clock_dial);
            loadSkill();
        }
    }

    public void loadSkill() {
        mImageView.setImageResource(mSkill);
    }

    @SuppressWarnings("deprecation")
    public void getData() {
        Time Calendar = new Time();
        Calendar.setToNow();
        float hour = Calendar.hour;
        float minute = Calendar.minute;

        mHour = getIntent().getFloatExtra(MainActivity.HOUR, hour);
        mMinute = getIntent().getFloatExtra(MainActivity.MINUTE, minute);

        mSkill = getIntent().getIntExtra(MainActivity.SKILL, R.drawable.clock_dial);
    }

    @Override
    public void onCompleted(float hour, float minute) {
        mHour = hour;
        mMinute = minute;

        mTextView.setText(Utils.formatTime(mHour, mMinute));
        SharedPreferences.Editor editor = getSharedPreferences(MainActivity.AnalogClockPref, Context.MODE_PRIVATE).edit();
        editor.putBoolean(MainActivity.SYNC, false);
    }
}
