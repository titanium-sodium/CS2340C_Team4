package com.example.sprintproject.model;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DestinationModel {
    private String id;
    private long startDate;  // Changed to long
    private long endDate;    // Changed to long
    private String location;
    private ArrayList<String> notes;
    private ArrayList<String> contributors;
    private int duration;

    public DestinationModel(long startDate, long endDate, String location)
            throws IllegalArgumentException {
        // Updated constructor
        if (location.length() == 0) {
            throw new IllegalArgumentException("Invalid location");
        }
        if (startDate <= 0 || endDate <= 0) {
            throw new IllegalArgumentException("Invalid date");
        }
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.contributors = new ArrayList<>();
        this.notes = new ArrayList<>();
        calculateDuration();
    }
    public DestinationModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getStartDate() {  // Updated return type
        return startDate;
    }

    public void setStartDate(long startDate) {  // Updated parameter type
        this.startDate = startDate;
        calculateDuration();
    }

    public long getEndDate() {  // Updated return type
        return endDate;
    }

    public void setEndDate(long endDate) {  // Updated parameter type
        this.endDate = endDate;
        calculateDuration();
    }

    public int getDuration() {
        return duration;
    }

    public void calculateDuration() {
        duration = (int) TimeUnit.MILLISECONDS.toDays(endDate - startDate);
        if (duration < 0) {
            duration = 0;
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<String> getNote() {
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