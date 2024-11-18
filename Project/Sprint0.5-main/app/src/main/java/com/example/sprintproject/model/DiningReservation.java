package com.example.sprintproject.model;

import androidx.annotation.NonNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DiningReservation {
    private String id;
    private String tripId;
    private String userId;
    private String website;
    private String location;
    private String time;
    private long reservationTime; // Unix timestamp for sorting

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);

    // Default constructor required for Firebase
    public DiningReservation() {}

    public DiningReservation(String userId, String tripId, String website,
                             String location, String time) {
        this.userId = userId;
        this.tripId = tripId;
        this.website = website;
        this.location = location;
        setTime(time);
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getTime() { return time; }
    public void setTime(String time) {
        this.time = time;
        updateReservationTimestamp();
    }

    public long getReservationTime() { return reservationTime; }

    private void updateReservationTimestamp() {
        if (time != null) {
            try {
                Date dateTime = TIME_FORMAT.parse(time);
                if (dateTime != null) {
                    this.reservationTime = dateTime.getTime();
                }
            } catch (ParseException e) {
                // If parsing fails, use current time as fallback
                this.reservationTime = System.currentTimeMillis();
            }
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "DiningReservation{" +
                "id='" + id + '\'' +
                ", tripId='" + tripId + '\'' +
                ", userId='" + userId + '\'' +
                ", location='" + location + '\'' +
                ", time='" + time + '\'' +
                ", website='" + website + '\'' +
                '}';
    }

    // Validate the reservation has all required fields
    public boolean isValid() {
        return id != null && !id.isEmpty() &&
                tripId != null && !tripId.isEmpty() &&
                userId != null && !userId.isEmpty() &&
                location != null && !location.isEmpty() &&
                time != null && !time.isEmpty() &&
                website != null && !website.isEmpty();
    }

    // Parse a time string to ensure it's in the correct format
    public static String formatTime(String inputTime) throws ParseException {
        Date time = TIME_FORMAT.parse(inputTime);
        return time != null ? TIME_FORMAT.format(time) : null;
    }
}