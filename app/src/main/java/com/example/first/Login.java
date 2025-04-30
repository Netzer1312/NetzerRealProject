package com.example.first;

import static com.example.first.R.id.btNexting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    EditText editTextName, editTextPassword; // Fields for email and password input
    Button btReg, buttonLogin, btHome;       // Buttons for registration, login, and home

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Set the layout for the login screen

        btReg = findViewById(R.id.btReg);                 // "Register" button
        buttonLogin = findViewById(R.id.btNexting);       // "Login" button
        editTextName = findViewById(R.id.UserEmail);      // Email input
        editTextPassword = findViewById(R.id.UserPassword); // Password input

        // Navigate to registration screen
        btReg.setOnClickListener(view -> {
            Intent toReg = new Intent(Login.this, Register.class);
            startActivity(toReg);
        });

        // Handle login logic when login button is clicked
        buttonLogin.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();        // Get entered username
            String password = editTextPassword.getText().toString().trim(); // Get entered password

            if (name.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Fill all", Toast.LENGTH_SHORT).show();
                return; // Make sure all fields are filled
            }

            // Reference to user data in Firebase
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(name);

            // Get user data once from Firebase
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class); // Convert snapshot to User object

                        if (user != null && user.getPassword().equals(password)) {
                            int bestScore = user.getBestScore(); // Get user's best score

                            Toast.makeText(Login.this, "Your login succeeded", Toast.LENGTH_SHORT).show();

                            // Navigate to start screen with user's data
                            Intent intent = new Intent(Login.this, Real_startGame.class);
                            intent.putExtra("username", name);
                            intent.putExtra("bestScore", bestScore);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Login.this, "Wrong password or username", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Login.this, "User does not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Login.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
