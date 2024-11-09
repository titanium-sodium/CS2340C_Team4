package com.example.sprintproject.model;

import java.util.ArrayList;

public class DiningReservation {
    private String userId;
    private String website;
    private String location;
    private String time;
    private long date;
    private ArrayList<String> notes;
    private ArrayList<String> contributors;

    public DiningReservation() {
        //Empty constructor
    }
    public DiningReservation(String userId, String website, String location, String time) throws IllegalArgumentException {
        if (time.isEmpty()) {
            throw new IllegalArgumentException("Reservation must include a time.");
        }
        if (location.isEmpty()) {
            throw new IllegalArgumentException("Reservation must include a location.");
        }
        this.userId = userId;
        this.website = website;
        this.location = location;
        this.time = time;
    }
    public DiningReservation(String userId, String website, String location, String time, long date, ArrayList<String> notes, ArrayList<String> contributors) {
        if (time.isEmpty()) {
            throw new IllegalArgumentException("Reservation must include a time.");
        }
        if (location.isEmpty()) {
            throw new IllegalArgumentException("Reservation must include a location.");
        }
        this.userId = userId;
        this.website = website;
        this.location = location;
        this.time = time;
        this.date = date;
        this.notes = notes;
        this.contributors = contributors;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public ArrayList<String> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<String> notes) {
        this.notes = notes;
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



