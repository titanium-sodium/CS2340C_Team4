package com.example.sprintproject.viewmodels;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprintproject.R;

import java.util.List;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ViewHolder> {


    private List<String> destinations;
    private List<Integer> daysPlanned;

    public DestinationAdapter(List<String> destinations, List<Integer> daysPlanned) {
        this.destinations = destinations;
        this.daysPlanned = daysPlanned;
    }


    @NonNull
    @Override
    public DestinationAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        //We inflate the layout here
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_destination, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationAdapter.ViewHolder holder, int position) {
        //where we assign values to each of the rows aka binds the data
        holder.destinationText.setText(destinations.get(position));
        holder.daysPlannedText.setText(daysPlanned.get(position) + " days planned");
    }

    @Override
    public int getItemCount() {
        //recycler view wants us to know the number of items that we want to display
        return destinations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView destinationText;
        private TextView daysPlannedText;

        public ViewHolder(@NonNull View itemView) {
            //takes the variables destinationText and daysPlannedText and passes the data onto text
            super(itemView);
            destinationText = itemView.findViewById(R.id.destinationText);
            daysPlannedText = itemView.findViewById(R.id.daysText);
        }
        public TextView getDestinationText() {
            return destinationText;
        }

        public TextView getDaysPlannedText() {
            return daysPlannedText;
        }
    }
}
