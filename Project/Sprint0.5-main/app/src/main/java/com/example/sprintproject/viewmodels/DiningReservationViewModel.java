package com.example.sprintproject.viewmodels;

import com.example.sprintproject.model.DiningDBModel;
import com.example.sprintproject.model.DiningReservation;
import com.example.sprintproject.views.MainActivity;
import com.google.firebase.database.DatabaseReference;

import java.util.UUID;

public class DiningReservationViewModel {
    private DatabaseReference diningDB;
    public DiningReservationViewModel(String userId) {
        diningDB = DiningDBModel.getInstance(userId);
    }

    public void addReservation(DiningReservation reservationModel) {
        String reservationId = UUID.randomUUID().toString();

        diningDB.child(reservationId).setValue(reservationModel);
        updateUI();
    }

    private void updateUI() {
        //TODO Andres
    }
}
