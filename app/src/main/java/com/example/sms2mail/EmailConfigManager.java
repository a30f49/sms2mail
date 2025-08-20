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
    private static final String KEY_SMTP_HOST = "smtp_host";
    private static final String KEY_SMTP_PORT = "smtp_port";
    private static final String KEY_USE_TLS = "use_tls";
    private static final String KEY_CONFIG_COMPLETED = "config_completed";
    
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
            editor.putString(KEY_SMTP_HOST, config.getSmtpHost());
            editor.putString(KEY_SMTP_PORT, config.getSmtpPort());
            editor.putBoolean(KEY_USE_TLS, config.isUseTls());
            editor.putBoolean(KEY_CONFIG_COMPLETED, true);
            
            boolean success = editor.commit();
            if (success) {
                Log.d(TAG, "邮件配置保存成功");
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
        config.setSmtpHost(prefs.getString(KEY_SMTP_HOST, DEFAULT_SMTP_HOST));
        config.setSmtpPort(prefs.getString(KEY_SMTP_PORT, DEFAULT_SMTP_PORT));
        config.setUseTls(prefs.getBoolean(KEY_USE_TLS, DEFAULT_USE_TLS));
        
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
               !config.getSmtpPort().trim().isEmpty();
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
     * 获取SMTP服务器预设列表
     */
    public static SmtpPreset[] getSmtpPresets() {
        return new SmtpPreset[] {
            new SmtpPreset("Gmail", "smtp.gmail.com", "587", true),
            new SmtpPreset("Outlook/Hotmail", "smtp-mail.outlook.com", "587", true),
            new SmtpPreset("Yahoo", "smtp.mail.yahoo.com", "587", true),
            new SmtpPreset("QQ邮箱", "smtp.qq.com", "587", true),
            new SmtpPreset("163邮箱", "smtp.163.com", "25", false),
            new SmtpPreset("126邮箱", "smtp.126.com", "25", false),
            new SmtpPreset("自定义", "", "", true)
        };
    }
    
    /**
     * SMTP预设配置类
     */
    public static class SmtpPreset {
        private final String name;
        private final String host;
        private final String port;
        private final boolean useTls;
        
        public SmtpPreset(String name, String host, String port, boolean useTls) {
            this.name = name;
            this.host = host;
            this.port = port;
            this.useTls = useTls;
        }
        
        public String getName() { return name; }
        public String getHost() { return host; }
        public String getPort() { return port; }
        public boolean isUseTls() { return useTls; }
        
        @Override
        public String toString() {
            return name;
        }
    }
}