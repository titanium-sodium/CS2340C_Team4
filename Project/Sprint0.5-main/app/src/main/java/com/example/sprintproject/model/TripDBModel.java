package com.example.sprintproject.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TripDBModel {
    private static TripDBModel realTimeDBInstance = null;
    private static DatabaseReference dbRef;

    private TripDBModel() {
        dbRef = FirebaseDatabase.getInstance().getReference().child("Trips");
    }
    public static synchronized DatabaseReference getInstance() {
        if (realTimeDBInstance == null) {
            realTimeDBInstance = new TripDBModel();
            return dbRef;
        }
        return dbRef;
    }

}
