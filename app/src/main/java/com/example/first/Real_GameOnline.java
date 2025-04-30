package com.example.first;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.*;

import java.io.*;
import java.util.*;

public class Real_GameOnline extends AppCompatActivity {

    // UI elements
    private String roomCode, username, player1, player2;
    private TextView textViewCityName, textViewScore, tvCdt, textViewTurn;
    private Button buttonOption1, buttonOption2;

    // Timer and game logic
    private CountDownTimer cdt;
    private DatabaseReference roomRef;
    private int score = 0;
    private boolean gameEnded = false;
    private List<LocationState> locationList = new ArrayList<>();
    private String currentCorrectState;
    private boolean sentMyScore = false;

    // Internal class to hold city-state pairs
    private static class LocationState {
        String city;
        String state;
        LocationState(String city, String state) {
            this.city = city;
            this.state = state;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_game_online);

        // Get values passed from previous screen
        roomCode = getIntent().getStringExtra("roomCode");
        username = getIntent().getStringExtra("username");
        player1 = getIntent().getStringExtra("player1");
        player2 = getIntent().getStringExtra("player2");

        // Initialize views
        textViewCityName = findViewById(R.id.textViewCityName);
        textViewScore = findViewById(R.id.textViewScore);
        tvCdt = findViewById(R.id.tvCdt);
        textViewTurn = findViewById(R.id.textViewTurn);
        buttonOption1 = findViewById(R.id.buttonOption1);
        buttonOption2 = findViewById(R.id.buttonOption2);

        // Firebase reference to current game room
        roomRef = FirebaseDatabase.getInstance().getReference("rooms").child(roomCode);

        loadCitiesFromCSV(); // Load city-state data
        generateNewQuestion(); // Generate first question
        startTimer(); // Start countdown

        // Set answer button listeners
        buttonOption1.setOnClickListener(v -> checkAnswer(buttonOption1.getText().toString()));
        buttonOption2.setOnClickListener(v -> checkAnswer(buttonOption2.getText().toString()));

        // Listen for game result from other player
        listenForGameEnd();
    }

    private void loadCitiesFromCSV() {
        // Read city and state data from CSV in assets
        try {
            InputStream is = getAssets().open("USLocations.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    locationList.add(new LocationState(parts[0].trim(), parts[1].trim()));
                }
            }
            reader.close();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to load locations!", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateNewQuestion() {
        if (locationList.size() < 2) {
            textViewCityName.setText("No Questions Available");
            return;
        }

        // Randomly choose one correct and one incorrect state
        Random random = new Random();
        LocationState correct = locationList.get(random.nextInt(locationList.size()));
        LocationState wrong;
        do {
            wrong = locationList.get(random.nextInt(locationList.size()));
        } while (wrong.state.equals(correct.state));

        currentCorrectState = correct.state;
        textViewCityName.setText(correct.city);

        // Shuffle options
        List<String> options = Arrays.asList(correct.state, wrong.state);
        Collections.shuffle(options);
        buttonOption1.setText(options.get(0));
        buttonOption2.setText(options.get(1));

        textViewScore.setText("Score: " + score);
    }

    private void startTimer() {
        if (cdt != null) cdt.cancel();

        // Countdown 10 seconds per question
        cdt = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                tvCdt.setText(secondsLeft + "s");

                // Color and blinking effect
                if (secondsLeft > 5) {
                    tvCdt.setTextColor(Color.parseColor("#03A9F4"));
                    tvCdt.clearAnimation();
                } else if (secondsLeft > 2) {
                    tvCdt.setTextColor(Color.parseColor("#FF9800"));
                    tvCdt.clearAnimation();
                } else if (secondsLeft == 1) {
                    tvCdt.setTextColor(Color.parseColor("#F44336"));
                    startBlinkingAnimation(tvCdt);
                } else {
                    tvCdt.setTextColor(Color.parseColor("#F44336"));
                    tvCdt.clearAnimation();
                }
            }

            public void onFinish() {
                tvCdt.setText("0s");
                tvCdt.setTextColor(Color.parseColor("#F44336"));
                tvCdt.clearAnimation();
                loseGame(); // Auto-lose if time runs out
            }
        }.start();
    }

    private void startBlinkingAnimation(TextView textView) {
        Animation blink = new AlphaAnimation(0.0f, 1.0f);
        blink.setDuration(300);
        blink.setInterpolator(new LinearInterpolator());
        blink.setRepeatMode(Animation.REVERSE);
        blink.setRepeatCount(Animation.INFINITE);
        textView.startAnimation(blink);
    }

    private void resetTimer() {
        cdt.cancel();
        startTimer();
    }

    private void checkAnswer(String selectedAnswer) {
        if (cdt != null) cdt.cancel();

        if (selectedAnswer.equals(currentCorrectState)) {
            score++;
            generateNewQuestion();
            startTimer();
        } else {
            loseGame();
        }
    }

    private void loseGame() {
        if (gameEnded) return;
        gameEnded = true;

        if (cdt != null) cdt.cancel();
        enableButtons(false);
        textViewTurn.setText("Waiting for result...");

        // Save player's score
        roomRef.child("playerScores").child(username).setValue(score);
        roomRef.child("players").child(username).child("status").setValue("loser");
        sentMyScore = true;

        String opponent = username.equals(player1) ? player2 : player1;
        DatabaseReference opponentScoreRef = roomRef.child("playerScores").child(opponent);

        // Wait for opponent score
        opponentScoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int opponentScore = snapshot.getValue(Integer.class);
                    finishGame(score, opponentScore);
                } else {
                    opponentScoreRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) return;
                            int opponentScore = snapshot.getValue(Integer.class);
                            finishGame(score, opponentScore);
                            opponentScoreRef.removeEventListener(this);
                        }

                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void listenForGameEnd() {
        // Wait for both players to submit scores
        roomRef.child("playerScores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || gameEnded) return;

                Long myScoreLong = snapshot.child(username).getValue(Long.class);
                Long opponentScoreLong = snapshot.child(username.equals(player1) ? player2 : player1).getValue(Long.class);

                if (!sentMyScore) {
                    roomRef.child("playerScores").child(username).setValue(score);
                    sentMyScore = true;
                }

                if (myScoreLong != null && opponentScoreLong != null) {
                    int myScore = myScoreLong.intValue();
                    int opponentScore = opponentScoreLong.intValue();

                    if (gameEnded) return;
                    gameEnded = true;

                    if (cdt != null) cdt.cancel();
                    enableButtons(false);
                    textViewTurn.setText("Game Over");

                    finishGame(myScore, opponentScore);
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void enableButtons(boolean enabled) {
        buttonOption1.setEnabled(enabled);
        buttonOption2.setEnabled(enabled);
    }

    // Navigate to different results screens
    private void goToWinner() {
        Intent intent = new Intent(this, WinnerActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

    private void goToLoser() {
        Intent intent = new Intent(this, LoserActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

    private void goToDraw() {
        Intent intent = new Intent(this, DrawActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

    private void finishGame(int myScore, int opponentScore) {
        new Handler().postDelayed(() -> {
            if (myScore > opponentScore) {
                goToWinner();
            } else if (myScore < opponentScore) {
                goToLoser();
            } else {
                goToDraw();
            }
        }, 1000); // Delay to allow UI to update
    }

    @Override
    protected void onDestroy() {
        if (cdt != null) {
            cdt.cancel();
        }
        super.onDestroy();
    }
}
