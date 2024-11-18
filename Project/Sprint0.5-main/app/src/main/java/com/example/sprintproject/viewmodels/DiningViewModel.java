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
    private MutableLiveData<List<DiningReservation>> reservations;
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

    public void sortReservations(boolean ascending) {
        List<DiningReservation> currentList = reservations.getValue();
        if (currentList != null) {
            if (ascending) {
                Collections.sort(currentList,
                        (a, b) -> Long.compare(a.getReservationTime(), b.getReservationTime()));
            } else {
                Collections.sort(currentList,
                        (a, b) -> Long.compare(b.getReservationTime(), a.getReservationTime()));
            }
            reservations.setValue(currentList);
        }
    }

    public void addReservation(DiningReservation reservation) {
        // Generate a new unique ID for the reservation
        String reservationId = diningRef.push().getKey();
        if (reservationId != null) {
            reservation.setId(reservationId);

            // Save to Firebase
            diningRef.child(reservationId).setValue(reservation)
                    .addOnSuccessListener(aVoid -> {
                        // Update local list after successful save
                        List<DiningReservation> currentList = reservations.getValue();
                        if (currentList != null) {
                            currentList.add(reservation);
                            reservations.setValue(currentList);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Log.e("DiningViewModel", "Failed to save reservation", e);
                    });
        }
    }

    private void loadReservations() {
        diningRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DiningReservation> reservationList = new ArrayList<>();
                for (DataSnapshot reservationSnapshot : snapshot.getChildren()) {
                    DiningReservation reservation = reservationSnapshot.getValue(DiningReservation.class);
                    if (reservation != null) {
                        reservationList.add(reservation);
                    }
                }
                reservations.setValue(reservationList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DiningViewModel", "Failed to load reservations", error.toException());
            }
        });
    }

    public void deleteReservation(String reservationId) {
        diningRef.child(reservationId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    List<DiningReservation> currentList = reservations.getValue();
                    if (currentList != null) {
                        currentList.removeIf(reservation ->
                                reservation.getId().equals(reservationId));
                        reservations.setValue(currentList);
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("DiningViewModel", "Failed to delete reservation", e));
    }

    public void updateReservation(DiningReservation reservation) {
        if (reservation.getId() != null) {
            diningRef.child(reservation.getId()).setValue(reservation)
                    .addOnSuccessListener(aVoid -> {
                        List<DiningReservation> currentList = reservations.getValue();
                        if (currentList != null) {
                            int index = -1;
                            for (int i = 0; i < currentList.size(); i++) {
                                if (currentList.get(i).getId().equals(reservation.getId())) {
                                    index = i;
                                    break;
                                }
                            }
                            if (index != -1) {
                                currentList.set(index, reservation);
                                reservations.setValue(currentList);
                            }
                        }
                    })
                    .addOnFailureListener(e ->
                            Log.e("DiningViewModel", "Failed to update reservation", e));
        }
    }
}