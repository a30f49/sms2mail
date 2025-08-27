package com.example.sms2mail;

import android.content.Context;
import java.util.Date;
import java.util.List;

public class LogManager {
    private static LogManager instance;
    private LogDatabaseHelper dbHelper;
    
    private LogManager(Context context) {
        dbHelper = new LogDatabaseHelper(context.getApplicationContext());
    }
    
    public static synchronized LogManager getInstance(Context context) {
        if (instance == null) {
            instance = new LogManager(context);
        }
        return instance;
    }
    
    // 记录短信接收日志
    public long logSmsReceived(String sender, String message) {
        SmsLog smsLog = new SmsLog(sender, message, new Date());
        return dbHelper.insertSmsLog(smsLog);
    }
    
    // 更新短信处理状态
    public void updateSmsProcessStatus(long smsLogId, boolean processed, String status) {
        dbHelper.updateSmsLogStatus(smsLogId, processed, status);
    }
    
    // 记录邮件发送日志
    public long logEmailSending(long smsLogId, String sender, String message, String recipients, String provider) {
        EmailLog emailLog = new EmailLog(smsLogId, sender, message, recipients, provider);
        return dbHelper.insertEmailLog(emailLog);
    }
    
    // 更新邮件发送结果
    public void updateEmailResult(long emailLogId, boolean success, String errorMessage) {
        dbHelper.updateEmailLogResult(emailLogId, success, errorMessage);
    }
    
    // 获取所有短信日志
    public List<SmsLog> getAllSmsLogs() {
        return dbHelper.getAllSmsLogs();
    }
    
    // 获取所有邮件日志
    public List<EmailLog> getAllEmailLogs() {
        return dbHelper.getAllEmailLogs();
    }
    
    // 根据短信ID获取邮件日志
    public List<EmailLog> getEmailLogsBySmsId(long smsLogId) {
        return dbHelper.getEmailLogsBySmsId(smsLogId);
    }
    
    // 获取统计信息
    public int getSmsLogCount() {
        return dbHelper.getSmsLogCount();
    }
    
    public int getEmailLogCount() {
        return dbHelper.getEmailLogCount();
    }
    
    public int getSuccessfulEmailCount() {
        return dbHelper.getSuccessfulEmailCount();
    }
    
    // 清理旧日志
    public void cleanOldLogs() {
        dbHelper.cleanOldLogs();
    }
}