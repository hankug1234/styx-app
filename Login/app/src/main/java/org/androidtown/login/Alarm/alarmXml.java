package org.androidtown.login.Alarm;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import org.androidtown.login.R;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class alarmXml extends AppCompatActivity {
    TextView text,clock;
    Calendar calendar;
    String hour,min;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);
        text = (TextView)findViewById(R.id.text);
        clock = (TextView)findViewById(R.id.clock);
        calendar = Calendar.getInstance();
        timer = new Timer();

        if(calendar.get(calendar.HOUR_OF_DAY) >= 10 ) hour = Integer.toString(calendar.get(calendar.HOUR_OF_DAY));
        else hour = "0"+(calendar.get(calendar.HOUR_OF_DAY));

        if(calendar.get(calendar.MINUTE) >= 10 ) min = Integer.toString(calendar.get(calendar.MINUTE));
        else min = "0"+(calendar.get(calendar.MINUTE));

        clock.setText(hour+" : "+min);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                |WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                |WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                |WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        timer.schedule( new TimerTask()
                        {
                            public void run()
                            {
                                Toast.makeText(getApplicationContext(),"종료",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), Ringtone.class);
                                stopService(intent);
                                finish();
                            }
                        }
                , 1800000);

    }
}


