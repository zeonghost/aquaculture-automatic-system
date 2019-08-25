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

import org.w3c.dom.Text;

import java.util.Objects;

import static com.example.aquaculture.Model.Constant.TIME_IN_STATUS;

public class TaskViewHolder extends RecyclerView.ViewHolder {
    //public TextView TaskId;
    public TextView date;
    public TextView receiver;
    public TextView receiverName;
    public TextView status;
    public TextView task;
    public TextView time;
    public TextView uploader;
    public TextView uploaderName;
    public Button edit;
    public Button delete;
    public Button done;

    public TaskViewHolder(@NonNull View itemView){
        super(itemView);
        //TaskId = itemView.findViewById(R.id.taskId);
        date = itemView.findViewById(R.id.date);
        receiver = itemView.findViewById(R.id.receiver);
        status = itemView.findViewById(R.id.status);
        task = itemView.findViewById(R.id.task);
        time = itemView.findViewById(R.id.time);
        uploader = itemView.findViewById(R.id.uploader);
        edit = itemView.findViewById(R.id.button);
        delete = itemView.findViewById(R.id.button2);
        done = itemView.findViewById(R.id.button3);
        uploaderName = itemView.findViewById(R.id.txtUploaderName);
        receiverName = itemView.findViewById(R.id.txtReceiverName);
        uploader.setVisibility(View.INVISIBLE);
        receiver.setVisibility(View.INVISIBLE);
        showButtonPerRole();
    }

    private void showButtonPerRole(){
        String role = MainActivity.sp.getString("role", "");
        if (Objects.equals(role, "Admin")){
            edit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            done.setVisibility(View.INVISIBLE);
        } else {
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            done.setVisibility(View.VISIBLE);
            if(TIME_IN_STATUS == 1){
                done.setVisibility(View.VISIBLE);
                delete.setVisibility(View.INVISIBLE);
            } else {
                done.setVisibility(View.GONE);
            }
        }
    }
}
