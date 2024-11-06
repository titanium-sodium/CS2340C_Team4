package com.example.sprintproject.model;

import java.util.concurrent.TimeUnit;

public class AccomodationsModel {
    private String id;
    private String checkInDate;
    private String checkOutDate;
    private String numberOfRooms;
    private String roomType;
    private String location;
    private String website;
    private int duration;

    public AccomodationsModel(String checkInDate, String checkOutDate,
                              String numberOfRooms, String roomType, String location)
            throws IllegalArgumentException {
        if (location.isEmpty()) {
            throw new IllegalArgumentException("Invalid location");
        }
        if (Integer.parseInt(checkInDate) <= 0 || Integer.parseInt(checkOutDate) <= 0) {
            throw new IllegalArgumentException("Invalid date");
        }
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfRooms = numberOfRooms;
        this.roomType = roomType;
        this.location = location;
        calculateDuration();
    }

    public AccomodationsModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCheckInDate() {
        return Long.parseLong(checkInDate);
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
        calculateDuration();
    }

    public long getCheckOutDate() {
        return Long.parseLong(checkOutDate);
    }

    public void setCheckOutDate(String checkOutDate) {
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


    public void setWebsite(String url) {
        this.website = url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getDuration() {
        return duration;
    }

    private void calculateDuration() {
        duration = (int) TimeUnit.MILLISECONDS.
                toDays(Long.parseLong(checkOutDate) - Long.parseLong(checkInDate));
        if (duration < 0) {
            duration = 0;
        }
    }

    public String getWebsite() {
        return website;
    }
}
