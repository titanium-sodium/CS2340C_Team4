package com.example.sprintproject.views;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sprintproject.R;
import com.example.sprintproject.model.DiningReservation;
import com.example.sprintproject.views.DiningAdapter;
import com.example.sprintproject.viewmodels.DiningReservationViewModel;
import com.example.sprintproject.viewmodels.FilterViewModel;

import java.util.ArrayList;
import java.util.List;

public class DiningEstablishmentsPage extends Fragment {
    private DiningReservationViewModel diningReservationViewModel;
    private RecyclerView reservationsRecyclerView;
    private DiningAdapter reservationAdapter;
    private List<DiningReservation> reservationsList;
    private String userId;

    public DiningEstablishmentsPage() {
        // Required empty public constructor
    }

    public DiningEstablishmentsPage(String userId) {
        this.userId = userId;
        diningReservationViewModel = new DiningReservationViewModel(userId);
        reservationsList = new ArrayList<>();
    }

    public static DiningEstablishmentsPage newInstance(String param1, String param2) {
        return new DiningEstablishmentsPage();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dining_reservations_screen, container, false);

        // Initialize RecyclerView
        reservationsRecyclerView = view.findViewById(R.id.recyclerView_dining);
        reservationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter.
        reservationAdapter = new DiningAdapter(reservationsList);
        reservationsRecyclerView.setAdapter(reservationAdapter);

        // Set up filter button
        FilterViewModel filterButton = new FilterViewModel(true, "Dining", diningReservationViewModel);

        // Set up buttons
        view.findViewById(R.id.addReservationButton).setOnClickListener(v -> openReservationForm());
        view.findViewById(R.id.filterButton).setOnClickListener(v -> filterButton.changeFilter(filterButton.getFilter(), filterButton.getType()));

        // Load existing reservations
        loadReservations();

        return view;
    }

    private void loadReservations() {
        diningReservationViewModel.getReservations().observe(getViewLifecycleOwner(), reservations -> {
            reservationsList.clear();
            reservationsList.addAll(reservations);
            reservationAdapter.notifyDataSetChanged();
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
                                MainActivity.getUserId(), website, location, time
                        );
                        diningReservationViewModel.addReservation(newReservation);
                        Toast.makeText(getContext(), "Reservation added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Please enter all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}