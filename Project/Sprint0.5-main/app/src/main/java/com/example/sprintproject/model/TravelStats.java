package com.example.sprintproject.model;

public class TravelStats {
    private int allottedDays;
    private int plannedDays;
    private float plannedPercentage;
    private int remainingDays;

    public TravelStats() {
        // Required empty constructor for Firebase
    }

    public TravelStats(int allottedDays, int plannedDays) {
        this.allottedDays = allottedDays;
        this.plannedDays = plannedDays;
        if (allottedDays < plannedDays) {
            throw new
                    IllegalArgumentException("Cannot have more planned days than total trip days");
        }
    }

    // Getters
    public int getAllottedDays() {
        return allottedDays;
    }

    public int getPlannedDays() {
        return plannedDays;
    }

    public int getRemainingDays() {
        return remainingDays;
    }

    // Setters
    public void setAllottedDays(int allottedDays) {
        this.allottedDays = allottedDays;
    }

    public void setPlannedDays(int plannedDays) {
        this.plannedDays = plannedDays;
    }

    public void setPlannedPercentage(float plannedPercentage) {
        this.plannedPercentage = plannedPercentage;
    }

    public void setRemainingDays(int remainingDays) {
        this.remainingDays = remainingDays;
    }
}