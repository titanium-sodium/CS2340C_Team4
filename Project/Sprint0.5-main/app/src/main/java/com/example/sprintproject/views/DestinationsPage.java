package com.example.sprintproject.views;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import com.example.sprintproject.R;
import com.example.sprintproject.model.DBModel;
import com.example.sprintproject.model.DestinationModel;
import com.example.sprintproject.model.DestinationsRepository;
import com.example.sprintproject.viewmodels.DBViewModel;
import com.example.sprintproject.viewmodels.DestinationAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class DestinationsPage extends Fragment {

    public DestinationsPage() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
            // Change this line to directly use the static method
            String userId = MainActivity.getUserId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.destination_screen, container, false);

        // Find the travel log button and set its click listener
        Button travelLogButton = view.findViewById(R.id.travelLogButton);
        travelLogButton.setOnClickListener(v -> opentravelLogForm());

        Button calculateTimeButton = view.findViewById(R.id.calculateButton);
        calculateTimeButton.setOnClickListener(v -> openCalculateTimeForm());
        // Find and setup the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.travelLogsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        // Sample data
        DatabaseReference DB = new DBViewModel().getDB();
        String userID = MainActivity.getUserId();
        List<String> destinations = new ArrayList<>();
        List<Integer> daysPlanned = new ArrayList<>();

        DB.child("users").child(userID).child("destinations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot destinationsSnapshot : snapshot.getChildren()) {
                        Log.d("TEST", String.valueOf(destinationsSnapshot));
                        DestinationModel destination = destinationsSnapshot.getValue(DestinationModel.class);
                        destinations.add(destination.getLocation());
                        daysPlanned.add(destination.getDuration());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Do nothing
            }
        });
        // Setup adapter
        DestinationAdapter destinationAdapter = new DestinationAdapter(destinations, daysPlanned);
        recyclerView.setAdapter(destinationAdapter);

        return view;
    }

    private void openCalculateTimeForm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.calculate_vacationtime, null);
        EditText durationInput = dialogView.findViewById(R.id.durationInput);
        EditText startTimeInput = dialogView.findViewById(R.id.startDateInput);
        EditText endTimeInput = dialogView.findViewById(R.id.endDateInput);

        builder.setView(dialogView).setTitle("Add Travel Log")
                .setPositiveButton("Submit", (dialog, which) -> {
                    String durationStr = durationInput.getText().toString();
                    String startTimeStr = startTimeInput.getText().toString();
                    String endTimeStr = endTimeInput.getText().toString();

                    if (!durationStr.isEmpty() && !startTimeStr.isEmpty() && !endTimeStr.isEmpty()) {
                        //Logic for calculation
                    } else {
                        Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void opentravelLogForm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.travel_log_form, null);
        EditText locationInput = dialogView.findViewById(R.id.locationText);
        EditText startTimeInput = dialogView.findViewById(R.id.startTime);
        EditText endTimeInput = dialogView.findViewById(R.id.endTime);

        builder.setView(dialogView).setTitle("Add Travel Log")
                .setPositiveButton("Submit", (dialog, which) -> {
                    String location = locationInput.getText().toString();
                    String startTimeStr = startTimeInput.getText().toString();
                    String endTimeStr = endTimeInput.getText().toString();

                    if (!location.isEmpty() && !startTimeStr.isEmpty() && !endTimeStr.isEmpty()) {
                        try {
                            // Convert string dates to milliseconds (you might want to use a DatePicker instead)
                            long startTime = Long.parseLong(startTimeStr);
                            long endTime = Long.parseLong(endTimeStr);
                            DatabaseReference DB = new DBViewModel().getDB();

                            DestinationModel destination = new DestinationModel(startTime, endTime, location);
                            DB.child("users").child(MainActivity.getUserId()).child("destinations").child(location+startTimeStr).setValue(destination);
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}