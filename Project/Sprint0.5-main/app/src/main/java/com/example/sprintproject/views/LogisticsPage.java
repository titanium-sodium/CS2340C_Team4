package com.example.sprintproject.views;

import android.app.AlertDialog;
import android.graphics.Color;
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
import com.example.sprintproject.model.NotesModel;
import com.example.sprintproject.model.TravelStats;
import com.example.sprintproject.model.UserModel;
import com.example.sprintproject.viewmodels.DBViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class LogisticsPage extends Fragment {
    private RecyclerView userList;
    private UserListAdapter adapter;
    private List<UserModel> contributors;
    private DBViewModel dbViewModel;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.logistics_screen, container, false);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Get userId from MainActivity - this is the most reliable source since it's passed from login
        userId = MainActivity.getUserId();

        if (userId == null) {
            Log.e("LogisticsPage", "UserId is null from MainActivity");
            // Fallback to Firebase Auth if MainActivity userId is null
            if (mAuth.getCurrentUser() != null) {
                userId = mAuth.getCurrentUser().getUid();
                Log.d("LogisticsPage", "Retrieved userId from FirebaseAuth: " + userId);
            } else {
                Log.e("LogisticsPage", "Unable to get userId from any source");
                Toast.makeText(getContext(), "Error: User not authenticated", Toast.LENGTH_LONG).show();
                return view;
            }
        } else {
            Log.d("LogisticsPage", "Retrieved userId from MainActivity: " + userId);
        }

        // Initialize ViewModel
        dbViewModel = new ViewModelProvider(requireActivity()).get(DBViewModel.class);

        // Set the userId in ViewModel
        dbViewModel.setCurrentUserId(userId);
        Log.d("LogisticsPage", "Set userId in DBViewModel: " + userId);

        // Initialize RecyclerView
        userList = view.findViewById(R.id.userList);
        contributors = new ArrayList<>();
        adapter = new UserListAdapter(contributors);
        userList.setLayoutManager(new LinearLayoutManager(getContext()));
        userList.setAdapter(adapter);

        // Set up visualization button
        view.findViewById(R.id.visualizeButton).setOnClickListener(v -> showVisualization());

        // Set up invite button
        view.findViewById(R.id.inviteUserButton).setOnClickListener(v -> showInviteDialog());

        view.findViewById(R.id.notesButton).setOnClickListener(v -> createNotes());

        // Load initial data
        loadContributors();

        return view;
    }

    private void createNotes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.logistics_notes, null);
        EditText noteInput = dialogView.findViewById(R.id.userNotes);

        builder.setView(dialogView).setTitle("Add Notes")
                .setPositiveButton("Submit", (dialog, which) -> {
                    String notes = noteInput.getText().toString();

                    if (!notes.isEmpty()) {
                        try {

                            String userId = MainActivity.getUserId();
                            DatabaseReference DB = new DBViewModel().getDB();
                            DB.child("users").child(userId).child("destinations").child("notes").child(userId)
                                    .setValue(new NotesModel(notes));

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

    private void showVisualization() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View chartView = getLayoutInflater().inflate(R.layout.chart_dialog, null);
        PieChart pieChart = chartView.findViewById(R.id.pieChart);

        // Configure basic chart settings
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterTextSize(16f);

        // Configure legend
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);

        // Get data from database with loading indicator
        Toast.makeText(getContext(), "Loading travel data...", Toast.LENGTH_SHORT).show();

        dbViewModel.getTravelStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                // Create pie chart entries
                List<PieEntry> entries = new ArrayList<>();
                float totalValue = stats.getAllottedDays() + stats.getPlannedDays();

                if (totalValue > 0) {
                    // Always add both entries with actual values
                    entries.add(new PieEntry(stats.getAllottedDays(), "Allotted Days"));
                    entries.add(new PieEntry(stats.getPlannedDays(), "Planned Days"));
                } else {
                    // If both values are 0, add a placeholder entry
                    entries.add(new PieEntry(1, "No Travel Days"));
                }

                PieDataSet dataSet = new PieDataSet(entries, "Travel Days Distribution");
                dataSet.setSliceSpace(3f);
                dataSet.setSelectionShift(5f);

                // Set colors
                ArrayList<Integer> colors = new ArrayList<>();
                colors.add(Color.rgb(64, 89, 128));  // Dark blue for allotted
                colors.add(Color.rgb(231, 76, 60));  // Red for planned
                dataSet.setColors(colors);

                // Configure data display
                PieData data = new PieData(dataSet);
                data.setValueFormatter(new PercentFormatter(pieChart));
                data.setValueTextSize(14f);
                data.setValueTextColor(Color.WHITE);

                // Update chart
                pieChart.setData(data);
                pieChart.highlightValues(null);

                // Add summary text
                String summaryText;
                if (totalValue > 0) {
                    summaryText = String.format("Allotted: %d days\nPlanned: %d days",
                            stats.getAllottedDays(), stats.getPlannedDays());

                    // Add warning if planned exceeds allotted
                    if (stats.getPlannedDays() > stats.getAllottedDays() && stats.getAllottedDays() > 0) {
                        summaryText += "\n⚠️ Exceeds allotted time";
                    }
                } else {
                    summaryText = "No travel days recorded";
                }

                pieChart.setCenterText(summaryText);

                // Animate and refresh
                pieChart.animateY(1400);
                pieChart.invalidate();
            } else {
                // Handle null stats case
                List<PieEntry> entries = new ArrayList<>();
                entries.add(new PieEntry(1, "No Data Available"));

                PieDataSet dataSet = new PieDataSet(entries, "");
                dataSet.setColor(Color.GRAY);

                PieData data = new PieData(dataSet);
                data.setValueFormatter(new PercentFormatter(pieChart));
                pieChart.setData(data);
                pieChart.setCenterText("No travel data available");
                pieChart.invalidate();
            }
        });

        AlertDialog dialog = builder.setView(chartView)
                .setTitle("Allotted vs Planned Days")
                .setPositiveButton("Close", null)
                .create();

        // Make dialog larger
        dialog.setOnShowListener(dialogInterface -> {
            AlertDialog alertDialog = (AlertDialog) dialogInterface;
            if (alertDialog.getWindow() != null) {
                alertDialog.getWindow().setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
            }
        });

        dialog.show();
    }

private void showInviteDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    View dialogView = getLayoutInflater().inflate(R.layout.invite_dialog, null);
    EditText emailInput = dialogView.findViewById(R.id.emailInput);

    builder.setView(dialogView)
            .setTitle("Invite User")
            .setPositiveButton("Invite", (dialog, which) -> {
                String email = emailInput.getText().toString();
                if (!email.isEmpty()) {
                    inviteUser(email);
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
}

private void inviteUser(String email) {
    dbViewModel.inviteUser(email).observe(getViewLifecycleOwner(), success -> {
        if (success) {
            Toast.makeText(getContext(), "Invitation sent!", Toast.LENGTH_SHORT).show();
            loadContributors();
        } else {
            Toast.makeText(getContext(), "Failed to send invitation", Toast.LENGTH_SHORT).show();
        }
    });
}

private void loadContributors() {
    dbViewModel.getContributors().observe(getViewLifecycleOwner(), users -> {
        contributors.clear();
        contributors.addAll(users);
        adapter.notifyDataSetChanged();
    });
}
}