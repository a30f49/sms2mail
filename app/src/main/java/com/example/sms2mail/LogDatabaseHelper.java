package com.example.sms2mail;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sms2mail_logs.db";
    private static final int DATABASE_VERSION = 1;
    
    // SMS日志表
    private static final String TABLE_SMS_LOG = "sms_log";
    private static final String COLUMN_SMS_ID = "id";
    private static final String COLUMN_SMS_SENDER = "sender";
    private static final String COLUMN_SMS_MESSAGE = "message";
    private static final String COLUMN_SMS_RECEIVED_TIME = "received_time";
    private static final String COLUMN_SMS_PROCESSED = "processed";
    private static final String COLUMN_SMS_PROCESS_STATUS = "process_status";
    
    // 邮件日志表
    private static final String TABLE_EMAIL_LOG = "email_log";
    private static final String COLUMN_EMAIL_ID = "id";
    private static final String COLUMN_EMAIL_SMS_LOG_ID = "sms_log_id";
    private static final String COLUMN_EMAIL_SENDER = "sender";
    private static final String COLUMN_EMAIL_MESSAGE = "message";
    private static final String COLUMN_EMAIL_RECIPIENTS = "recipient_emails";
    private static final String COLUMN_EMAIL_SEND_TIME = "send_time";
    private static final String COLUMN_EMAIL_SUCCESS = "success";
    private static final String COLUMN_EMAIL_ERROR_MESSAGE = "error_message";
    private static final String COLUMN_EMAIL_PROVIDER = "provider";
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    
    public LogDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建SMS日志表
        String createSmsLogTable = "CREATE TABLE " + TABLE_SMS_LOG + " (" +
                COLUMN_SMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SMS_SENDER + " TEXT NOT NULL, " +
                COLUMN_SMS_MESSAGE + " TEXT NOT NULL, " +
                COLUMN_SMS_RECEIVED_TIME + " TEXT NOT NULL, " +
                COLUMN_SMS_PROCESSED + " INTEGER DEFAULT 0, " +
                COLUMN_SMS_PROCESS_STATUS + " TEXT DEFAULT 'PENDING'" +
                ")";
        
        // 创建邮件日志表
        String createEmailLogTable = "CREATE TABLE " + TABLE_EMAIL_LOG + " (" +
                COLUMN_EMAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL_SMS_LOG_ID + " INTEGER, " +
                COLUMN_EMAIL_SENDER + " TEXT NOT NULL, " +
                COLUMN_EMAIL_MESSAGE + " TEXT NOT NULL, " +
                COLUMN_EMAIL_RECIPIENTS + " TEXT NOT NULL, " +
                COLUMN_EMAIL_SEND_TIME + " TEXT NOT NULL, " +
                COLUMN_EMAIL_SUCCESS + " INTEGER DEFAULT 0, " +
                COLUMN_EMAIL_ERROR_MESSAGE + " TEXT, " +
                COLUMN_EMAIL_PROVIDER + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_EMAIL_SMS_LOG_ID + ") REFERENCES " + TABLE_SMS_LOG + "(" + COLUMN_SMS_ID + ")" +
                ")";
        
        db.execSQL(createSmsLogTable);
        db.execSQL(createEmailLogTable);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMAIL_LOG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS_LOG);
        onCreate(db);
    }
    
    // SMS日志操作
    public long insertSmsLog(SmsLog smsLog) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_SMS_SENDER, smsLog.getSender());
        values.put(COLUMN_SMS_MESSAGE, smsLog.getMessage());
        values.put(COLUMN_SMS_RECEIVED_TIME, dateFormat.format(smsLog.getReceivedTime()));
        values.put(COLUMN_SMS_PROCESSED, smsLog.isProcessed() ? 1 : 0);
        values.put(COLUMN_SMS_PROCESS_STATUS, smsLog.getProcessStatus());
        
        long id = db.insert(TABLE_SMS_LOG, null, values);
        db.close();
        return id;
    }
    
    public void updateSmsLogStatus(long id, boolean processed, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_SMS_PROCESSED, processed ? 1 : 0);
        values.put(COLUMN_SMS_PROCESS_STATUS, status);
        
        db.update(TABLE_SMS_LOG, values, COLUMN_SMS_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    
    // 邮件日志操作
    public long insertEmailLog(EmailLog emailLog) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_EMAIL_SMS_LOG_ID, emailLog.getSmsLogId());
        values.put(COLUMN_EMAIL_SENDER, emailLog.getSender());
        values.put(COLUMN_EMAIL_MESSAGE, emailLog.getMessage());
        values.put(COLUMN_EMAIL_RECIPIENTS, emailLog.getRecipientEmails());
        values.put(COLUMN_EMAIL_SEND_TIME, dateFormat.format(emailLog.getSendTime()));
        values.put(COLUMN_EMAIL_SUCCESS, emailLog.isSuccess() ? 1 : 0);
        values.put(COLUMN_EMAIL_ERROR_MESSAGE, emailLog.getErrorMessage());
        values.put(COLUMN_EMAIL_PROVIDER, emailLog.getProvider());
        
        long id = db.insert(TABLE_EMAIL_LOG, null, values);
        db.close();
        return id;
    }
    
    public void updateEmailLogResult(long id, boolean success, String errorMessage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_EMAIL_SUCCESS, success ? 1 : 0);
        values.put(COLUMN_EMAIL_ERROR_MESSAGE, errorMessage);
        
        db.update(TABLE_EMAIL_LOG, values, COLUMN_EMAIL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    
    // 查询操作
    public List<SmsLog> getAllSmsLogs() {
        List<SmsLog> smsLogs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_SMS_LOG, null, null, null, null, null, 
                COLUMN_SMS_RECEIVED_TIME + " DESC");
        
        if (cursor.moveToFirst()) {
            do {
                SmsLog smsLog = new SmsLog();
                smsLog.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SMS_ID)));
                smsLog.setSender(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SMS_SENDER)));
                smsLog.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SMS_MESSAGE)));
                
                try {
                    String timeStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SMS_RECEIVED_TIME));
                    smsLog.setReceivedTime(dateFormat.parse(timeStr));
                } catch (Exception e) {
                    smsLog.setReceivedTime(new Date());
                }
                
                smsLog.setProcessed(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SMS_PROCESSED)) == 1);
                smsLog.setProcessStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SMS_PROCESS_STATUS)));
                
                smsLogs.add(smsLog);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return smsLogs;
    }
    
    public List<EmailLog> getAllEmailLogs() {
        List<EmailLog> emailLogs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_EMAIL_LOG, null, null, null, null, null, 
                COLUMN_EMAIL_SEND_TIME + " DESC");
        
        if (cursor.moveToFirst()) {
            do {
                EmailLog emailLog = new EmailLog();
                emailLog.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_ID)));
                emailLog.setSmsLogId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_SMS_LOG_ID)));
                emailLog.setSender(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_SENDER)));
                emailLog.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_MESSAGE)));
                emailLog.setRecipientEmails(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_RECIPIENTS)));
                
                try {
                    String timeStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_SEND_TIME));
                    emailLog.setSendTime(dateFormat.parse(timeStr));
                } catch (Exception e) {
                    emailLog.setSendTime(new Date());
                }
                
                emailLog.setSuccess(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_SUCCESS)) == 1);
                emailLog.setErrorMessage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_ERROR_MESSAGE)));
                emailLog.setProvider(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_PROVIDER)));
                
                emailLogs.add(emailLog);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return emailLogs;
    }
    
    public List<EmailLog> getEmailLogsBySmsId(long smsLogId) {
        List<EmailLog> emailLogs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_EMAIL_LOG, null, 
                COLUMN_EMAIL_SMS_LOG_ID + " = ?", new String[]{String.valueOf(smsLogId)}, 
                null, null, COLUMN_EMAIL_SEND_TIME + " DESC");
        
        if (cursor.moveToFirst()) {
            do {
                EmailLog emailLog = new EmailLog();
                emailLog.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_ID)));
                emailLog.setSmsLogId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_SMS_LOG_ID)));
                emailLog.setSender(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_SENDER)));
                emailLog.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_MESSAGE)));
                emailLog.setRecipientEmails(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_RECIPIENTS)));
                
                try {
                    String timeStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_SEND_TIME));
                    emailLog.setSendTime(dateFormat.parse(timeStr));
                } catch (Exception e) {
                    emailLog.setSendTime(new Date());
                }
                
                emailLog.setSuccess(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_SUCCESS)) == 1);
                emailLog.setErrorMessage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_ERROR_MESSAGE)));
                emailLog.setProvider(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_PROVIDER)));
                
                emailLogs.add(emailLog);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return emailLogs;
    }
    
    // 统计信息
    public int getSmsLogCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_SMS_LOG, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
    
    public int getEmailLogCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_EMAIL_LOG, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
    
    public int getSuccessfulEmailCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_EMAIL_LOG + " WHERE " + COLUMN_EMAIL_SUCCESS + " = 1", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
    
    // 清理旧日志（保留最近30天）
    public void cleanOldLogs() {
        SQLiteDatabase db = this.getWritableDatabase();
        long thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);
        String thirtyDaysAgoStr = dateFormat.format(new Date(thirtyDaysAgo));
        
        db.delete(TABLE_EMAIL_LOG, COLUMN_EMAIL_SEND_TIME + " < ?", new String[]{thirtyDaysAgoStr});
        db.delete(TABLE_SMS_LOG, COLUMN_SMS_RECEIVED_TIME + " < ?", new String[]{thirtyDaysAgoStr});
        
        db.close();
    }
}
