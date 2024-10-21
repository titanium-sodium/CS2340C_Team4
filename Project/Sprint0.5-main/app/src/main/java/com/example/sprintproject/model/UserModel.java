package com.example.sprintproject.model;

import androidx.annotation.Keep;

import com.example.sprintproject.viewmodels.DBViewModel;
import com.google.firebase.database.DatabaseReference;
import java.util.UUID;

@Keep
public class UserModel {

    private String email;

    public UserModel(String email) {

        this.email = email;

    }

    static DatabaseReference DB = new DBViewModel().getDB();

    public static void writeNewUser(String email) {

        UserModel user = new UserModel(email);
        String userId = UUID.randomUUID().toString();
        DB.child("users").child(userId).setValue(user);
        System.out.println("Made new user");
    }
    public String getEmail() {
        return email;
    }
}
