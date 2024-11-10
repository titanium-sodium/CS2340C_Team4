package com.example.sprintproject.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sprintproject.model.DBModel;
import com.example.sprintproject.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class InviteUserViewModel {
    private static final String TAG = "InviteUserViewModel";
    private String currentUserId;
    private DatabaseReference db;

    public InviteUserViewModel() {
        db = DBModel.getInstance();
    }

    public LiveData<Boolean> inviteUser(String email) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        if (currentUserId == null) {
            Log.e(TAG, "Cannot invite user: current user is null");
            result.setValue(false);
            return result;
        }

        db.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean userFound = false;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    try {
                        UserModel user = userSnapshot.getValue(UserModel.class);
                        Log.d("EMAIL", "useremail:" + user.getEmail() + "email:" + email);
                        if (user.getEmail() != null && user.getEmail().equals(email)) {
                            String invitedUserId = user.getUserId();

                            // Add contributor to current user
                            db.child("users").child(currentUserId)
                                    .child("contributors")
                                    .child(invitedUserId)
                                    .child("email")
                                    .setValue(email)
                                    .addOnSuccessListener(aVoid -> {
                                        // Add current user as contributor to invited user
                                        FirebaseAuth auth = FirebaseAuth.getInstance();
                                        String currentUserEmail = auth.getCurrentUser() != null
                                                ? auth.getCurrentUser().getEmail() : "";

                                        db.child("users").child(invitedUserId)
                                                .child("contributors")
                                                .child(currentUserId)
                                                .child("email")
                                                .setValue(currentUserEmail)
                                                .addOnSuccessListener(unused -> {
                                                    // Share data after successful invitation
                                                    shareDataWithUser(invitedUserId);
                                                    result.setValue(true);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e(TAG, "Failed to add reverse contributor: "
                                                            + e.getMessage());
                                                    result.setValue(false);
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Failed to add contributor: " + e.getMessage());
                                        result.setValue(false);
                                    });
                            userFound = true;
                            break;
                        }
                    } catch (DatabaseException e) {
                        Log.e(TAG, "Error parsing user data: " + e.getMessage());
                    }
                }
                if (!userFound) {
                    result.setValue(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error inviting user: " + error.getMessage());
                result.setValue(false);
            }
        });

        return result;
    }
    private void shareDataWithUser(String targetUserId) {
        // Share travelStats
        db.child("users").child(currentUserId).child("travelStats")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            db.child("users").child(targetUserId)
                                    .child("travelStats")
                                    .setValue(snapshot.getValue())
                                    .addOnFailureListener(e -> Log.e(TAG,
                                            "Error sharing travelStats: " + e.getMessage()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error reading travelStats for sharing: " + error.getMessage());
                    }
                });

        // Share destinations
        db.child("users").child(currentUserId).child("destinations")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            db.child("users").child(targetUserId)
                                    .child("destinations")
                                    .setValue(snapshot.getValue())
                                    .addOnFailureListener(e -> Log.e(TAG,
                                            "Error sharing destinations: " + e.getMessage()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error reading destinations for sharing: " + error.getMessage());
                    }
                });

        // Share notes
        db.child("users").child(currentUserId).child("notes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            db.child("users").child(targetUserId)
                                    .child("notes")
                                    .setValue(snapshot.getValue())
                                    .addOnFailureListener(e -> Log.e(TAG,
                                            "Error sharing notes: " + e.getMessage()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error reading notes for sharing: " + error.getMessage());
                    }
                });
    }
}
