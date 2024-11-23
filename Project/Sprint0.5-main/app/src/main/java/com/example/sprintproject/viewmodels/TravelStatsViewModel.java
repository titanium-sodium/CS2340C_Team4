package com.example.sprintproject.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.sprintproject.model.TravelStats;
import com.example.sprintproject.model.TripDBModel;
import com.google.firebase.database.*;

public class TravelStatsViewModel extends ViewModel {
    private final MutableLiveData<TravelStats> travelStats = new MutableLiveData<>();
    private String currentTripId;
    private ValueEventListener statsListener;

    public LiveData<TravelStats> getTravelStats() {
        return travelStats;
    }

    public void loadTravelStats(String tripId) {
        if (tripId == null) {
            return;
        }


        // Don't reload if we're already listening to this trip
        if (tripId.equals(currentTripId)) {
            return;
        }

        // Clean up existing listener
        removeExistingListener();

        currentTripId = tripId;
        setupNewListener();
    }

    private void removeExistingListener() {
        if (statsListener != null && currentTripId != null) {
            TripDBModel.getTripReference(currentTripId)
                    .child("travelStats")
                    .removeEventListener(statsListener);
        }
    }

    private void setupNewListener() {
        statsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TravelStats stats = parseSnapshot(snapshot);
                calculateDerivedStats(stats);
                travelStats.setValue(stats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                TravelStats defaultStats = new TravelStats();
                defaultStats.setAllottedDays(0);
                defaultStats.setPlannedDays(0);
                defaultStats.setPlannedPercentage(0);
                defaultStats.setRemainingDays(0);
                travelStats.setValue(defaultStats);
            }
        };

        TripDBModel.getTripReference(currentTripId)
                .child("travelStats")
                .addValueEventListener(statsListener);
    }

    private TravelStats parseSnapshot(DataSnapshot snapshot) {
        TravelStats stats = new TravelStats();

        try {
            if (snapshot.exists()) {
                TravelStats snapshotStats = snapshot.getValue(TravelStats.class);
                if (snapshotStats != null) {
                    return snapshotStats;
                }

                // Manual parsing if automatic deserialization fails
                if (snapshot.hasChild("allottedDays")) {
                    stats.setAllottedDays(snapshot.child("allottedDays").getValue(Integer.class));
                }
                if (snapshot.hasChild("plannedDays")) {
                    stats.setPlannedDays(snapshot.child("plannedDays").getValue(Integer.class));
                }
                if (snapshot.hasChild("remainingDays")) {
                    stats.setRemainingDays(snapshot.child("remainingDays").getValue(Integer.class));
                }
                if (snapshot.hasChild("plannedPercentage")) {
                    Double percentage = snapshot.child("plannedPercentage").getValue(Double.class);
                    if (percentage != null) {
                        stats.setPlannedPercentage(percentage.floatValue());
                    }
                }
            }
        } catch (Exception e) {
            // If anything goes wrong, return a stats object with default values
            stats = new TravelStats();
            stats.setAllottedDays(0);
            stats.setPlannedDays(0);
            stats.setPlannedPercentage(0);
            stats.setRemainingDays(0);
        }

        return stats;
    }

    private void calculateDerivedStats(TravelStats stats) {
        if (stats.getAllottedDays() > 0) {
            // Calculate percentage
            float percentage = ((float) stats.getPlannedDays() / stats.getAllottedDays()) * 100;
            stats.setPlannedPercentage(percentage);

            // Calculate remaining days
            int remaining = stats.getAllottedDays() - stats.getPlannedDays();
            stats.setRemainingDays(remaining);
        } else {
            stats.setPlannedPercentage(0);
            stats.setRemainingDays(0);
        }
    }

    public void updatePlannedDays(String tripId, int plannedDays) {
        if (tripId == null) {
            return;
        }

        DatabaseReference statsRef = TripDBModel.getTripReference(tripId)
                .child("travelStats");

        statsRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                TravelStats stats = mutableData.getValue(TravelStats.class);
                if (stats == null) {
                    stats = new TravelStats();
                }
                stats.setPlannedDays(plannedDays);
                calculateDerivedStats(stats);
                mutableData.setValue(stats);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed,
                                   DataSnapshot currentData) {
                if (committed && currentData.exists()) {
                    TravelStats updatedStats = currentData.getValue(TravelStats.class);
                    if (updatedStats != null) {
                        calculateDerivedStats(updatedStats);
                        travelStats.setValue(updatedStats);
                    }
                }
            }
        });
    }

    public void updateAllottedDays(String tripId, int allottedDays) {
        if (tripId == null) {
            return;
        }

        DatabaseReference statsRef = TripDBModel.getTripReference(tripId)
                .child("travelStats");

        statsRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                TravelStats stats = mutableData.getValue(TravelStats.class);
                if (stats == null) {
                    stats = new TravelStats();
                }
                stats.setAllottedDays(allottedDays);
                calculateDerivedStats(stats);
                mutableData.setValue(stats);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed,
                                   DataSnapshot currentData) {
                if (committed && currentData.exists()) {
                    TravelStats updatedStats = currentData.getValue(TravelStats.class);
                    if (updatedStats != null) {
                        calculateDerivedStats(updatedStats);
                        travelStats.setValue(updatedStats);
                    }
                }
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        removeExistingListener();
    }
}