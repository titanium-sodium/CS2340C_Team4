package com.example.sprintproject.views;

import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sprintproject.model.DiningReservation;
import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.example.sprintproject.R;

public class DiningAdapter extends RecyclerView.Adapter<DiningAdapter.ViewHolder> {
    // Rest of your adapter code remains the same
    private List<DiningReservation> reservations;

    public DiningAdapter(List<DiningReservation> reservations) {
        this.reservations = reservations;
    }

    @NonNull
    @Override
    public DiningAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dining_res, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            DiningReservation reservation = reservations.get(position);
            Log.d("Reservation log", reservation.getLocation());
            // Add null checks and logging
            if (holder.locationText != null) {
                holder.locationText.setText(reservation.getLocation());
            } else {
                Log.e("TAG", "locationText is null");
            }

            if (holder.timeText != null) {
                holder.timeText.setText(reservation.getTime());
            } else {
                Log.e("TAG", "timeText is null");
            }

            if (holder.websiteText != null) {
                holder.websiteText.setText(reservation.getWebsite());
            } else {
                Log.e("TAG", "websiteText is null");
            }
        } catch (Exception e) {
            Log.e("TAG", "Error binding ViewHolder: " + e.getMessage());
        }
    }


    @Override
    public int getItemCount() {
        return reservations != null ? reservations.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView locationText;
        private final TextView timeText;
        private final TextView websiteText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationText = itemView.findViewById(R.id.location_text);
            timeText = itemView.findViewById(R.id.time_text);
            websiteText = itemView.findViewById(R.id.website_text);
        }
    }
}