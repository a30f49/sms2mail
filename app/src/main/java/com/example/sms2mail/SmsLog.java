package com.example.sms2mail;

import java.util.Date;

public class SmsLog {
    private long id;
    private String sender;
    private String message;
    private Date receivedTime;
    private boolean processed;
    private String processStatus;

    public SmsLog() {
    }

    public SmsLog(String sender, String message, Date receivedTime) {
        this.sender = sender;
        this.message = message;
        this.receivedTime = receivedTime;
        this.processed = false;
        this.processStatus = "PENDING";
    }

    public SmsLog(long id, String sender, String message, Date receivedTime, boolean processed, String processStatus) {
        this.id = id;
        this.sender = sender;
        this.message = message;
        this.receivedTime = receivedTime;
        this.processed = processed;
        this.processStatus = processStatus;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Date getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(Date receivedTime) {
        this.receivedTime = receivedTime;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }
}