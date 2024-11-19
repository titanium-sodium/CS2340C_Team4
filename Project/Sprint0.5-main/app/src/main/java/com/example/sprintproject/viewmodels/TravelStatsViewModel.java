package com.example.sprintproject.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.sprintproject.model.TravelStats;
import com.example.sprintproject.model.TripDBModel;
import com.example.sprintproject.views.MainActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TravelStatsViewModel extends ViewModel {
    private final MutableLiveData<TravelStats> travelStats = new MutableLiveData<>();
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public LiveData<TravelStats> getTravelStats() {
        if (travelStats.getValue() == null) {
            loadTravelStats();
        }
        return travelStats;
    }

    private void loadTravelStats() {
        TripDBModel.getTripReference(MainActivity.getTripId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            int allottedDays = 0;
                            int plannedDays = 0;

                            if (snapshot.exists()) {
                                if (snapshot.hasChild("allottedDays")) {
                                    Object allottedValue = snapshot.child("allottedDays").
                                            getValue();
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
                                TravelStats newStats = new TravelStats();
                                TripDBModel.getTripReference(MainActivity.getTripId());
                            }

                            TravelStats stats = new TravelStats();
                            stats.setAllottedDays(allottedDays);
                            stats.setPlannedDays(plannedDays);
                            travelStats.setValue(stats);

                        } catch (Exception e) {
                            Log.e("TravelStatsVM ERROR", "Error calculating travel stats", e);
                            travelStats.setValue(new TravelStats());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("TravelStatsVM ERROR", "Error loading travel stats: " + error.getMessage());
                        travelStats.setValue(new TravelStats());
                    }
                });
    }

    public Task<Void> updateAllottedDays(String tripId, int allottedDays) {
        DatabaseReference userRef = TripDBModel.getTripReference(tripId).child("travelStats");

        final TravelStats updatedStats = new TravelStats();
        TravelStats current = travelStats.getValue();
        int plannedDays = current != null ? current.getPlannedDays() : 0;

        // Update all related fields
        updatedStats.setAllottedDays(allottedDays);
        updatedStats.setPlannedDays(plannedDays);

        // Calculate percentage and remaining days
        int percentage = allottedDays > 0 ? (plannedDays * 100) / allottedDays : 0;
        updatedStats.setPlannedPercentage(percentage);
        updatedStats.setRemainingDays(allottedDays - plannedDays);

        // Update entire stats object in Firebase
        return userRef.setValue(updatedStats)
                .addOnSuccessListener(aVoid -> travelStats.setValue(updatedStats));
    }

    public Task<Void> updatePlannedDays(String tripId, int plannedDays) {
        DatabaseReference userRef = TripDBModel.getTripReference(tripId).child("travelStats");

        final TravelStats updatedStats = new TravelStats();
        TravelStats current = travelStats.getValue();
        int allottedDays = current != null ? current.getAllottedDays() : 0;

        // Update all related fields
        updatedStats.setAllottedDays(allottedDays);
        updatedStats.setPlannedDays(plannedDays);

        // Calculate percentage and remaining days
        int percentage = allottedDays > 0 ? (plannedDays * 100) / allottedDays : 0;
        updatedStats.setPlannedPercentage(percentage);
        updatedStats.setRemainingDays(allottedDays - plannedDays);

        // Update entire stats object in Firebase
        return userRef.setValue(updatedStats)
                .addOnSuccessListener(aVoid -> travelStats.setValue(updatedStats));
    }

    public Task<Void> addPlannedDays(String userId, int additionalDays) {
        TravelStats current = travelStats.getValue();
        final TravelStats updatedStats = new TravelStats();

        int currentPlannedDays = current != null ? current.getPlannedDays() : 0;
        int allottedDays = current != null ? current.getAllottedDays() : 0;
        int newPlannedDays = currentPlannedDays + additionalDays;

        // Update all fields
        updatedStats.setAllottedDays(allottedDays);
        updatedStats.setPlannedDays(newPlannedDays);
        int percentage = allottedDays > 0 ? (newPlannedDays * 100) / allottedDays : 0;
        updatedStats.setPlannedPercentage(percentage);
        updatedStats.setRemainingDays(allottedDays - newPlannedDays);

        DatabaseReference userRef = database.child("users").child(userId).child("travelStats");
        return userRef.setValue(updatedStats)
                .addOnSuccessListener(aVoid -> travelStats.setValue(updatedStats));
    }
}