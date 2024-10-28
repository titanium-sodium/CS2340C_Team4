package com.example.sprintproject.views;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.sprintproject.R;
import com.example.sprintproject.model.DestinationModel;
import com.example.sprintproject.model.DestinationsRepository;
import com.example.sprintproject.model.TravelStats;
import com.example.sprintproject.viewmodels.DestinationAdapter;
import com.example.sprintproject.viewmodels.DestinationViewModel;
import com.example.sprintproject.viewmodels.TravelStatsViewModel;

public class DestinationsPage extends Fragment {
    private DestinationViewModel destinationViewModel;
    private TravelStatsViewModel travelStatsViewModel;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    private TextView allottedDaysText;
    private TextView plannedDaysText;
    private String userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = MainActivity.getUserId();
        if (userId == null || userId.isEmpty()) {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "Please log in to access this page",
                        Toast.LENGTH_LONG).show();
                // Simplified navigation to login
                try {
                    Intent intent = new Intent(getActivity(), LoginPage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                } catch (Exception e) {
                    getActivity().finish();
                }
                return;
            }
        }
        destinationViewModel = new ViewModelProvider(this).get(DestinationViewModel.class);
        travelStatsViewModel = new ViewModelProvider(this).get(TravelStatsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (userId == null || userId.isEmpty()) {
            // Simplified navigation to login
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), LoginPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
            // Return an empty view since we're navigating away
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
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            List<String> destinations = Arrays.asList("Atlanta", "New York", "Tokyo", "Paris");
            List<Integer> daysPlanned = Arrays.asList(5, 3, 7, 4);

            DestinationAdapter destinationAdapter = new DestinationAdapter(destinations, daysPlanned);
            recyclerView.setAdapter(destinationAdapter);
        }

        // Load and observe travel stats
        loadTravelStats();

        return view;
    }

    private void loadTravelStats() {
        if (userId != null && !userId.isEmpty()) {
            travelStatsViewModel.loadTravelStats(userId);
            observeTravelStats();
        }
    }

    private void observeTravelStats() {
        travelStatsViewModel.getTravelStats().observe(getViewLifecycleOwner(), this::updateStatsDisplay);
    }

    private void updateStatsDisplay(TravelStats stats) {
        if (stats == null) return;

        if (allottedDaysText != null) {
            allottedDaysText.setText(String.format("Allotted Days: %d", stats.getAllottedDays()));
        }
        if (plannedDaysText != null) {
            plannedDaysText.setText(String.format("Planned Days: %d", stats.getPlannedDays()));
        }
    }

    private void openTravelLogForm() {
        if (getContext() == null) return;

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
                    submitTravelLog(locationInput, startTimeInput, endTimeInput);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openCalculateTimeForm() {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.calculate_vacationtime, null);

        // Initialize input fields
        EditText durationInput = dialogView.findViewById(R.id.durationInput);
        EditText startDateInput = dialogView.findViewById(R.id.startDateInput);
        EditText endDateInput = dialogView.findViewById(R.id.endDateInput);

        // Set up date pickers
        setupDatePicker(startDateInput, "Select Start Date");
        setupDatePicker(endDateInput, "Select End Date");

        // Add calculate button inside the form
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

        // Count filled fields
        int filledFields = 0;
        if (!durationStr.isEmpty()) filledFields++;
        if (!startDateStr.isEmpty()) filledFields++;
        if (!endDateStr.isEmpty()) filledFields++;

        if (filledFields != 2) {
            Toast.makeText(getContext(), "Please fill exactly two fields to calculate the third",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Calculate based on which fields are filled
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

    private void saveVacationTime(EditText durationInput, EditText startTimeInput, EditText endTimeInput) {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getContext(), "User not authenticated. Please log in again.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String durationStr = durationInput.getText().toString();
        if (durationStr.isEmpty()) {
            Toast.makeText(getContext(), "Please calculate duration first", Toast.LENGTH_SHORT).show();
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

    private void submitTravelLog(EditText locationInput, EditText startTimeInput, EditText endTimeInput) {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getContext(), "User not authenticated. Please log in again.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String location = locationInput.getText().toString();
        String startTimeStr = startTimeInput.getText().toString();
        String endTimeStr = endTimeInput.getText().toString();

        if (location.isEmpty() || startTimeStr.isEmpty() || endTimeStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Date startDate = sdf.parse(startTimeStr);
            Date endDate = sdf.parse(endTimeStr);

            if (startDate.after(endDate)) {
                Toast.makeText(getContext(), "Start date cannot be after end date",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate the number of days for this trip
            long diffInMillies = endDate.getTime() - startDate.getTime();
            int tripDays = (int) (diffInMillies / (1000 * 60 * 60 * 24)) + 1;

            DestinationModel destination = new DestinationModel(
                    startDate.getTime(),
                    endDate.getTime(),
                    location
            );

            // Save destination and update planned days
            DestinationsRepository.getInstance().addDestination(destination, userId)
                    .addOnSuccessListener(id -> {
                        travelStatsViewModel.addPlannedDays(userId, tripDays);
                        Toast.makeText(getContext(), "Travel log added successfully",
                                Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(),
                            "Failed to add travel log: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        } catch (ParseException e) {
            Toast.makeText(getContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupDatePicker(EditText editText, String title) {
        if (editText == null || getContext() == null) return;

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