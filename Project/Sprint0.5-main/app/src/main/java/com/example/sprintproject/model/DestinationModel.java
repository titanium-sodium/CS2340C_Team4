package com.example.sprintproject.model;

import com.example.sprintproject.viewmodels.DBViewModel;
import com.google.firebase.database.DatabaseReference;

import java.util.List;
import java.util.ArrayList;

public class DestinationModel {
    private String startDate;
    private String endDate;
    private String location;
    private String note;
    private ArrayList<String> contributors;

    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public DestinationModel() {
        this.contributors = new ArrayList<>();
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

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
}