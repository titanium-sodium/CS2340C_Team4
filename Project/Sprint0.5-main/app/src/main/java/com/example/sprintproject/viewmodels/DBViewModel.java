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
                                    Object allottedValue = snapshot.
                                            child("allottedDays").getValue();
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
                                db.child("users").child(currentUserId).
                                        child("travelStats").setValue(newStats);
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
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
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

    public void addNote(String note) {
        if (currentUserId == null || note.isEmpty()) {
            return;
        }

        NotesModel newNote = new NotesModel(note);

        // First get existing notes
        db.child("users").child(currentUserId).child("notes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<NotesModel> notesList = new ArrayList<>();

                        // Add existing notes
                        for (DataSnapshot noteSnap : snapshot.getChildren()) {
                            NotesModel existingNote = noteSnap.getValue(NotesModel.class);
                            if (existingNote != null) {
                                notesList.add(existingNote);
                            }
                        }

                        // Add new note
                        notesList.add(newNote);

                        // Save entire list
                        db.child("users").child(currentUserId).child("notes")
                                .setValue(notesList).addOnSuccessListener(aVoid -> {
                                    loadNotes();
                                    // Share the updated notes with all contributors
                                    shareNotesWithContributors(notesList);
                                })
                                .addOnFailureListener(e -> Log.e(TAG, "Error adding note: "
                                        + e.getMessage()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error reading existing notes: " + error.getMessage());
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
                                                        + contributorId + ": " + e.getMessage()));
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
}