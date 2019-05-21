package com.example.aquaculture.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.aquaculture.R;


public class PondViewHolder extends RecyclerView.ViewHolder{
    public TextView piId;
    public TextView pondName;
    public TextView location;

    public PondViewHolder(@NonNull View itemView) {
        super(itemView);
        piId = itemView.findViewById(R.id.txtViewPiID);
        pondName = itemView.findViewById(R.id.txtViewPondName);
        location = itemView.findViewById(R.id.txtViewPondLocation);
    }
}