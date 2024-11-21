package com.example.sprintproject.viewmodels;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.sprintproject.model.DiningReservation;
import com.example.sprintproject.model.TripDBModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiningViewModel extends ViewModel {
    private static final String TAG = "DiningViewModel";
    private final MutableLiveData<List<DiningReservation>> reservations;
    private String currentUserId;
    private String currentTripId;
    private DatabaseReference diningRef;

    public DiningViewModel() {
        reservations = new MutableLiveData<>(new ArrayList<>());
    }

    public void setCurrentIds(String userId, String tripId) {
        this.currentUserId = userId;
        this.currentTripId = tripId;
        this.diningRef = TripDBModel.getTripReference(tripId).child("dining");
        loadReservations();
    }

    public LiveData<List<DiningReservation>> getReservations() {
        return reservations;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public String getCurrentTripId() {
        return currentTripId;
    }

    public void addReservation(DiningReservation reservation) {
        if (reservation == null) {
            Log.e(TAG, "Reservation is null");
            return;
        }

        if (!reservation.isValid()) {
            Log.e(TAG, "Invalid reservation data: " + reservation);
            return;
        }

        String reservationId = diningRef.push().getKey();
        if (reservationId == null) {
            Log.e(TAG, "Failed to generate Firebase key");
            return;
        }

        reservation.setId(reservationId);
        Log.d(TAG, "Saving reservation to path: " + diningRef.child(reservationId).toString());
        Log.d(TAG, "Reservation data: " + reservation);

        diningRef.child(reservationId).setValue(reservation)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Successfully saved reservation");
                    List<DiningReservation> currentList = reservations.getValue();
                    if (currentList != null) {
                        currentList.add(reservation);
                        sortReservations(true);
                        reservations.setValue(currentList);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to save reservation", e));
    }

    public void sortReservations(boolean ascending) {
        List<DiningReservation> currentList = reservations.getValue();
        if (currentList != null) {
            if (ascending) {
                Collections.sort(currentList,
                        (a, b) -> Long.compare(a.getReservationTimestamp(), b.getReservationTimestamp()));
            } else {
                Collections.sort(currentList,
                        (a, b) -> Long.compare(b.getReservationTimestamp(), a.getReservationTimestamp()));
            }
            reservations.setValue(currentList);
        }
    }

    private void loadReservations() {
        diningRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DiningReservation> reservationList = new ArrayList<>();
                for (DataSnapshot reservationSnapshot : snapshot.getChildren()) {
                    try {
                        DiningReservation reservation = reservationSnapshot.getValue(DiningReservation.class);
                        if (reservation != null && reservation.isValid()) {
                            reservation.setId(reservationSnapshot.getKey());
                            reservationList.add(reservation);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing reservation data: " + e.getMessage());
                    }
                }
                Collections.sort(reservationList,
                        (a, b) -> Long.compare(a.getReservationTimestamp(), b.getReservationTimestamp()));
                reservations.setValue(reservationList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load reservations", error.toException());
            }
        });
    }
}