package com.example.sprintproject.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.sprintproject.model.DBModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class DBViewModel extends ViewModel {
    private DatabaseReference DB;

    public DBViewModel() {
        DB = DBModel.getInstance();
    }

    public DatabaseReference getDB() {
        return DB;
    }
}
