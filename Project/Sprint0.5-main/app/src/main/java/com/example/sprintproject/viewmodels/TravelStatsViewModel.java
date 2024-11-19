package com.example.sprintproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.sprintproject.model.TravelStats;
import com.example.sprintproject.model.TripDBModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TravelStatsViewModel extends ViewModel {
    private final MutableLiveData<TravelStats> travelStats = new MutableLiveData<>();
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public LiveData<TravelStats> getTravelStats() {
        return travelStats;
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