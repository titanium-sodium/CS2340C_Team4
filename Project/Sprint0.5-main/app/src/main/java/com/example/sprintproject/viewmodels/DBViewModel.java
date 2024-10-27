package com.example.sprintproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.sprintproject.model.DBModel;

import com.example.sprintproject.model.TravelStats;
import com.example.sprintproject.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.example.sprintproject.model.DBModel;

import java.util.ArrayList;
import java.util.List;

public class DBViewModel extends ViewModel {
    private DatabaseReference DB;
    private MutableLiveData<TravelStats> travelStatsLiveData;
    private MutableLiveData<List<UserModel>> contributorsLiveData;

    public DBViewModel() {
        DB = DBModel.getInstance();
        travelStatsLiveData = new MutableLiveData<>();
        contributorsLiveData = new MutableLiveData<>();
        loadTravelStats();
    }

    public DatabaseReference getDB() {return DB;}
    public LiveData<TravelStats> getTravelStats() {
        if (travelStatsLiveData.getValue() == null) {
            loadTravelStats();
        }
        return travelStatsLiveData;
    }

    // Add this method to load travel stats from database
    private void loadTravelStats() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DB.child("users").child(userId).child("travelStats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        TravelStats stats = snapshot.getValue(TravelStats.class);
                        if (stats != null) {
                            travelStatsLiveData.setValue(stats);
                        } else {
                            travelStatsLiveData.setValue(new TravelStats()); // or default values
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.err.println("Error loading travel stats: " + error.getMessage());
                    }
                });
    }

    public LiveData<Boolean> inviteUser(String email) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Find user by email similar to LoginPage approach
        DB.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean userFound = false;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    UserModel user = userSnapshot.getValue(UserModel.class);
                    if (user != null && user.getEmail().equals(email)) {
                        // Found the user, add them as contributor
                        String invitedUserId = user.getUserId();
                        DB.child("users").child(currentUserId)
                                .child("contributors")
                                .child(invitedUserId)
                                .setValue(true)
                                .addOnSuccessListener(aVoid -> {
                                    result.setValue(true);
                                    // Also add the current user as contributor to invited user's list
                                    DB.child("users").child(invitedUserId)
                                            .child("contributors")
                                            .child(currentUserId)
                                            .setValue(true);
                                })
                                .addOnFailureListener(e -> {
                                    System.err.println("Failed to add contributor: " + e.getMessage());
                                    result.setValue(false);
                                });
                        userFound = true;
                        break;
                    }
                }
                if (!userFound) {
                    result.setValue(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Error inviting user: " + error.getMessage());
                result.setValue(false);
            }
        });

        return result;
    }

    public LiveData<List<UserModel>> getContributors() {
        if (contributorsLiveData.getValue() == null) {
            loadContributors();
        }
        return contributorsLiveData;
    }

    private void loadContributors() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DB.child("users").child(currentUserId).child("contributors")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<UserModel> contributors = new ArrayList<>();

                        for (DataSnapshot contributorSnapshot : snapshot.getChildren()) {
                            String contributorId = contributorSnapshot.getKey();
                            if (contributorId != null) {
                                DB.child("users").child(contributorId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot userSnapshot) {
                                                UserModel user = userSnapshot.getValue(UserModel.class);
                                                if (user != null) {
                                                    contributors.add(user);
                                                    contributorsLiveData.setValue(contributors);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError error) {
                                                System.err.println("Error loading contributor: " + error.getMessage());
                                            }
                                        });
                            }
                        }

                        if (!snapshot.exists()) {
                            contributorsLiveData.setValue(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.err.println("Error loading contributors: " + error.getMessage());
                        contributorsLiveData.setValue(new ArrayList<>());
                    }
                });
    }


    // Method to update allotted days
    public void updateAllottedDays(int days) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DB.child("users").child(userId).child("travelStats").child("duration").setValue(days);
        loadTravelStats(); // Reload stats after update
    }
}