package com.example.sprintproject.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sprintproject.R;
import com.example.sprintproject.viewmodels.AuthViewModel;
import com.example.sprintproject.viewmodels.DBViewModel;
import com.example.sprintproject.model.UserModel;
import com.example.sprintproject.viewmodels.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class LoginPage extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button createAccountButton;

    //variables for sending userID to other activities
    private final UserViewModel userViewModel = new UserViewModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);



        AuthViewModel viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        auth = viewModel.getAuth();
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginPage.this,
                                    new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            //Toast
                                            Toast.makeText(LoginPage.this,
                                                    "Authentication successful.",
                                                    Toast.LENGTH_SHORT).show();
                                            //Finding the user
                                            DatabaseReference DB = new DBViewModel().getDB();
                                            //async wait operation
                                            DB.child("users").addChildEventListener(new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                                    try {
                                                        String userId = snapshot.getKey();
                                                        String userEmail = snapshot.child("email").getValue(String.class);

                                                        if (userEmail != null && userEmail.equals(email)) {
                                                            Log.d("SUCCESS", "User found: " + userId);
                                                            Intent intent = new Intent(LoginPage.this, MainActivity.class);
                                                            intent.putExtra("userId", userId);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    } catch (Exception e) {
                                                        Log.e("Firebase", "Error retrieving user data", e);
                                                        Toast.makeText(LoginPage.this,
                                                                "Error retrieving user data",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                @Override
                                                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                                    //Do nothing
                                                }

                                                @Override
                                                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                                                    //Do nothing
                                                }

                                                @Override
                                                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                                    //Do nothing
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    //Do nothing
                                                }

                                            });
                                        } else {
                                            Toast.makeText(LoginPage.this,
                                                    "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                } else {
                    Toast.makeText(LoginPage.this,
                            "Please enter email and password",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, CreateAccountPage.class);
                startActivity(intent);
            }
        });
    }
}