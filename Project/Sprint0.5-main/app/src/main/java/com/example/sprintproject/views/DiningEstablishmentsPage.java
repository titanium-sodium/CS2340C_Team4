package com.example.sprintproject.views;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprintproject.R;
import com.example.sprintproject.model.DiningReservation;
import com.example.sprintproject.viewmodels.DiningViewModel;
import com.example.sprintproject.viewmodels.FilterViewModel;

import java.util.Calendar;

public class DiningEstablishmentsPage extends Fragment {
    private static final String TAG = "DiningEstablishmentsPage";
    private DiningViewModel diningViewModel;
    private RecyclerView reservationsRecyclerView;
    private DiningAdapter reservationAdapter;
    private String tripId;
    private Calendar selectedDateTime;

    public DiningEstablishmentsPage(String tripId) {
        this.tripId = tripId;
        this.selectedDateTime = Calendar.getInstance();
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
        view.findViewById(R.id.filterButton).setOnClickListener(v -> changeFilter());
    }

    //TODO filters
    private void changeFilter() {
        FilterViewModel diningFilterModel = new FilterViewModel(
                true, "Dining", new DiningViewModel());
        diningFilterModel.changeFilter(diningFilterModel.getFilter(), "Dining");
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

        timeInput.setFocusable(false);
        timeInput.setClickable(true);
        timeInput.setOnClickListener(v -> showDateTimePicker(timeInput));

        builder.setView(dialogView)
                .setTitle("New Reservation")
                .setPositiveButton("Add Reservation", (dialog, which) -> {
                    String website = websiteInput.getText().toString().trim();
                    String location = locationInput.getText().toString().trim();

                    if (!website.isEmpty() && !location.isEmpty() && selectedDateTime != null) {
                        String currentUserId = diningViewModel.getCurrentUserId();
                        String currentTripId = diningViewModel.getCurrentTripId();

                        if (currentUserId != null && currentTripId != null) {
                            try {
                                DiningReservation newReservation = new DiningReservation(
                                        currentUserId,
                                        currentTripId,
                                        website,
                                        location,
                                        DiningReservation.formatDateTime(
                                                selectedDateTime.getTime()),
                                        selectedDateTime.getTimeInMillis()
                                );

                                Log.d(TAG, "Creating reservation: " + newReservation);
                                diningViewModel.addReservation(newReservation);
                                Toast.makeText(getContext(), "Adding reservation...",
                                        Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e(TAG, "Error creating reservation", e);
                                Toast.makeText(getContext(), "Error creating reservation",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "User or trip ID is missing",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Please fill in all fields and select a time",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDateTimePicker(EditText timeInput) {
        if (getContext() == null) {
            return;
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    new TimePickerDialog(
                            getContext(),
                            (timeView, hourOfDay, minute) -> {
                                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDateTime.set(Calendar.MINUTE, minute);
                                timeInput.setText(DiningReservation.formatDateTime(
                                        selectedDateTime.getTime()));
                            },
                            selectedDateTime.get(Calendar.HOUR_OF_DAY),
                            selectedDateTime.get(Calendar.MINUTE),
                            true
                    ).show();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}