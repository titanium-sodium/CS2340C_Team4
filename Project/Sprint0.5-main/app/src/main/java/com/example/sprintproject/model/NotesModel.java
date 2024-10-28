package com.example.sprintproject.model;

public class NotesModel {
    private String notes;
    private String userId;
    private String userEmail;
    private long timestamp;


    public NotesModel(String notes) throws IllegalArgumentException {
        if (notes.length() == 0) {
            throw new IllegalArgumentException("Note cannot be empty");
        }
        this.notes = notes;
        this.timestamp = System.currentTimeMillis();
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