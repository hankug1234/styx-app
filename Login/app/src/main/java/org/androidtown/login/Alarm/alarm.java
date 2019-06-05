package org.androidtown.login.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class alarm extends BroadcastReceiver {
    Context context;
    Intent ringIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Toast.makeText(context, "STYX : 30분 후 종료됩니다", Toast.LENGTH_LONG).show();
        ringIntent = new Intent(context, Ringtone.class);
        ringIntent.putExtra("울린 시간",System.currentTimeMillis());
        context.startService(ringIntent);
    }
}
