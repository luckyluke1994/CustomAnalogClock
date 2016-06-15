package com.example.android.customanalogclock.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.android.customanalogclock.AlarmActivity;
import com.example.android.customanalogclock.R;
import com.example.android.customanalogclock.ui.CustomAnalogClock;

/**
 * Created by lucky_luke on 6/15/2016.
 */
public class RingtonePlayingService extends Service{
    MediaPlayer media_song;
    static boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final NotificationManager mNM = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        Intent i = new Intent(getBaseContext(), AlarmActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getBaseContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext())
                .setContentTitle("Custom Analog Clock")
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);

        String state = intent.getStringExtra(CustomAnalogClock.RINGTONE_STATUS);
        switch (state) {
            case "play":
                startId = 1;
                break;
            case "stop":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }

        if (!this.isRunning && startId == 1){
            media_song = MediaPlayer.create(this, R.raw.ringtone1);
            media_song.start();
            mNM.notify(0, mBuilder.build());
            this.isRunning = true;
            media_song.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Intent i = new Intent();
                    i.setAction(CustomAnalogClock.RINGTONE_RECEIVER);
                    getBaseContext().sendBroadcast(i);
                }
            });
        } else if (startId == 0 && this.isRunning){
            media_song.stop();
            media_song.release();
            this.isRunning = false;
            media_song = null;
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isRunning = false;
    }
}
