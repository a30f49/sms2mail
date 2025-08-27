package com.example.sms2mail;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class LogActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LogAdapter logAdapter;
    private List<LogEntry> logEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        logEntries = new ArrayList<>();

        loadLogEntries();

        logAdapter = new LogAdapter(logEntries);
        recyclerView.setAdapter(logAdapter);
    }

    private void loadLogEntries() {
        LogManager logManager = LogManager.getInstance(this);
        logEntries.clear();
        
        // 加载短信日志作为统一的日志条目
        for (SmsLog smsLog : logManager.getAllSmsLogs()) {
            String status = smsLog.getProcessStatus();
            if (smsLog.isProcessed()) {
                // 获取相关的邮件日志以构建完整的状态信息
                StringBuilder statusBuilder = new StringBuilder(status);
                for (EmailLog emailLog : logManager.getEmailLogsBySmsId(smsLog.getId())) {
                    if (statusBuilder.length() > 0) {
                        statusBuilder.append(" | ");
                    }
                    statusBuilder.append("Email: ")
                        .append(emailLog.isSuccess() ? "Sent" : "Failed")
                        .append(emailLog.getErrorMessage() != null && !emailLog.getErrorMessage().isEmpty() ? 
                                " - " + emailLog.getErrorMessage() : "");
                }
                status = statusBuilder.toString();
            }
            
            logEntries.add(new LogEntry(
                smsLog.getId(),
                smsLog.getSender(),
                smsLog.getMessage(),
                smsLog.getReceivedTime().getTime(),
                status
            ));
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadLogEntries();
        logAdapter.notifyDataSetChanged();
    }
}
