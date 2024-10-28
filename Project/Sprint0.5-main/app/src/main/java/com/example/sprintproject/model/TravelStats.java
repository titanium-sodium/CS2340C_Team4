package com.example.sprintproject.model;

public class TravelStats {
    private int allottedDays;
    private int plannedDays;

    // Default constructor required for Firebase
    public TravelStats() {
        this.allottedDays = 0;
        this.plannedDays = 0;
    }

    public TravelStats(int allottedDays, int plannedDays) throws IllegalArgumentException {
        if (plannedDays > allottedDays) {
            throw new IllegalArgumentException(
                    "Cannot have more planned days than total trip days");
        }
        this.allottedDays = allottedDays;
        this.plannedDays = plannedDays;
    }

    public int getAllottedDays() {
        return allottedDays;
    }

    public void setAllottedDays(int allottedDays) {
        this.allottedDays = allottedDays;
    }

    public int getPlannedDays() {
        return plannedDays;
    }

    public void setPlannedDays(int plannedDays)  {
        this.plannedDays = plannedDays;
    }
}