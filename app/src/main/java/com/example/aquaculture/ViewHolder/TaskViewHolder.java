package com.example.aquaculture.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.aquaculture.R;

public class TaskViewHolder extends RecyclerView.ViewHolder {
    public TextView TaskId;
    public TextView date;
    public TextView receiver;
    public TextView status;
    public TextView task;
    public TextView time;
    public TextView uploader;
    public Button edit;
    public Button delete;

    public TaskViewHolder(@NonNull View itemView){
        super(itemView);
        TaskId = itemView.findViewById(R.id.taskId);
        date = itemView.findViewById(R.id.date);
        receiver = itemView.findViewById(R.id.receiver);
        status = itemView.findViewById(R.id.status);
        task = itemView.findViewById(R.id.task);
        time = itemView.findViewById(R.id.time);
        uploader = itemView.findViewById(R.id.uploader);
        edit = itemView.findViewById(R.id.button);
        delete = itemView.findViewById(R.id.button2);
    }
}
