package com.example.sms2mail;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class EmailConfigActivity extends AppCompatActivity {
    
    private Spinner spinnerProvider;
    private EditText etSenderEmail, etSenderPassword, etReceiverEmail;
    private Button btnSave, btnTest, btnTestReceiver;
    private ProgressBar progressBarTest;
    private TextView tvTestStatus;
    private EmailConfigManager configManager;
    private EmailConfig.EmailProvider selectedProvider = EmailConfig.EmailProvider.GMAIL;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_config);
        
        initViews();
        setupToolbar();
        loadSettings();
        setupClickListeners();
    }
    
    private void initViews() {
        spinnerProvider = findViewById(R.id.spinnerProvider);
        etSenderEmail = findViewById(R.id.etSenderEmail);
        etSenderPassword = findViewById(R.id.etSenderPassword);
        etReceiverEmail = findViewById(R.id.etReceiverEmail);
        btnSave = findViewById(R.id.btnSave);
        btnTest = findViewById(R.id.btnTest);
        btnTestReceiver = findViewById(R.id.btnTestReceiver);
        progressBarTest = findViewById(R.id.progressBarTest);
        tvTestStatus = findViewById(R.id.tvTestStatus);
        
        configManager = new EmailConfigManager(this);
        setupProviderSpinner();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_email_config);
        }
    }
    
    private void setupProviderSpinner() {
        EmailConfig.EmailProvider[] providers = EmailConfigManager.getSupportedProviders();
        ArrayAdapter<EmailConfig.EmailProvider> adapter = new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_item, providers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvider.setAdapter(adapter);
        
        spinnerProvider.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProvider = providers[position];
                Toast.makeText(EmailConfigActivity.this, "已选择: " + selectedProvider.getDisplayName(), Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 不做任何操作
            }
        });
    }
    
    private void testEmailConfigWithReceiver() {
        String senderEmail = etSenderEmail.getText().toString().trim();
        String senderPassword = etSenderPassword.getText().toString().trim();
        String receiverEmail = etReceiverEmail.getText().toString().trim();
        
        // 验证输入
        if (senderEmail.isEmpty() || senderPassword.isEmpty() || receiverEmail.isEmpty()) {
            Toast.makeText(this, "请填写完整的邮箱配置信息", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!isValidEmail(senderEmail)) {
            Toast.makeText(this, "发送邮箱格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!isValidReceiverEmails(receiverEmail)) {
            Toast.makeText(this, "接收邮箱格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 创建临时配置用于测试
        EmailConfig testConfig = new EmailConfig(senderEmail, senderPassword, receiverEmail, selectedProvider);
        
        // 执行SMTP连接测试并发送到接收邮箱
        SmtpConnectionTester.testConnectionWithReceiver(testConfig, new SmtpConnectionTester.TestCallback() {
            @Override
            public void onTestStart() {
                runOnUiThread(() -> {
                    // 禁用按钮和显示进度
                    btnTest.setEnabled(false);
                    btnTestReceiver.setEnabled(false);
                    btnSave.setEnabled(false);
                    btnTestReceiver.setText("发送中...");
                    
                    // 显示进度条和状态
                    progressBarTest.setVisibility(View.VISIBLE);
                    tvTestStatus.setVisibility(View.VISIBLE);
                    tvTestStatus.setText("正在发送测试邮件到接收邮箱...");
                    tvTestStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                    
                    Toast.makeText(EmailConfigActivity.this, "开始发送测试邮件", Toast.LENGTH_SHORT).show();
                });
            }
            
            @Override
            public void onTestSuccess(String message) {
                runOnUiThread(() -> {
                    // 恢复按钮状态
                    btnTest.setEnabled(true);
                    btnTestReceiver.setEnabled(true);
                    btnSave.setEnabled(true);
                    btnTestReceiver.setText(R.string.btn_test_send_email);
                    
                    // 隐藏进度条，显示成功状态
                    progressBarTest.setVisibility(View.GONE);
                    tvTestStatus.setText("✅ 测试邮件发送成功");
                    tvTestStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    
                    // 3秒后隐藏状态文本
                    tvTestStatus.postDelayed(() -> tvTestStatus.setVisibility(View.GONE), 3000);
                    
                    new androidx.appcompat.app.AlertDialog.Builder(EmailConfigActivity.this)
                        .setTitle("✅ 发送成功")
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
                });
            }
            
            @Override
            public void onTestFailure(String error) {
                runOnUiThread(() -> {
                    // 恢复按钮状态
                    btnTest.setEnabled(true);
                    btnTestReceiver.setEnabled(true);
                    btnSave.setEnabled(true);
                    btnTestReceiver.setText(R.string.btn_test_send_email);
                    
                    // 隐藏进度条，显示失败状态
                    progressBarTest.setVisibility(View.GONE);
                    tvTestStatus.setText("❌ 测试邮件发送失败");
                    tvTestStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    
                    // 5秒后隐藏状态文本
                    tvTestStatus.postDelayed(() -> tvTestStatus.setVisibility(View.GONE), 5000);
                    
                    new androidx.appcompat.app.AlertDialog.Builder(EmailConfigActivity.this)
                        .setTitle("❌ 发送失败")
                        .setMessage(error)
                        .setNeutralButton("查看帮助", (dialog, which) -> showSmtpHelp())
                        .setPositiveButton("确定", null)
                        .show();
                });
            }
        });
    }
    
    private void showSmtpHelp() {
        String helpMessage = "SMTP连接常见问题解决方案：\n\n" +
                "1. 检查网络连接是否正常\n" +
                "2. 确认邮箱地址和密码正确\n" +
                "3. 对于Gmail：需要使用应用专用密码\n" +
                "4. 对于QQ邮箱：需要开启SMTP服务并使用授权码\n" +
                "5. 对于163邮箱：需要开启客户端授权密码\n" +
                "6. 检查防火墙是否阻止了连接\n" +
                "7. 某些邮箱可能需要开启\"不够安全的应用\"访问权限";
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
             .setTitle("📖 SMTP配置帮助")
             .setMessage(helpMessage)
             .setPositiveButton("我知道了", null)
             .show();
     }
    
    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveSettings());
        btnTest.setOnClickListener(v -> testEmailConfig());
        btnTestReceiver.setOnClickListener(v -> testEmailConfigWithReceiver());
    }
    
    private void loadSettings() {
        EmailConfig config = configManager.getEmailConfig();
        
        // 设置服务商选择
        selectedProvider = config.getProvider();
        EmailConfig.EmailProvider[] providers = EmailConfigManager.getSupportedProviders();
        for (int i = 0; i < providers.length; i++) {
            if (providers[i] == selectedProvider) {
                spinnerProvider.setSelection(i);
                break;
            }
        }
        
        etSenderEmail.setText(config.getSenderEmail());
        etSenderPassword.setText(config.getSenderPassword());
        etReceiverEmail.setText(config.getReceiverEmail());
    }
    
    private void saveSettings() {
        String senderEmail = etSenderEmail.getText().toString().trim();
        String senderPassword = etSenderPassword.getText().toString().trim();
        String receiverEmail = etReceiverEmail.getText().toString().trim();
        
        if (senderEmail.isEmpty() || senderPassword.isEmpty() || receiverEmail.isEmpty()) {
            Toast.makeText(this, R.string.msg_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 验证邮箱格式
        if (!isValidEmail(senderEmail)) {
            Toast.makeText(this, "发送邮箱格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!isValidReceiverEmails(receiverEmail)) {
            Toast.makeText(this, "接收邮箱格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        
        EmailConfig config = new EmailConfig(senderEmail, senderPassword, receiverEmail, selectedProvider);
        
        if (configManager.saveEmailConfig(config)) {
            Toast.makeText(this, R.string.msg_settings_saved, Toast.LENGTH_SHORT).show();
            finish(); // 保存成功后返回主页面
        } else {
            Toast.makeText(this, R.string.msg_save_failed, Toast.LENGTH_SHORT).show();
        }
    }
    
    private void testEmailConfig() {
        String senderEmail = etSenderEmail.getText().toString().trim();
        String senderPassword = etSenderPassword.getText().toString().trim();
        String receiverEmail = etReceiverEmail.getText().toString().trim();
        
        if (senderEmail.isEmpty() || senderPassword.isEmpty() || receiverEmail.isEmpty()) {
            Toast.makeText(this, R.string.msg_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 验证邮箱格式
        if (!isValidEmail(senderEmail)) {
            Toast.makeText(this, "发送邮箱格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 创建临时配置用于测试
        EmailConfig testConfig = new EmailConfig(senderEmail, senderPassword, receiverEmail, selectedProvider);
        
        // 执行SMTP连接测试
        SmtpConnectionTester.testConnection(testConfig, new SmtpConnectionTester.TestCallback() {
            @Override
            public void onTestStart() {
                runOnUiThread(() -> {
                    // 禁用按钮和显示进度
                    btnTest.setEnabled(false);
                    btnTestReceiver.setEnabled(false);
                    btnSave.setEnabled(false);
                    btnTest.setText(R.string.msg_smtp_testing);
                    
                    // 显示进度条和状态
                    progressBarTest.setVisibility(View.VISIBLE);
                    tvTestStatus.setVisibility(View.VISIBLE);
                    tvTestStatus.setText("正在连接SMTP服务器...");
                    tvTestStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                    
                    Toast.makeText(EmailConfigActivity.this, R.string.msg_smtp_test_start, Toast.LENGTH_SHORT).show();
                });
            }
            
            @Override
            public void onTestSuccess(String message) {
                runOnUiThread(() -> {
                    // 恢复按钮状态
                    btnTest.setEnabled(true);
                    btnTestReceiver.setEnabled(true);
                    btnSave.setEnabled(true);
                    btnTest.setText(R.string.btn_test_config);
                    
                    // 隐藏进度条，显示成功状态
                    progressBarTest.setVisibility(View.GONE);
                    tvTestStatus.setText("✅ SMTP连接测试成功");
                    tvTestStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    
                    // 3秒后隐藏状态文本
                    tvTestStatus.postDelayed(() -> tvTestStatus.setVisibility(View.GONE), 3000);
                    
                    new androidx.appcompat.app.AlertDialog.Builder(EmailConfigActivity.this)
                        .setTitle("✅ 测试成功")
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
                });
            }
            
            @Override
            public void onTestFailure(String error) {
                runOnUiThread(() -> {
                    // 恢复按钮状态
                    btnTest.setEnabled(true);
                    btnTestReceiver.setEnabled(true);
                    btnSave.setEnabled(true);
                    btnTest.setText(R.string.btn_test_config);
                    
                    // 隐藏进度条，显示失败状态
                    progressBarTest.setVisibility(View.GONE);
                    tvTestStatus.setText("❌ SMTP连接测试失败");
                    tvTestStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    
                    // 5秒后隐藏状态文本
                    tvTestStatus.postDelayed(() -> tvTestStatus.setVisibility(View.GONE), 5000);
                    
                    new androidx.appcompat.app.AlertDialog.Builder(EmailConfigActivity.this)
                        .setTitle("❌ 测试失败")
                        .setMessage(error)
                        .setNeutralButton("查看帮助", (dialog, which) -> showSmtpHelp())
                        .setPositiveButton("确定", null)
                        .show();
                });
            }
        });
    }
    
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    private boolean isValidReceiverEmails(String emails) {
        if (emails.trim().isEmpty()) {
            return false;
        }
        
        // 分割多个邮箱地址
        String[] emailArray = emails.split(";");
        for (String email : emailArray) {
            String trimmedEmail = email.trim();
            if (!trimmedEmail.isEmpty() && !isValidEmail(trimmedEmail)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}