package com.example.sprintproject.viewmodels;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sprintproject.model.AccommodationsDBModel;
import com.example.sprintproject.model.AccommodationsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import com.example.sprintproject.model.AccommodationsFilterModel;

public class AccommodationsViewModel {
    private DatabaseReference accommodationsDB;
    private MutableLiveData<List<AccommodationsModel>> accommodationsLiveData;
    private boolean isAscending = true;
    private String currentSortField = "checkInDate"; // default sort field
    private String currentTripId;

    public AccommodationsViewModel(String userId, String tripId) {
        this.currentTripId = tripId;
        accommodationsDB = AccommodationsDBModel.getInstance(tripId);
        accommodationsLiveData = new MutableLiveData<>(new ArrayList<>());
        setupDatabaseListener();
    }

    public void setTripId(String tripId) {
        this.currentTripId = tripId;
        // Update the database reference for the new trip
        accommodationsDB = AccommodationsDBModel.getInstance(tripId);
        setupDatabaseListener(); // Reload data for new trip
    }

    public void sortAccommodations(boolean ascending) {
        List<AccommodationsModel> currentList = accommodationsLiveData.getValue();
        if (currentList != null) {
            Collections.sort(currentList, getComparator(ascending));
            accommodationsLiveData.setValue(currentList);
        }
    }

    private Comparator<AccommodationsModel> getComparator(boolean ascending) {
        Comparator<AccommodationsModel> comparator = (a1, a2) -> {
            switch (currentSortField) {
            case "checkInDate":
                return AccommodationsFilterModel.compareAccommodationDates(
                        a1.getCheckInDate(),
                        a2.getCheckInDate()
                );
            case "checkOutDate":
                return AccommodationsFilterModel.compareAccommodationDates(
                        a1.getCheckOutDate(),
                        a2.getCheckOutDate()
                );
            case "duration":
                return Long.compare(a1.getDuration(), a2.getDuration());
            default:
                return AccommodationsFilterModel.compareAccommodationDates(
                        a1.getCheckInDate(),
                        a2.getCheckInDate()
                );
            }
        };

        return ascending ? comparator : comparator.reversed();
    }

    public void setSortOrder(boolean ascending, String sortField) {
        this.isAscending = ascending;
        this.currentSortField = sortField;
    }

    private void setupDatabaseListener() {
        // Create query based on sort order
        Query query = isAscending
                ? accommodationsDB.orderByChild(currentSortField)
                : accommodationsDB.orderByChild(currentSortField).limitToLast(1000);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<AccommodationsModel> accommodations = new ArrayList<>();

                // If descending order, we need to add items at the beginning of the list
                if (!isAscending) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AccommodationsModel accommodation =
                                snapshot.getValue(AccommodationsModel.class);
                        if (accommodation != null) {
                            accommodation.setId(snapshot.getKey()); // Save the Firebase key as ID
                            accommodations.add(0, accommodation);
                            // Add at beginning for descending
                        }
                    }
                } else {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AccommodationsModel accommodation =
                                snapshot.getValue(AccommodationsModel.class);
                        if (accommodation != null) {
                            accommodation.setId(snapshot.getKey());
                            accommodations.add(accommodation);
                        }
                    }
                }
                accommodationsLiveData.setValue(accommodations);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("AccommodationsViewModel", "Database Error: " + databaseError.getMessage());
            }
        });
    }

    public void addAccommodations(AccommodationsModel accommodationsModel) {
        String accommodationId = UUID.randomUUID().toString();
        Log.d("AccommodationsViewModel", "Adding accommodation with rooms: "
                + accommodationsModel.getNumberOfRooms());

        accommodationsDB.child(accommodationId).setValue(accommodationsModel)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AccommodationsViewModel", "Accommodation added successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("AccommodationsViewModel", "Error adding accommodation: "
                            + e.getMessage());
                });
    }

    public LiveData<List<AccommodationsModel>> getAccommodations() {
        return accommodationsLiveData;
    }
}