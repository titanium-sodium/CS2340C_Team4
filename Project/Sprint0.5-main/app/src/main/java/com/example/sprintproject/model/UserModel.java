package com.example.sprintproject.model;

import com.example.sprintproject.viewmodels.DBViewModel;
import com.google.firebase.database.DatabaseReference;

public class UserModel {

    private String email, password;

    public UserModel(String email, String password) {

        this.email = email;
        this.password = password;

    }

    static DatabaseReference DB = new DBViewModel().getDB();

    public static void writeNewUser(String userId, String email, String password) {

        UserModel user = new UserModel(email, password);
        DB.child("users").child(userId).setValue(user);

    }

}
