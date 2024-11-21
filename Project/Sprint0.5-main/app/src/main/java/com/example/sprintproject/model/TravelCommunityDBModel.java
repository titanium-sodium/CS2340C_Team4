package com.example.sprintproject.model;

import com.google.firebase.database.DatabaseReference;
import com.google.android.gms.tasks.Task;

public class TravelCommunityDBModel {
    private static TravelCommunityDBModel realTimeDBInstance = null;
    private static DatabaseReference dbRef;

    private TravelCommunityDBModel() {
        dbRef = DBModel.getInstance().child("travelCommunity");
    }

    public static synchronized DatabaseReference getInstance() {
        if (realTimeDBInstance == null) {
            realTimeDBInstance = new TravelCommunityDBModel();
        }
        return dbRef;
    }

    public static DatabaseReference createCommunityPost() {
        String postId = getInstance().push().getKey();
        if (postId != null) {
            return getInstance().child(postId);
        }
        return null;
    }

    public static Task<Void> saveCommunityPost(CommunityPost post) {
        DatabaseReference postRef = createCommunityPost();
        if (postRef != null) {
            post.setPostId(postRef.getKey());
            return postRef.setValue(post);
        }
        return null;
    }

    public static DatabaseReference getCommunityPosts() {
        return getInstance();
    }
}