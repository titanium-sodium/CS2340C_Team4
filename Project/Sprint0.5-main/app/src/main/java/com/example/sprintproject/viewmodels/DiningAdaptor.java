package com.example.sprintproject.viewmodels;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sprintproject.model.DiningReservation;
import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.example.sprintproject.R;

public class DiningAdaptor extends RecyclerView.Adapter<DiningAdaptor.ViewHolder> {

    private List<DiningReservation> reservations;

    public DiningAdaptor(List<DiningReservation> reservations) {
        this.reservations = reservations;
    }


    @NonNull
    @Override
    public DiningAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dining_res, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiningReservation reservation = reservations.get(position);
        holder.locationText.setText(reservation.getLocation());
        holder.restaurantText.setText(reservation.getUserId());
        holder.timeText.setText(reservation.getTime());
        holder.websiteText.setText(reservation.getWebsite());
    }


    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView locationText;
        private TextView restaurantText;
        private TextView timeText;
        private TextView websiteText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            locationText = itemView.findViewById(R.id.locationText);
            restaurantText = itemView.findViewById(R.id.restaurantText);
            timeText = itemView.findViewById(R.id.timeText);
            websiteText = itemView.findViewById(R.id.websiteText);

        }
    }

}
