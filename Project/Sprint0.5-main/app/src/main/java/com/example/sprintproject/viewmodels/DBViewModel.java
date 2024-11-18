package com.example.sprintproject.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprintproject.model.AuthModel;
import com.example.sprintproject.model.DBModel;
import com.example.sprintproject.model.TravelStats;
import com.example.sprintproject.model.UserModel;
import com.example.sprintproject.model.NotesModel;
import com.example.sprintproject.views.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class DBViewModel extends ViewModel {
    private static DBViewModel instance;
    private final DatabaseReference db;
    private final MutableLiveData<TravelStats> travelStatsLiveData;
    private final MutableLiveData<List<UserModel>> contributorsLiveData;
    private final MutableLiveData<List<NotesModel>> notesLiveData;
    private static final String TAG = "DBViewModel";
    private String currentUserId;
    private String currentTripId;

    public DBViewModel() {
        db = DBModel.getInstance();
        travelStatsLiveData = new MutableLiveData<>();
        contributorsLiveData = new MutableLiveData<>();
        notesLiveData = new MutableLiveData<>();

        String mainActivityUserId = MainActivity.getUserId();
        if (mainActivityUserId != null) {
            setCurrentUserId(mainActivityUserId);
            Log.d(TAG, "Initialized DBViewModel with userId from MainActivity: "
                    + mainActivityUserId);
        } else {
            Log.d(TAG, "No userId available from MainActivity during DBViewModel initialization");
        }
    }

    public DatabaseReference getDB() {
        return db;
    }

    public void setCurrentUserId(String userId) {
        if (userId == null) {
            Log.e(TAG, "Attempted to set null userId");
            return;
        }

        if (userId.equals(this.currentUserId)) {
            Log.d(TAG, "UserId unchanged, skipping reload");
            return;
        }

        Log.d(TAG, "Setting currentUserId: " + userId);
        this.currentUserId = userId;

        loadTravelStats();
        loadContributors();
        loadNotes();
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    private void ensureUserIdSet() {
        if (currentUserId == null) {
            String mainActivityUserId = MainActivity.getUserId();
            if (mainActivityUserId != null) {
                setCurrentUserId(mainActivityUserId);
            } else {
                FirebaseAuth auth = AuthModel.getInstance();
                if (auth.getCurrentUser() != null) {
                    setCurrentUserId(auth.getCurrentUser().getUid());
                }
            }
        }
    }

    public LiveData<TravelStats> getTravelStats() {
        ensureUserIdSet();
        if (travelStatsLiveData.getValue() == null && currentUserId != null) {
            loadTravelStats();
        }
        return travelStatsLiveData;
    }

    private void loadTravelStats() {
        if (currentUserId == null || db == null) {
            Log.e(TAG, "Cannot load travel stats: missing requirements");
            travelStatsLiveData.setValue(new TravelStats());
            return;
        }

        db.child("users").child(currentUserId).child("travelStats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            int allottedDays = 0;
                            int plannedDays = 0;

                            if (snapshot.exists()) {
                                if (snapshot.hasChild("allottedDays")) {
                                    Object allottedValue = snapshot.child("allottedDays").
                                            getValue();
                                    if (allottedValue != null) {
                                        if (allottedValue instanceof Long) {
                                            allottedDays = ((Long) allottedValue).intValue();
                                        } else if (allottedValue instanceof Integer) {
                                            allottedDays = (Integer) allottedValue;
                                        } else if (allottedValue instanceof String) {
                                            allottedDays = Integer.parseInt((String) allottedValue);
                                        }
                                    }
                                }

                                if (snapshot.hasChild("plannedDays")) {
                                    Object plannedValue = snapshot.child("plannedDays").getValue();
                                    if (plannedValue != null) {
                                        if (plannedValue instanceof Long) {
                                            plannedDays = ((Long) plannedValue).intValue();
                                        } else if (plannedValue instanceof Integer) {
                                            plannedDays = (Integer) plannedValue;
                                        } else if (plannedValue instanceof String) {
                                            plannedDays = Integer.parseInt((String) plannedValue);
                                        }
                                    }
                                }
                            } else {
                                TravelStats newStats = new TravelStats();
                                db.child("users").child(currentUserId).child("travelStats").
                                        setValue(newStats);
                            }

                            TravelStats stats = new TravelStats();
                            stats.setAllottedDays(allottedDays);
                            stats.setPlannedDays(plannedDays);
                            travelStatsLiveData.setValue(stats);

                        } catch (Exception e) {
                            Log.e(TAG, "Error calculating travel stats", e);
                            travelStatsLiveData.setValue(new TravelStats());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error loading travel stats: " + error.getMessage());
                        travelStatsLiveData.setValue(new TravelStats());
                    }
                });
    }

    public LiveData<List<UserModel>> getContributors() {
        if (contributorsLiveData.getValue() == null) {
            loadContributors();
        }
        return contributorsLiveData;
    }

    private void loadContributors() {
        if (currentUserId == null) {
            Log.e(TAG, "Cannot load contributors: userId is null");
            contributorsLiveData.setValue(new ArrayList<>());
            return;
        }

        db.child("users").child(currentUserId).child("contributors")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<UserModel> contributors = new ArrayList<>();
                        for (DataSnapshot contributorSnapshot : snapshot.getChildren()) {
                            String contributorId = contributorSnapshot.getKey();
                            String contributorEmail = contributorSnapshot.child("email").
                                    getValue(String.class);

                            if (contributorId != null && contributorEmail != null) {
                                UserModel contributor = new UserModel();
                                contributor.setUserId(contributorId);
                                contributor.setEmail(contributorEmail);
                                contributors.add(contributor);
                            }
                        }
                        contributorsLiveData.setValue(contributors);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Error loading contributors: " + error.getMessage());
                        contributorsLiveData.setValue(new ArrayList<>());
                    }
                });
    }

    public LiveData<List<NotesModel>> getNotes() {
        if (notesLiveData.getValue() == null) {
            loadNotes();
        }
        return notesLiveData;
    }

    private void loadNotes() {
        if (currentUserId == null) {
            Log.e(TAG, "Cannot load notes: userId is null");
            notesLiveData.setValue(new ArrayList<>());
            return;
        }

        db.child("users").child(currentUserId).child("notes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<NotesModel> notes = new ArrayList<>();
                        for (DataSnapshot noteSnapshot : snapshot.getChildren()) {
                            try {
                                NotesModel note = noteSnapshot.getValue(NotesModel.class);
                                if (note != null) {
                                    notes.add(note);
                                }
                            } catch (DatabaseException e) {
                                Log.e(TAG, "Error parsing note: " + e.getMessage());
                            }
                        }
                        notesLiveData.setValue(notes);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Error loading notes: " + error.getMessage());
                        notesLiveData.setValue(new ArrayList<>());
                    }
                });
    }

    private void shareNotesWithContributors(List<NotesModel> notes) {
        // Get current contributors
        db.child("users").child(currentUserId).child("contributors")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot contributorSnapshot : snapshot.getChildren()) {
                            String contributorId = contributorSnapshot.getKey();
                            if (contributorId != null) {
                                // Update notes for each contributor
                                db.child("users").child(contributorId)
                                        .child("notes")
                                        .setValue(notes)
                                        .addOnFailureListener(e -> Log.e(TAG,
                                                "Error sharing notes with contributor "
                                                        + contributorId
                                                        + ": " + e.getMessage()));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error getting contributors for note sharing: "
                                + error.getMessage());
                    }
                });
    }

    public void setCurrentTripId(String tripId) {
        if (tripId == null) {
            Log.e(TAG, "Attempted to set null tripId");
            return;
        }

        if (tripId.equals(this.currentTripId)) {
            Log.d(TAG, "TripId unchanged, skipping reload");
            return;
        }

        Log.d(TAG, "Setting currentTripId: " + tripId);
        this.currentTripId = tripId;

        // Reload all trip-specific data
        loadTripData();
    }

    public String getCurrentTripId() {
        return currentTripId;
    }

    private void ensureTripIdSet() {
        if (currentTripId == null) {
            String mainActivityTripId = MainActivity.getTripId();
            if (mainActivityTripId != null) {
                setCurrentTripId(mainActivityTripId);
            } else {
                Log.e(TAG, "No tripId available");
            }
        }
    }

    private void loadTripData() {
        if (currentTripId == null || currentUserId == null) {
            Log.e(TAG, "Cannot load trip data: missing requirements");
            return;
        }

        // Load trip-specific data
        loadTripContributors();
        loadTripNotes();
        loadTripStats();
    }

    private void loadTripContributors() {
        DBModel.getTripReference().child(currentTripId).child("contributors")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<UserModel> contributors = new ArrayList<>();
                        for (DataSnapshot contributorSnapshot : snapshot.getChildren()) {
                            String contributorId = contributorSnapshot.getKey();
                            if (contributorId != null) {
                                // Get user details from users collection
                                DBModel.getUsersReference().child(contributorId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot userDetails) {
                                                UserModel contributor = userDetails.getValue(UserModel.class);
                                                if (contributor != null) {
                                                    contributors.add(contributor);
                                                    contributorsLiveData.setValue(contributors);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.e(TAG, "Error loading contributor details", error.toException());
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error loading trip contributors", error.toException());
                        contributorsLiveData.setValue(new ArrayList<>());
                    }
                });
    }

    private void loadTripStats() {
        DBModel.getTripReference().child(currentTripId).child("travelStats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            TravelStats stats = snapshot.getValue(TravelStats.class);
                            if (stats == null) {
                                stats = new TravelStats();
                            }
                            travelStatsLiveData.setValue(stats);
                        } catch (DatabaseException e) {
                            Log.e(TAG, "Error parsing travel stats", e);
                            travelStatsLiveData.setValue(new TravelStats());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error loading trip stats", error.toException());
                        travelStatsLiveData.setValue(new TravelStats());
                    }
                });
    }

    /**
     * Adds a note to the current trip
     * @param note The note text to add
     */
    public void addNote(String note) {
        if (currentUserId == null || currentTripId == null || note == null || note.trim().isEmpty()) {
            Log.e(TAG, "Cannot add note: missing requirements");
            return;
        }

        try {
            NotesModel newNote = new NotesModel.Builder(note)
                    .withUserId(currentUserId)
                    .withTripId(currentTripId)
                    .withUserEmail(AuthModel.getInstance().getCurrentUser().getEmail())
                    .build();

            DatabaseReference notesRef = DBModel.getTripReference()
                    .child(currentTripId)
                    .child("notes");

            // Generate a new note ID
            String noteId = notesRef.push().getKey();
            if (noteId == null) {
                Log.e(TAG, "Failed to generate note ID");
                return;
            }

            // Add the note to the trip's notes collection
            notesRef.child(noteId)
                    .setValue(newNote)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Note added successfully");
                        loadTripNotes();  // Reload notes to refresh the UI
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error adding note", e));

        } catch (Exception e) {
            Log.e(TAG, "Error creating note", e);
        }
    }

    private void loadTripNotes() {
        if (currentTripId == null) {
            Log.e(TAG, "Cannot load notes: tripId is null");
            notesLiveData.setValue(new ArrayList<>());
            return;
        }

        DBModel.getTripReference()
                .child(currentTripId)
                .child("notes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<NotesModel> notes = new ArrayList<>();
                        for (DataSnapshot noteSnapshot : snapshot.getChildren()) {
                            try {
                                NotesModel note = noteSnapshot.getValue(NotesModel.class);
                                if (note != null) {
                                    notes.add(note);
                                }
                            } catch (DatabaseException e) {
                                Log.e(TAG, "Error parsing note", e);
                            }
                        }
                        // Sort notes by timestamp (newest first)
                        notes.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                        notesLiveData.setValue(notes);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error loading notes", error.toException());
                        notesLiveData.setValue(new ArrayList<>());
                    }
                });
    }
}