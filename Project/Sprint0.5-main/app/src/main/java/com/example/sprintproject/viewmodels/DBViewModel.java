package com.example.sprintproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.sprintproject.model.DBModel;

import com.example.sprintproject.model.TravelStats;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DBViewModel extends ViewModel {
    private DatabaseReference DB;
    private MutableLiveData<TravelStats> travelStatsLiveData;

    public DBViewModel() {
        DB = DBModel.getInstance();
        travelStatsLiveData = new MutableLiveData<>();
    }
    public DatabaseReference getDB() {return DB;}
    public LiveData<TravelStats> getTravelStats() {
        if (travelStatsLiveData == null) {
            travelStatsLiveData = new MutableLiveData<>();
            loadTravelStats();
        }
        return travelStatsLiveData;
    }

    private void loadTravelStats() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get allotted days from user settings
        DB.child("users").child(userId).child("vacationTime")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot userSnapshot) {
                        // Get allotted days
                        int allottedDays = 0;
                        if (userSnapshot.exists() && userSnapshot.child("duration").exists()) {
                            allottedDays = userSnapshot.child("duration").getValue(Integer.class);
                        }

                        // Get planned days from destinations
                        DB.child("users").child(userId)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot destinationsSnapshot) {
                                        int plannedDays = 0;

                                        // Calculate total planned days from all destinations
                                        for (DataSnapshot destination : destinationsSnapshot.getChildren()) {
                                            if (destination.child("duration").exists()) {
                                                plannedDays += destination.child("duration").getValue(Integer.class);
                                            }
                                        }

                                        // Update LiveData with new TravelStats
                                        travelStatsLiveData.setValue(new TravelStats(allottedDays, plannedDays));
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        // Handle error
                                        System.err.println("Error loading destinations: " + error.getMessage());
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Handle error
                        System.err.println("Error loading user data: " + error.getMessage());
                    }
                });
    }

    // Method to update allotted days
    public void updateAllottedDays(int days) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DB.child("users").child(userId).child("vacationTime").child("duration").setValue(days);
    }
}