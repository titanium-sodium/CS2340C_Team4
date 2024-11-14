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
import com.example.sprintproject.model.AccommodationsModel;
import com.example.sprintproject.viewmodels.AccommodationsViewModel;
import com.example.sprintproject.viewmodels.FilterViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccommodationsPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccommodationsPage extends Fragment {

    private AccommodationsViewModel accommodationsViewModel;
    private List<AccommodationsModel> accommodationsModels;
    private RecyclerView accommodationRecyclerViewer;
    private AccommodationAdapter accommodationAdapter;
    private static String userId;
    private static String tripId;

    public AccommodationsPage(String userId, String tripId) {
        this.userId = userId;
        this.tripId = tripId;
        accommodationsViewModel = new AccommodationsViewModel(userId, tripId);
        accommodationsModels = new ArrayList<>();
    }

    public static AccommodationsPage newInstance(String userId, String tripId) {
        return new AccommodationsPage(userId, tripId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void updateTripId(String newTripId) {
        tripId = newTripId;
        if (accommodationsViewModel != null) {
            accommodationsViewModel.setTripId(newTripId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.accommodation_screen, container, false);

        // Initialize filter button
        FilterViewModel filterButton = new FilterViewModel(true, "Accommodations",
                accommodationsViewModel);

        // Initialize buttons
        view.findViewById(R.id.newResButton).setOnClickListener(v -> openAccommodationsForm());
        view.findViewById(R.id.filterButton).setOnClickListener(v ->
                filterButton.changeFilter(filterButton.getFilter(), filterButton.getType()));

        // Initialize RecyclerView
        accommodationRecyclerViewer = view.findViewById(R.id.accommodations_recycler);
        accommodationRecyclerViewer.setLayoutManager(new LinearLayoutManager(getContext()));

        accommodationAdapter = new AccommodationAdapter(accommodationsModels);
        accommodationRecyclerViewer.setAdapter(accommodationAdapter);

        // Load initial data
        loadReservations();

        return view;
    }

    private void openAccommodationsForm() {
        if (tripId == null || tripId.isEmpty()) {
            Toast.makeText(getContext(),
                    "Please select a trip first", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.accommodation_res_dialog, null);

        // Get form fields
        EditText checkInInput = dialogView.findViewById(R.id.checkInEditText);
        EditText checkOutInput = dialogView.findViewById(R.id.checkOutInput);
        EditText addressInput = dialogView.findViewById(R.id.addressEditText);
        EditText numberRoomsInput = dialogView.findViewById(R.id.roomCountEditText);
        EditText roomTypeInput = dialogView.findViewById(R.id.roomTypeDropdown);

        builder.setView(dialogView)
                .setTitle("New Accommodation")
                .setPositiveButton("Add Accommodation", (dialog, which) -> {
                    // Get form values
                    String checkIn = checkInInput.getText().toString().trim();
                    String checkOut = checkOutInput.getText().toString().trim();
                    String address = addressInput.getText().toString().trim();
                    String numberRooms = numberRoomsInput.getText().toString().trim();
                    String roomType = roomTypeInput.getText().toString().trim();

                    // Validate inputs
                    if (!checkIn.isEmpty() && !checkOut.isEmpty() && !address.isEmpty()
                            && !numberRooms.isEmpty() && !roomType.isEmpty()) {
                        try {
                            AccommodationsModel newAccommodation = new AccommodationsModel(
                                    checkIn,
                                    checkOut,
                                    Integer.parseInt(numberRooms),
                                    roomType,
                                    address
                            );
                            accommodationsViewModel.addAccommodations(newAccommodation);
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(),
                                    "Please enter a valid number of rooms",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(),
                                "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadReservations() {
        if (accommodationsViewModel != null) {
            accommodationsViewModel.getAccommodations().observe(getViewLifecycleOwner(),
                    reservations -> {
                        accommodationsModels.clear();
                        accommodationsModels.addAll(reservations);
                        accommodationAdapter.notifyDataSetChanged();
                    });
        }
    }
}