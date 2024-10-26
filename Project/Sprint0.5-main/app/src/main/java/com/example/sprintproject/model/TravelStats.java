package com.example.sprintproject.model;

public class TravelStats {
    private int allottedDays;
    private int plannedDays;

    public TravelStats() {
        this.allottedDays = 0;
        this.plannedDays = 0;
    }
    public TravelStats(int allottedDays, int plannedDays) {
        this.allottedDays = allottedDays;
        this.plannedDays = plannedDays;
    }

    public int getAllottedDays() {
        return allottedDays;
    }

    public int getPlannedDays() {
        return plannedDays;
    }

}