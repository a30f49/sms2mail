package com.example.sms2mail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private List<LogEntry> logEntries;

    public LogAdapter(List<LogEntry> logEntries) {
        this.logEntries = logEntries;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogEntry logEntry = logEntries.get(position);
        holder.senderTextView.setText("Sender: " + logEntry.getSender());
        holder.bodyTextView.setText("Message: " + logEntry.getBody());
        holder.timestampTextView.setText("Time: " + formatTimestamp(logEntry.getTimestamp()));
        holder.statusTextView.setText("Status: " + logEntry.getForwardStatus());
    }

    @Override
    public int getItemCount() {
        return logEntries.size();
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView senderTextView;
        TextView bodyTextView;
        TextView timestampTextView;
        TextView statusTextView;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            bodyTextView = itemView.findViewById(R.id.bodyTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }
    }
}
