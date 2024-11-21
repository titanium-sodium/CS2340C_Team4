package com.example.sprintproject.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sprintproject.model.AccommodationsModel;
import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.example.sprintproject.R;
import com.google.android.material.card.MaterialCardView;

public class AccommodationAdapter extends RecyclerView.Adapter<AccommodationAdapter.ViewHolder> {
    private List<AccommodationsModel> accommodations;
    private Context context;

    public AccommodationAdapter(List<AccommodationsModel> accommodations) {
        this.accommodations = accommodations;
    }

    @NonNull
    @Override
    public AccommodationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_accommodation_res, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            AccommodationsModel accommodation = accommodations.get(position);
            MaterialCardView cardView = holder.itemView.findViewById(R.id.accommodation_card);

            if (holder.hotelName != null) {
                holder.hotelName.setText(accommodation.getLocation());
            }

            if (holder.checkIn != null && holder.checkOut != null) {
                holder.checkIn.setText("Check-in: " + accommodation.getCheckInDate());
                holder.checkOut.setText("Check-out: " + accommodation.getCheckOutDate());
            }

            if (holder.roomType != null && holder.numberRooms != null) {
                holder.roomType.setText("Room Type: " + accommodation.getRoomType());
                holder.numberRooms.setText("Rooms: " + accommodation.getNumberOfRooms());
            }

            // Add status indicator with muted colors
            if (holder.statusText != null) {
                if (accommodation.isReservationPassed()) {
                    holder.statusText.setText("PAST");
                    holder.statusText.setTextColor(Color.parseColor("#737373")); // Muted gray
                    holder.statusText.setBackgroundResource(R.drawable.status_background);
                    if (cardView != null) {
                        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.past_reservation));
                    }
                } else if (accommodation.isCurrentReservation()) {
                    holder.statusText.setText("CURRENT");
                    holder.statusText.setTextColor(Color.parseColor("#539355")); // Muted green
                    holder.statusText.setBackgroundResource(R.drawable.status_background);
                    if (cardView != null) {
                        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.current_reservation));
                    }
                } else {
                    holder.statusText.setText("UPCOMING");
                    holder.statusText.setTextColor(Color.parseColor("#5771a8")); // Muted blue
                    holder.statusText.setBackgroundResource(R.drawable.status_background);
                    if (cardView != null) {
                        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.upcoming_reservation));
                    }
                }
            }

        } catch (Exception e) {
            Log.e("TAG", "Error binding ViewHolder: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return accommodations != null ? accommodations.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView hotelName;
        private final TextView checkIn;
        private final TextView checkOut;
        private final TextView numberRooms;
        private final TextView roomType;
        private final TextView statusText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hotelName = itemView.findViewById(R.id.hotelText);
            checkIn = itemView.findViewById(R.id.ciTimeText);
            checkOut = itemView.findViewById(R.id.coTimeText);
            numberRooms = itemView.findViewById(R.id.roomCountText);
            roomType = itemView.findViewById(R.id.roomTypeText);
            statusText = itemView.findViewById(R.id.statusText);
        }
    }
}