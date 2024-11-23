package com.example.sprintproject.model;

import androidx.annotation.NonNull;

public class NotesModel {
    private String notes;
    private String userId;
    private String userEmail;
    private long timestamp;
    private String tripId;
    private String location;

    public NotesModel(String notes) throws IllegalArgumentException {
        if (notes == null || notes.trim().isEmpty()) {
            throw new IllegalArgumentException("Note cannot be empty");
        }
        this.notes = notes.trim();
        this.timestamp = System.currentTimeMillis();
    }

    // Constructor with all fields
    public NotesModel(String notes, String userId, String userEmail,
                      String tripId, String location) {
        this(notes); // Call the basic constructor for validation
        this.userId = userId;
        this.userEmail = userEmail;
        this.tripId = tripId;
        this.location = location;
    }

    public NotesModel() {
        // Empty constructor required for Firebase
    }

    // Original getters and setters
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        if (notes == null || notes.trim().isEmpty()) {
            throw new IllegalArgumentException("Note cannot be empty");
        }
        this.notes = notes.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // New getters and setters for added fields
    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @NonNull
    @Override
    public String toString() {
        return "NotesModel{"
                + "notes='" + notes + '\''
                + ", userId='" + userId + '\''
                + ", userEmail='" + userEmail + '\''
                + ", timestamp=" + timestamp
                + ", tripId='" + tripId + '\''
                + ", location='" + location + '\''
                +
                '}';
    }

    /**
     * Creates a copy of the note with a new timestamp
     * Useful for updating notes while maintaining history
     * @return NotesModel
     * @param newNotes notes
     */
    public NotesModel createUpdatedCopy(String newNotes) {
        NotesModel updatedNote = new NotesModel(newNotes);
        updatedNote.setUserId(this.userId);
        updatedNote.setUserEmail(this.userEmail);
        updatedNote.setTripId(this.tripId);
        updatedNote.setLocation(this.location);
        return updatedNote;
    }

    /**
     * Validates if the note has all required fields set
     * @return true if the note is valid, false otherwise
     */
    public boolean isValid() {
        return notes != null && !notes.trim().isEmpty()
                && userId != null && !userId.isEmpty()
                && userEmail != null && !userEmail.isEmpty()
                && tripId != null && !tripId.isEmpty()
                && timestamp > 0;
    }

    /**
     * Builder pattern for creating notes with optional fields
     */
    public static class Builder {
        private final NotesModel note;

        public Builder(String noteText) {
            note = new NotesModel(noteText);
        }

        public Builder withUserId(String userId) {
            note.setUserId(userId);
            return this;
        }

        public Builder withUserEmail(String userEmail) {
            note.setUserEmail(userEmail);
            return this;
        }

        public Builder withTripId(String tripId) {
            note.setTripId(tripId);
            return this;
        }

        public Builder withLocation(String location) {
            note.setLocation(location);
            return this;
        }

        public NotesModel build() {
            if (!note.isValid()) {
                throw new IllegalStateException("Note missing required fields");
            }
            return note;
        }
    }
}