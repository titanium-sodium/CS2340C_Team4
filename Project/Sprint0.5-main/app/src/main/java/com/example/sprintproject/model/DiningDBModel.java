package com.example.sprintproject.model;

import com.google.firebase.database.DatabaseReference;

public class DiningDBModel {
    private static DiningDBModel realTimeDBInstance = null;
    private static DatabaseReference dbRef;

    private DiningDBModel() {
        dbRef = DBModel.getInstance().child("dining");
    }
    public static synchronized DatabaseReference getInstance() {
        if (realTimeDBInstance == null) {
            realTimeDBInstance = new DiningDBModel();
            return dbRef;
        }
        return dbRef;
    }
}
