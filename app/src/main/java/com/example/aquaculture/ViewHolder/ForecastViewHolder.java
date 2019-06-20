package com.example.aquaculture.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.aquaculture.R;

public class ForecastViewHolder extends RecyclerView.ViewHolder {
    public TextView timestampForecast;
    public TextView tempForecast;


    public ForecastViewHolder(@NonNull View itemView) {
        super(itemView);
        timestampForecast = itemView.findViewById(R.id.txtViewDateTimeForecast);
        tempForecast = itemView.findViewById(R.id.txtViewTempForecast);
    }
}
