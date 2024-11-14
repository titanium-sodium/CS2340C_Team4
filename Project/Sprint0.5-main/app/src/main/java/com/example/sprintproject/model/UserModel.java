package com.example.sprintproject.model;

import java.util.ArrayList;

public class UserModel {
    private String email;
    private String userId;
    private ArrayList<DestinationModel> destinations;
    private TravelStats travelStats;
    private ArrayList<String> tripIds; // Store references to trips

    public UserModel(String userId, String email) throws IllegalArgumentException {
        if (userId == null || userId.length() == 0) {
            throw new IllegalArgumentException("userId cannot be empty");
        }
        if (email == null || email.length() == 0) {
            throw new IllegalArgumentException("email cannot be empty");
        }
        this.email = email;
        this.userId = userId;
        this.destinations = new ArrayList<>();
        this.tripIds = new ArrayList<>();
        this.travelStats = new TravelStats(0, 0);
    }

    // Required empty constructor for Firebase
    public UserModel() {
        this.destinations = new ArrayList<>();
        this.tripIds = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<DestinationModel> getDestinations() {
        return destinations;
    }

    public void addDestination(DestinationModel destination) {
        destinations.add(destination);
    }

    public void removeDestination(DestinationModel destination) {
        destinations.remove(destination);
    }

    public ArrayList<String> getTripIds() {
        return tripIds;
    }

    public void setTripIds(ArrayList<String> tripIds) {
        this.tripIds = tripIds;
    }

    public void addTripId(String tripId) {
        if (tripIds == null) {
            tripIds = new ArrayList<>();
        }
        tripIds.add(tripId);
    }

    public void removeTripId(String tripId) {
        if (tripIds != null) {
            tripIds.remove(tripId);
        }
    }

    public TravelStats getTravelStats() {
        return travelStats;
    }

    public void setTravelStats(TravelStats travelStats) {
        this.travelStats = travelStats;
    }
}