package com.example.sprintproject.viewmodels;

import com.example.sprintproject.model.DiningDBModel;
import com.example.sprintproject.model.ReservationModel;
import com.google.firebase.database.DatabaseReference;

public class DiningReservationViewModel {
    private DatabaseReference diningDB;
    public DiningReservationViewModel() {
        diningDB = DiningDBModel.getInstance();
    }

    public void addReservation(ReservationModel reservationModel) {
        //TODO
    }
}
