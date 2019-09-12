package com.example.aquaculture.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aquaculture.R;

public class LogViewHolder extends RecyclerView.ViewHolder {
    public TextView logTime;
    public TextView logDetail;
    public LinearLayout logHolder;

    public LogViewHolder(@NonNull View itemView) {
        super(itemView);
        logHolder = itemView.findViewById(R.id.logHolderLayout);
        logTime = itemView.findViewById(R.id.log_time);
        logDetail = itemView.findViewById(R.id.log_detail);
    }
}
