package com.example.sms2mail;

/**
 * 邮件配置数据类
 * 支持SMTP发送和POP3接收配置
 */
public class EmailConfig {
    private String senderEmail = "";
    private String senderPassword = "";
    private String receiverEmail = "";
    
    // SMTP发送配置
    private String smtpHost = "smtp.gmail.com";
    private String smtpPort = "587";
    private boolean smtpUseTls = true;
    private boolean smtpUseSSL = false;
    
    // POP3接收配置（预留功能）
    private String pop3Host = "pop.gmail.com";
    private String pop3Port = "995";
    private boolean pop3UseSSL = true;
    
    // 邮件服务商类型
    private EmailProvider provider = EmailProvider.GMAIL;
    
    /**
     * 邮件服务商枚举
     */
    public enum EmailProvider {
        GMAIL("Gmail", "smtp.gmail.com", "587", true, false, "pop.gmail.com", "995", true),
        OUTLOOK("Outlook", "smtp-mail.outlook.com", "587", true, false, "outlook.office365.com", "995", true),
        YAHOO("Yahoo", "smtp.mail.yahoo.com", "587", true, false, "pop.mail.yahoo.com", "995", true),
        QQ("QQ邮箱", "smtp.qq.com", "587", true, false, "pop.qq.com", "995", true),
        NETEASE_163("163邮箱", "smtp.163.com", "25", false, false, "pop.163.com", "110", false),
        NETEASE_126("126邮箱", "smtp.126.com", "25", false, false, "pop.126.com", "110", false),
        CUSTOM("自定义", "", "", true, false, "", "", true);
        
        private final String displayName;
        private final String smtpHost;
        private final String smtpPort;
        private final boolean smtpUseTls;
        private final boolean smtpUseSSL;
        private final String pop3Host;
        private final String pop3Port;
        private final boolean pop3UseSSL;
        
        EmailProvider(String displayName, String smtpHost, String smtpPort, boolean smtpUseTls, boolean smtpUseSSL,
                     String pop3Host, String pop3Port, boolean pop3UseSSL) {
            this.displayName = displayName;
            this.smtpHost = smtpHost;
            this.smtpPort = smtpPort;
            this.smtpUseTls = smtpUseTls;
            this.smtpUseSSL = smtpUseSSL;
            this.pop3Host = pop3Host;
            this.pop3Port = pop3Port;
            this.pop3UseSSL = pop3UseSSL;
        }
        
        public String getDisplayName() { return displayName; }
        public String getSmtpHost() { return smtpHost; }
        public String getSmtpPort() { return smtpPort; }
        public boolean isSmtpUseTls() { return smtpUseTls; }
        public boolean isSmtpUseSSL() { return smtpUseSSL; }
        public String getPop3Host() { return pop3Host; }
        public String getPop3Port() { return pop3Port; }
        public boolean isPop3UseSSL() { return pop3UseSSL; }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    public EmailConfig() {}
    
    public EmailConfig(String senderEmail, String senderPassword, String receiverEmail) {
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
        this.receiverEmail = receiverEmail;
    }
    
    public EmailConfig(String senderEmail, String senderPassword, String receiverEmail, EmailProvider provider) {
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
        this.receiverEmail = receiverEmail;
        this.provider = provider;
        applyProviderSettings();
    }
    
    /**
     * 应用邮件服务商的默认设置
     */
    public void applyProviderSettings() {
        if (provider != EmailProvider.CUSTOM) {
            this.smtpHost = provider.getSmtpHost();
            this.smtpPort = provider.getSmtpPort();
            this.smtpUseTls = provider.isSmtpUseTls();
            this.smtpUseSSL = provider.isSmtpUseSSL();
            this.pop3Host = provider.getPop3Host();
            this.pop3Port = provider.getPop3Port();
            this.pop3UseSSL = provider.isPop3UseSSL();
        }
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
    
    public EmailProvider getProvider() {
        return provider;
    }
    
    public void setProvider(EmailProvider provider) {
        this.provider = provider != null ? provider : EmailProvider.GMAIL;
        applyProviderSettings();
    }
    
    // SMTP相关
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
    
    public boolean isSmtpUseTls() {
        return smtpUseTls;
    }
    
    public void setSmtpUseTls(boolean smtpUseTls) {
        this.smtpUseTls = smtpUseTls;
    }
    
    public boolean isSmtpUseSSL() {
        return smtpUseSSL;
    }
    
    public void setSmtpUseSSL(boolean smtpUseSSL) {
        this.smtpUseSSL = smtpUseSSL;
    }
    
    // POP3相关
    public String getPop3Host() {
        return pop3Host;
    }
    
    public void setPop3Host(String pop3Host) {
        this.pop3Host = pop3Host != null ? pop3Host : "pop.gmail.com";
    }
    
    public String getPop3Port() {
        return pop3Port;
    }
    
    public void setPop3Port(String pop3Port) {
        this.pop3Port = pop3Port != null ? pop3Port : "995";
    }
    
    public boolean isPop3UseSSL() {
        return pop3UseSSL;
    }
    
    public void setPop3UseSSL(boolean pop3UseSSL) {
        this.pop3UseSSL = pop3UseSSL;
    }
    
    // 兼容性方法（保持向后兼容）
    @Deprecated
    public boolean isUseTls() {
        return smtpUseTls;
    }
    
    @Deprecated
    public void setUseTls(boolean useTls) {
        this.smtpUseTls = useTls;
    }
    
    @Override
    public String toString() {
        return "EmailConfig{" +
                "senderEmail='" + senderEmail + '\'' +
                ", receiverEmail='" + receiverEmail + '\'' +
                ", provider=" + provider +
                ", smtpHost='" + smtpHost + '\'' +
                ", smtpPort='" + smtpPort + '\'' +
                ", smtpUseTls=" + smtpUseTls +
                ", smtpUseSSL=" + smtpUseSSL +
                ", pop3Host='" + pop3Host + '\'' +
                ", pop3Port='" + pop3Port + '\'' +
                ", pop3UseSSL=" + pop3UseSSL +
                '}';
    }
}