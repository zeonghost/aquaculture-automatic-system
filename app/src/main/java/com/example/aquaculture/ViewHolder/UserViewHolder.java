package com.example.aquaculture.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.aquaculture.R;


public class UserViewHolder extends RecyclerView.ViewHolder {
    public TextView userName;
    public TextView firstName;
    public TextView lastName;
    public TextView role;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        userName = itemView.findViewById(R.id.txtLinkUserName);
        firstName = itemView.findViewById(R.id.txtLinkFirstName);
        lastName = itemView.findViewById(R.id.txtLinkLastName);
        role = itemView.findViewById(R.id.txtLinkUserRole);
    }
}
