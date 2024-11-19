package com.example.sprintproject.model;

import java.util.ArrayList;

public class UserModel {
    private String email;
    private String userId;
    private ArrayList<String> tripIds;

    public UserModel(String userId, String email) throws IllegalArgumentException {
        validateUserData(userId, email);
        this.email = email;
        this.userId = userId;
        this.tripIds = new ArrayList<>();
    }

    // Required empty constructor for Firebase
    public UserModel() {
        this.tripIds = new ArrayList<>();
    }

    private void validateUserData(String userId, String email) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("userId cannot be empty");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("email cannot be empty");
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws IllegalArgumentException {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("email cannot be empty");
        }
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) throws IllegalArgumentException {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("userId cannot be empty");
        }
        this.userId = userId;
    }




}