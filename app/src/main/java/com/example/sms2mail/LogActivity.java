package com.example.sms2mail;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    private LogDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        logEntries = new ArrayList<>();
        dbHelper = new LogDatabaseHelper(this);

        loadLogEntries();

        logAdapter = new LogAdapter(logEntries);
        recyclerView.setAdapter(logAdapter);
    }

    private void loadLogEntries() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(LogDatabaseHelper.TABLE_LOGS, null, null, null, null, null, LogDatabaseHelper.COLUMN_TIMESTAMP + " DESC");

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_ID));
                String sender = cursor.getString(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_SENDER));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_BODY));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_TIMESTAMP));
                String forwardStatus = cursor.getString(cursor.getColumnIndexOrThrow(LogDatabaseHelper.COLUMN_FORWARD_STATUS));
                logEntries.add(new LogEntry(id, sender, body, timestamp, forwardStatus));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }
}
