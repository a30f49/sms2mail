package com.example.sms2mail;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService extends IntentService {
    private static final String TAG = "EmailService";
    
    public EmailService() {
        super("EmailService");
    }
    
    @Override
    protected void onHandleIntent(Intent intent) {
        String smsSender = intent.getStringExtra("sender");
        String smsMessage = intent.getStringExtra("message");
        
        EmailConfigManager configManager = new EmailConfigManager(this);
        EmailConfig config = configManager.getEmailConfig();
        
        if (!configManager.isConfigValid(config)) {
            Log.e(TAG, getString(R.string.log_email_settings_incomplete));
            return;
        }
        
        sendEmail(config, smsSender, smsMessage);
    }
    
    private void sendEmail(EmailConfig config, String smsSender, String smsMessage) {
        try {
            Properties props = createSmtpProperties(config);
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getSenderEmail(), config.getSenderPassword());
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.getSenderEmail()));
            
            // 处理多个接收邮箱地址
            String[] receiverEmails = parseReceiverEmails(config.getReceiverEmail());
            InternetAddress[] recipients = new InternetAddress[receiverEmails.length];
            for (int i = 0; i < receiverEmails.length; i++) {
                recipients[i] = new InternetAddress(receiverEmails[i]);
            }
            message.setRecipients(Message.RecipientType.TO, recipients);
            
            message.setSubject(getString(R.string.email_subject_prefix, smsSender));
            
            String emailBody = getString(R.string.email_body_template, 
                smsSender, smsMessage, new java.util.Date().toString());
            
            message.setText(emailBody);
            
            Transport.send(message);
            Log.d(TAG, getString(R.string.log_email_sent_success) + " [" + config.getProvider().getDisplayName() + 
                      "] 发送到 " + receiverEmails.length + " 个邮箱");
            
        } catch (MessagingException e) {
            Log.e(TAG, getString(R.string.log_email_sent_failed, e.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, getString(R.string.log_email_sent_failed, e.getMessage()));
        }
    }
    
    /**
     * 根据配置创建SMTP属性
     */
    private Properties createSmtpProperties(EmailConfig config) {
        Properties props = new Properties();
        
        // 基本SMTP配置
        props.put("mail.smtp.host", config.getSmtpHost());
        props.put("mail.smtp.port", config.getSmtpPort());
        props.put("mail.smtp.auth", "true");
        
        // TLS/SSL配置
        if (config.isSmtpUseSSL()) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", config.getSmtpPort());
        } else if (config.isSmtpUseTls()) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }
        
        // 调试和超时设置
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.connectiontimeout", "10000");
        
        Log.d(TAG, "SMTP配置: " + config.getProvider().getDisplayName() + 
                   " - " + config.getSmtpHost() + ":" + config.getSmtpPort() + 
                   " (TLS:" + config.isSmtpUseTls() + ", SSL:" + config.isSmtpUseSSL() + ")");
        
        return props;
    }
    
    /**
     * 解析多个接收邮箱地址
     */
    private String[] parseReceiverEmails(String receiverEmails) {
        if (receiverEmails == null || receiverEmails.trim().isEmpty()) {
            return new String[0];
        }
        
        String[] emails = receiverEmails.split(";");
        java.util.List<String> validEmails = new java.util.ArrayList<>();
        
        for (String email : emails) {
            String trimmedEmail = email.trim();
            if (!trimmedEmail.isEmpty()) {
                validEmails.add(trimmedEmail);
            }
        }
        
        return validEmails.toArray(new String[0]);
    }
}