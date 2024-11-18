package com.example.sprintproject.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sprintproject.R;
import com.example.sprintproject.model.DBModel;
import com.example.sprintproject.model.TripDBModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TripsListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TripAdapter adapter;
    private List<TripItem> tripsList;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_list);

        userId = getIntent().getStringExtra("userId");
        tripsList = new ArrayList<>();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewTrips);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TripAdapter();
        recyclerView.setAdapter(adapter);

        // Get trips from Firebase
        loadTrips();

        // Setup New Trip button
        Button btnNewTrip = findViewById(R.id.btnNewTrip);
        btnNewTrip.setOnClickListener(v -> showNewTripDialog());
    }

    private void showNewTripDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Trip");

        // Set up the input
        final EditText input = new EditText(this);
        input.setHint("Enter trip name");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tripName = input.getText().toString().trim();
                if (!tripName.isEmpty()) {
                    createNewTrip(tripName);
                } else {
                    Toast.makeText(TripsListActivity.this,
                            "Trip name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showEditTripDialog(TripItem trip) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Trip Name");

        // Set up the input
        final EditText input = new EditText(this);
        input.setText(trip.name);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString().trim();
                if (!newName.isEmpty()) {
                    updateTripName(trip.id, newName);
                } else {
                    Toast.makeText(TripsListActivity.this,
                            "Trip name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void loadTrips() {
        DatabaseReference tripsRef = TripDBModel.getInstance(userId);
        tripsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tripsList.clear();
                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    String tripId = tripSnapshot.getKey();
                    String tripName = tripSnapshot.child("tripName").getValue(String.class);
                    if (tripName == null) {
                        tripName = "Unnamed Trip";
                    }
                    tripsList.add(new TripItem(tripId, tripName));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TripsListActivity.this,
                        "Failed to load trips", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewTrip(String tripName) {
        // Create references to both locations
        DatabaseReference userTripsRef = TripDBModel.getInstance(userId);
        DatabaseReference allTripsRef = DBModel.getTripReference();

        // Generate new trip ID
        String newTripId = userTripsRef.push().getKey();

        if (newTripId != null) {
            // First, add to user's trips
            userTripsRef.child(newTripId).child("tripName").setValue(tripName)
                    .addOnSuccessListener(aVoid -> {
                        // Then, add to global trips list
                        DatabaseReference globalTripRef = allTripsRef.child(newTripId);
                        globalTripRef.child("tripName").setValue(tripName);
                        globalTripRef.child("email").setValue(userId);

                        // Navigate to MainActivity with the new trip
                        Intent intent = new Intent(TripsListActivity.this, MainActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("tripId", newTripId);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> Toast.makeText(TripsListActivity.this,
                            "Failed to create trip: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(TripsListActivity.this,
                    "Failed to generate trip ID",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTripName(String tripId, String newName) {
        // Update in user's trips
        DatabaseReference userTripsRef = TripDBModel.getInstance(userId);
        DatabaseReference globalTripsRef = DBModel.getTripReference();

        // Update both locations
        userTripsRef.child(tripId).child("tripName").setValue(newName)
                .addOnSuccessListener(aVoid -> {
                    // Also update in global trips
                    globalTripsRef.child(tripId).child("tripName").setValue(newName)
                            .addOnSuccessListener(aVoid2 ->
                                    Toast.makeText(TripsListActivity.this,
                                            "Trip name updated successfully", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(TripsListActivity.this,
                                            "Failed to update global trip name", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(TripsListActivity.this,
                                "Failed to update trip name", Toast.LENGTH_SHORT).show());
    }

    private static class TripItem {
        String id;
        String name;

        TripItem(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

        @NonNull
        @Override
        public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_trip, parent, false);
            return new TripViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
            TripItem trip = tripsList.get(position);
            holder.tripNameTextView.setText(trip.name);

            // Click to open trip
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(TripsListActivity.this, MainActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("tripId", trip.id);
                startActivity(intent);
            });

            // Long click to edit name
            holder.itemView.setOnLongClickListener(v -> {
                showEditTripDialog(trip);
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return tripsList.size();
        }

        class TripViewHolder extends RecyclerView.ViewHolder {
            TextView tripNameTextView;

            TripViewHolder(View itemView) {
                super(itemView);
                tripNameTextView = itemView.findViewById(R.id.tripNameTextView);
            }
        }
    }
}