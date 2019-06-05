package org.androidtown.login.Alarm;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;

import org.androidtown.login.R;


public class Ringtone extends Service {
    MediaPlayer ring;
    Vibrator vib;

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        ring = MediaPlayer.create(this, R.raw.crystals);
        ring.setLooping(true);
        ring.start();
        vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createWaveform(new long[]{500,1000},0));
        }
        Intent xmlIntent = new Intent(getApplicationContext(), alarmXml.class);
        xmlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(xmlIntent);
        return Service.START_REDELIVER_INTENT;
        }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ring.stop();
        ring.release();
        vib.cancel();

    }



}
