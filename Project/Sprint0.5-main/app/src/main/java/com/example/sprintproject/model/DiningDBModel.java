package com.example.sprintproject.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DiningDBModel {
    private static DiningDBModel instance = null;
    private static DatabaseReference dbRef;

    private DiningDBModel() {
        dbRef = DBModel.getInstance().child("dining");
    }

    public static synchronized DatabaseReference getInstance() {
        if (instance == null) {
            instance = new DiningDBModel();
        }
        return dbRef;
    }

    // Helper methods similar to DBModel
    public static DatabaseReference getDiningForTrip(String tripId) {
        return getInstance().child(tripId);
    }

    public static DatabaseReference getDiningReference() {
        return getInstance();
    }
}