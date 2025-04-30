package com.example.first;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;

public class Register extends AppCompatActivity {

    Button bToGame;                    // Button to proceed with registration
    ImageView logo;                    // Logo image that will rotate on click
    Button btToLog;                    // Button to switch to login screen
    EditText UserEmailR, UserPasswordR, UserPasswordC, UserNameR, UserPhoneR; // Input fields

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Load the layout

        // Bind views from XML
        UserEmailR = findViewById(R.id.UserEmailR);
        UserPasswordC = findViewById(R.id.UserPasswordC);
        UserPasswordR = findViewById(R.id.UserPasswordR);
        UserNameR = findViewById(R.id.UserNameR);
        UserPhoneR = findViewById(R.id.UserPhoneR);
        bToGame = findViewById(R.id.btToGame);
        btToLog = findViewById(R.id.btToLogIn);
        logo = findViewById(R.id.imageView);

        // Logo rotation animation on click
        logo.setOnClickListener(v -> {
            Animation rotate = AnimationUtils.loadAnimation(Register.this, R.anim.rotate);
            logo.startAnimation(rotate);
        });

        // Register button click → calls registerUser()
        bToGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        // Login button click → navigates to Login activity
        btToLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Register.this, Login.class);
                startActivity(intent1);
            }
        });
    }

    private void registerUser() {
        // Extract user inputs
        String emailRe = UserEmailR.getText().toString().trim();
        String passwordRe = UserPasswordR.getText().toString();
        String confirmPassword = UserPasswordC.getText().toString();
        String nameRe = UserNameR.getText().toString().trim();
        String phoneRe = UserPhoneR.getText().toString().trim();

        // Input validations
        if (nameRe.isEmpty() || emailRe.isEmpty() || passwordRe.isEmpty() || phoneRe.isEmpty()) {
            Toast.makeText(this, "please fill all", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailRe).matches()) {
            Toast.makeText(this, "The email is incorrect", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwordRe.length() < 6) {
            Toast.makeText(this, "password must be at least 6 numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passwordRe.equals(confirmPassword)) {
            Toast.makeText(this, "The passwords are not the same", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase: Check if user already exists
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(nameRe);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(Register.this, "User already exists", Toast.LENGTH_SHORT).show();
                } else {
                    // Create new user object and push to Firebase
                    User user = new User(nameRe, emailRe, passwordRe, phoneRe, 0);
                    usersRef.setValue(user).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Register.this, Real_startGame.class);
                            intent.putExtra("username", nameRe);
                            intent.putExtra("bestScore", 0);
                            startActivity(intent); // Navigate to start game
                        } else {
                            Toast.makeText(Register.this, "Registration failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Register.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
