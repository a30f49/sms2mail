package com.example.sms2mail;

/**
 * 邮件配置数据类
 */
public class EmailConfig {
    private String senderEmail = "";
    private String senderPassword = "";
    private String receiverEmail = "";
    private String smtpHost = "smtp.gmail.com";
    private String smtpPort = "587";
    private boolean useTls = true;
    
    public EmailConfig() {}
    
    public EmailConfig(String senderEmail, String senderPassword, String receiverEmail) {
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
        this.receiverEmail = receiverEmail;
    }
    
    public EmailConfig(String senderEmail, String senderPassword, String receiverEmail, 
                      String smtpHost, String smtpPort, boolean useTls) {
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
        this.receiverEmail = receiverEmail;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.useTls = useTls;
    }
    
    // Getters and Setters
    public String getSenderEmail() {
        return senderEmail;
    }
    
    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail != null ? senderEmail : "";
    }
    
    public String getSenderPassword() {
        return senderPassword;
    }
    
    public void setSenderPassword(String senderPassword) {
        this.senderPassword = senderPassword != null ? senderPassword : "";
    }
    
    public String getReceiverEmail() {
        return receiverEmail;
    }
    
    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail != null ? receiverEmail : "";
    }
    
    public String getSmtpHost() {
        return smtpHost;
    }
    
    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost != null ? smtpHost : "smtp.gmail.com";
    }
    
    public String getSmtpPort() {
        return smtpPort;
    }
    
    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort != null ? smtpPort : "587";
    }
    
    public boolean isUseTls() {
        return useTls;
    }
    
    public void setUseTls(boolean useTls) {
        this.useTls = useTls;
    }
    
    @Override
    public String toString() {
        return "EmailConfig{" +
                "senderEmail='" + senderEmail + '\'' +
                ", receiverEmail='" + receiverEmail + '\'' +
                ", smtpHost='" + smtpHost + '\'' +
                ", smtpPort='" + smtpPort + '\'' +
                ", useTls=" + useTls +
                '}';
    }
}