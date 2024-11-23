package com.example.sprintproject.model;

import androidx.annotation.NonNull;
import android.util.Log;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DestinationModel {
    private static final String TAG = "DestinationModel";
    private static DatabaseReference database;

    // Model properties
    private String id;
    private String tripId;
    private long startDate;
    private long endDate;
    private String location;
    private int duration;

    // Database node names
    private static final String TRIPS_NODE = "trips";
    private static final String DESTINATIONS_NODE = "destinations";

    // Database operations
    private static DatabaseReference getDatabase() {
        if (database == null) {
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    // Constructor for new destinations
    public DestinationModel(long startDate, long endDate, String location)
            throws IllegalArgumentException {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid location");
        }
        if (startDate <= 0 || endDate <= 0) {
            throw new IllegalArgumentException("Invalid date");
        }
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        calculateDuration();
    }


    // Empty constructor for Firebase
    public DestinationModel() { }



    // Save destination to a specific trip
    public void saveToTrip(String tripId) {
        if (tripId == null || tripId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid trip ID");
        }

        this.tripId = tripId;

        // Create new destination under the trip's destinations node
        DatabaseReference destinationRef = getDatabase()
                .child(TRIPS_NODE)
                .child(tripId)
                .child(DESTINATIONS_NODE)
                .push();

        String destinationId = destinationRef.getKey();
        this.id = destinationId;

        // Save only the specified fields
        destinationRef.child("location").setValue(location);
        destinationRef.child("startDate").setValue(startDate);
        destinationRef.child("endDate").setValue(endDate);
        destinationRef.child("duration").setValue(duration);
    }


    // Load destinations for a specific trip
    public static void loadTripDestinations(String tripId, final DestinationLoadCallback callback) {
        getDatabase()
                .child(TRIPS_NODE)
                .child(tripId)
                .child(DESTINATIONS_NODE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<DestinationModel> destinations = new ArrayList<>();
                        for (DataSnapshot destinationSnapshot : snapshot.getChildren()) {
                            try {
                                if (isValidDestinationData(destinationSnapshot)) {
                                    DestinationModel destination = new DestinationModel();
                                    destination.setId(destinationSnapshot.getKey());
                                    destination.setTripId(tripId);
                                    destination.setLocation(destinationSnapshot
                                            .child("location").getValue(String.class));
                                    destination.setStartDate(destinationSnapshot.child(
                                            "startDate").getValue(Long.class));
                                    destination.setEndDate(destinationSnapshot.child(
                                            "endDate").getValue(Long.class));
                                    destination.calculateDuration();
                                    destinations.add(destination);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error converting destination: " + e.getMessage());
                            }
                        }
                        callback.onDestinationsLoaded(destinations);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    // Validate destination data before conversion
    private static boolean isValidDestinationData(DataSnapshot snapshot) {
        return snapshot.hasChild("location")
                && snapshot.hasChild("startDate")
                && snapshot.hasChild("endDate");
    }





    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
        calculateDuration();
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
        calculateDuration();
    }

    public int getDuration() {
        return duration;
    }

    public void calculateDuration() {
        duration = (int) TimeUnit.MILLISECONDS.toDays(endDate - startDate) + 1;
        if (duration < 0) {
            duration = 0;
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public interface DestinationLoadCallback {
        void onDestinationsLoaded(ArrayList<DestinationModel> destinations);
        void onError(String errorMessage);
    }



}