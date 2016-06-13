package com.example.android.customanalogclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TimePicker;

/**
 * Created by lucky_luke on 6/9/2016.
 */
public class TimePickerFragment extends DialogFragment{
    private Context mContext;

    private float mHour;
    private float mMinute;
    private SetTimeComplete mCallbacks;

    public interface SetTimeComplete {
        void onCompleted(float hour, float minute);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_time, null);

        final TimePicker timePicker = (TimePicker) v.findViewById(R.id.dialog_time_timePicker);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour((int) mHour);
            timePicker.setMinute((int) mMinute);
        } else {
            timePicker.setCurrentHour((int) mHour);
            timePicker.setCurrentMinute((int) mMinute);
        }
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mHour = (float) hourOfDay;
                mMinute = (float) minute;
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.set_time)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallbacks.onCompleted(mHour, mMinute);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (SetTimeComplete) activity;
    }

    public void setTime(float hour, float minute) {
        mHour = hour;
        mMinute = minute;
    }
}
