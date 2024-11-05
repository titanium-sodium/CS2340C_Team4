package com.example.sprintproject.views;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import com.example.sprintproject.R;
import com.example.sprintproject.model.DestinationModel;

import com.example.sprintproject.model.TravelStats;
import com.example.sprintproject.viewmodels.DBViewModel;
import com.example.sprintproject.viewmodels.DestinationAdapter;
import com.example.sprintproject.viewmodels.DestinationViewModel;
import com.example.sprintproject.viewmodels.TravelStatsViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DestinationsPage extends Fragment {
    private DestinationViewModel destinationViewModel;
    private TravelStatsViewModel travelStatsViewModel;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    private TextView allottedDaysText;
    private TextView plannedDaysText;
    private String userId;
    private DestinationAdapter destinationAdapter;
    private List<String> destinations = new ArrayList<>();
    private List<Integer> daysPlanned = new ArrayList<>();

    public DestinationsPage(String userId) {
        this.userId = userId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = MainActivity.getUserId();
        if (userId == null || userId.isEmpty()) {
            navigateToLogin();
            return;
        }
        destinationViewModel = new ViewModelProvider(this).get(DestinationViewModel.class);
        travelStatsViewModel = new ViewModelProvider(this).get(TravelStatsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (userId == null || userId.isEmpty()) {
            navigateToLogin();
            return new View(getContext());
        }

        View view = inflater.inflate(R.layout.destination_screen, container, false);

        // Initialize TextViews for stats display
        allottedDaysText = view.findViewById(R.id.allottedDaysText);

        // Initialize buttons
        Button travelLogButton = view.findViewById(R.id.travelLogButton);
        Button calculateTimeButton = view.findViewById(R.id.calculateButton);

        // Set click listeners
        if (travelLogButton != null) {
            travelLogButton.setOnClickListener(v -> openTravelLogForm());
        }

        if (calculateTimeButton != null) {
            calculateTimeButton.setOnClickListener(v -> openCalculateTimeForm());
        }

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.travelLogsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter with empty lists
        destinationAdapter = new DestinationAdapter(destinations, daysPlanned);
        recyclerView.setAdapter(destinationAdapter);

        // Load data from Firebase
        loadDestinationsData();
        loadTravelStats();

        return view;
    }

    private void loadDestinationsData() {
        DatabaseReference db = new DBViewModel().getDB();
        db.child("users").child(userId).child("destinations")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        destinations.clear();
                        daysPlanned.clear();

                        int totalPlannedDays = 0;

                        for (DataSnapshot destinationSnapshot : snapshot.getChildren()) {
                            if (!destinationSnapshot.getKey().equals("notes")) {
                                DestinationModel destination = destinationSnapshot
                                        .getValue(DestinationModel.class);
                                if (destination != null) {
                                    destinations.add(destination.getLocation());
                                    long durationInMillis = destination.getEndDate()
                                            - destination.getStartDate();
                                    int durationInDays = (int) (durationInMillis
                                            / (1000 * 60 * 60 * 24)) + 1;
                                    daysPlanned.add(durationInDays);
                                    totalPlannedDays += durationInDays;
                                }
                            }
                        }

                        // Update the plannedDays in travelStats
                        db.child("users").child(userId).child("travelStats")
                                .child("plannedDays")
                                .setValue(totalPlannedDays);

                        // Update the adapter
                        if (destinationAdapter != null) {
                            destinationAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DestinationsPage", "Error loading destinations: "
                                + error.getMessage());
                    }
                });
    }

    private void loadTravelStats() {
        if (userId != null && !userId.isEmpty()) {
            travelStatsViewModel.loadTravelStats(userId);
            observeTravelStats();
        }
    }

    private void observeTravelStats() {
        travelStatsViewModel.getTravelStats().observe(getViewLifecycleOwner(),
                this::updateStatsDisplay);
    }

    private void updateStatsDisplay(TravelStats stats) {
        if (stats == null) {
            return;
        }

        if (allottedDaysText != null) {
            allottedDaysText.setText(String.format("Allotted Days: %d", stats.getAllottedDays()));
        }
        if (plannedDaysText != null) {
            plannedDaysText.setText(String.format("Planned Days: %d", stats.getPlannedDays()));
        }
    }

    private void navigateToLogin() {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), "Please log in to access this page",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void openTravelLogForm() {
        if (getContext() == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.travel_log_form, null);

        EditText locationInput = dialogView.findViewById(R.id.locationText);
        EditText startTimeInput = dialogView.findViewById(R.id.startTime);
        EditText endTimeInput = dialogView.findViewById(R.id.endTime);

        // Set up date pickers
        setupDatePicker(startTimeInput, "Select Start Date");
        setupDatePicker(endTimeInput, "Select End Date");

        builder.setView(dialogView)
                .setTitle("Add Travel Log")
                .setPositiveButton("Submit", (dialog, which) -> {
                    String location = locationInput.getText().toString();
                    String startTimeStr = startTimeInput.getText().toString();
                    String endTimeStr = endTimeInput.getText().toString();

                    if (!location.isEmpty() && !startTimeStr.isEmpty() && !endTimeStr.isEmpty()) {
                        try {
                            Date startDate = sdf.parse(startTimeStr);
                            Date endDate = sdf.parse(endTimeStr);

                            if (startDate != null && endDate != null) {
                                DatabaseReference db = new DBViewModel().getDB();
                                DestinationModel destination = new DestinationModel(
                                        startDate.getTime(),
                                        endDate.getTime(),
                                        location
                                );
                                db.child("users").child(userId).child("destinations")
                                        .child(location + startDate.getTime())
                                        .setValue(destination);
                            }
                        } catch (ParseException e) {
                            Toast.makeText(getContext(), "Invalid date format",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Please fill all fields",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openCalculateTimeForm() {
        if (getContext() == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.calculate_vacationtime, null);

        EditText durationInput = dialogView.findViewById(R.id.durationInput);
        EditText startDateInput = dialogView.findViewById(R.id.startDateInput);
        EditText endDateInput = dialogView.findViewById(R.id.endDateInput);

        setupDatePicker(startDateInput, "Select Start Date");
        setupDatePicker(endDateInput, "Select End Date");

        Button calculateButton = dialogView.findViewById(R.id.calculateButton);
        if (calculateButton != null) {
            calculateButton.setOnClickListener(v -> {
                try {
                    calculateVacationTime(durationInput, startDateInput, endDateInput);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error in calculation: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        builder.setView(dialogView)
                .setTitle("Calculate Vacation Time")
                .setPositiveButton("Save", (dialog, which) -> {
                    saveVacationTime(durationInput, startDateInput, endDateInput);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void calculateVacationTime(EditText durationInput, EditText startDateInput,
                                       EditText endDateInput) {
        String durationStr = durationInput.getText().toString();
        String startDateStr = startDateInput.getText().toString();
        String endDateStr = endDateInput.getText().toString();

        int filledFields = 0;
        if (!durationStr.isEmpty()) {
            filledFields++;
        }
        if (!startDateStr.isEmpty()) {
            filledFields++;
        }
        if (!endDateStr.isEmpty()) {
            filledFields++;
        }

        if (filledFields != 2) {
            Toast.makeText(getContext(), "Please fill exactly two fields to calculate the third",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (!startDateStr.isEmpty() && !endDateStr.isEmpty()) {
                // Calculate duration
                Date startDate = sdf.parse(startDateStr);
                Date endDate = sdf.parse(endDateStr);
                if (startDate != null && endDate != null) {
                    long diffInMillies = endDate.getTime() - startDate.getTime();
                    int diffInDays = (int) (diffInMillies / (1000 * 60 * 60 * 24)) + 1;
                    durationInput.setText(String.valueOf(diffInDays));
                }
            } else if (!startDateStr.isEmpty() && !durationStr.isEmpty()) {
                // Calculate end date
                Date startDate = sdf.parse(startDateStr);
                int duration = Integer.parseInt(durationStr);
                if (startDate != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(startDate);
                    cal.add(Calendar.DAY_OF_YEAR, duration - 1);
                    endDateInput.setText(sdf.format(cal.getTime()));
                }
            } else if (!endDateStr.isEmpty() && !durationStr.isEmpty()) {
                // Calculate start date
                Date endDate = sdf.parse(endDateStr);
                int duration = Integer.parseInt(durationStr);
                if (endDate != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(endDate);
                    cal.add(Calendar.DAY_OF_YEAR, -(duration - 1));
                    startDateInput.setText(sdf.format(cal.getTime()));
                }
            }
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid duration format", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveVacationTime(EditText durationInput, EditText startTimeInput,
                                  EditText endTimeInput) {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getContext(), "User not authenticated. Please log in again.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String durationStr = durationInput.getText().toString();
        if (durationStr.isEmpty()) {
            Toast.makeText(getContext(), "Please calculate duration first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int allottedDays = Integer.parseInt(durationStr);
            travelStatsViewModel.updateAllottedDays(userId, allottedDays);
            Toast.makeText(getContext(), "Vacation time saved", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid duration format", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupDatePicker(EditText editText, String title) {
        if (editText == null || getContext() == null) {
            return;
        }

        editText.setFocusable(false);
        editText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year, month, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        editText.setText(sdf.format(selectedDate.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.setTitle(title);
            datePickerDialog.show();
        });
    }
}