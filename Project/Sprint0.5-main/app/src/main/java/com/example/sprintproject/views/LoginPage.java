package com.example.sprintproject.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sprintproject.R;
import com.example.sprintproject.viewmodels.AuthViewModel;
import com.example.sprintproject.viewmodels.DBViewModel;
import com.example.sprintproject.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class LoginPage extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button createAccountButton;

    //variables for sending userID to other activities
    protected UserModel[] users;
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
                                            DatabaseReference DB = new DBViewModel().getDB();
                                            DB.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {


                                                @Override
                                                public void onComplete(@NonNull Task<DataSnapshot> task) {

                                                    if (task.isSuccessful()) {
                                                        //creates an array of users to search through
                                                        users = (UserModel[]) task.getResult().getValue();
                                                    }

                                                }

                                            });

                                            //not sure where this fits into everything yet, so putting it here with a placeholder string for now


                                            for (int i = 0; i < users.length; i++) {

                                                if (users[i].getEmail().equals(email)) {

                                                    matchedUserId = users[i].getUserId();

                                                }

                                            }

                                            Toast.makeText(LoginPage.this,
                                                    "Authentication successful.",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginPage.this,
                                                    MainActivity.class);
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