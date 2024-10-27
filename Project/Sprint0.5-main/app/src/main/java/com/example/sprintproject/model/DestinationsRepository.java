package com.example.sprintproject.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DestinationsRepository {
    private static DestinationsRepository instance;
    private final DatabaseReference databaseRef;
    private static final String DESTINATIONS_PATH = "destinations";

    // Private constructor for Singleton pattern
    private DestinationsRepository() {
        databaseRef = FirebaseDatabase.getInstance().getReference(DESTINATIONS_PATH);
        addSampleDataIfNeeded();
    }

    // Singleton getInstance method
    public static synchronized DestinationsRepository getInstance() {
        if (instance == null) {
            instance = new DestinationsRepository();
        }
        return instance;
    }

    private void addSampleDataIfNeeded() {
        databaseRef.child("user1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Add sample data if no data exists
                    Map<String, Object> sample1 = new HashMap<>();
                    sample1.put("location", "Paris, France");
                    sample1.put("startDate", System.currentTimeMillis());
                    sample1.put("endDate", System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));
                    sample1.put("duration", 7);

                    Map<String, Object> sample2 = new HashMap<>();
                    sample2.put("location", "Tokyo, Japan");
                    sample2.put("startDate", System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000));
                    sample2.put("endDate", System.currentTimeMillis() + (40L * 24 * 60 * 60 * 1000));
                    sample2.put("duration", 10);

                    DatabaseReference userRef = databaseRef.child("user1");
                    userRef.push().setValue(sample1);
                    userRef.push().setValue(sample2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
            }
        });
    }

    // CRUD Operations

    public Task<String> addDestination(DestinationModel destination, String userId) {
        String key = databaseRef.child(userId).push().getKey();
        Map<String, Object> destinationValues = new HashMap<>();
        destinationValues.put("location", destination.getLocation());
        destinationValues.put("startDate", destination.getStartDate());
        destinationValues.put("endDate", destination.getEndDate());
        destinationValues.put("duration", destination.calculateDuration());

        return databaseRef.child(userId).child(key).setValue(destinationValues)
                .continueWith(task -> key);
    }

    public Task<List<DestinationModel>> getAllDestinations(String userId) {
        TaskCompletionSource<List<DestinationModel>> taskCompletionSource = new TaskCompletionSource<>();

        databaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<DestinationModel> destinations = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DestinationModel destination = new DestinationModel(
                            snapshot.child("location").getValue(String.class),
                            snapshot.child("startDate").getValue(Long.class),
                            snapshot.child("endDate").getValue(Long.class)
                    );
                    destination.setId(snapshot.getKey());
                    destinations.add(destination);
                }
                taskCompletionSource.setResult(destinations);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                taskCompletionSource.setException(databaseError.toException());
            }
        });

        return taskCompletionSource.getTask();
    }

    public Task<Void> updateDestination(DestinationModel destination, String userId) {
        Map<String, Object> destinationValues = new HashMap<>();
        destinationValues.put("location", destination.getLocation());
        destinationValues.put("startDate", destination.getStartDate());
        destinationValues.put("endDate", destination.getEndDate());
        destinationValues.put("duration", destination.calculateDuration());

        return databaseRef.child(userId).child(destination.getId()).updateChildren(destinationValues);
    }

    public Task<Void> deleteDestination(String destinationId, String userId) {
        return databaseRef.child(userId).child(destinationId).removeValue();
    }

    public Task<Integer> getTotalTripDays(String userId) {
        TaskCompletionSource<Integer> taskCompletionSource = new TaskCompletionSource<>();

        databaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalDays = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Integer duration = snapshot.child("duration").getValue(Integer.class);
                    if (duration != null) {
                        totalDays += duration;
                    }
                }
                taskCompletionSource.setResult(totalDays);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                taskCompletionSource.setException(databaseError.toException());
            }
        });

        return taskCompletionSource.getTask();
    }

    public Task<Boolean> hasDateOverlap(long startDate, long endDate, String userId, String excludeId) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();

        databaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean hasOverlap = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (excludeId != null && snapshot.getKey().equals(excludeId)) {
                        continue;
                    }

                    Long existingStart = snapshot.child("startDate").getValue(Long.class);
                    Long existingEnd = snapshot.child("endDate").getValue(Long.class);

                    if (existingStart != null && existingEnd != null) {
                        if ((startDate >= existingStart && startDate <= existingEnd) ||
                                (endDate >= existingStart && endDate <= existingEnd)) {
                            hasOverlap = true;
                            break;
                        }
                    }
                }
                taskCompletionSource.setResult(hasOverlap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                taskCompletionSource.setException(databaseError.toException());
            }
        });

        return taskCompletionSource.getTask();
    }
}