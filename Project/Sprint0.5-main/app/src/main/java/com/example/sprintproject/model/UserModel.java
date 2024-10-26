package com.example.sprintproject.model;


import com.example.sprintproject.viewmodels.DBViewModel;
import com.google.firebase.database.DatabaseReference;

public class UserModel {

    private String email;
    private String userId;

    public UserModel(String userId, String email) {
        this.email = email;
        this.userId = userId;
    }
    public UserModel() {}

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
