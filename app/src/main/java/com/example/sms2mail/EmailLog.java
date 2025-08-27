package com.example.sms2mail;

import java.util.Date;

public class EmailLog {
    private long id;
    private long smsLogId;
    private String sender;
    private String message;
    private String recipientEmails;
    private Date sendTime;
    private boolean success;
    private String errorMessage;
    private String provider;

    public EmailLog() {
        this.sendTime = new Date();
        this.success = false;
        this.errorMessage = "";
    }

    public EmailLog(long smsLogId, String sender, String message, String recipientEmails, String provider) {
        this.smsLogId = smsLogId;
        this.sender = sender;
        this.message = message;
        this.recipientEmails = recipientEmails;
        this.provider = provider;
        this.sendTime = new Date();
        this.success = false;
        this.errorMessage = "";
    }

    public EmailLog(long id, long smsLogId, String sender, String message, String recipientEmails, Date sendTime, boolean success, String errorMessage, String provider) {
        this.id = id;
        this.smsLogId = smsLogId;
        this.sender = sender;
        this.message = message;
        this.recipientEmails = recipientEmails;
        this.sendTime = sendTime;
        this.success = success;
        this.errorMessage = errorMessage;
        this.provider = provider;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSmsLogId() {
        return smsLogId;
    }

    public void setSmsLogId(long smsLogId) {
        this.smsLogId = smsLogId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecipientEmails() {
        return recipientEmails;
    }

    public void setRecipientEmails(String recipientEmails) {
        this.recipientEmails = recipientEmails;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}