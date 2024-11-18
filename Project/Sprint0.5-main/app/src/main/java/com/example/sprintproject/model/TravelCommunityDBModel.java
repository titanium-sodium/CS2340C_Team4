package com.example.sprintproject.model;

import com.google.firebase.database.DatabaseReference;
import com.google.android.gms.tasks.Task;

public class TravelCommunityDBModel {
    private static TravelCommunityDBModel realTimeDBInstance = null;
    private static DatabaseReference dbRef;

    private TravelCommunityDBModel(String tripId) {
        dbRef = TripDBModel.getTripReference(tripId).child("travelCommunity");
    }

    public static synchronized DatabaseReference getInstance(String tripId) {
        if (realTimeDBInstance == null || !dbRef.getRef().toString().contains(tripId)) {
            realTimeDBInstance = new TravelCommunityDBModel(tripId);
        }
        return dbRef;
    }

    public static DatabaseReference createCommunityPost(String tripId) {
        String postId = getInstance(tripId).push().getKey();
        if (postId != null) {
            return getInstance(tripId).child(postId);
        }
        return null;
    }

    public static Task<Void> saveCommunityPost(String tripId, CommunityPost post) {
        DatabaseReference postRef = createCommunityPost(tripId);
        if (postRef != null) {
            post.setPostId(postRef.getKey());
            return postRef.setValue(post);
        }
        return null;
    }

    public static DatabaseReference getCommunityPosts(String tripId) {
        return getInstance(tripId);
    }
}