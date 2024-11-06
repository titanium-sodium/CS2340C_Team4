package com.example.sprintproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.sprintproject.model.AccommodationsModel;
import com.example.sprintproject.model.AccommodationsDBModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccommodationsViewModel {
    private DatabaseReference AccommodationsDB;
    private MutableLiveData<List<AccommodationsModel>> reservationsLiveData;

    public AccommodationsViewModel(String userId) {
        AccommodationsDB = AccommodationsDBModel.getInstance(userId);
        reservationsLiveData = new MutableLiveData<List<AccommodationsModel>>(new ArrayList<>());
        setupDatabaseListener();
    }

    private void setupDatabaseListener() {
        AccommodationsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<AccommodationsModel> reservations = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AccommodationsModel reservation = snapshot.getValue(AccommodationsModel.class);
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

    public void addAccommodations(AccommodationsModel reservationModel) {
        String reservationId = UUID.randomUUID().toString();
        AccommodationsDB.child(reservationId).setValue(reservationModel)
                .addOnSuccessListener(aVoid -> {
                    // Reservation added successfully
                    // The ValueEventListener will automatically update the LiveData
                })
                .addOnFailureListener(e -> {
                    // Handle the error here
                    System.out.println("Error adding reservation: " + e.getMessage());
                });
    }

    public LiveData<List<AccommodationsModel>> getAccommodations() {
        return reservationsLiveData;
    }
}