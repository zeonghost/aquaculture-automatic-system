package com.example.aquaculture.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.aquaculture.R;

public class PartnerViewHolder extends RecyclerView.ViewHolder {
    public TextView deviceId;
    public TextView fullNamePartner;
    public TextView username;


    public PartnerViewHolder(@NonNull View itemView) {
        super(itemView);
        deviceId = itemView.findViewById(R.id.txtViewDevice);
        fullNamePartner = itemView.findViewById(R.id.txtViewFullname);
        username = itemView.findViewById(R.id.txtViewUsername);
    }
}
