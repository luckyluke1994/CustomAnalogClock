package com.example.android.customanalogclock.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;

import com.example.android.customanalogclock.MainActivity;
import com.example.android.customanalogclock.R;
import com.example.android.customanalogclock.service.RingtonePlayingService;

import java.util.TimeZone;

/**
 * Created by lucky_luke on 6/7/2016.
 */
public class CustomAnalogClock extends View {
    public static final String RINGTONE_RECEIVER = "complete";
    public static final String RINGTONE_STATUS = "status";
    @SuppressWarnings("deprecation")
    private Time mCalendar;

    private Drawable mHourHand;
    private Drawable mMinuteHand;
    private Drawable mSecondHand;
    private Drawable mDial;

    private int mDialWidth;
    private int mDialHeight;

    private Context mContext;

    private boolean mAttached;
    private boolean isSync;

    private final Handler mHandler = new Handler();
    private float mMinutes;
    private float mHour;
    private float mSecond = 0;
    private boolean mChanged;
    boolean mSeconds=false;
    MyCount counter = new MyCount(10000, 1000);

    private int mSkill = R.drawable.clock_dial;
    private Resources r;

    boolean isAlarm;
    float hourAlarm;
    float minuteAlarm;
    boolean isStartService;

    public CustomAnalogClock(Context context) {
        super(context);
    }

    /**
     * This constructor allows the layout editor to create and edit an instance of your view
     */
    @SuppressWarnings("deprecation")
    public CustomAnalogClock(Context context, AttributeSet attrs) {
        super(context, attrs);

        isAlarm = false;
        isStartService = false;

        r = context.getResources();
        mContext = context;

        loadSkill();

        mHourHand = r.getDrawable(R.drawable.clock_hour);
        mMinuteHand = r.getDrawable(R.drawable.clock_minute);
        mSecondHand = r.getDrawable(R.drawable.clockgoog_minute);

        mCalendar = new Time();

        isSync = context.getSharedPreferences(MainActivity.AnalogClockPref, Context.MODE_PRIVATE)
                .getBoolean(MainActivity.SYNC, true);

        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter();

            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

            getContext().registerReceiver(mIntentReceiver, filter, null, mHandler);
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RINGTONE_RECEIVER);
        getContext().registerReceiver(mRingtoneReceiver, intentFilter);

        mCalendar = new Time();

