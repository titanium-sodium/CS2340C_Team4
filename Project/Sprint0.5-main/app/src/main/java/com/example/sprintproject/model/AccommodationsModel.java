package com.example.sprintproject.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AccommodationsModel {
    private String id;
    private String checkInDate;
    private String checkOutDate;
    private int numberOfRooms;
    private String roomType;
    private String location;
    private String website;
    private Long duration;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    public AccommodationsModel(String checkInDate, String checkOutDate,
                               int numberOfRooms, String roomType, String location)
            throws IllegalArgumentException {
        if (location == null || location.isEmpty()) {
            throw new IllegalArgumentException("Invalid location");
        }
        if (checkInDate == null || checkInDate.isEmpty() || checkOutDate == null
                || checkOutDate.isEmpty()) {
            throw new IllegalArgumentException("Dates cannot be null or empty");
        }

        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfRooms = numberOfRooms;
        this.roomType = roomType;
        this.location = location;
        calculateDuration();
    }

    public AccommodationsModel() {
        // Default constructor needed for Firebase
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getDuration() {
        return duration;
    }

    private void calculateDuration() {
        try {
            Date checkIn = sdf.parse(checkInDate);
            Date checkOut = sdf.parse(checkOutDate);
            if (checkIn != null && checkOut != null) {
                duration = TimeUnit.MILLISECONDS.toDays(checkOut.getTime() - checkIn.getTime()) + 1;
                if (duration < 0) {
                    duration = 0L;
                }
            } else {
                duration = 0L;
            }
        } catch (ParseException e) {
            duration = 0L;
        }
    }

    public static SimpleDateFormat getDateFormat() {
        return sdf;
    }

    public boolean isReservationPassed() {
        try {
            Date checkOut = sdf.parse(checkOutDate);
            Date currentDate = new Date();
            return checkOut != null && checkOut.before(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isCurrentReservation() {
        try {
            Date checkIn = sdf.parse(checkInDate);
            Date checkOut = sdf.parse(checkOutDate);
            Date currentDate = new Date();
            return checkIn != null && checkOut != null &&
                    !checkIn.after(currentDate) && !checkOut.before(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}