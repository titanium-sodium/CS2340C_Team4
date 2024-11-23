package com.example.sprintproject.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sprintproject.R;
import com.example.sprintproject.viewmodels.AuthViewModel;
import com.example.sprintproject.viewmodels.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.sprintproject.model.DBModel;

import java.util.UUID;

public class CreateAccountPage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailInput;
    private EditText passwordInput;
    private UserViewModel userViewModel = new UserViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_accounts_page);

        //auth view model instantiation
        AuthViewModel viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        mAuth = viewModel.getAuth();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button createAccountButton = findViewById(R.id.createAccountButton);
        TextView loginPrompt = findViewById(R.id.loginPromptTextView);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        loginPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });
    }

    private void createAccount() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this,
                    "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();
                            // Use Firebase UID instead of random UUID

                            userViewModel.setUserEmail(email);
                            userViewModel.setUserUID(userId);

                            //creates an instance of user model and writes it to the DB
                            userViewModel.writeNewUser(userViewModel.getUserModel());

                            // Create initial trip
                            String tripId = UUID.randomUUID().toString();
                            DBModel.getUsersReference().child(userId).child("trips")
                                    .child(tripId).setValue(true);
                            DBModel.getTripReference().child(tripId).child("userID")
                                    .setValue(userId);

                            Toast.makeText(CreateAccountPage.this,
                                    "Account created successfully",
                                    Toast.LENGTH_SHORT).show();

                            // Navigate directly to MainActivity instead of LoginPage
                            Intent intent = new Intent(CreateAccountPage.this, MainActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("tripId", tripId);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(CreateAccountPage.this,
                                    "Account creation failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(CreateAccountPage.this, LoginPage.class);
        startActivity(intent);
        finish();
    }
}