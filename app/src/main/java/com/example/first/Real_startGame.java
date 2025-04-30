package com.example.first;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Real_startGame extends AppCompatActivity {

    Button BtStartReg, BtStartMode, BtAchievements, buttonOnlineMode;
    TextView textBestScore, textLastResult;
    String username;
    int bestScore;

    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

        setContentView(R.layout.activity_real_start_game);

        // Connect UI components from layout
        buttonOnlineMode = findViewById(R.id.buttonOnlineMode);
        BtStartReg = findViewById(R.id.startReg);
        BtStartMode = findViewById(R.id.startMode);
        textBestScore = findViewById(R.id.textBestScore);
        textLastResult = findViewById(R.id.LastResult);
        BtAchievements = findViewById(R.id.buttonAchievements);

        // Delayed bounce animation for each button
        new Handler().postDelayed(() -> BtStartReg.startAnimation(bounce), 100);
        new Handler().postDelayed(() -> BtStartMode.startAnimation(bounce), 300);
        new Handler().postDelayed(() -> buttonOnlineMode.startAnimation(bounce), 500);
        new Handler().postDelayed(() -> BtAchievements.startAnimation(bounce), 700);

        // Retrieve data passed from previous activity
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        if (username == null) username = "unknown";

        // Reference to the user's data in Firebase
        userRef = FirebaseDatabase.getInstance().getReference("users").child(username);

        // Load user data and update UI
        fetchUserData();

        // Start regular mode button
        BtStartReg.setOnClickListener(v -> {
            Intent intent1 = new Intent(Real_startGame.this, Real_GameReg.class);
            intent1.putExtra("username", username);
            intent1.putExtra("bestScore", bestScore);
            startActivity(intent1);
        });

        // Online multiplayer mode
        buttonOnlineMode.setOnClickListener(v -> {
            Intent intent5 = new Intent(Real_startGame.this, GameRoom.class);
            startActivity(intent5);
        });

        // Start challenge mode button
        BtStartMode.setOnClickListener(v -> {
            Intent intent1 = new Intent(Real_startGame.this, Real_GameMode.class);
            intent1.putExtra("username", username);
            intent1.putExtra("bestScore", bestScore);
            startActivity(intent1);
        });

        // Leaderboard / Achievements
        BtAchievements.setOnClickListener(v -> {
            Intent intent4 = new Intent(Real_startGame.this, Leaderboard.class);
            intent.putExtra("username", username);
            intent.putExtra("bestScore", bestScore);
            startActivity(intent4);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchUserData(); // Reload user data when returning to this screen

        // Re-run the animations when resuming the activity
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        new Handler().postDelayed(() -> BtStartReg.startAnimation(bounce), 100);
        new Handler().postDelayed(() -> BtStartMode.startAnimation(bounce), 300);
        new Handler().postDelayed(() -> buttonOnlineMode.startAnimation(bounce), 500);
        new Handler().postDelayed(() -> BtAchievements.startAnimation(bounce), 700);
    }

    private void fetchUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        bestScore = user.getBestScore(); // Fetch the user's best score
                    } else {
                        bestScore = 0; // Default if data is missing
                    }
                } else {
                    bestScore = 0; // Default if user doesn't exist
                }

                // Update the UI with user data
                textBestScore.setText("Best Score: " + bestScore);
                textLastResult.setText("Welcome, " + username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Real_startGame.this, "Failed to load user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
