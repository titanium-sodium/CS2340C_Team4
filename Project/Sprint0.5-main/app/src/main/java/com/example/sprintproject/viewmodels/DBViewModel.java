package com.example.sprintproject.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.sprintproject.model.DBModel;
import com.example.sprintproject.model.TravelStats;
import com.example.sprintproject.model.UserModel;
import com.example.sprintproject.views.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class DBViewModel extends ViewModel {
    private static DBViewModel instance;
    private final DatabaseReference DB;
    private final MutableLiveData<TravelStats> travelStatsLiveData;
    private final MutableLiveData<List<UserModel>> contributorsLiveData;
    private static final String TAG = "DBViewModel";
    private String currentUserId;

    public DBViewModel() {
        DB = DBModel.getInstance();
        travelStatsLiveData = new MutableLiveData<>();
        contributorsLiveData = new MutableLiveData<>();

        // Try to get userId from MainActivity on initialization
        String mainActivityUserId = MainActivity.getUserId();
        if (mainActivityUserId != null) {
            setCurrentUserId(mainActivityUserId);
            Log.d(TAG, "Initialized DBViewModel with userId from MainActivity: " + mainActivityUserId);
        } else {
            Log.d(TAG, "No userId available from MainActivity during DBViewModel initialization");
        }
    }

    public DatabaseReference getDB() {
        return DB;
    }

    public void setCurrentUserId(String userId) {
        if (userId == null) {
            Log.e(TAG, "Attempted to set null userId");
            return;
        }

        if (userId.equals(this.currentUserId)) {
            Log.d(TAG, "UserId unchanged, skipping reload");
            return;
        }

        Log.d(TAG, "Setting currentUserId: " + userId);
        this.currentUserId = userId;

        // Load data with new userId
        loadTravelStats();
        loadContributors();
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    private void ensureUserIdSet() {
        if (currentUserId == null) {
            String mainActivityUserId = MainActivity.getUserId();
            if (mainActivityUserId != null) {
                setCurrentUserId(mainActivityUserId);
                Log.d(TAG, "Retrieved and set userId from MainActivity: " + mainActivityUserId);
            } else {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() != null) {
                    setCurrentUserId(auth.getCurrentUser().getUid());
                    Log.d(TAG, "Retrieved and set userId from FirebaseAuth: " + currentUserId);
                } else {
                    Log.e(TAG, "No userId available from any source");
                }
            }
        }
    }

    public LiveData<TravelStats> getTravelStats() {
        ensureUserIdSet();
        if (travelStatsLiveData.getValue() == null && currentUserId != null) {
            loadTravelStats();
        }
        return travelStatsLiveData;
    }

    private void loadTravelStats() {
        if (currentUserId == null) {
            Log.e(TAG, "Cannot load travel stats: userId is null");
            travelStatsLiveData.setValue(new TravelStats());
            return;
        }

        if (DB == null) {
            Log.e(TAG, "Cannot load travel stats: DB reference is null");
            travelStatsLiveData.setValue(new TravelStats());
            return;
        }

        Log.d(TAG, "Loading travel stats for userId: " + currentUserId);

        DB.child("users").child(currentUserId).child("travelStats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Log.d(TAG, "Snapshot exists: " + snapshot.exists());
                            Log.d(TAG, "Snapshot value: " + snapshot.getValue());

                            int allottedDays = 0;
                            int plannedDays = 0;

                            if (snapshot.exists()) {
                                if (snapshot.hasChild("allottedDays")) {
                                    Object allottedValue = snapshot.child("allottedDays").getValue();
                                    if (allottedValue != null) {
                                        if (allottedValue instanceof Long) {
                                            allottedDays = ((Long) allottedValue).intValue();
                                        } else if (allottedValue instanceof Integer) {
                                            allottedDays = (Integer) allottedValue;
                                        } else if (allottedValue instanceof String) {
                                            allottedDays = Integer.parseInt((String) allottedValue);
                                        }
                                    }
                                }

                                if (snapshot.hasChild("plannedDays")) {
                                    Object plannedValue = snapshot.child("plannedDays").getValue();
                                    if (plannedValue != null) {
                                        if (plannedValue instanceof Long) {
                                            plannedDays = ((Long) plannedValue).intValue();
                                        } else if (plannedValue instanceof Integer) {
                                            plannedDays = (Integer) plannedValue;
                                        } else if (plannedValue instanceof String) {
                                            plannedDays = Integer.parseInt((String) plannedValue);
                                        }
                                    }
                                }
                            } else {
                                // Initialize travel stats for new users
                                TravelStats newStats = new TravelStats();
                                DB.child("users").child(currentUserId).child("travelStats").setValue(newStats);
                            }

                            TravelStats stats = new TravelStats();
                            stats.setAllottedDays(allottedDays);
                            stats.setPlannedDays(plannedDays);
                            travelStatsLiveData.setValue(stats);

                        } catch (Exception e) {
                            Log.e(TAG, "Error calculating travel stats", e);
                            travelStatsLiveData.setValue(new TravelStats());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error loading travel stats: " + error.getMessage());
                        travelStatsLiveData.setValue(new TravelStats());
                    }
                });
    }

    public LiveData<List<UserModel>> getContributors() {
        if (contributorsLiveData.getValue() == null) {
            loadContributors();
        }
        return contributorsLiveData;
    }

    private void loadContributors() {
        if (currentUserId == null) {
            Log.e(TAG, "Cannot load contributors: userId is null");
            contributorsLiveData.setValue(new ArrayList<>());
            return;
        }

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
                                                Log.e(TAG, "Error loading contributor: " + error.getMessage());
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
                        Log.e(TAG, "Error loading contributors: " + error.getMessage());
                        contributorsLiveData.setValue(new ArrayList<>());
                    }
                });
    }

    public LiveData<Boolean> inviteUser(String email) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        if (currentUserId == null) {
            Log.e(TAG, "Cannot invite user: current user is null");
            result.setValue(false);
            return result;
        }

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
                                    Log.e(TAG, "Failed to add contributor: " + e.getMessage());
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
                Log.e(TAG, "Error inviting user: " + error.getMessage());
                result.setValue(false);
            }
        });

        return result;
    }

    public void updateAllottedDays(int days) {
        if (days >= 0 && currentUserId != null) {
            DB.child("users").child(currentUserId).child("destinations").child("duration")
                    .setValue(days)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Successfully updated allotted days to: " + days);
                        loadTravelStats();
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to update allotted days: " + e.getMessage()));
        }
    }
}