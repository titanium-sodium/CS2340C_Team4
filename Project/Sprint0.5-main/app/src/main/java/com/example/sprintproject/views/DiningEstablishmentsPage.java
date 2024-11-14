package com.example.sprintproject.views;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sprintproject.R;
import com.example.sprintproject.model.DiningReservation;
import com.example.sprintproject.viewmodels.DiningViewModel;

import java.util.ArrayList;

public class DiningEstablishmentsPage extends Fragment {
    private DiningViewModel diningViewModel;
    private RecyclerView reservationsRecyclerView;
    private DiningAdapter reservationAdapter;
    private String tripId;

    public DiningEstablishmentsPage(String tripId) {
        this.tripId = tripId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diningViewModel = new ViewModelProvider(this).get(DiningViewModel.class);
        String userId = MainActivity.getUserId();
        diningViewModel.setCurrentIds(userId, tripId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dining_reservations_screen, container, false);

        setupRecyclerView(view);
        setupButtons(view);
        observeReservations();

        return view;
    }

    private void setupRecyclerView(View view) {
        reservationsRecyclerView = view.findViewById(R.id.recyclerView_dining);
        reservationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reservationAdapter = new DiningAdapter();
        reservationsRecyclerView.setAdapter(reservationAdapter);
    }

    private void setupButtons(View view) {
        view.findViewById(R.id.addReservationButton).setOnClickListener(v -> openReservationForm());
    }

    private void observeReservations() {
        diningViewModel.getReservations().observe(getViewLifecycleOwner(), reservations -> {
            reservationAdapter.updateReservations(reservations);
        });
    }

    private void openReservationForm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dining_res_dialog, null);

        EditText locationInput = dialogView.findViewById(R.id.locationEditText);
        EditText timeInput = dialogView.findViewById(R.id.timeEditText);
        EditText websiteInput = dialogView.findViewById(R.id.websiteText);

        builder.setView(dialogView)
                .setTitle("New Reservation")
                .setPositiveButton("Add Reservation", (dialog, which) -> {
                    String website = websiteInput.getText().toString().trim();
                    String time = timeInput.getText().toString().trim();
                    String location = locationInput.getText().toString().trim();

                    if (!website.isEmpty() && !time.isEmpty() && !location.isEmpty()) {
                        DiningReservation newReservation = new DiningReservation(
                                diningViewModel.getCurrentUserId(),
                                diningViewModel.getCurrentTripId(),
                                website,
                                location,
                                time
                        );
                        diningViewModel.addReservation(newReservation);
                        Toast.makeText(getContext(), "Reservation added successfully",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Please fill in all fields",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}