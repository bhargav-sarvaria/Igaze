package com.igaze.Utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.igaze.Activities.ActivitySignIn;

/**
 * Created by ascent-3 on 17/5/18.
 */

public class SMSReceiver extends BroadcastReceiver {

    final SmsManager sms = SmsManager.getDefault();
    public SMSReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            Object[] pdusObj = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdusObj.length];

            for (int i = 0; i < pdusObj.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
            }

            for (SmsMessage currentMessage : messages) {

                String number = currentMessage.getDisplayOriginatingAddress();
                Constants.showLog(number);
                try{
                    if(number.contains("IGAZE")){
                        String otp = currentMessage.getDisplayMessageBody().substring(30,36);
                        ActivitySignIn.otp.setText(otp);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}