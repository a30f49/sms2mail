package com.example.sms2mail;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class EnhancedLogActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EnhancedLogAdapter logAdapter;
    private List<LogDisplayEntry> logEntries;
    private Handler refreshHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        logEntries = new ArrayList<>();
        logAdapter = new EnhancedLogAdapter(logEntries);
        recyclerView.setAdapter(logAdapter);

        loadEnhancedLogEntries();
    }

    private void loadEnhancedLogEntries() {
        LogManager logManager = LogManager.getInstance(this);
        logEntries.clear();
        
        // Process SMS logs
        for (SmsLog smsLog : logManager.getAllSmsLogs()) {
            // Add SMS received entry
            logEntries.add(new LogDisplayEntry(
                LogDisplayEntry.TYPE_SMS_RECEIVED,
                "SMS Received",
                "SMS received from network",
                smsLog.getMessage(),
                smsLog.getReceivedTime(),
                smsLog.getSender(),
                true,
                null
            ));
            
            // Add corresponding email entries
            List<EmailLog> emailLogs = logManager.getEmailLogsBySmsId(smsLog.getId());
            for (EmailLog emailLog : emailLogs) {
                LogDisplayEntry emailEntry;
                if (emailLog.isSuccess()) {
                    emailEntry = new LogDisplayEntry(
                        LogDisplayEntry.TYPE_EMAIL_SENT,
                        "Email Sent",
                        "Email successfully forwarded",
                        emailLog.getMessage(),
                        emailLog.getSendTime(),
                        emailLog.getSender(),
                        true,
                        emailLog.getRecipientEmails()
                    );
                } else {
                    String errorDetails = emailLog.getErrorMessage() != null && 
                                         !emailLog.getErrorMessage().isEmpty() ? 
                                         emailLog.getErrorMessage() : "Unknown error";
                    emailEntry = new LogDisplayEntry(
                        LogDisplayEntry.TYPE_EMAIL_FAILED,
                        "Email Failed",
                        "Error: " + errorDetails,
                        emailLog.getMessage(),
                        emailLog.getSendTime(),
                        emailLog.getSender(),
                        false,
                        emailLog.getRecipientEmails()
                    );
                }
                logEntries.add(emailEntry);
            }
        }
        logAdapter.updateData(logEntries);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Auto-refresh on resume to show any new logs
        refreshHandler.postDelayed(() -> loadEnhancedLogEntries(), 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        refreshHandler.removeCallbacksAndMessages(null);
    }
}