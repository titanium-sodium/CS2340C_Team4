package com.example.sprintproject.model;

public class VacationTime {
    private String userId;
    private int duration;
    private String startDate;
    private String endDate;

    public VacationTime(String userId, int duration, String startDate, String endDate) {
        this.userId = userId;
        this.duration = duration;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}
