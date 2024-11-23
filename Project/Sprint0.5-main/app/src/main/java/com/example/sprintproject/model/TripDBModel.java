package com.example.sprintproject.model;

import com.google.firebase.database.DatabaseReference;

public class TripDBModel {
    private static TripDBModel realTimeDBInstance = null;
    private static DatabaseReference dbRef;

    private TripDBModel(String userId) {
        dbRef = DBModel.getInstance().child("users").child(userId).child("trips");
    }

    public static synchronized DatabaseReference getInstance(String userId) {
        if (realTimeDBInstance == null || !dbRef.getRef().toString().contains(userId)) {
            realTimeDBInstance = new TripDBModel(userId);
        }
        return dbRef;
    }

    public static DatabaseReference getTripReference(String tripId) {
        return DBModel.getInstance().child("trips").child(tripId);
    }

}
