package com.example.sms2mail;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private EditText etSenderEmail, etSenderPassword, etReceiverEmail;
    private Button btnSave, btnStartService, btnStopService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupClickListeners();
        requestPermissions();
    }
    
    private void initViews() {
        etSenderEmail = findViewById(R.id.etSenderEmail);
        etSenderPassword = findViewById(R.id.etSenderPassword);
        etReceiverEmail = findViewById(R.id.etReceiverEmail);
        btnSave = findViewById(R.id.btnSave);
        btnStartService = findViewById(R.id.btnStartService);
        btnStopService = findViewById(R.id.btnStopService);
        
        // 加载保存的设置
        loadSettings();
    }
    
    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveSettings());
        btnStartService.setOnClickListener(v -> startSmsService());
        btnStopService.setOnClickListener(v -> stopSmsService());
    }
    
    private void requestPermissions() {
        String[] permissions = {
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.INTERNET
        };
        
        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }
        
        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }
    
    private void saveSettings() {
        String senderEmail = etSenderEmail.getText().toString().trim();
        String senderPassword = etSenderPassword.getText().toString().trim();
        String receiverEmail = etReceiverEmail.getText().toString().trim();
        
        if (senderEmail.isEmpty() || senderPassword.isEmpty() || receiverEmail.isEmpty()) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 保存到SharedPreferences
        getSharedPreferences("email_settings", MODE_PRIVATE)
            .edit()
            .putString("sender_email", senderEmail)
            .putString("sender_password", senderPassword)
            .putString("receiver_email", receiverEmail)
            .apply();
            
        Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show();
    }
    
    private void loadSettings() {
        android.content.SharedPreferences prefs = getSharedPreferences("email_settings", MODE_PRIVATE);
        etSenderEmail.setText(prefs.getString("sender_email", ""));
        etSenderPassword.setText(prefs.getString("sender_password", ""));
        etReceiverEmail.setText(prefs.getString("receiver_email", ""));
    }
    
    private void startSmsService() {
        Intent serviceIntent = new Intent(this, SmsMonitorService.class);
        startForegroundService(serviceIntent);
        Toast.makeText(this, "短信监控服务已启动", Toast.LENGTH_SHORT).show();
    }
    
    private void stopSmsService() {
        Intent serviceIntent = new Intent(this, SmsMonitorService.class);
        stopService(serviceIntent);
        Toast.makeText(this, "短信监控服务已停止", Toast.LENGTH_SHORT).show();
    }
}