package com.example.sprintproject.model;

public class DiningReservation {
    private String id;
    private String tripId;
    private String userId;
    private String website;
    private String location;
    private String time;
    private String date;

    // Default constructor required for Firebase
    public DiningReservation() {}

    public DiningReservation(String userId, String tripId, String website, String location, String time) {
        this.userId = userId;
        this.tripId = tripId;
        this.website = website;
        this.location = location;
        this.time = time;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}