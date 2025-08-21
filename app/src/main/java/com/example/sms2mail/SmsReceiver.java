package com.example.sms2mail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                        String sender = sms.getDisplayOriginatingAddress();
                        String message = sms.getDisplayMessageBody();
                        
                        Log.d(TAG, context.getString(R.string.log_sms_received, sender, message));
                        logSmsToDatabase(context, sender, message, "Forwarding");
                        
                        // 启动邮件发送服务
                        Intent emailIntent = new Intent(context, EmailService.class);
                        emailIntent.putExtra("sender", sender);
                        emailIntent.putExtra("message", message);
                        context.startService(emailIntent);
                    }
                }
            }
        }
    }

    private void logSmsToDatabase(Context context, String sender, String body, String forwardStatus) {
        LogDatabaseHelper dbHelper = new LogDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LogDatabaseHelper.COLUMN_SENDER, sender);
        values.put(LogDatabaseHelper.COLUMN_BODY, body);
        values.put(LogDatabaseHelper.COLUMN_TIMESTAMP, System.currentTimeMillis());
        values.put(LogDatabaseHelper.COLUMN_FORWARD_STATUS, forwardStatus);

        db.insert(LogDatabaseHelper.TABLE_LOGS, null, values);
        db.close();
    }
}