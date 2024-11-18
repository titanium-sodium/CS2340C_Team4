package com.example.sprintproject.model;

import com.google.firebase.database.DatabaseReference;

public class TripDBModel {
    private static TripDBModel realTimeDBInstance = null;
    private static DatabaseReference dbRef;

    private TripDBModel(String userId) {
        dbRef = DBModel.getInstance().child("users").child(userId).child("trips");
    }

    public static synchronized DatabaseReference getInstance(String userId) {
        if (realTimeDBInstance == null || !dbRef.getRef().toString().contains(userId)) {
            realTimeDBInstance = new TripDBModel(userId);
        }
        return dbRef;
    }

    public static DatabaseReference getTripReference(String tripId) {
        return DBModel.getInstance().child("trips").child(tripId);
    }

    public static DatabaseReference createCommunityPost(String tripId) {
        String postId = DBModel.getCommunityPostsReference().push().getKey();
        return DBModel.getCommunityPostsReference().child(postId);
    }

    public static void shareTrip(String tripId, String targetUserId) {
        DatabaseReference tripRef = getTripReference(tripId);
        DatabaseReference sharedRef = DBModel.getSharedTripsReference()
                .child(tripId)
                .child("contributors")
                .child(targetUserId);

        sharedRef.setValue(true);
    }
}
