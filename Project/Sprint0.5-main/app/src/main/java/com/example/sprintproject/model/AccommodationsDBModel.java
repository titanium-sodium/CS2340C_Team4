package com.example.sprintproject.model;

import com.google.firebase.database.DatabaseReference;

public class AccommodationsDBModel {
    private static AccommodationsDBModel realTimeDBInstance = null;
    private static DatabaseReference dbRef;

    private AccommodationsDBModel(String userId) {
        dbRef = DBModel.getInstance().child("users").child(userId).child("Accommodations");
    }
    public static synchronized DatabaseReference getInstance(String userId) {
        if (realTimeDBInstance == null) {
            realTimeDBInstance = new AccommodationsDBModel(userId);
            return dbRef;
        }
        return dbRef;
    }
}
