package com.example.sprintproject.model;
import com.google.firebase.auth.FirebaseAuth;

public class AuthModel {
    private static AuthModel authInstance = null;
    private static FirebaseAuth auth;

    private AuthModel() {
        auth = FirebaseAuth.getInstance();
    }
    public static synchronized FirebaseAuth getInstance() {
        if (authInstance == null) {
            authInstance = new AuthModel();
        }
        return auth;
    }

}
