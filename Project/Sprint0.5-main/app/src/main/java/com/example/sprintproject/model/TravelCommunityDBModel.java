package com.example.sprintproject.model;

import com.google.firebase.database.DatabaseReference;

public class TravelCommunityDBModel {
    private static TravelCommunityDBModel realTimeDBInstance = null;
    private static DatabaseReference dbRef;

    private TravelCommunityDBModel(String userId) {
        dbRef = DBModel.getInstance().child("TravelCommunity");
    }
    public static synchronized DatabaseReference getInstance(String userId) {
        if (realTimeDBInstance == null) {
            realTimeDBInstance = new TravelCommunityDBModel(userId);
            return dbRef;
        }
        return dbRef;
    }
}

