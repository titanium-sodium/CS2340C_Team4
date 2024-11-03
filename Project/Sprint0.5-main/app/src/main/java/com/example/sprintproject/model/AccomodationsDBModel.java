package com.example.sprintproject.model;

import com.google.firebase.database.DatabaseReference;

public class AccomodationsDBModel {
    private static AccomodationsDBModel realTimeDBInstance = null;
    private static DatabaseReference dbRef;

    private AccomodationsDBModel() {
        dbRef = DBModel.getInstance().child("Accomodations");
    }
    public static synchronized DatabaseReference getInstance() {
        if (realTimeDBInstance == null) {
            realTimeDBInstance = new AccomodationsDBModel();
            return dbRef;
        }
        return dbRef;
    }
}
