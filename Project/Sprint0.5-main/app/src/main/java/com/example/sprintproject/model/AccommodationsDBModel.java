package com.example.sprintproject.model;

import com.google.firebase.database.DatabaseReference;

public class AccommodationsDBModel {
    private static AccommodationsDBModel realTimeDBInstance = null;
    private static DatabaseReference dbRef;

    private AccommodationsDBModel(String tripId) {
        dbRef = DBModel.getInstance().child("trips").child(tripId).child("accommodations");
    }

    public static synchronized DatabaseReference getInstance(String tripId) {
        if (realTimeDBInstance == null || !dbRef.getRef().toString().contains(tripId)) {
            realTimeDBInstance = new AccommodationsDBModel(tripId);
        }
        return dbRef;
    }
}