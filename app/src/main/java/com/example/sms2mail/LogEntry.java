package com.example.sms2mail;

public class LogEntry {
    private long id;
    private String sender;
    private String body;
    private long timestamp;
    private String forwardStatus;

    public LogEntry(long id, String sender, String body, long timestamp, String forwardStatus) {
        this.id = id;
        this.sender = sender;
        this.body = body;
        this.timestamp = timestamp;
        this.forwardStatus = forwardStatus;
    }

    public long getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getBody() {
        return body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getForwardStatus() {
        return forwardStatus;
    }
}
