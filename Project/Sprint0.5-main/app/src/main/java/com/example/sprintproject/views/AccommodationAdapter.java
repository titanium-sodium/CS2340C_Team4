package com.example.sprintproject.views;

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
    public AccommodationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_accommodation_res, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            AccommodationsModel accommodation = accommodations.get(position);
            Log.d("Accommodation log", accommodation.getLocation());

            if (holder.locationText != null) {
                holder.locationText.setText(accommodation.getLocation());
            } else {
                Log.e("TAG", "locationText is null");
            }

            if (holder.timeText != null) {
                String dateRange = "Check-in: " + accommodation.getCheckInDate() +
                        "\nCheck-out: " + accommodation.getCheckOutDate() +
                        "\nDuration: " + accommodation.getDuration() + " days";
                holder.timeText.setText(dateRange);
            } else {
                Log.e("TAG", "timeText is null");
            }

            if (holder.websiteText != null && accommodation.getWebsite() != null) {
                holder.websiteText.setText(accommodation.getWebsite());
            } else {
                Log.e("TAG", "websiteText is null or website not available");
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