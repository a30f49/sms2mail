package com.example.sms2mail;

import java.util.Date;

public class LogDisplayEntry {
    public static final int TYPE_SMS_RECEIVED = 1;
    public static final int TYPE_EMAIL_SENT = 2;
    public static final int TYPE_EMAIL_FAILED = 3;
    
    private int type;
    private String title;
    private String details;
    private String body;
    private Date timestamp;
    private String sender;
    private boolean isSuccess;
    private String recipient;
    
    public LogDisplayEntry(int type, String title, String details, String body, Date timestamp, String sender, boolean isSuccess, String recipient) {
        this.type = type;
        this.title = title;
        this.details = details;
        this.body = body;
        this.timestamp = timestamp;
        this.sender = sender;
        this.isSuccess = isSuccess;
        this.recipient = recipient;
    }

    public int getType() { return type; }
    public String getTitle() { return title; }
    public String getDetails() { return details; }
    public String getBody() { return body; }
    public Date getTimestamp() { return timestamp; }
    public String getSender() { return sender; }
    public boolean isSuccess() { return isSuccess; }
    public String getRecipient() { return recipient; }
    
    public boolean isSmsEntry() { return type == TYPE_SMS_RECEIVED; }
    public boolean isEmailEntry() { return type == TYPE_EMAIL_SENT || type == TYPE_EMAIL_FAILED; }
}