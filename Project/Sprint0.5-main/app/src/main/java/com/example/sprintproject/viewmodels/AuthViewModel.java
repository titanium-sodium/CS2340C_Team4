package com.example.sprintproject.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.sprintproject.model.AuthModel;
import com.google.firebase.auth.FirebaseAuth;

public class AuthViewModel extends ViewModel {
    private FirebaseAuth auth;

    public AuthViewModel() {
        auth = AuthModel.getInstance();
    }

    public FirebaseAuth getAuth() {
        return auth;
    }
}
