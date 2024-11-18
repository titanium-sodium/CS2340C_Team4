package com.example.sprintproject.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBModel {
    private static DBModel realTimeDBInstance = null;
    private static DatabaseReference dbRef;

    private DBModel() {
        if (dbRef == null) {
            dbRef = FirebaseDatabase.getInstance().getReference();
        }
    }

    public static synchronized DatabaseReference getInstance() {
        if (realTimeDBInstance == null) {
            realTimeDBInstance = new DBModel();
        }
        return dbRef;
    }

    public static DatabaseReference getUsersReference() {
        return getInstance().child("users");
    }

    public static DatabaseReference getTripReference() {
        return getInstance().child("trips");
    }

    public static DatabaseReference getDestinationsReference() {
        return getInstance().child("destinations");
    }

    public static DatabaseReference getTravelCommunityReference(String tripId) {
        return getTripReference().child(tripId).child("travelCommunity");
    }

    public static DatabaseReference getCommunityPostsReference() {
        return getInstance().child("communityPosts");
    }

    public static DatabaseReference getSharedTripsReference() {
        return getInstance().child("sharedTrips");
    }
}