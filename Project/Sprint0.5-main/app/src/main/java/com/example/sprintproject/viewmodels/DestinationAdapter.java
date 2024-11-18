package com.example.sprintproject.viewmodels;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sprintproject.R;
import com.example.sprintproject.model.DestinationModel;
import java.util.List;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ViewHolder> {
    private List<DestinationModel> destinations;

    public DestinationAdapter(List<DestinationModel> destinations) {
        this.destinations = destinations;
    }

    @NonNull
    @Override
    public DestinationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_destination, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationAdapter.ViewHolder holder, int position) {
        DestinationModel destination = destinations.get(position);
        holder.destinationText.setText(destination.getLocation());
        holder.daysPlannedText.setText(destination.getDuration() + " days planned");
    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView destinationText;
        private TextView daysPlannedText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            destinationText = itemView.findViewById(R.id.destinationText);
            daysPlannedText = itemView.findViewById(R.id.daysText);
        }
    }
}