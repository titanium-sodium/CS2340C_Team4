package com.example.sprintproject.views;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ProgressBar;

import com.example.sprintproject.R;
import com.example.sprintproject.model.CommunityPost;
import com.example.sprintproject.viewmodels.TravelCommunityViewModel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TravelCommunityPage extends Fragment {
    private TravelCommunityViewModel viewModel;
    private RecyclerView communityRecyclerView;
    private ProgressBar loadingIndicator;
    private String userId;
    private CommunityPostAdapter adapter;

    public TravelCommunityPage() {
        // Required empty public constructor
    }

    public TravelCommunityPage(String userId) {
        this.userId = userId;
    }

    public static TravelCommunityPage newInstance(String userId) {
        TravelCommunityPage fragment = new TravelCommunityPage();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TravelCommunityViewModel.class);

        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.travel_community_screen, container, false);

        // Initialize views
        communityRecyclerView = view.findViewById(R.id.communityRecycler);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);

        // Setup RecyclerView
        adapter = new CommunityPostAdapter(new ArrayList<>());
        communityRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        communityRecyclerView.setAdapter(adapter);

        // Setup add post button
        view.findViewById(R.id.addPost).setOnClickListener(v -> openAddPostForm());

        // Observe ViewModel data
        setupObservers();

        // Load initial data
        viewModel.loadCommunityPosts(userId);

        return view;
    }

    private void setupObservers() {
        viewModel.getCommunityPosts().observe(getViewLifecycleOwner(), posts -> {
            adapter.updatePosts(posts);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openAddPostForm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.travel_post_dialog, null);

        EditText startInput = dialogView.findViewById(R.id.startEditText);
        EditText endInput = dialogView.findViewById(R.id.endEditText);
        EditText destinationInput = dialogView.findViewById(R.id.destinationEditText);
        EditText accommodationsInput = dialogView.findViewById(R.id.accomodationsEditText);
        EditText reservationInput = dialogView.findViewById(R.id.diningReservationEditText);
        EditText noteInput = dialogView.findViewById(R.id.notesEditText);

        builder.setView(dialogView)
                .setTitle("New Travel Post")
                .setPositiveButton("Create Post", (dialog, which) -> {
                    String start = startInput.getText().toString();
                    String end = endInput.getText().toString();
                    String destination = destinationInput.getText().toString();
                    String accommodations = accommodationsInput.getText().toString();
                    String reservation = reservationInput.getText().toString();
                    String notes = noteInput.getText().toString();

                    if (validateInputs(start, end, destination)) {
                        createCommunityPost(start, end, destination,
                                accommodations, reservation, notes);
                    } else {
                        Toast.makeText(getContext(),
                                "Please fill in all required fields",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private boolean validateInputs(String start, String end, String destination) {
        return !start.isEmpty() && !end.isEmpty() && !destination.isEmpty();
    }

    private void createCommunityPost(String start, String end, String destination,
                                     String accommodations, String reservation, String notes) {
        CommunityPost post = new CommunityPost();

        // Set basic info
        post.setDuration(start + " - " + end);

        // Set destinations
        ArrayList<String> destinations = new ArrayList<>();
        destinations.add(destination);
        post.setDestinations(destinations);

        // Set accommodations
        Map<String, String> accommodationsMap = new HashMap<>();
        accommodationsMap.put(destination, accommodations);
        post.setAccommodations(accommodationsMap);

        // Set dining reservations
        Map<String, String> diningMap = new HashMap<>();
        diningMap.put(destination, reservation);
        post.setDiningReservations(diningMap);

        // Set notes and reflections
        post.setNotes(notes);

        // Save post
        viewModel.createCommunityPost(userId, post)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(),
                                "Post created successfully",
                                Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Failed to create post: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }
}