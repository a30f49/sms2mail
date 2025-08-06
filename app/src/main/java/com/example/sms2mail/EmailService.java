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
        
        SharedPreferences prefs = getSharedPreferences("email_settings", MODE_PRIVATE);
        String senderEmail = prefs.getString("sender_email", "");
        String senderPassword = prefs.getString("sender_password", "");
        String receiverEmail = prefs.getString("receiver_email", "");
        
        if (senderEmail.isEmpty() || senderPassword.isEmpty() || receiverEmail.isEmpty()) {
            Log.e(TAG, "邮件设置不完整");
            return;
        }
        
        sendEmail(senderEmail, senderPassword, receiverEmail, smsSender, smsMessage);
    }
    
    private void sendEmail(String senderEmail, String senderPassword, String receiverEmail, 
                          String smsSender, String smsMessage) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com"); // 根据需要修改SMTP服务器
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
            message.setSubject("新短信通知 - 来自 " + smsSender);
            
            String emailBody = "您收到一条新短信:\n\n" +
                             "发送者: " + smsSender + "\n" +
                             "内容: " + smsMessage + "\n" +
                             "时间: " + new java.util.Date();
            
            message.setText(emailBody);
            
            Transport.send(message);
            Log.d(TAG, "邮件发送成功");
            
        } catch (MessagingException e) {
            Log.e(TAG, "邮件发送失败: " + e.getMessage());
        }
    }
}