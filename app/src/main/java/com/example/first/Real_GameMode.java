package com.example.first;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Real_GameMode extends AppCompatActivity {

    Button State1, State2, SpeakButton; // Two answer buttons and speech button
    TextView TextScore, tvCdt, TextCityName; // Score, timer, and city display
    CountDownTimer cdt; // Countdown timer
    int countScore = 0; // Current score
    int bestScore; // Best score from user
    String username; // Current username
    List<LocationState> locations; // All locations
    List<LocationState> remainingLocations; // Remaining ones in this round
    Random random = new Random();
    TextToSpeech textToSpeech;
    LocationState currentLocation; // Current question
    DatabaseReference userRef; // Reference to user's Firebase data

    private MediaPlayer correctSound;
    private MediaPlayer wrongSound;
    private MediaPlayer newRecordSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_game_mode);

        // Bind views
        tvCdt = findViewById(R.id.tvCdt);
        State1 = findViewById(R.id.State1);
        TextScore = findViewById(R.id.textScore);
        State2 = findViewById(R.id.State2);
        TextCityName = findViewById(R.id.TextName_CityOrState);
        SpeakButton = findViewById(R.id.SpeakButton);

        // Get user info from intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        bestScore = intent.getIntExtra("bestScore", 0);

        // Firebase user reference
        userRef = FirebaseDatabase.getInstance().getReference("users").child(username);

        // Load locations from CSV
        try {
            LocationDataStates locationDatabase = new LocationDataStates(this, "USLocations.csv");
            locations = locationDatabase.getLocations();
            remainingLocations = new ArrayList<>(locations);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Load sound effects
        correctSound = MediaPlayer.create(this, R.raw.correct_answer);
        wrongSound = MediaPlayer.create(this, R.raw.wrong_answer);
        newRecordSound = MediaPlayer.create(this, R.raw.new_record);

        // Set first question
        updateRandomLocation();

        // Button listeners
        State1.setOnClickListener(v -> checkAnswer(State1.getText().toString()));
        State2.setOnClickListener(v -> checkAnswer(State2.getText().toString()));
        SpeakButton.setOnClickListener(v -> speakLocation());

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
            }
        });

        // Start countdown
        startTimer();
    }

    private void updateRandomLocation() {
        // Resets the remaining list if empty
        if (remainingLocations.isEmpty()) {
            remainingLocations = new ArrayList<>(locations);
        }

        // Pick random location
        if (!remainingLocations.isEmpty()) {
            int randomNumber = random.nextInt(remainingLocations.size());
            currentLocation = remainingLocations.remove(randomNumber);
            TextScore.setText(String.valueOf(countScore));
            TextCityName.setText(currentLocation.getCityName());

            // Animation on city name
            Animation appearAnim = AnimationUtils.loadAnimation(this, R.anim.city_appear);
            Animation flickerAnim = AnimationUtils.loadAnimation(this, R.anim.city_flicker);
            TextCityName.startAnimation(appearAnim);
            TextCityName.startAnimation(flickerAnim);

            // Randomize answer position
            if (random.nextBoolean()) {
                State1.setText(currentLocation.getCorrectState());
                State2.setText(currentLocation.getWrongState());
            } else {
                State1.setText(currentLocation.getWrongState());
                State2.setText(currentLocation.getCorrectState());
            }
        } else {
            TextCityName.setText("No locations available!");
        }
    }

    private void checkAnswer(String selectedState) {
        // Correct answer
        if (selectedState.equals(currentLocation.getCorrectState())) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            if (correctSound != null) correctSound.start();
            countScore++;
            resetTimer();
            updateRandomLocation();
        } else {
            // Wrong answer
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
            if (wrongSound != null) wrongSound.start();
            youFailed();
        }
    }

    private void startTimer() {
        if (cdt != null) {
            cdt.cancel();
        }

        // Countdown logic
        cdt = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                tvCdt.setText(secondsLeft + "s");

                // Change color based on time left
                if (secondsLeft > 5) {
                    tvCdt.setTextColor(Color.parseColor("#03A9F4")); // Blue
                    tvCdt.clearAnimation();
                } else if (secondsLeft > 2) {
                    tvCdt.setTextColor(Color.parseColor("#FF9800")); // Orange
                    tvCdt.clearAnimation();
                } else if (secondsLeft == 1) {
                    tvCdt.setTextColor(Color.parseColor("#F44336")); // Red
                    startBlinkingAnimation(tvCdt); // Blink at 1 second
                } else {
                    tvCdt.setTextColor(Color.parseColor("#F44336"));
                    tvCdt.clearAnimation();
                }
            }

            public void onFinish() {
                tvCdt.setText("0s");
                tvCdt.setTextColor(Color.parseColor("#F44336"));
                tvCdt.clearAnimation();
                youFailed(); // Timeout → end game
            }
        }.start();
    }

    private void startBlinkingAnimation(TextView textView) {
        // Simple blink animation
        Animation blink = new AlphaAnimation(0.0f, 1.0f);
        blink.setDuration(300);
        blink.setInterpolator(new LinearInterpolator());
        blink.setRepeatMode(Animation.REVERSE);
        blink.setRepeatCount(Animation.INFINITE);
        textView.startAnimation(blink);
    }

    private void resetTimer() {
        cdt.cancel();
        startTimer(); // Restart timer
    }

    private void youFailed() {
        int finalScore = countScore;
        countScore = 0;

        // Save new best score if needed
        if (bestScore < finalScore) {
            userRef.child("bestScore").setValue(finalScore)
                    .addOnSuccessListener(aVoid -> {
                        if (newRecordSound != null) newRecordSound.start();
                        Toast.makeText(this, "New Record!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update score", Toast.LENGTH_SHORT).show());
        }

        // Return to start screen
        Intent toReg = new Intent(this, Real_startGame.class);
        startActivity(toReg);
    }

    private void speakLocation() {
        // Speak the name of the current city
        String locationName = TextCityName.getText().toString();
        textToSpeech.speak(locationName, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onDestroy() {
        // Release all resources on exit
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (correctSound != null) {
            correctSound.release();
            correctSound = null;
        }
        if (wrongSound != null) {
            wrongSound.release();
            wrongSound = null;
        }
        if (newRecordSound != null) {
            newRecordSound.release();
            newRecordSound = null;
        }
        super.onDestroy();
    }
}
