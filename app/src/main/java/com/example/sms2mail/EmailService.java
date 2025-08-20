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
            Log.e(TAG, getString(R.string.log_email_settings_incomplete));
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
            message.setSubject(getString(R.string.email_subject_prefix, smsSender));
            
            String emailBody = getString(R.string.email_body_template, 
                smsSender, smsMessage, new java.util.Date().toString());
            
            message.setText(emailBody);
            
            Transport.send(message);
            Log.d(TAG, getString(R.string.log_email_sent_success));
            
        } catch (MessagingException e) {
            Log.e(TAG, getString(R.string.log_email_sent_failed, e.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, getString(R.string.log_email_sent_failed, e.getMessage()));
        }
    }
}