package com.example.sprintproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;

import com.example.sprintproject.model.DiningDBModel;
import com.example.sprintproject.model.DiningReservation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DiningViewModel extends ViewModel {
    private static final String TAG = "DiningViewModel";
    private final DatabaseReference diningDB;
    private final MutableLiveData<List<DiningReservation>> reservationsLiveData;
    private String currentTripId;
    private String currentUserId;

    public DiningViewModel() {
        diningDB = DiningDBModel.getInstance();
        reservationsLiveData = new MutableLiveData<>(new ArrayList<>());
    }

    public void setCurrentIds(String userId, String tripId) {
        if (userId == null || tripId == null) {
            Log.e(TAG, "Attempted to set null userId or tripId");
            return;
        }

        if (userId.equals(this.currentUserId) && tripId.equals(this.currentTripId)) {
            Log.d(TAG, "IDs unchanged, skipping reload");
            return;
        }

        this.currentUserId = userId;
        this.currentTripId = tripId;
        loadReservations();
    }

    private void loadReservations() {
        if (currentTripId == null || currentUserId == null) {
            Log.e(TAG, "Cannot load reservations: missing requirements");
            reservationsLiveData.setValue(new ArrayList<>());
            return;
        }

        DatabaseReference tripDiningRef = DiningDBModel.getDiningForTrip(currentTripId);
        tripDiningRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<DiningReservation> reservations = new ArrayList<>();
                for (DataSnapshot reservationSnapshot : snapshot.getChildren()) {
                    try {
                        DiningReservation reservation = reservationSnapshot.getValue(DiningReservation.class);
                        if (reservation != null) {
                            reservations.add(reservation);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing reservation: " + e.getMessage());
                    }
                }
                reservationsLiveData.setValue(reservations);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error loading reservations: " + error.getMessage());
                reservationsLiveData.setValue(new ArrayList<>());
            }
        });
    }

    public void addReservation(DiningReservation reservation) {
        if (currentTripId == null || currentUserId == null) {
            Log.e(TAG, "Cannot add reservation: missing requirements");
            return;
        }

        String reservationId = UUID.randomUUID().toString();
        reservation.setId(reservationId);

        DatabaseReference tripDiningRef = DiningDBModel.getDiningForTrip(currentTripId);
        tripDiningRef.child(reservationId).setValue(reservation)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Reservation added successfully");
                    // The ValueEventListener will automatically update the LiveData
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding reservation: " + e.getMessage());
                });
    }

    public LiveData<List<DiningReservation>> getReservations() {
        if (reservationsLiveData.getValue() == null) {
            loadReservations();
        }
        return reservationsLiveData;
    }

    public String getCurrentTripId() {
        return currentTripId;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }
}