package com.example.sassy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME = 100;
    private static int PICK_CONTACT =100;
    private static MainActivity instance;
    private static String recNum = "";
    private static String recName = "x";

    TextView c_name;
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
            Manifest.permission.SET_ALARM,
            Manifest.permission.READ_CONTACTS
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent mySuperIntent = new Intent(MainActivity.this, SplashScreen.class);
                    startActivity(mySuperIntent);
                }
            }, SPLASH_TIME);
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error in Main Splash",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        setContentView(R.layout.main);

        instance = this;
        try {
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error in getting permission call",Toast.LENGTH_LONG).show();
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

                setContentView(R.layout.select_contact);
                Button b = findViewById(R.id.select_button);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                            startActivityForResult(intent, PICK_CONTACT);



                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Error in contact screen",Toast.LENGTH_LONG).show();
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
            intent2.putExtra(AlarmClock.EXTRA_VIBRATE, true);
            intent2.putExtra(AlarmClock.EXTRA_MESSAGE," Alert!") ;
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                String name = "";
                String number = "";
                Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();
                String hasPhone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                if (hasPhone.equals("1")) {
                    Cursor phones = getContentResolver().query
                            (ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                            + " = " + contactId, null, null);
                    while (phones.moveToNext()) {
                        number = phones.getString(phones.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[-() ]", "");
                        name = phones.getString(phones.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    }
                    phones.close();
                   recNum=number;
                   recName=name;
                    setContentView(R.layout.alarmscreen);
                    c_name= findViewById(R.id.contact_name);
                    c_name.setText(recName);

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

                } else {
                    Toast.makeText(getApplicationContext(), "This contact has no phone number", Toast.LENGTH_LONG).show();
                }
                cursor.close();
            }
        }
    }
}

