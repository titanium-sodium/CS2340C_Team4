// In models package
// CommunityPost.java
package com.example.sprintproject.model;

import java.util.List;
import java.util.Map;

public class CommunityPost {
    private String postId;
    private String tripId;
    private String userId;
    private String duration;
    private List<String> destinations;
    private Map<String, String> accommodations;  // destination -> accommodation
    private Map<String, String> diningReservations;  // destination -> dining
    private Map<String, String> transportation;  // destination -> transport
    private String notes;
    private String userReflections;

    // Empty constructor for Firebase
    public CommunityPost() {}

    public CommunityPost(String tripId, String userId, String duration) {
        this.tripId = tripId;
        this.userId = userId;
        this.duration = duration;
    }

    // Getters and setters
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public List<String> getDestinations() { return destinations; }
    public void setDestinations(List<String> destinations) { this.destinations = destinations; }

    public Map<String, String> getAccommodations() { return accommodations; }
    public void setAccommodations(Map<String, String> accommodations) {
        this.accommodations = accommodations;
    }

    public Map<String, String> getDiningReservations() { return diningReservations; }
    public void setDiningReservations(Map<String, String> diningReservations) {
        this.diningReservations = diningReservations;
    }

    public Map<String, String> getTransportation() { return transportation; }
    public void setTransportation(Map<String, String> transportation) {
        this.transportation = transportation;
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getUserReflections() { return userReflections; }
    public void setUserReflections(String userReflections) {
        this.userReflections = userReflections;
    }
}