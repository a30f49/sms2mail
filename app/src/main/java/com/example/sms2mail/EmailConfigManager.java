package com.example.sms2mail;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 邮件配置管理器
 * 负责邮件设置的持久化存储和管理
 */
public class EmailConfigManager {
    private static final String TAG = "EmailConfigManager";
    private static final String PREFS_NAME = "email_config";
    
    // 配置键名
    private static final String KEY_SENDER_EMAIL = "sender_email";
    private static final String KEY_SENDER_PASSWORD = "sender_password";
    private static final String KEY_RECEIVER_EMAIL = "receiver_email";
    private static final String KEY_PROVIDER = "provider";
    private static final String KEY_SMTP_HOST = "smtp_host";
    private static final String KEY_SMTP_PORT = "smtp_port";
    private static final String KEY_SMTP_USE_TLS = "smtp_use_tls";
    private static final String KEY_SMTP_USE_SSL = "smtp_use_ssl";
    private static final String KEY_POP3_HOST = "pop3_host";
    private static final String KEY_POP3_PORT = "pop3_port";
    private static final String KEY_POP3_USE_SSL = "pop3_use_ssl";
    private static final String KEY_CONFIG_COMPLETED = "config_completed";
    
    // 兼容性键名
    private static final String KEY_USE_TLS = "use_tls";
    
    // 默认SMTP配置
    private static final String DEFAULT_SMTP_HOST = "smtp.gmail.com";
    private static final String DEFAULT_SMTP_PORT = "587";
    private static final boolean DEFAULT_USE_TLS = true;
    
    private final SharedPreferences prefs;
    private final Context context;
    
    public EmailConfigManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * 保存邮件配置
     */
    public boolean saveEmailConfig(EmailConfig config) {
        try {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_SENDER_EMAIL, config.getSenderEmail());
            editor.putString(KEY_SENDER_PASSWORD, config.getSenderPassword());
            editor.putString(KEY_RECEIVER_EMAIL, config.getReceiverEmail());
            editor.putString(KEY_PROVIDER, config.getProvider().name());
            editor.putString(KEY_SMTP_HOST, config.getSmtpHost());
            editor.putString(KEY_SMTP_PORT, config.getSmtpPort());
            editor.putBoolean(KEY_SMTP_USE_TLS, config.isSmtpUseTls());
            editor.putBoolean(KEY_SMTP_USE_SSL, config.isSmtpUseSSL());
            editor.putString(KEY_POP3_HOST, config.getPop3Host());
            editor.putString(KEY_POP3_PORT, config.getPop3Port());
            editor.putBoolean(KEY_POP3_USE_SSL, config.isPop3UseSSL());
            editor.putBoolean(KEY_CONFIG_COMPLETED, true);
            
            boolean success = editor.commit();
            if (success) {
                Log.d(TAG, "邮件配置保存成功: " + config.getProvider().getDisplayName());
            } else {
                Log.e(TAG, "邮件配置保存失败");
            }
            return success;
        } catch (Exception e) {
            Log.e(TAG, "保存邮件配置时发生错误: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取邮件配置
     */
    public EmailConfig getEmailConfig() {
        EmailConfig config = new EmailConfig();
        config.setSenderEmail(prefs.getString(KEY_SENDER_EMAIL, ""));
        config.setSenderPassword(prefs.getString(KEY_SENDER_PASSWORD, ""));
        config.setReceiverEmail(prefs.getString(KEY_RECEIVER_EMAIL, ""));
        
        // 获取邮件服务商
        String providerName = prefs.getString(KEY_PROVIDER, EmailConfig.EmailProvider.GMAIL.name());
        try {
            EmailConfig.EmailProvider provider = EmailConfig.EmailProvider.valueOf(providerName);
            config.setProvider(provider);
        } catch (IllegalArgumentException e) {
            config.setProvider(EmailConfig.EmailProvider.GMAIL);
        }
        
        // 获取SMTP配置（如果是自定义或需要覆盖默认值）
        config.setSmtpHost(prefs.getString(KEY_SMTP_HOST, config.getSmtpHost()));
        config.setSmtpPort(prefs.getString(KEY_SMTP_PORT, config.getSmtpPort()));
        config.setSmtpUseTls(prefs.getBoolean(KEY_SMTP_USE_TLS, config.isSmtpUseTls()));
        config.setSmtpUseSSL(prefs.getBoolean(KEY_SMTP_USE_SSL, config.isSmtpUseSSL()));
        
        // 获取POP3配置
        config.setPop3Host(prefs.getString(KEY_POP3_HOST, config.getPop3Host()));
        config.setPop3Port(prefs.getString(KEY_POP3_PORT, config.getPop3Port()));
        config.setPop3UseSSL(prefs.getBoolean(KEY_POP3_USE_SSL, config.isPop3UseSSL()));
        
        // 兼容性处理
        if (prefs.contains(KEY_USE_TLS) && !prefs.contains(KEY_SMTP_USE_TLS)) {
            config.setSmtpUseTls(prefs.getBoolean(KEY_USE_TLS, DEFAULT_USE_TLS));
        }
        
        return config;
    }
    
    /**
     * 检查是否已完成初始配置
     */
    public boolean isConfigCompleted() {
        return prefs.getBoolean(KEY_CONFIG_COMPLETED, false) && 
               isConfigValid(getEmailConfig());
    }
    
    /**
     * 验证配置是否有效
     */
    public boolean isConfigValid(EmailConfig config) {
        return config != null &&
               !config.getSenderEmail().trim().isEmpty() &&
               !config.getSenderPassword().trim().isEmpty() &&
               !config.getReceiverEmail().trim().isEmpty() &&
               !config.getSmtpHost().trim().isEmpty() &&
               !config.getSmtpPort().trim().isEmpty() &&
               isValidReceiverEmails(config.getReceiverEmail());
    }
    
    /**
     * 验证多个接收邮箱地址格式
     */
    private boolean isValidReceiverEmails(String emails) {
        if (emails == null || emails.trim().isEmpty()) {
            return false;
        }
        
        String[] emailArray = emails.split(";");
        for (String email : emailArray) {
            String trimmedEmail = email.trim();
            if (!trimmedEmail.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                return false;
            }
        }
        return emailArray.length > 0;
    }
    
    /**
     * 清除所有配置
     */
    public boolean clearConfig() {
        try {
            boolean success = prefs.edit().clear().commit();
            if (success) {
                Log.d(TAG, "邮件配置已清除");
            }
            return success;
        } catch (Exception e) {
            Log.e(TAG, "清除邮件配置时发生错误: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取所有支持的邮件服务商
     */
    public static EmailConfig.EmailProvider[] getSupportedProviders() {
        return EmailConfig.EmailProvider.values();
    }
}