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
import com.example.sprintproject.views.NotesAdapter;
import com.example.sprintproject.views.UserListAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class LogisticsPage extends Fragment {
    private RecyclerView userList;
    private RecyclerView notesList;
    private UserListAdapter userAdapter;
    private NotesAdapter notesAdapter;
    private List<UserModel> contributors;
    private List<NotesModel> notes;
    private DBViewModel dbViewModel;
    private FirebaseAuth mAuth;
    private String userId;
    private static final String TAG = "LogisticsPage";

    private void initializeRecyclerViews(View view) {
        // Debug logging to help identify the issue
        Log.d(TAG, "Initializing RecyclerViews");

        // Initialize Contributors RecyclerView
        userList = view.findViewById(R.id.userList);
        if (userList == null) {
            Log.e(TAG, "userList RecyclerView not found in layout");
            return;
        }

        contributors = new ArrayList<>();
        userAdapter = new UserListAdapter(contributors);
        userList.setLayoutManager(new LinearLayoutManager(requireContext()));
        userList.setAdapter(userAdapter);

        // Initialize Notes RecyclerView
        notesList = view.findViewById(R.id.notesList);
        if (notesList == null) {
            Log.e(TAG, "notesList RecyclerView not found in layout");
            return;
        }

        notes = new ArrayList<>();
        notesAdapter = new NotesAdapter(notes);
        notesList.setLayoutManager(new LinearLayoutManager(requireContext()));
        notesList.setAdapter(notesAdapter);

        Log.d(TAG, "RecyclerViews initialized successfully");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Make sure we're inflating the correct layout
        View view = inflater.inflate(R.layout.logistics_screen, container, false);

        // Initialize everything after view inflation
        mAuth = FirebaseAuth.getInstance();
        initializeUserId();
        initializeViewModel();
        initializeRecyclerViews(view); // This should now work correctly
        setupButtons(view);
        loadData();

        return view;
    }

    private void initializeUserId() {
        userId = MainActivity.getUserId();
        if (userId == null && mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
            Log.d(TAG, "Retrieved userId from FirebaseAuth: " + userId);
        }

        if (userId == null) {
            Log.e(TAG, "Unable to get userId from any source");
            Toast.makeText(getContext(), "Error: User not authenticated", Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViewModel() {
        dbViewModel = new ViewModelProvider(requireActivity()).get(DBViewModel.class);
        if (userId != null) {
            dbViewModel.setCurrentUserId(userId);
            Log.d(TAG, "Set userId in DBViewModel: " + userId);
        }
    }

    private void setupButtons(View view) {
        view.findViewById(R.id.visualizeButton).setOnClickListener(v -> showVisualization());
        view.findViewById(R.id.inviteUserButton).setOnClickListener(v -> showInviteDialog());
        view.findViewById(R.id.notesButton).setOnClickListener(v -> showNotesDialog());
    }

    private void loadData() {
        loadContributors();
        loadNotes();
    }

    private void showNotesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.logistics_notes, null);
        EditText noteInput = dialogView.findViewById(R.id.userNotes);
        RecyclerView notesView = dialogView.findViewById(R.id.notesRecyclerView); // Changed to match layout ID

        // Set up notes RecyclerView in dialog
        notesView.setLayoutManager(new LinearLayoutManager(getContext()));
        notesView.setAdapter(notesAdapter);

        builder.setView(dialogView)
                .setTitle("Travel Notes")
                .setPositiveButton("Add Note", (dialog, which) -> {
                    String noteText = noteInput.getText().toString().trim();
                    if (!noteText.isEmpty()) {
                        dbViewModel.addNote(noteText);
                        Toast.makeText(getContext(), "Note added", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Close", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showVisualization() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View chartView = getLayoutInflater().inflate(R.layout.chart_dialog, null);
        PieChart pieChart = chartView.findViewById(R.id.pieChart);

        configureChart(pieChart);
        loadChartData(pieChart);

        AlertDialog dialog = builder.setView(chartView)
                .setTitle("Travel Days Overview")
                .setPositiveButton("Close", null)
                .create();

        // Make dialog larger
        dialog.setOnShowListener(dialogInterface -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
            }
        });

        dialog.show();
    }

    private void configureChart(PieChart pieChart) {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterTextSize(16f);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);
    }

    private void loadChartData(PieChart pieChart) {
        dbViewModel.getTravelStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                updateChartWithStats(pieChart, stats);
            } else {
                showEmptyChart(pieChart);
            }
        });
    }

    private void updateChartWithStats(PieChart pieChart, TravelStats stats) {
        List<PieEntry> entries = new ArrayList<>();
        float totalValue = stats.getAllottedDays() + stats.getPlannedDays();

        if (totalValue > 0) {
            entries.add(new PieEntry(stats.getAllottedDays(), "Allotted Days"));
            entries.add(new PieEntry(stats.getPlannedDays(), "Planned Days"));
        } else {
            entries.add(new PieEntry(1, "No Travel Days"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Travel Days Distribution");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(64, 89, 128));
        colors.add(Color.rgb(231, 76, 60));
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);

        String summaryText = createSummaryText(stats, totalValue);
        pieChart.setCenterText(summaryText);

        pieChart.animateY(1400);
        pieChart.invalidate();
    }

    private String createSummaryText(TravelStats stats, float totalValue) {
        if (totalValue > 0) {
            String text = String.format("Allotted: %d days\nPlanned: %d days",
                    stats.getAllottedDays(), stats.getPlannedDays());
            if (stats.getPlannedDays() > stats.getAllottedDays() && stats.getAllottedDays() > 0) {
                text += "\n⚠️ Exceeds allotted time";
            }
            return text;
        }
        return "No travel days recorded";
    }

    private void showEmptyChart(PieChart pieChart) {
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

    private void showInviteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.invite_dialog, null);
        EditText emailInput = dialogView.findViewById(R.id.emailInput);

        builder.setView(dialogView)
                .setTitle("Invite Travel Partner")
                .setPositiveButton("Send Invitation", (dialog, which) -> {
                    String email = emailInput.getText().toString().trim();
                    if (!email.isEmpty()) {
                        inviteUser(email);
                    } else {
                        Toast.makeText(getContext(), "Please enter an email address", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void inviteUser(String email) {
        dbViewModel.inviteUser(email).observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Invitation sent successfully!", Toast.LENGTH_SHORT).show();
                loadContributors();
            } else {
                Toast.makeText(getContext(), "Failed to send invitation. User not found.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadContributors() {
        dbViewModel.getContributors().observe(getViewLifecycleOwner(), users -> {
            contributors.clear();
            if (users != null) {
                contributors.addAll(users);
            }
            userAdapter.notifyDataSetChanged();
        });
    }

    private void loadNotes() {
        dbViewModel.getNotes().observe(getViewLifecycleOwner(), notesList -> {
            notes.clear();
            if (notesList != null) {
                notes.addAll(notesList);
            }
            notesAdapter.notifyDataSetChanged();
        });
    }
}