package com.example.sprintproject.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBModel {
    private static DBModel realTimeDBInstance = null;
    private static DatabaseReference dbRef;

    private DBModel() {
        if (dbRef == null) {  // Additional null check for safety
            dbRef = FirebaseDatabase.getInstance().getReference();
        }
    }

    public static synchronized DatabaseReference getInstance() {
        if (realTimeDBInstance == null) {
            realTimeDBInstance = new DBModel();
        }
        return dbRef;
    }

    // Helper methods for common paths
    public static DatabaseReference getUsersReference() {
        return getInstance().child("users");
    }

    public static DatabaseReference getTripsReference() {
        return getInstance().child("trips");
    }

    public static DatabaseReference getTravelCommunityReference() {
        return getInstance().child("travelCommunity");
    }
}