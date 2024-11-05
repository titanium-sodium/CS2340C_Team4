package com.example.sprintproject.model;

import com.google.firebase.database.DatabaseReference;

public class DiningDBModel {
    private static DiningDBModel realTimeDBInstance = null;
    private static DatabaseReference dbRef;

    private DiningDBModel(String userId) {
        dbRef = DBModel.getInstance().child("users").child(userId).child("dining");
    }
    public static synchronized DatabaseReference getInstance(String userId) {
        if (realTimeDBInstance == null) {
            realTimeDBInstance = new DiningDBModel(userId);
            return dbRef;
        }
        return dbRef;
    }
}
