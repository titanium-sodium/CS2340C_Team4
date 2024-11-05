package com.example.sprintproject.viewmodels;

import com.example.sprintproject.model.AccomodationsDBModel;
import com.example.sprintproject.model.AccomodationsModel;
import com.example.sprintproject.views.MainActivity;
import com.google.firebase.database.DatabaseReference;

import java.util.UUID;

public class AccomodationsViewModel {
    private DatabaseReference accomodationsDB;
    public AccomodationsViewModel() {
        accomodationsDB = AccomodationsDBModel.getInstance(MainActivity.getUserId());
    }

    public void addAccommodations(AccomodationsModel accomodations) {
        String reservationId = UUID.randomUUID().toString();

        accomodationsDB.child(reservationId).setValue(accomodations);
        updateUI();
    }

    private void updateUI() {
        //@TODO Andres
    }
}
