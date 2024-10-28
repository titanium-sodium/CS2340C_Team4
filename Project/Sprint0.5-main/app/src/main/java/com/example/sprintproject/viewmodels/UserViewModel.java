package com.example.sprintproject.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.sprintproject.model.UserModel;
import com.google.firebase.database.DatabaseReference;



public class UserViewModel extends ViewModel {
    private UserModel userModel;
    public UserViewModel() {
        userModel = new UserModel("temp", "temp@temp.com");
    }
    //writes a new user into the database
    public void writeNewUser(UserModel user) {
        DatabaseReference db = new DBViewModel().getDB();
        db.child("users").child(user.getUserId()).setValue(user);
    }
    public UserModel getUserModel() {
        return userModel;
    }
    public String getUserEmail() {
        return userModel.getEmail();
    }
    public void setUserEmail(String email) {
        userModel.setEmail(email);
    }
    public String getUserUID() {
        return userModel.getUserId();
    }
    public void setUserUID(String userId) {
        userModel.setUserId(userId);
    }
}
