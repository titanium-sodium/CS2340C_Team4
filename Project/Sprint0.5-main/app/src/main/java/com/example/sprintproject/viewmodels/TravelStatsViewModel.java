package com.example.sprintproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.sprintproject.model.TravelStats;
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
        return travelStats;
    }

    public void loadTravelStats(String userId) {
        database.child("users").child(userId).child("travelStats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        TravelStats stats = snapshot.getValue(TravelStats.class);
                        if (stats == null) {
                            stats = new TravelStats(); // Use default values
                        }
                        travelStats.setValue(stats);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Handle error
                    }
                });
    }

    public Task<Void> updateAllottedDays(String userId, int allottedDays) {
        DatabaseReference userRef = database.child("users").child(userId).child("travelStats");
        return userRef.child("allottedDays").setValue(allottedDays)
                .addOnSuccessListener(aVoid -> {
                    // Update the LiveData
                    TravelStats currentStats = travelStats.getValue();
                    if (currentStats != null) {
                        travelStats.setValue(new TravelStats(allottedDays, currentStats.getPlannedDays()));
                    } else {
                        travelStats.setValue(new TravelStats(allottedDays, 0));
                    }
                });
    }

    public Task<Void> addPlannedDays(String userId, int additionalDays) {
        TravelStats currentStats = travelStats.getValue();
        if (currentStats != null) {
            int newPlannedDays = currentStats.getPlannedDays() + additionalDays;
            DatabaseReference userRef = database.child("users").child(userId).child("travelStats");
            return userRef.child("plannedDays").setValue(newPlannedDays)
                    .addOnSuccessListener(aVoid -> {
                        travelStats.setValue(new TravelStats(currentStats.getAllottedDays(), newPlannedDays));
                    });
        }
        return database.child("users").child(userId).child("travelStats")
                .child("plannedDays").setValue(additionalDays);
    }
}