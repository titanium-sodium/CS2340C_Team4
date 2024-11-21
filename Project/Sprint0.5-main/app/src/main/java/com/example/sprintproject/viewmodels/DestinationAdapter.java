package com.example.sprintproject.viewmodels;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sprintproject.R;
import com.example.sprintproject.model.DestinationModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ViewHolder> {
    private List<DestinationModel> destinations;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    public DestinationAdapter(List<DestinationModel> destinations) {
        this.destinations = destinations;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView destinationText;
        public TextView daysText;
        public TextView startDateText;
        public TextView endDateText;

        public ViewHolder(View itemView) {
            super(itemView);
            destinationText = itemView.findViewById(R.id.destinationText);
            daysText = itemView.findViewById(R.id.daysText);
            startDateText = itemView.findViewById(R.id.startDateText);
            endDateText = itemView.findViewById(R.id.endDateText);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_destination, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DestinationModel destination = destinations.get(position);

        holder.destinationText.setText(destination.getLocation());
        holder.daysText.setText(destination.getDuration() + " days");

        // Format and set the dates
        String startDate = sdf.format(new Date(destination.getStartDate()));
        String endDate = sdf.format(new Date(destination.getEndDate()));

        holder.startDateText.setText("Start Date: " + startDate);
        holder.endDateText.setText("End Date: " + endDate);
    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }
}