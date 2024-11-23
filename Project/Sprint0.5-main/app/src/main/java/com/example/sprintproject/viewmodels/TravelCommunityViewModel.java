package com.example.sprintproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;

import com.example.sprintproject.model.CommunityPost;
import com.example.sprintproject.model.TravelCommunityDBModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class TravelCommunityViewModel extends ViewModel {
    private static final String TAG = "TravelCommunityViewModel";
    private final MutableLiveData<List<CommunityPost>> communityPosts;
    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<Boolean> isLoading;

    public TravelCommunityViewModel() {
        communityPosts = new MutableLiveData<>(new ArrayList<>());
        errorMessage = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
    }

    // Getters for LiveData
    public LiveData<List<CommunityPost>> getCommunityPosts() {
        return communityPosts;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // Load all community posts for a specific trip
    public void loadCommunityPosts(String tripId) {
        isLoading.setValue(true);

        TravelCommunityDBModel.getCommunityPosts()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<CommunityPost> posts = new ArrayList<>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            CommunityPost post = postSnapshot.getValue(CommunityPost.class);
                            if (post != null) {
                                posts.add(post);
                            }
                        }
                        communityPosts.setValue(posts);
                        isLoading.setValue(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        errorMessage.setValue("Failed to load community posts: "
                                + error.getMessage());
                        isLoading.setValue(false);
                    }
                });
    }

    // Create a new community post
    public Task<Void> createCommunityPost(String tripId, CommunityPost post) {
        isLoading.setValue(true);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        post.setUserId(userId);

        return TravelCommunityDBModel.saveCommunityPost(post)
                .addOnSuccessListener(aVoid -> {
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorMessage.setValue("Failed to create post: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up any resources if needed
    }
}