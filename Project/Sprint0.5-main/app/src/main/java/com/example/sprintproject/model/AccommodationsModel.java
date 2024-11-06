package com.example.sprintproject.model;

import java.util.concurrent.TimeUnit;

public class AccommodationsModel {
    private String id;
    private Long checkInDate;
    private Long checkOutDate;
    private String numberOfRooms;
    private String roomType;
    private String location;
    private String website;
    private Long duration;

    public AccommodationsModel(Long checkInDate, Long checkOutDate, String numberOfRooms, String roomType, String location)
            throws IllegalArgumentException {
        if (location == null || location.length() == 0) {
            throw new IllegalArgumentException("Invalid location");
        }
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        if (checkInDate <= 0 || checkOutDate <= 0) {
            throw new IllegalArgumentException("Invalid date");
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

    public Long getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Long checkInDate) {
        this.checkInDate = checkInDate;
        calculateDuration();
    }

    public Long getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Long checkOutDate) {
        this.checkOutDate = checkOutDate;
        calculateDuration();
    }

    public int getNumberOfRooms() {
        return Integer.parseInt(numberOfRooms);
    }

    public void setNumberOfRooms(String numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
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

        duration = TimeUnit.MILLISECONDS.toDays(checkOutDate - checkInDate);
        if (duration < 0) {
            duration = 0L;
        }
    }
}