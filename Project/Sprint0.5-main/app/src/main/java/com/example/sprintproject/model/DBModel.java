package com.example.sprintproject.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBModel {
    private static DBModel realTimeDBInstance = null;
    private static DatabaseReference dbRef;

    private DBModel() {
        dbRef = FirebaseDatabase.getInstance().getReference();
    }
    public static synchronized DatabaseReference getInstance() {
        if (realTimeDBInstance == null) {
            realTimeDBInstance = new DBModel();
            return dbRef;
        }
        return dbRef;
    }

}
