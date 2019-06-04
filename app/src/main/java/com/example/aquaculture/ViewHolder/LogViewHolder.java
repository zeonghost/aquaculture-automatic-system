package com.example.aquaculture.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.aquaculture.R;

public class LogViewHolder extends RecyclerView.ViewHolder {
    public TextView logTime;
    public TextView logDetail;

    public LogViewHolder(@NonNull View itemView) {
        super(itemView);
        logTime = itemView.findViewById(R.id.log_time);
        logDetail = itemView.findViewById(R.id.log_detail);
    }
}
