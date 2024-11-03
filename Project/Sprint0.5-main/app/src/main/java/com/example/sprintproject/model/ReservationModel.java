package com.example.sprintproject.model;

public class ReservationModel {
    private String userId;
    private String website;
    private String location;
    private String time;
    public ReservationModel() {
        //Empty constructor
    }
    public ReservationModel(String userId, String website, String location, String time) {
        this.userId = userId;
        this.website = website;
        this.location = location;
        this.time = time;
    }
}
