package com.example.sprintproject.model;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class LodgingModel {
    private String id;
    private long checkInDate;
    private long checkOutDate;
    private int numberOfRooms;
    private String roomType;
    private String location;
    private ArrayList<String> notes;
    private ArrayList<String> contributors;
    private int duration;

    public LodgingModel(long checkInDate, long checkOutDate, int numberOfRooms, String roomType, String location)
            throws IllegalArgumentException {
        if (location.length() == 0) {
            throw new IllegalArgumentException("Invalid location");
        }
        if (checkInDate <= 0 || checkOutDate <= 0) {
            throw new IllegalArgumentException("Invalid date");
        }
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfRooms = numberOfRooms;
        this.roomType = roomType;
        this.location = location;
        this.notes = new ArrayList<>();
        this.contributors = new ArrayList<>();
        calculateDuration();
    }

    public LodgingModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(long checkInDate) {
        this.checkInDate = checkInDate;
        calculateDuration();
    }

    public long getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(long checkOutDate) {
        this.checkOutDate = checkOutDate;
        calculateDuration();
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
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
        duration = (int) TimeUnit.MILLISECONDS.toDays(checkOutDate - checkInDate);
        if (duration < 0) {
            duration = 0;
        }
    }

    public ArrayList<String> getNotes() {
        return notes;
    }

    public void addNote(String note) {
        this.notes.add(note);
    }

    public ArrayList<String> getContributors() {
        return contributors;
    }

    public void setContributors(ArrayList<String> contributors) {
        this.contributors = contributors;
    }

    public void addContributor(String contributorId) {
        if (contributors == null) {
            contributors = new ArrayList<>();
        }
        if (!contributors.contains(contributorId)) {
            contributors.add(contributorId);
        }
    }
}
