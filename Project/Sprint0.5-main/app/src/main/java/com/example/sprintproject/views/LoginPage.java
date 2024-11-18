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
import com.example.sprintproject.model.DBModel;
import com.example.sprintproject.viewmodels.AuthViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.UUID;

public class LoginPage extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button createAccountButton;
    private static LoginPage instance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
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
                                                Toast.makeText(LoginPage.this,
                                                        "Authentication successful.",
                                                        Toast.LENGTH_SHORT).show();

                                                // Get user reference directly from email
                                                DatabaseReference usersRef = DBModel.getUsersReference();
                                                usersRef.orderByChild("email").equalTo(email)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.exists()) {
                                                                    // Get the first (and should be only) user with this email
                                                                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                                                                    String userId = userSnapshot.getKey();
                                                                    checkUserTrips(userId);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                Log.e("Firebase", "Error finding user", error.toException());
                                                                Toast.makeText(LoginPage.this,
                                                                        "Error retrieving user data",
                                                                        Toast.LENGTH_SHORT).show();
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

    private void checkUserTrips(String userId) {
        // Check both user's trips and the main trips collection
        DatabaseReference userTripsRef = DBModel.getUsersReference()
                .child(userId)
                .child("trips");

        userTripsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tripId = "";
                if (snapshot.getChildrenCount() > 0) {
                    // Get the first trip ID from user's trips
                    tripId = snapshot.getChildren().iterator().next().getKey();
                } else {
                    // Create new trip ID and add to both user's trips and main trips collection
                    tripId = UUID.randomUUID().toString();
                    userTripsRef.child(tripId).setValue(true);
                    DBModel.getTripReference().child(tripId).child("userID").setValue(userId);
                }

                // Launch main activity with user and trip IDs
                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error checking trips", error.toException());
                Toast.makeText(LoginPage.this,
                        "Error checking trips",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void finishLoginActivity() {
        if (instance != null) {
            instance.finish();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (instance == this) {
            instance = null;
        }
    }
}