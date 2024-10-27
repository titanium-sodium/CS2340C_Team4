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

    private DestinationsRepository() {
        databaseRef = FirebaseDatabase.getInstance().getReference(DESTINATIONS_PATH);
    }

    public static synchronized DestinationsRepository getInstance() {
        if (instance == null) {
            instance = new DestinationsRepository();
        }
        return instance;
    }

    public Task<String> addDestination(DestinationModel destination, String userId) {
        String key = databaseRef.child(userId).push().getKey();
        Map<String, Object> destinationValues = new HashMap<>();
        destinationValues.put("location", destination.getLocation());
        destinationValues.put("startDate", destination.getStartDate());
        destinationValues.put("endDate", destination.getEndDate());
        destinationValues.put("duration", destination.getDuration());

        return databaseRef.child(userId).child(key).setValue(destinationValues)
                .continueWith(task -> key);
    }

    private Map<String, Object> destinationToMap(DestinationModel destination) {
        Map<String, Object> result = new HashMap<>();
        result.put("location", destination.getLocation());
        result.put("startDate", destination.getStartDate());
        result.put("endDate", destination.getEndDate());
        result.put("duration", destination.getDuration());
        result.put("notes", destination.getNote());
        result.put("contributors", destination.getContributors());
        return result;
    }

    public Task<Void> updateDestination(DestinationModel destination, String userId) {
        Map<String, Object> destinationValues = new HashMap<>();
        destinationValues.put("location", destination.getLocation());
        destinationValues.put("startDate", destination.getStartDate());
        destinationValues.put("endDate", destination.getEndDate());
        destinationValues.put("duration", destination.getDuration());  // Changed from calculateDuration() to getDuration()

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