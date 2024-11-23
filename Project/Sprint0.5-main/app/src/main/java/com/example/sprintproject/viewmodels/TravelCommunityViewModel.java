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

    // Update an existing community post
    public Task<Void> updateCommunityPost(String tripId, CommunityPost post) {
        isLoading.setValue(true);

        if (post.getPostId() == null) {
            errorMessage.setValue("Invalid post ID");
            isLoading.setValue(false);
            return null;
        }

        return TravelCommunityDBModel.getInstance()
                .child(post.getPostId())
                .setValue(post)
                .addOnSuccessListener(aVoid -> {
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorMessage.setValue("Failed to update post: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }

    // Delete a community post
    public Task<Void> deleteCommunityPost(String tripId, String postId) {
        isLoading.setValue(true);

        return TravelCommunityDBModel.getInstance()
                .child(postId)
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorMessage.setValue("Failed to delete post: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }

    // Get a single community post
    public void loadSinglePost(String tripId, String postId) {
        isLoading.setValue(true);

        TravelCommunityDBModel.getInstance()
                .child(postId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        CommunityPost post = snapshot.getValue(CommunityPost.class);
                        if (post != null) {
                            List<CommunityPost> currentPosts = communityPosts.getValue();
                            if (currentPosts == null) {
                                currentPosts = new ArrayList<>();
                            }
                            currentPosts.add(post);
                            communityPosts.setValue(currentPosts);
                        }
                        isLoading.setValue(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        errorMessage.setValue("Failed to load post: " + error.getMessage());
                        isLoading.setValue(false);
                    }
                });
    }

    // Filter posts by user
    public void filterPostsByUser(String userId) {
        List<CommunityPost> allPosts = communityPosts.getValue();
        if (allPosts != null) {
            List<CommunityPost> filteredPosts = new ArrayList<>();
            for (CommunityPost post : allPosts) {
                if (post.getUserId().equals(userId)) {
                    filteredPosts.add(post);
                }
            }
            communityPosts.setValue(filteredPosts);
        }
    }

    // Clear any error messages
    public void clearError() {
        errorMessage.setValue(null);
    }

    // Reset loading state
    public void resetLoadingState() {
        isLoading.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up any resources if needed
    }
}