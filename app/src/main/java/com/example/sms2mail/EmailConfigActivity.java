package com.example.sms2mail;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class EmailConfigActivity extends AppCompatActivity {
    
    private Spinner spinnerProvider;
    private EditText etSenderEmail, etSenderPassword, etReceiverEmail;
    private Button btnSave, btnTest;
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
    
    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveSettings());
        btnTest.setOnClickListener(v -> testEmailConfig());
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
        
        // 这里可以添加测试邮件发送的逻辑
        Toast.makeText(this, R.string.msg_test_email_feature_coming, Toast.LENGTH_SHORT).show();
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