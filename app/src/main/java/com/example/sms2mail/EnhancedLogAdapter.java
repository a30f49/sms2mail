package com.example.sms2mail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EnhancedLogAdapter extends RecyclerView.Adapter<EnhancedLogAdapter.LogViewHolder> {

    private List<LogDisplayEntry> logEntries;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public EnhancedLogAdapter(List<LogDisplayEntry> logEntries) {
        this.logEntries = logEntries;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.enhanced_log_item, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogDisplayEntry entry = logEntries.get(position);
        
        // Set icon based on log type
        int iconRes;
        int colorRes;
        String statusText;
        
        switch (entry.getType()) {
            case LogDisplayEntry.TYPE_SMS_RECEIVED:
                iconRes = R.drawable.ic_sms_received;
                colorRes = R.color.sms_success;
                statusText = "SMS";
                break;
            case LogDisplayEntry.TYPE_EMAIL_SENT:
                iconRes = R.drawable.ic_email_sent;
                colorRes = R.color.email_success;
                statusText = "SENT";
                break;
            case LogDisplayEntry.TYPE_EMAIL_FAILED:
                iconRes = R.drawable.ic_email_failed;
                colorRes = R.color.failure_red;
                statusText = "FAILED";
                break;
            default:
                iconRes = R.drawable.ic_sms_received;
                colorRes = R.color.sms_success;
                statusText = "UNKNOWN";
        }

        holder.iconImageView.setImageResource(iconRes);
        holder.iconImageView.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), colorRes));
        
        holder.titleTextView.setText(entry.getTitle());
        holder.statusTextView.setText(statusText);
        holder.statusTextView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), colorRes));
        
        holder.detailsTextView.setText(entry.getDetails());
        holder.senderTextView.setText("From: " + entry.getSender());
        holder.bodyTextView.setText(entry.getBody());
        holder.timestampTextView.setText(sdf.format(entry.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return logEntries.size();
    }

    public void updateData(List<LogDisplayEntry> newEntries) {
        logEntries.clear();
        logEntries.addAll(newEntries);
        notifyDataSetChanged();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView titleTextView;
        TextView statusTextView;
        TextView detailsTextView;
        TextView senderTextView;
        TextView bodyTextView;
        TextView timestampTextView;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            detailsTextView = itemView.findViewById(R.id.detailsTextView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            bodyTextView = itemView.findViewById(R.id.bodyTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }
    }
}