        // Make sure we update to the current time
        onTimeChanged();
        counter.start();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

//        if (!mAttached) {
//            mAttached = true;
//            IntentFilter filter = new IntentFilter();
//
//            filter.addAction(Intent.ACTION_TIME_TICK);
//            filter.addAction(Intent.ACTION_TIME_CHANGED);
//            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
//
//            getContext().registerReceiver(mIntentReceiver, filter, null, mHandler);
//        }
//
//        mCalendar = new Time();
//
//        // Make sure we update to the current time
//        onTimeChanged();
//        counter.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f;
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = (float) widthSize / (float) mDialWidth;
        }

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = (float) heightSize / (float) mDialHeight;
        }

        float scale = Math.min(hScale, vScale);

        setMeasuredDimension(resolveSize((int) (mDialWidth * scale), widthMeasureSpec),
                resolveSize((int) (mDialHeight * scale), heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChanged = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean changed = mChanged;
        if (changed) {
            mChanged = false;
        }
        boolean seconds = mSeconds;
        if (seconds ) {
            mSeconds = false;
        }

        int availableWidth  = getWidth();
        int availableHeight = getHeight();

        int x = availableWidth / 2;
        int y = availableHeight / 2;

        final Drawable dial = mDial;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();

        boolean scaled = false;

        if (availableWidth < w || availableHeight < h) {
            scaled = true;
            float scale = Math.min((float) availableWidth / (float) w,
                                   (float) availableHeight / (float) h);
            canvas.save();
            canvas.scale(scale, scale, x, y);
        }

        if (changed) {
            dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        dial.draw(canvas);

        canvas.save();
        float hourDegree = (mHour / 12.0f * 360.0f) + ((mMinutes + (mSecond / 60.0f)) / 60.0f * 30.0f);
        canvas.rotate(hourDegree, x, y);
        final Drawable hourHand = mHourHand;
        if (changed) {
            w = hourHand.getIntrinsicWidth();
            h = hourHand.getIntrinsicHeight();
            hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        hourHand.draw(canvas);
        canvas.restore();

        canvas.save();
        float minuteDegree = (mMinutes / 60.0f * 360.0f) + (mSecond / 60.0f * 6.0f);
        canvas.rotate(minuteDegree, x, y);
        final Drawable minuteHand = mMinuteHand;
        if (changed) {
            w = minuteHand.getIntrinsicWidth();
            h = minuteHand.getIntrinsicHeight();
            minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        minuteHand.draw(canvas);
        canvas.restore();

        canvas.save();
        canvas.rotate(mSecond * 6.0f, x, y);

        if (seconds) {
            w = mSecondHand.getIntrinsicWidth();
            h = mSecondHand.getIntrinsicHeight();
            mSecondHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        mSecondHand.draw(canvas);
        canvas.restore();
        if (scaled) {
            canvas.restore();
        }
    }

    private void onTimeChanged() {
        if (isSync) {
            mCalendar.setToNow();

            int hour = mCalendar.hour;
            int minute = mCalendar.minute;
            int second = mCalendar.second;

            mMinutes = minute;
            mHour = hour;
            mSecond = second;
            mChanged = true;
        }
    }

    public float getHour() {
        return mHour;
    }

    public float getMinute() {
        return mMinutes;
    }

    public void setSync(boolean sync) {
        isSync = sync;
        onTimeChanged();
        CustomAnalogClock.this.invalidate();
    }

    public void setTime(float hour, float minute) {
        mHour = hour;
        mMinutes = minute;
        isSync = false;
        CustomAnalogClock.this.invalidate();
    }

    public void setAlarm(boolean isOn, float hour, float minute) {
        isAlarm = isOn;
        hourAlarm = hour;
        minuteAlarm = minute;
    }

    public boolean getAlarm() {
        return isAlarm;
    }

    public float getHourAlarm() {
        return hourAlarm;
    }

    public float getMinuteAlarm() {
        return minuteAlarm;
    }

    public void setSkill(int skill) {
        mSkill = skill;
        mChanged = true;
        loadSkill();
        mDial.invalidateSelf();
        this.invalidate();
    }

    public int getSkill() {
        return mSkill;
    }

    @SuppressWarnings("deprecation")
    public void loadSkill() {
        mDial = r.getDrawable(mSkill);

        mDialWidth = mDial.getIntrinsicWidth();
        mDialHeight = mDial.getIntrinsicHeight();
    }

    public class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mCalendar.setToNow();

            if (isAlarm) {
                if (hourAlarm == mHour) {
                    if (minuteAlarm == mMinutes) {
                        if (!isStartService) {
                            isStartService = true;
                            Intent serviceIntent = new Intent(mContext, RingtonePlayingService.class);
                            serviceIntent.putExtra(RINGTONE_STATUS, "play");
                            mContext.startService(serviceIntent);
                        }
                    }
                }
            }

            int hour = mCalendar.hour;
            int minute = mCalendar.minute;
            int second = mCalendar.second;

            mSecond = second;
            if (mSecond == 0) {
                mMinutes++;
                if (mMinutes > 59) {
                    mMinutes -= 60;
                    mHour++;
                    if (mHour > 23) {
                        mHour -= 24;
                    }
                }
            }
            mSeconds = true;

            CustomAnalogClock.this.invalidate();
        }

        @Override
        public void onFinish() {
            counter.start();
        }
    }

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                String tz = intent.getStringExtra("time-zone");
                mCalendar = new Time(TimeZone.getTimeZone(tz).getID());
            }

            isSync = true;
            onTimeChanged();

            invalidate();
        }
    };

    private final BroadcastReceiver mRingtoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RINGTONE_RECEIVER)) {
                isStartService = false;
            }
        }
    };
}
