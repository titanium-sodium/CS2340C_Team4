package com.example.sprintproject.views;

import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sprintproject.model.DiningReservation;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.example.sprintproject.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiningAdapter extends ListAdapter<DiningReservation, DiningAdapter.ViewHolder> {
    private static final String TAG = "DiningAdapter";
    private static final SimpleDateFormat DISPLAY_FORMAT =
            new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);

    private static final DiffUtil.ItemCallback<DiningReservation> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<DiningReservation>() {
                @Override
                public boolean areItemsTheSame(@NonNull DiningReservation oldItem,
                                               @NonNull DiningReservation newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull DiningReservation oldItem,
                                                  @NonNull DiningReservation newItem) {
                    return oldItem.getLocation().equals(newItem.getLocation())
                            && oldItem.getTime().equals(newItem.getTime())
                            && oldItem.getWebsite().equals(newItem.getWebsite())
                            && oldItem.getReservationTimestamp()
                            == newItem.getReservationTimestamp();
                }
            };

    public DiningAdapter() {
        super(DIFF_CALLBACK);
        submitList(new ArrayList<>());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dining_res, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiningReservation reservation = getItem(position);
        try {
            holder.bind(reservation);
        } catch (Exception e) {
            Log.e(TAG, "Error binding ViewHolder at position " + position, e);
        }
    }

    public void updateReservations(List<DiningReservation> newReservations) {
        submitList(newReservations);
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

        public void bind(DiningReservation reservation) {
            if (reservation == null) {
                Log.e(TAG, "Attempted to bind null reservation");
                return;
            }

            try {
                if (locationText != null) {
                    locationText.setText(reservation.getLocation());
                }

                if (timeText != null) {
                    timeText.setText(
                            DISPLAY_FORMAT.format(new Date(reservation.getReservationTimestamp())));
                }

                if (websiteText != null) {
                    websiteText.setText(reservation.getWebsite());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error binding reservation data", e);
            }
        }
    }
}