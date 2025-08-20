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
    private android.widget.TextView tvServiceStatus;
    
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
        tvServiceStatus = findViewById(R.id.tvServiceStatus);
        
        // 加载保存的设置
        loadSettings();
        updateServiceStatus();
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
            Toast.makeText(this, R.string.msg_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 保存到SharedPreferences
        getSharedPreferences("email_settings", MODE_PRIVATE)
            .edit()
            .putString("sender_email", senderEmail)
            .putString("sender_password", senderPassword)
            .putString("receiver_email", receiverEmail)
            .apply();
            
        Toast.makeText(this, R.string.msg_settings_saved, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, R.string.msg_service_started, Toast.LENGTH_SHORT).show();
        tvServiceStatus.setText(R.string.status_service_running);
    }
    
    private void stopSmsService() {
        Intent serviceIntent = new Intent(this, SmsMonitorService.class);
        stopService(serviceIntent);
        Toast.makeText(this, R.string.msg_service_stopped, Toast.LENGTH_SHORT).show();
        updateServiceStatus();
    }
    
    private void updateServiceStatus() {
        // 简单的状态显示，实际应用中可以检查服务是否真正运行
        tvServiceStatus.setText(R.string.status_service_stopped);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (allGranted) {
                Toast.makeText(this, R.string.msg_permissions_required, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.msg_permissions_denied, Toast.LENGTH_LONG).show();
            }
        }
    }
}