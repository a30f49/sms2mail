package com.example.sms2mail;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final String PREFS_SERVICE_STATE = "service_state";
    private static final String KEY_SERVICE_RUNNING = "is_service_running";
    
    private Button btnToggleService;
    private TextView tvServiceStatus, tvConfigStatus;
    private boolean isServiceRunning = false;
    private EmailConfigManager configManager;
    
    // 服务状态广播接收器
    private BroadcastReceiver serviceStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsMonitorService.ACTION_SERVICE_STATUS.equals(intent.getAction())) {
                boolean running = intent.getBooleanExtra(SmsMonitorService.EXTRA_SERVICE_RUNNING, false);
                isServiceRunning = running;
                saveServiceState(running);
                updateServiceStatus();
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupToolbar();
        setupClickListeners();
        requestPermissions();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 注册广播接收器
        IntentFilter filter = new IntentFilter(SmsMonitorService.ACTION_SERVICE_STATUS);
        registerReceiver(serviceStatusReceiver, filter);
        updateServiceStatus();
        updateConfigStatus();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_email_config) {
            startActivity(new Intent(this, EmailConfigActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void showAboutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(R.string.app_name)
            .setMessage("版本: 1.0.0\n\n一个简单实用的短信转邮件应用\n\n© 2025 SMS2Mail Team")
            .setPositiveButton("确定", null)
            .show();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // 注销广播接收器
        try {
            unregisterReceiver(serviceStatusReceiver);
        } catch (IllegalArgumentException e) {
            // 接收器未注册，忽略
        }
    }
    
    private void initViews() {
        btnToggleService = findViewById(R.id.btnToggleService);
        tvServiceStatus = findViewById(R.id.tvServiceStatus);
        tvConfigStatus = findViewById(R.id.tvConfigStatus);
        
        configManager = new EmailConfigManager(this);
        updateServiceStatus();
        updateConfigStatus();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }
    
    private void setupClickListeners() {
        btnToggleService.setOnClickListener(v -> toggleSmsService());
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
    

    
    private void toggleSmsService() {
        if (isServiceRunning) {
            stopSmsService();
        } else {
            startSmsService();
        }
    }
    
    private void startSmsService() {
        // 检查邮件配置是否完整
        if (!configManager.isConfigCompleted()) {
            Toast.makeText(this, R.string.msg_config_required, Toast.LENGTH_SHORT).show();
            // 打开邮箱配置页面
            startActivity(new Intent(this, EmailConfigActivity.class));
            return;
        }
        
        Intent serviceIntent = new Intent(this, SmsMonitorService.class);
        startForegroundService(serviceIntent);
        
        // 保存服务状态
        saveServiceState(true);
        updateServiceStatus();
        
        Toast.makeText(this, R.string.msg_service_started, Toast.LENGTH_SHORT).show();
    }
    
    private void stopSmsService() {
        Intent serviceIntent = new Intent(this, SmsMonitorService.class);
        stopService(serviceIntent);
        
        // 保存服务状态
        saveServiceState(false);
        updateServiceStatus();
        
        Toast.makeText(this, R.string.msg_service_stopped, Toast.LENGTH_SHORT).show();
    }
    
    private void updateServiceStatus() {
        // 检查服务是否真正在运行
        isServiceRunning = isServiceRunning(SmsMonitorService.class) || getSavedServiceState();
        
        if (isServiceRunning) {
            // 服务运行中 - 显示停止按钮
            btnToggleService.setText(R.string.btn_stop_monitoring);
            btnToggleService.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(this, android.R.color.holo_red_dark)));
            tvServiceStatus.setText(R.string.status_service_running);
            tvServiceStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        } else {
            // 服务已停止 - 显示启动按钮
            btnToggleService.setText(R.string.btn_start_monitoring);
            btnToggleService.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(this, android.R.color.holo_green_dark)));
            tvServiceStatus.setText(R.string.status_service_stopped);
            tvServiceStatus.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        }
    }
    
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
    private void updateConfigStatus() {
        if (configManager.isConfigCompleted()) {
            tvConfigStatus.setText(R.string.config_status_configured);
            tvConfigStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        } else {
            tvConfigStatus.setText(R.string.config_status_not_configured);
            tvConfigStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
        }
    }
    
    private void saveServiceState(boolean running) {
        SharedPreferences prefs = getSharedPreferences(PREFS_SERVICE_STATE, MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_SERVICE_RUNNING, running).apply();
    }
    
    private boolean getSavedServiceState() {
        SharedPreferences prefs = getSharedPreferences(PREFS_SERVICE_STATE, MODE_PRIVATE);
        return prefs.getBoolean(KEY_SERVICE_RUNNING, false);
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