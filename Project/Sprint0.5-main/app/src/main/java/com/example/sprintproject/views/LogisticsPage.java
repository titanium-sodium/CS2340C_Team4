package com.example.sprintproject.views;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
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
import com.example.sprintproject.model.UserModel;
import com.example.sprintproject.views.UserListAdapter;
import com.example.sprintproject.viewmodels.DBViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class LogisticsPage extends Fragment {
    private RecyclerView userList;
    private UserListAdapter adapter;
    private List<UserModel> contributors;
    private DBViewModel dbViewModel;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.logistics_screen, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize ViewModel
        dbViewModel = new ViewModelProvider(requireActivity()).get(DBViewModel.class);

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

        // Load initial data
        loadContributors();

        return view;
    }

    private void showVisualization() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View chartView = getLayoutInflater().inflate(R.layout.chart_dialog, null);
        PieChart pieChart = chartView.findViewById(R.id.pieChart);

        // Get data from database
        dbViewModel.getTravelStats().observe(getViewLifecycleOwner(), stats -> {
            int allottedDays = stats.getAllottedDays();
            int plannedDays = stats.getPlannedDays();
            int remainingDays = allottedDays - plannedDays;

            // Create pie chart entries
            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(plannedDays, "Planned Days"));
            entries.add(new PieEntry(remainingDays, "Remaining Days"));

            PieDataSet dataSet = new PieDataSet(entries, "Travel Days");
            dataSet.setColors(Color.BLUE, Color.GRAY);

            PieData data = new PieData(dataSet);
            pieChart.setData(data);
            pieChart.getDescription().setEnabled(false);
            pieChart.invalidate();
        });

        builder.setView(chartView)
                .setTitle("Travel Days Overview")
                .setPositiveButton("Close", null)
                .show();
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