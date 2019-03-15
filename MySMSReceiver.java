package com.example.sassy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class MySMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String messageText = getSMSContent(intent);
        Log.e("Message", messageText);

        if(messageText.charAt(0)=='~'){
            if(messageText.charAt(1)=='%') {
                if(messageText.charAt(2)=='&'){
                    int hrr = Integer.parseInt(messageText.substring(3, 5));
                    int minr = Integer.parseInt(messageText.substring(6, 8));
                    com.example.sassy.MainActivity.getInstance().setAlarm(hrr,minr);
                }
            }
        }
        else{
            Toast.makeText(context,"Error in Receiver",Toast.LENGTH_SHORT).show();
        }
    }
    private String getSMSContent(Intent intent) {
        try {

            Bundle data = intent.getExtras();
            final Object[] pdusObj = (Object[]) data.get("pdus");
            for (Object obj : pdusObj) {
                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) obj);

                String senderNumber = currentMessage.getDisplayOriginatingAddress(); // Sender Number
                Log.e("senderNumber", senderNumber);

                String smsContent = currentMessage.getDisplayMessageBody(); // SMS body Content
                Log.e("smsContent", smsContent);
                return smsContent;
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return "";
    }
}
