package com.example.sprintproject.viewmodels;

import android.util.Log;

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
import java.util.Objects;
import java.util.UUID;

public class AccommodationsViewModel {
    private DatabaseReference AccommodationsDB;
    private MutableLiveData<List<AccommodationsModel>> accomodationsLiveData;

    public AccommodationsViewModel(String userId) {
        AccommodationsDB = AccommodationsDBModel.getInstance(userId);
        accomodationsLiveData = new MutableLiveData<List<AccommodationsModel>>(new ArrayList<>());
        setupDatabaseListener();
    }

    private void setupDatabaseListener() {
        AccommodationsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<AccommodationsModel> accomodations = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AccommodationsModel reservation = snapshot.getValue(AccommodationsModel.class);
                    if (reservation != null) {
                        accomodations.add(reservation);
                    }
                }
                accomodationsLiveData.setValue(accomodations);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
                System.out.println("Database Error: " + databaseError.getMessage());
            }
        });
    }

    public void addAccommodations(AccommodationsModel accommodationsModel) {
        String reservationId = UUID.randomUUID().toString();
        Log.d ("MODEL",String.valueOf(accommodationsModel.getNumberOfRooms()));
        AccommodationsDB.child(reservationId).setValue(accommodationsModel)
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
        return accomodationsLiveData;
    }
}