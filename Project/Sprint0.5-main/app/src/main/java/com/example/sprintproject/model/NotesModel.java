package com.example.sprintproject.model;

public class NotesModel {
    private String notes;
    private String userId;
    private String userEmail;
    private long timestamp;

    // Default constructor required for Firebase
    public NotesModel() {
        this.timestamp = System.currentTimeMillis();
    }

    public NotesModel(String notes) {
        this.notes = notes;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and setters
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
}