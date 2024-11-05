package com.example.sprintproject.model;

import com.google.firebase.database.DatabaseReference;

public class AccomodationsDBModel {
    private static AccomodationsDBModel realTimeDBInstance = null;
    private static DatabaseReference dbRef;

    private AccomodationsDBModel(String userId) {
        dbRef = DBModel.getInstance().child("users").child(userId).child("Accomodations");
    }
    public static synchronized DatabaseReference getInstance(String userId) {
        if (realTimeDBInstance == null) {
            realTimeDBInstance = new AccomodationsDBModel(userId);
            return dbRef;
        }
        return dbRef;
    }
}
