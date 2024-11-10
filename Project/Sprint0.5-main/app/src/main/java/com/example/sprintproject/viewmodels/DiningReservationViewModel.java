package com.example.sprintproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.sprintproject.model.DiningDBModel;
import com.example.sprintproject.model.DiningReservation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DiningReservationViewModel {
    private DatabaseReference diningDB;
    private MutableLiveData<List<DiningReservation>> reservationsLiveData;
    private boolean isAscending = true;
    private String currentSortField = "date"; // default sort field

    public DiningReservationViewModel(String userId) {
        diningDB = DiningDBModel.getInstance(userId);
        reservationsLiveData = new MutableLiveData<>(new ArrayList<>());
        setupDatabaseListener();
    }

    public void setSortOrder(boolean ascending, String sortField) {
        isAscending = ascending;
        currentSortField = sortField;
        setupDatabaseListener(); // Reload with new sort order
    }

    private void setupDatabaseListener() {
        // Create query based on sort order
        Query query = isAscending
                ? diningDB.orderByChild(currentSortField)
                : diningDB.orderByChild(currentSortField).limitToLast(1000); // Reverse order trick

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<DiningReservation> reservations = new ArrayList<>();

                // If descending order, we need to add items at the beginning of the list
                if (!isAscending) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DiningReservation reservation = snapshot.getValue(DiningReservation.class);
                        if (reservation != null) {
                            reservations.add(0, reservation); // Add at beginning for descending
                        }
                    }
                } else {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DiningReservation reservation = snapshot.getValue(DiningReservation.class);
                        if (reservation != null) {
                            reservations.add(reservation); // Add at end for ascending
                        }
                    }
                }
                reservationsLiveData.setValue(reservations);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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