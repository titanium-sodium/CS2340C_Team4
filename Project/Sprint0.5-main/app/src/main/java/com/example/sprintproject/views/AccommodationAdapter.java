package com.example.sprintproject.views;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sprintproject.model.AccommodationsModel;
import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.example.sprintproject.R;

public class AccommodationAdapter extends RecyclerView.Adapter<AccommodationAdapter.ViewHolder> {
    // Rest of your adapter code remains the same
    private List<AccommodationsModel> accommodations;

    public AccommodationAdapter(List<AccommodationsModel> accommodations) {
        this.accommodations = accommodations;
    }

    @NonNull
    @Override
    public AccommodationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_accommodation_res, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            AccommodationsModel accommodation = accommodations.get(position);
            Log.d("Accommodation log", accommodation.getLocation());

            if (holder.hotelName != null) {
                holder.hotelName.setText(accommodation.getLocation());
            } else {
                Log.e("TAG", "hotelName is null");
            }

            if (holder.checkIn != null && holder.checkOut != null) {
                holder.checkIn.setText("Check-in: " + accommodation.getCheckInDate());
                holder.checkOut.setText("Check-Out" + accommodation.getCheckOutDate());
            } else {
                Log.e("TAG", "timeText is null");
            }
            if (holder.roomType != null && holder.numberRooms != null) {
                holder.roomType.setText("Room Type:" + accommodation.getRoomType());
                holder.numberRooms.setText(accommodation.getNumberOfRooms());
            } else {
                Log.e("TAG", "Rooms null");
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hotelName = itemView.findViewById(R.id.hotelText);
            checkIn = itemView.findViewById(R.id.ciTimeText);
            checkOut = itemView.findViewById(R.id.coTimeText);
            numberRooms = itemView.findViewById(R.id.roomCountText);
            roomType = itemView.findViewById(R.id.roomTypeText);
        }
    }
}