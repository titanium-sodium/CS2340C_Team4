package com.example.sprintproject.model;


import com.example.sprintproject.viewmodels.DBViewModel;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;

public class UserModel {

    private String email;
    private String userId;
    private ArrayList<DestinationModel> destinations;
    private TravelStats travelStats;

    public UserModel(String userId, String email) {
        this.email = email;
        this.userId = userId;
        this.destinations = new ArrayList<>();
        this.travelStats = new TravelStats(0,0);
    }
    public UserModel() {}

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
    public ArrayList<DestinationModel> getDestinations() { return destinations; }
    public void addDestinations(DestinationModel destination) {
        destinations.add(destination);
    }
    public void removeDestination(DestinationModel destination) {
        destinations.remove(destination);
    }
    public TravelStats getTravelStats() {
        return travelStats;
    }
    public void setTravelStats(TravelStats travelStats) {
        this.travelStats = travelStats;
    }
}
