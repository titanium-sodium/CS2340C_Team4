package com.example.sprintproject.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.sprintproject.model.AccommodationsModel;
import com.example.sprintproject.model.AccommodationsDBModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AccommodationsViewModel {
    private DatabaseReference AccommodationsDB;
    private MutableLiveData<List<AccommodationsModel>> accomodationsLiveData;
    private boolean isAscending = true;
    private String currentSortField = "checkInDate"; // default sort field

    public AccommodationsViewModel(String userId) {
        AccommodationsDB = AccommodationsDBModel.getInstance(userId);
        accomodationsLiveData = new MutableLiveData<>(new ArrayList<>());
        setupDatabaseListener();
    }

    public void setSortOrder(boolean ascending, String sortField) {
        isAscending = ascending;
        currentSortField = sortField;
        setupDatabaseListener(); // Reload with new sort order
    }

    private void setupDatabaseListener() {
        // Create query based on sort order
        Query query = isAscending ?
                AccommodationsDB.orderByChild(currentSortField) :
                AccommodationsDB.orderByChild(currentSortField).limitToLast(1000); // Reverse order trick

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<AccommodationsModel> accommodations = new ArrayList<>();

                // If descending order, we need to add items at the beginning of the list
                if (!isAscending) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AccommodationsModel accommodation = snapshot.getValue(AccommodationsModel.class);
                        if (accommodation != null) {
                            accommodations.add(0, accommodation); // Add at beginning for descending
                        }
                    }
                } else {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AccommodationsModel accommodation = snapshot.getValue(AccommodationsModel.class);
                        if (accommodation != null) {
                            accommodations.add(accommodation); // Add at end for ascending
                        }
                    }
                }
                accomodationsLiveData.setValue(accommodations);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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