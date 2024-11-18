package com.example.sprintproject.model;

import java.util.ArrayList;

public class UserModel {
    private String email;
    private String userId;
    private ArrayList<DestinationModel> destinations;
    private TravelStats travelStats;
    private ArrayList<String> tripIds;

    public UserModel(String userId, String email) throws IllegalArgumentException {
        validateUserData(userId, email);
        this.email = email;
        this.userId = userId;
        this.destinations = new ArrayList<>();
        this.tripIds = new ArrayList<>();
        this.travelStats = new TravelStats();
    }

    // Required empty constructor for Firebase
    public UserModel() {
        this.destinations = new ArrayList<>();
        this.tripIds = new ArrayList<>();
        this.travelStats = new TravelStats();
    }

    private void validateUserData(String userId, String email) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("userId cannot be empty");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("email cannot be empty");
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws IllegalArgumentException {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("email cannot be empty");
        }
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) throws IllegalArgumentException {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("userId cannot be empty");
        }
        this.userId = userId;
    }

    public ArrayList<DestinationModel> getDestinations() {
        return destinations;
    }

    public void addDestination(DestinationModel destination) throws IllegalArgumentException {
        if (destination == null) {
            throw new IllegalArgumentException("destination cannot be null");
        }
        destinations.add(destination);
    }

    public void removeDestination(DestinationModel destination) throws IllegalArgumentException {
        if (destination == null) {
            throw new IllegalArgumentException("destination cannot be null");
        }
        destinations.remove(destination);
    }

    public ArrayList<String> getTripIds() {
        return tripIds;
    }

    public void setTripIds(ArrayList<String> tripIds) throws IllegalArgumentException {
        if (tripIds == null) {
            throw new IllegalArgumentException("tripIds cannot be null");
        }
        this.tripIds = tripIds;
    }

    public void addTripId(String tripId) throws IllegalArgumentException {
        if (tripId == null || tripId.isEmpty()) {
            throw new IllegalArgumentException("tripId cannot be empty");
        }
        if (tripIds == null) {
            tripIds = new ArrayList<>();
        }
        tripIds.add(tripId);
    }

    public void removeTripId(String tripId) throws IllegalArgumentException {
        if (tripId == null || tripId.isEmpty()) {
            throw new IllegalArgumentException("tripId cannot be empty");
        }
        if (tripIds != null) {
            tripIds.remove(tripId);
        }
    }

    public TravelStats getTravelStats() {
        return travelStats;
    }

    public void setTravelStats(TravelStats travelStats) throws IllegalArgumentException {
        if (travelStats == null) {
            throw new IllegalArgumentException("travelStats cannot be null");
        }
        this.travelStats = travelStats;
    }
}