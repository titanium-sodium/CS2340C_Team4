package com.example.sprintproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.sprintproject.model.DiningDBModel;
import com.example.sprintproject.model.DiningReservation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DiningReservationViewModel {
    private DatabaseReference diningDB;
    private MutableLiveData<List<DiningReservation>> reservationsLiveData;

    public DiningReservationViewModel(String userId) {
        diningDB = DiningDBModel.getInstance(userId);
        reservationsLiveData = new MutableLiveData<>(new ArrayList<>());
        setupDatabaseListener();
    }

    private void setupDatabaseListener() {
        diningDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<DiningReservation> reservations = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DiningReservation reservation = snapshot.getValue(DiningReservation.class);
                    if (reservation != null) {
                        reservations.add(reservation);
                    }
                }
                reservationsLiveData.setValue(reservations);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
                System.out.println("Database Error: " + databaseError.getMessage());
            }
        });
    }

    public void addReservation(DiningReservation reservationModel) {
        String reservationId = UUID.randomUUID().toString();
        diningDB.child(reservationId).setValue(reservationModel)
                .addOnSuccessListener(aVoid -> {
                    // Reservation added successfully
                    // The ValueEventListener will automatically update the LiveData
                })
                .addOnFailureListener(e -> {
                    // Handle the error here
                    System.out.println("Error adding reservation: " + e.getMessage());
                });
    }

    public LiveData<List<DiningReservation>> getReservations() {
        return reservationsLiveData;
    }
}