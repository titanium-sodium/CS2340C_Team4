package com.example.sprintproject.viewmodels;

import com.example.sprintproject.model.AccomodationsDBModel;
import com.example.sprintproject.model.DiningDBModel;
import com.example.sprintproject.model.LodgingModel;
import com.example.sprintproject.model.ReservationModel;
import com.google.firebase.database.DatabaseReference;

import java.util.UUID;

public class AccomodationsViewModel {
    private DatabaseReference accomodationsDB;
    public AccomodationsViewModel() {
        accomodationsDB = AccomodationsDBModel.getInstance();
    }

    public void addAccomodations(LodgingModel accomodations) {
        String reservationId = UUID.randomUUID().toString();

        accomodationsDB.child(reservationId).setValue(accomodations);

    }
}
