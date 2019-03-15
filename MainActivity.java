package com.example.sassy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.AlarmClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static MainActivity instance;

    EditText recNum1;
    String recNum;
    Button setAlarm;
    String hour;
    String min;
    Button exit;
    String msgString = "";
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.SET_ALARM
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        instance = this;
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        setAlarm = findViewById(R.id.setAlarm);
        exit = findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setContentView(R.layout.alarmscreen);
                recNum1 = findViewById(R.id.recNum1);
                Button ok = findViewById(R.id.confirmTime);
                final TimePicker alarmTime = findViewById(R.id.alarmTime);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int hr = alarmTime.getHour();
                            int mmin = alarmTime.getMinute();
                            hour = Integer.toString(hr);
                            min = Integer.toString(mmin);
                            recNum = recNum1.getText().toString();
                            if (hr < 10 && mmin < 10) {
                                msgString = "~%&0" + hour + ":0" + min;
                            } else if (mmin < 10 && hr > 10) {
                                msgString = "~%&" + hour + ":0" + min;
                            } else if (hr < 10 && mmin > 10) {
                                msgString = "~%&0" + hour + ":" + min;
                            } else {
                                msgString = "~%&" + hour + ":" + min;
                            }
                            try {
                                SmsManager smsmanager = SmsManager.getDefault();
                                smsmanager.sendTextMessage(recNum, null, msgString, null, null);
                                Toast.makeText(getApplicationContext(), "Your SMS was sent successfully!",
                                        Toast.LENGTH_LONG).show();
                            } catch (Exception ex) {
                                Toast.makeText(getApplicationContext(), "Error in sending SMS!",
                                        Toast.LENGTH_LONG).show();
                                ex.printStackTrace();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error in calculating time", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    public static MainActivity getInstance() {
        return instance;
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    public void setAlarm(int hr, int min)
    {
        try {

            Intent intent2 = new Intent(AlarmClock.ACTION_SET_ALARM);
            intent2.putExtra(AlarmClock.EXTRA_HOUR, hr);
            intent2.putExtra(AlarmClock.EXTRA_MINUTES, min);
            intent2.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
            startActivity(intent2);
            Toast.makeText(getApplicationContext(),"Alarm has been set by another device",Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }
}
