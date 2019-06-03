package com.example.aquaculture.ViewHolder;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.aquaculture.MainActivity;
import com.example.aquaculture.R;

import java.util.Objects;

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
    public Button done;

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
        done = itemView.findViewById(R.id.button3);
        showButtonPerRole();
    }

    private void showButtonPerRole(){
        String role = MainActivity.sp.getString("role", "");
        if (Objects.equals(role, "Admin")){
            edit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            done.setVisibility(View.INVISIBLE);
        } else {
            edit.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
            done.setVisibility(View.VISIBLE);
        }
    }
}
