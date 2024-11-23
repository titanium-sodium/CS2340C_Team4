package com.example.sprintproject.viewmodels;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprintproject.R;

import java.util.List;

public class TravelAdaptor extends RecyclerView.Adapter<TravelAdaptor.ViewHolder> {
    private List<String> destination;
    private List<Integer> duration;

    public TravelAdaptor(List<String> destination, List<Integer> duration) {
        this.destination = destination;
        this.duration = duration;
    }

    @NonNull
    @Override
    public TravelAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_travel_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelAdaptor.ViewHolder holder, int position) {
        holder.destinationIDTextHolder.setText(destination.get(position));
        holder.durationIDTextHolder.setText("Duration: " + duration.get(position));
    }

    @Override
    public int getItemCount() {
        return destination.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView destinationIDTextHolder;
        private TextView durationIDTextHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            destinationIDTextHolder = itemView.findViewById(R.id.destinationText);
            durationIDTextHolder = itemView.findViewById(R.id.durationText);
        }


    }
}
