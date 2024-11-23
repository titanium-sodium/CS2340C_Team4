package com.example.sprintproject.model;

import androidx.annotation.NonNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DiningReservation {
    private static final String TAG = "DiningReservation";
    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);

    private String id;
    private String tripId;
    private String userId;
    private String website;
    private String location;
    private String time;
    private long reservationTimestamp;

    // Default constructor required for Firebase
    public DiningReservation() {

    }

    public DiningReservation(String userId, String tripId, String website,
                             String location, String time, long timestamp) {
        if (time.isEmpty()) {
            throw new IllegalArgumentException("Reservation must include a time.");
        }
        if (location.isEmpty()) {
            throw new IllegalArgumentException("Reservation must include a location.");
        }
        this.userId = userId;
        this.tripId = tripId;
        this.website = website;
        this.location = location;
        this.time = TIME_FORMAT.format(new Date(timestamp));
        this.reservationTimestamp = timestamp;
    }

    // Getters and setters
    public String getId() {
        return id; }
    public void setId(String id) {
        this.id = id; }

    public String getTripId() {
        return tripId; }
    public void setTripId(String tripId) {
        this.tripId = tripId; }

    public String getUserId() {
        return userId; }
    public void setUserId(String userId) {
        this.userId = userId; }

    public String getWebsite() {
        return website; }

    public String getLocation() {
        return location; }

    public String getTime() {
        return time; }
    public long getDate() {
        return reservationTimestamp;
    }

    public long getReservationTimestamp() {
        return reservationTimestamp; }

    public boolean isValid() {
        return tripId != null && !tripId.isEmpty()
                && userId != null && !userId.isEmpty()
                && location != null && !location.isEmpty()
                && time != null && !time.isEmpty()
                && website != null && !website.isEmpty();
    }

    public static String formatDateTime(Date date) {

        return TIME_FORMAT.format(date);
    }

    @NonNull
    @Override
    public String toString() {
        return "DiningReservation{"
                + "id='" + id + '\''
                + ", tripId='" + tripId + '\''
                + ", userId='" + userId + '\''
                + ", location='" + location + '\''
                + ", time='" + time + '\''
                + ", website='" + website + '\''
                + ", timestamp='" + reservationTimestamp + '\''
                +
                '}';
    }
}