package com.example.sprintproject.model;

import java.util.concurrent.TimeUnit;

public class AccommodationsModel {
    private String id;
    private String checkInDate;
    private String checkOutDate;
    private int numberOfRooms;
    private String roomType;
    private String location;
    private String website;
    private Long duration;

    public AccommodationsModel(String checkInDate, String checkOutDate, int numberOfRooms, String roomType, String location)
            throws IllegalArgumentException {
        if (location == null || location.isEmpty()) {
            throw new IllegalArgumentException("Invalid location");
        }
        if (checkInDate == null || checkInDate.isEmpty() || checkOutDate == null || checkOutDate.isEmpty()) {
            throw new IllegalArgumentException("Dates cannot be null or empty");
        }

        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfRooms = numberOfRooms;
        this.roomType = roomType;
        this.location = location;
        calculateDuration();
    }

    public AccommodationsModel() {
        // Default constructor needed for Firebase
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCheckInDate() {
        return checkInDate;
    }


    public String getCheckOutDate() {
        return checkOutDate;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String URL) {
        this.website = URL;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getDuration() {
        return duration;
    }

    private void calculateDuration() {
        if (checkInDate == null || checkOutDate == null) {
            duration = 0L;
            return;
        }

        duration = TimeUnit.MILLISECONDS.toDays(Long.parseLong(checkOutDate)
                - Long.parseLong(checkInDate));
        if (duration < 0) {
            duration = 0L;
        }
    }
}