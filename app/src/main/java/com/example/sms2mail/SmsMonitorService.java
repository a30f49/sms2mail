package com.example.sms2mail;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

public class SmsMonitorService extends Service {
    private static final String CHANNEL_ID = "SMS_MONITOR_CHANNEL";
    private static final int NOTIFICATION_ID = 1;
    private SmsReceiver smsReceiver;
    
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        smsReceiver = new SmsReceiver();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification());
        
        // 注册短信接收器
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(1000);
        registerReceiver(smsReceiver, filter);
        
        return START_STICKY; // 服务被杀死后自动重启
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        );
        channel.setDescription(getString(R.string.notification_channel_desc));
        
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
    
    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build();
    }
}