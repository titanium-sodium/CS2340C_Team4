package com.example.sprintproject.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.example.sprintproject.model.UserModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Objects;

public class UserViewModel extends ViewModel {

    private UserModel userModel;
    public UserViewModel() {
        userModel = new UserModel("", "");
    }
    //writes a new user into the database
    public void writeNewUser(UserModel user) {
        DatabaseReference DB = new DBViewModel().getDB();
        DB.child("users").child(user.getUserId()).setValue(user);
    }
    public String findUser(String email) {
        HashMap<String, String> users = new HashMap<>();
        DatabaseReference DB = new DBViewModel().getDB();
        String emailToReturn = "";
        DB.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                UserModel dataSnapshot = new UserModel(snapshot.getValue(UserModel.class).getUserId(),
                        snapshot.getValue(UserModel.class).getEmail());
                users.put(dataSnapshot.getEmail(), dataSnapshot.getUserId());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Do nothing
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //Do nothing
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Do nothing
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Do nothing
            }
        });
        if (!users.isEmpty()) {
            emailToReturn = users.get(email);
        }
        return emailToReturn;
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
