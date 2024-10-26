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
import java.util.Objects;

public class LoginPage extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button createAccountButton;

    //variables for sending userID to other activities
    private final UserViewModel userViewModel = new UserViewModel();
    protected String matchedUserId;
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
                                            //create new instance of DB
                                            userViewModel.findUser(email);

                                            Toast.makeText(LoginPage.this,
                                                    "Authentication successful.",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginPage.this,
                                                    MainActivity.class);
                                            matchedUserId = "hi";
                                            intent.putExtra("userId", matchedUserId);
                                            startActivity(intent);
                                            finish();
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