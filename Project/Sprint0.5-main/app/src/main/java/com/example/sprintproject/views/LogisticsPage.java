package com.example.sprintproject.views;

import android.app.AlertDialog;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.util.Log;


import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprintproject.R;

import com.example.sprintproject.model.DBModel;
import com.example.sprintproject.model.NotesModel;
import com.example.sprintproject.model.UserModel;

import com.example.sprintproject.viewmodels.ChartViewModel;
import com.example.sprintproject.viewmodels.InviteUserViewModel;


import com.example.sprintproject.viewmodels.DBViewModel;
import com.example.sprintproject.viewmodels.TravelStatsViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


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
    private TravelStatsViewModel travelStatsViewModel;
    private ChartViewModel chartViewModel = new ChartViewModel();
    private InviteUserViewModel inviteUserViewModel = new InviteUserViewModel();
    private String userId;
    private String tripId;
    private static final String TAG = "LogisticsPage";

    public LogisticsPage(String userId) {
        this.userId = userId;
    }

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
        View view = inflater.inflate(R.layout.logistics_screen, container, false);

        travelStatsViewModel = new TravelStatsViewModel();
        initializeIds();
        initializeViewModel();
        initializeRecyclerViews(view);
        setupButtons(view);
        loadData();

        return view;
    }

    private void initializeIds() {
        userId = MainActivity.getUserId();
        tripId = MainActivity.getTripId();

        if (userId == null || tripId == null) {
            Log.e(TAG, "Missing required IDs");
            Toast.makeText(getContext(), "Error: Missing required data", Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViewModel() {
        dbViewModel = new ViewModelProvider(requireActivity()).get(DBViewModel.class);
        if (userId != null && tripId != null) {
            dbViewModel.setCurrentUserId(userId);
            dbViewModel.setCurrentTripId(tripId);
            Log.d(TAG, "Initialized ViewModel with userId: " + userId + " and tripId: " + tripId);
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
        RecyclerView notesView = dialogView.findViewById(R.id.notesRecyclerView);
        // Changed to match layout ID

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
    //Main method for chart visualization
    private void showVisualization() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View chartView = getLayoutInflater().inflate(R.layout.chart_dialog, null);
        PieChart pieChart = chartView.findViewById(R.id.pieChart);

        chartViewModel.configureChart(pieChart);
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



    private void showInviteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.invite_dialog, null);
        EditText emailInput = dialogView.findViewById(R.id.emailInput);

        builder.setView(dialogView)
                .setTitle("Invite Travel Partner")
                .setPositiveButton("Send Invitation", (dialog, which) -> {
                    String email = emailInput.getText().toString().trim();
                    if (email != null && !email.isEmpty()) {
                        inviteUser(email);
                    } else {
                        Toast.makeText(getContext(),
                                "Please enter an email address", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void inviteUser(String email) {
        // First find user by email
        DBModel.getUsersReference().orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Get the user ID
                            String invitedUserId = snapshot.getChildren().iterator().next().getKey();

                            // Add trip to invited user's trips
                            DBModel.getUsersReference()
                                    .child(invitedUserId)
                                    .child("trips")
                                    .child(tripId)
                                    .setValue(true);

                            // Add user to trip's contributors
                            DBModel.getTripReference()
                                    .child(tripId)
                                    .child("contributors")
                                    .child(invitedUserId)
                                    .setValue(true);

                            Toast.makeText(getContext(),
                                    "User invited successfully!",
                                    Toast.LENGTH_SHORT).show();

                            loadContributors();
                        } else {
                            Toast.makeText(getContext(),
                                    "User not found",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error inviting user", error.toException());
                    }
                });
    }

    private void loadContributors() {
        // Get contributors from the specific trip
        DBModel.getTripReference().child(tripId).child("contributors")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<UserModel> users = new ArrayList<>();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String contributorId = userSnapshot.getKey();
                            // Get user details from users collection
                            DBModel.getUsersReference().child(contributorId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot userDetails) {
                                            UserModel user = userDetails.getValue(UserModel.class);
                                            if (user != null) {
                                                users.add(user);
                                                userAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e(TAG, "Error loading user details", error.toException());
                                        }
                                    });
                        }
                        contributors.clear();
                        contributors.addAll(users);
                        userAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error loading contributors", error.toException());
                    }
                });
    }

    private void loadNotes() {
        // Get notes from the specific trip
        DBModel.getTripReference().child(tripId).child("notes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<NotesModel> notesList = new ArrayList<>();
                        for (DataSnapshot noteSnapshot : snapshot.getChildren()) {
                            NotesModel note = noteSnapshot.getValue(NotesModel.class);
                            if (note != null) {
                                notesList.add(note);
                            }
                        }
                        notes.clear();
                        notes.addAll(notesList);
                        notesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error loading notes", error.toException());
                    }
                });
    }

    private void loadChartData(PieChart pieChart) {
        travelStatsViewModel.getTravelStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                chartViewModel.updateChartWithStats(pieChart, stats);
            } else {
                chartViewModel.showEmptyChart(pieChart);
            }
        });
    }
}