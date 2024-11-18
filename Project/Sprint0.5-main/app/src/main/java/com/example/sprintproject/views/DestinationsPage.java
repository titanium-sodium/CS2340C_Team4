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
import com.example.sprintproject.model.TripDBModel;
import com.example.sprintproject.viewmodels.DestinationAdapter;
import com.example.sprintproject.viewmodels.DestinationViewModel;
import com.example.sprintproject.viewmodels.TravelStatsViewModel;
import com.google.firebase.database.*;

public class DestinationsPage extends Fragment {
    private static final String TAG = "DestinationsPage";
    private DestinationViewModel destinationViewModel;
    private TravelStatsViewModel travelStatsViewModel;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    private TextView allottedDaysText;
    private TextView plannedDaysText;
    private String userId;
    private String currentTripId;
    private DestinationAdapter destinationAdapter;
    private List<DestinationModel> destinations = new ArrayList<>();

    public DestinationsPage(String userId) {
        this.userId = userId;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = MainActivity.getUserId();
        if (userId == null || userId.isEmpty()) {
            navigateToLogin();
            return;
        }
        destinationViewModel = new ViewModelProvider(this).get(DestinationViewModel.class);
        travelStatsViewModel = new ViewModelProvider(this).get(TravelStatsViewModel.class);
        loadOrCreateDefaultTrip();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (userId == null || userId.isEmpty()) {
            navigateToLogin();
            return new View(getContext());
        }

        View view = inflater.inflate(R.layout.destination_screen, container, false);
        allottedDaysText = view.findViewById(R.id.allottedDaysText);

        Button travelLogButton = view.findViewById(R.id.travelLogButton);
        Button calculateTimeButton = view.findViewById(R.id.calculateButton);

        if (travelLogButton != null) {
            travelLogButton.setOnClickListener(v -> openTravelLogForm());
        }

        if (calculateTimeButton != null) {
            calculateTimeButton.setOnClickListener(v -> openCalculateTimeForm());
        }

        RecyclerView recyclerView = view.findViewById(R.id.travelLogsRecyclerView);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            destinationAdapter = new DestinationAdapter(destinations);
            recyclerView.setAdapter(destinationAdapter);
        }

        loadTravelStats();
        return view;
    }

    private void loadOrCreateDefaultTrip() {
        DatabaseReference userTripsRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("trips");

        userTripsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || !snapshot.hasChildren()) {
                    DatabaseReference newTripRef = FirebaseDatabase.getInstance().getReference()
                            .child("trips")
                            .push();
                    String newTripId = newTripRef.getKey();

                    newTripRef.child("userID").setValue(userId);
                    userTripsRef.child(newTripId).setValue(true);
                    currentTripId = newTripId;
                } else {
                    currentTripId = snapshot.getChildren().iterator().next().getKey();
                }
                Log.d("TRIPID", currentTripId);
                loadDestinationsData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading trips: " + error.getMessage());
            }
        });
    }

    private void loadDestinationsData() {
        if (currentTripId == null) {
            Log.e(TAG, "No trip ID available");
            return;
        }

        DestinationModel.loadTripDestinations(currentTripId, new DestinationModel.DestinationLoadCallback() {
            @Override
            public void onDestinationsLoaded(ArrayList<DestinationModel> destinationsList) {
                destinations.clear();
                destinations.addAll(destinationsList);

                int totalPlannedDays = 0;
                for (DestinationModel destination : destinationsList) {
                    totalPlannedDays += destination.getDuration();
                }

                travelStatsViewModel.updatePlannedDays(currentTripId, totalPlannedDays);

                if (destinationAdapter != null) {
                    destinationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error loading destinations: " + errorMessage);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error loading destinations: " + errorMessage,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openTravelLogForm() {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.travel_log_form, null);

        EditText locationInput = dialogView.findViewById(R.id.locationText);
        EditText startTimeInput = dialogView.findViewById(R.id.startTime);
        EditText endTimeInput = dialogView.findViewById(R.id.endTime);

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

                            if (startDate != null && endDate != null && currentTripId != null) {
                                DestinationModel destination = new DestinationModel(
                                        startDate.getTime(),
                                        endDate.getTime(),
                                        location
                                );
                                destination.saveToTrip(currentTripId);
                                Toast.makeText(getContext(), "Travel log added successfully",
                                        Toast.LENGTH_SHORT).show();
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
            travelStatsViewModel.updateAllottedDays(currentTripId, allottedDays);
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
    private void loadTravelStats() {
        if (userId != null && !userId.isEmpty()) {
            DatabaseReference userRef = TripDBModel.getTripReference(userId);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        TravelStats stats = snapshot.getValue(TravelStats.class);
                        if (stats != null) {
                            updateStatsDisplay(stats);
                        }
                    } else {
                        // Initialize default travel stats if none exist
                        TravelStats defaultStats = new TravelStats();
                        defaultStats.setAllottedDays(0);
                        defaultStats.setPlannedDays(0);
                        defaultStats.setPlannedPercentage(0);
                        defaultStats.setRemainingDays(0);
                        userRef.setValue(defaultStats);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error loading travel stats: " + error.getMessage());
                    if (getContext() != null) {
                        Toast.makeText(getContext(),
                                "Error loading travel stats: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void updateStatsDisplay(TravelStats stats) {
        if (stats == null || getContext() == null) {
            return;
        }

        if (allottedDaysText != null) {
            allottedDaysText.setText(String.format(Locale.getDefault(),
                    "Allotted Days: %d", stats.getAllottedDays()));
        }

        if (plannedDaysText != null) {
            plannedDaysText.setText(String.format(Locale.getDefault(),
                    "Planned Days: %d", stats.getPlannedDays()));
        }
    }

    public void updateTravelStats(int plannedDays) {
        if (userId == null || userId.isEmpty()) {
            return;
        }

        DatabaseReference statsRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("travelStats");

        statsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TravelStats currentStats;
                if (snapshot.exists()) {
                    currentStats = snapshot.getValue(TravelStats.class);
                } else {
                    currentStats = new TravelStats();
                }

                if (currentStats != null) {
                    currentStats.setPlannedDays(plannedDays);

                    // Calculate percentage and remaining days
                    if (currentStats.getAllottedDays() > 0) {
                        int percentage = (plannedDays * 100) / currentStats.getAllottedDays();
                        currentStats.setPlannedPercentage(percentage);
                        currentStats.setRemainingDays(
                                currentStats.getAllottedDays() - plannedDays);
                    }

                    statsRef.setValue(currentStats)
                            .addOnSuccessListener(aVoid -> {
                                if (getContext() != null) {
                                    Toast.makeText(getContext(),
                                            "Travel stats updated",
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                if (getContext() != null) {
                                    Toast.makeText(getContext(),
                                            "Failed to update travel stats: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error updating travel stats: " + error.getMessage());
                if (getContext() != null) {
                    Toast.makeText(getContext(),
                            "Error updating travel stats: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}