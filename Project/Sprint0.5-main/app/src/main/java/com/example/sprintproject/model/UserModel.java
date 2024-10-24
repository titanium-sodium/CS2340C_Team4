package com.example.sprintproject.model;


import android.os.DeadSystemException;

import com.example.sprintproject.viewmodels.DBViewModel;
import com.google.firebase.database.DatabaseReference;
import java.util.List;

public class UserModel {

    private String email;
    private String userId;
    private List<DestinationModel> destinations; // needs the location added

    public UserModel(String userId, String email) {
        this.email = email;
        this.userId = userId;
    }

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
