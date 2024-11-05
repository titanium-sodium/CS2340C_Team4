package com.example.sprintproject.viewmodels;


import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.sprintproject.R;

import com.example.sprintproject.model.AccomodationsModel;



public class AccomodationAdaptor extends RecyclerView.Adapter<AccomodationAdaptor.ViewHolder> {

    private List<AccomodationsModel> lodgingList;

    public AccomodationAdaptor (List<AccomodationsModel> accReservations) {
        this.lodgingList = accReservations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_accomodation_res, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AccomodationsModel lodgingModel = lodgingList.get(position);
        holder.ciTimeText.setText("Check-in: " + lodgingModel.getCheckInDate());
        holder.coTimeText.setText("Check-out: " + lodgingModel.getCheckOutDate());
        holder.roomCountText.setText(lodgingModel.getNumberOfRooms());
        holder.roomTypeText.setText(lodgingModel.getRoomType());
        holder.websiteText.setText(lodgingModel.getWebsite());


    }

    @Override
    public int getItemCount() {
        return lodgingList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView ciTimeText,coTimeText,
                roomCountText, roomTypeText, websiteText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ciTimeText = itemView.findViewById(R.id.ciText);
            coTimeText = itemView.findViewById(R.id.coText);
            roomCountText = itemView.findViewById(R.id.roomCountText);
            roomTypeText = itemView.findViewById(R.id.roomTypeText);
            websiteText = itemView.findViewById(R.id.websiteText);
        }

    }
}
