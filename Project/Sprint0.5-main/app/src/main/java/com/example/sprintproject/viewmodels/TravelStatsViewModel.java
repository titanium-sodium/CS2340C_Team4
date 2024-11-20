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
        if (tripId == null) return;

        if (tripId.equals(currentTripId)) {
            return;
        }

        if (statsListener != null && currentTripId != null) {
            TripDBModel.getTripReference(currentTripId)
                    .child("travelStats")
                    .removeEventListener(statsListener);
        }

        currentTripId = tripId;

        statsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    TravelStats stats = new TravelStats();
                    if (snapshot.exists()) {
                        stats = snapshot.getValue(TravelStats.class);
                        if (stats == null) {
                            stats = new TravelStats();
                            if (snapshot.hasChild("allottedDays")) {
                                stats.setAllottedDays(snapshot.child("allottedDays").getValue(Integer.class));
                            }
                            if (snapshot.hasChild("plannedDays")) {
                                stats.setPlannedDays(snapshot.child("plannedDays").getValue(Integer.class));
                            }
                        }
                    }
                    travelStats.setValue(stats);
                } catch (Exception e) {
                    travelStats.setValue(new TravelStats());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                travelStats.setValue(new TravelStats());
            }
        };

        TripDBModel.getTripReference(tripId)
                .child("travelStats")
                .addValueEventListener(statsListener);
    }

    public void updatePlannedDays(String tripId, int plannedDays) {
        if (tripId == null) return;

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
                mutableData.setValue(stats);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (committed && currentData.exists()) {
                    travelStats.setValue(currentData.getValue(TravelStats.class));
                }
            }
        });
    }

    public void updateAllottedDays(String tripId, int allottedDays) {
        if (tripId == null) return;

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
                mutableData.setValue(stats);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (committed && currentData.exists()) {
                    travelStats.setValue(currentData.getValue(TravelStats.class));
                }
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (statsListener != null && currentTripId != null) {
            TripDBModel.getTripReference(currentTripId)
                    .child("travelStats")
                    .removeEventListener(statsListener);
        }
    }
}