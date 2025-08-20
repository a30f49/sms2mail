package com.example.sms2mail;

import android.os.AsyncTask;
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

/**
 * SMTP连接测试器
 * 用于测试邮件配置的有效性
 */
public class SmtpConnectionTester {
    private static final String TAG = "SmtpConnectionTester";
    
    /**
     * 测试结果回调接口
     */
    public interface TestCallback {
        void onTestStart();
        void onTestSuccess(String message);
        void onTestFailure(String error);
    }
    
    /**
     * 测试SMTP连接（仅连接测试，发送到发送者邮箱）
     * @param config 邮件配置
     * @param callback 测试结果回调
     */
    public static void testConnection(EmailConfig config, TestCallback callback) {
        new SmtpTestTask(config, callback, false).execute();
    }
    
    /**
     * 测试SMTP连接并发送到接收邮箱
     * @param config 邮件配置
     * @param callback 测试结果回调
     */
    public static void testConnectionWithReceiver(EmailConfig config, TestCallback callback) {
        new SmtpTestTask(config, callback, true).execute();
    }
    
    /**
     * 异步SMTP测试任务
     */
    private static class SmtpTestTask extends AsyncTask<Void, Void, TestResult> {
        private final EmailConfig config;
        private final TestCallback callback;
        private final boolean sendToReceiver;
        
        public SmtpTestTask(EmailConfig config, TestCallback callback, boolean sendToReceiver) {
            this.config = config;
            this.callback = callback;
            this.sendToReceiver = sendToReceiver;
        }
        
        @Override
        protected void onPreExecute() {
            if (callback != null) {
                callback.onTestStart();
            }
        }
        
        @Override
        protected TestResult doInBackground(Void... voids) {
            try {
                // 验证配置完整性
                if (!isConfigValid(config)) {
                    return new TestResult(false, "配置信息不完整，请检查所有必填项");
                }
                
                // 创建SMTP属性
                Properties props = createSmtpProperties(config);
                
                // 创建会话
                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(config.getSenderEmail(), config.getSenderPassword());
                    }
                });
                
                // 启用调试模式
                session.setDebug(false);
                
                // 创建测试邮件
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(config.getSenderEmail()));
                
                // 根据测试类型设置接收者
                String recipientEmail;
                String testType;
                if (sendToReceiver) {
                    recipientEmail = config.getReceiverEmail();
                    testType = "发送到接收邮箱";
                } else {
                    recipientEmail = config.getSenderEmail();
                    testType = "连接测试";
                }
                
                message.setRecipients(Message.RecipientType.TO, 
                    InternetAddress.parse(recipientEmail));
                
                message.setSubject("SMS2Mail SMTP" + testType);
                message.setText("这是一封SMS2Mail SMTP" + testType + "邮件。\n\n" +
                               "如果您收到此邮件，说明SMTP配置正确，可以正常发送邮件。\n\n" +
                               "测试时间: " + new java.util.Date().toString() + "\n" +
                               "发送邮箱: " + config.getSenderEmail() + "\n" +
                               "接收邮箱: " + recipientEmail + "\n" +
                               "SMTP服务器: " + config.getSmtpHost() + ":" + config.getSmtpPort() + "\n" +
                               "加密方式: " + (config.isSmtpUseSSL() ? "SSL" : (config.isSmtpUseTls() ? "TLS" : "无")));
                
                // 连接并发送测试邮件
                Transport transport = session.getTransport("smtp");
                transport.connect(config.getSmtpHost(), 
                                Integer.parseInt(config.getSmtpPort()),
                                config.getSenderEmail(), 
                                config.getSenderPassword());
                
                // 发送邮件
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();
                
                Log.d(TAG, "SMTP" + testType + "成功: " + config.getProvider().getDisplayName());
                return new TestResult(true, "SMTP" + testType + "成功！\n\n" +
                                           "服务器: " + config.getSmtpHost() + ":" + config.getSmtpPort() + "\n" +
                                           "加密: " + (config.isSmtpUseSSL() ? "SSL" : (config.isSmtpUseTls() ? "TLS" : "无")) + "\n" +
                                           "已发送测试邮件到: " + recipientEmail);
                
            } catch (MessagingException e) {
                Log.e(TAG, "SMTP连接测试失败", e);
                return new TestResult(false, parseMessagingException(e));
            } catch (NumberFormatException e) {
                Log.e(TAG, "端口号格式错误", e);
                return new TestResult(false, "SMTP端口号格式错误: " + config.getSmtpPort());
            } catch (Exception e) {
                Log.e(TAG, "SMTP连接测试异常", e);
                return new TestResult(false, "连接测试失败: " + e.getMessage());
            }
        }
        
        @Override
        protected void onPostExecute(TestResult result) {
            if (callback != null) {
                if (result.success) {
                    callback.onTestSuccess(result.message);
                } else {
                    callback.onTestFailure(result.message);
                }
            }
        }
    }
    
    /**
     * 创建SMTP属性
     */
    private static Properties createSmtpProperties(EmailConfig config) {
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
            props.put("mail.smtp.socketFactory.fallback", "false");
        } else if (config.isSmtpUseTls()) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }
        
        // 超时设置
        props.put("mail.smtp.timeout", "15000");
        props.put("mail.smtp.connectiontimeout", "15000");
        props.put("mail.smtp.writetimeout", "15000");
        
        return props;
    }
    
    /**
     * 验证配置有效性
     */
    private static boolean isConfigValid(EmailConfig config) {
        return config != null &&
               !config.getSenderEmail().trim().isEmpty() &&
               !config.getSenderPassword().trim().isEmpty() &&
               !config.getSmtpHost().trim().isEmpty() &&
               !config.getSmtpPort().trim().isEmpty() &&
               android.util.Patterns.EMAIL_ADDRESS.matcher(config.getSenderEmail()).matches();
    }
    
    /**
     * 解析MessagingException错误信息
     */
    private static String parseMessagingException(MessagingException e) {
        String message = e.getMessage();
        if (message == null) {
            return "SMTP连接失败，请检查网络连接";
        }
        
        // 常见错误处理
        if (message.contains("Authentication failed") || message.contains("535")) {
            return "认证失败，请检查邮箱地址和密码\n\n提示：Gmail等邮箱需要使用应用专用密码";
        } else if (message.contains("Connection timed out") || message.contains("timeout")) {
            return "连接超时，请检查网络连接和SMTP服务器地址";
        } else if (message.contains("Connection refused") || message.contains("refused")) {
            return "连接被拒绝，请检查SMTP服务器地址和端口号";
        } else if (message.contains("Unknown host") || message.contains("host")) {
            return "无法解析SMTP服务器地址，请检查服务器设置";
        } else if (message.contains("SSL") || message.contains("TLS")) {
            return "SSL/TLS连接失败，请检查加密设置";
        } else {
            return "SMTP连接失败: " + message;
        }
    }
    
    /**
     * 测试结果数据类
     */
    private static class TestResult {
        final boolean success;
        final String message;
        
        TestResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}