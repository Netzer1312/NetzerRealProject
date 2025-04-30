package com.example.first;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Real_GameReg extends AppCompatActivity {

    Button City, State, SpeakButton;
    TextView TextScore, WeatherHint, tvCdt, TextName_CityOrState;
    CountDownTimer cdt;
    int countScore = 0;
    int bestScore;
    String username;
    List<Location> locations;
    List<Location> remainingLocations;
    Random random = new Random();
    TextToSpeech textToSpeech;
    private MediaPlayer correctSound;
    private MediaPlayer wrongSound;
    private MediaPlayer newRecordSound;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(); // Background thread for API call
    private final Handler handler = new Handler(); // Used to update UI from background thread

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_game_reg);

        // UI Elements initialization
        WeatherHint = findViewById(R.id.WeatherHint);
        tvCdt = findViewById(R.id.tvCdt);
        City = findViewById(R.id.City);
        TextScore = findViewById(R.id.textScore);
        State = findViewById(R.id.State);
        TextName_CityOrState = findViewById(R.id.TextName_CityOrState);
        SpeakButton = findViewById(R.id.SpeakButton);

        // Get data passed from previous activity
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        bestScore = intent.getIntExtra("bestScore", 0);

        // Load locations from CSV
        try {
            LocationDatabase locationDatabase = new LocationDatabase(this, "Locations.csv");
            locations = locationDatabase.getLocations();
            remainingLocations = new ArrayList<>(locations);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Load sound effects
        correctSound = MediaPlayer.create(this, R.raw.correct_answer);
        wrongSound = MediaPlayer.create(this, R.raw.wrong_answer);
        newRecordSound = MediaPlayer.create(this, R.raw.new_record);

        updateRandomLocation();

        // Button click listeners
        City.setOnClickListener(v -> checkAnswer(true));
        State.setOnClickListener(v -> checkAnswer(false));
        SpeakButton.setOnClickListener(v -> speakLocation());

        // Initialize text-to-speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
            }
        });

        startTimer();
    }

    private void updateRandomLocation() {
        if (remainingLocations.isEmpty()) {
            remainingLocations = new ArrayList<>(locations);
        }

        if (!remainingLocations.isEmpty()) {
            int index = random.nextInt(remainingLocations.size());
            Location randomLocation = remainingLocations.remove(index);
            TextScore.setText(String.valueOf(countScore));
            TextName_CityOrState.setText(randomLocation.getName());

            // Apply animations to the city name
            Animation appearAnim = AnimationUtils.loadAnimation(this, R.anim.city_appear);
            Animation flickerAnim = AnimationUtils.loadAnimation(this, R.anim.city_flicker);
            TextName_CityOrState.startAnimation(appearAnim);
            TextName_CityOrState.startAnimation(flickerAnim);

            // Get weather info for current city
            fetchWeather(randomLocation.getName());
        } else {
            TextName_CityOrState.setText("No locations available!");
        }
    }

    private void fetchWeather(String cityName) {
        executor.execute(() -> {
            try {
                // Call OpenWeatherMap API
                String apiKey = "3d2745270f571fdae81164efc130ac7f";
                String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey + "&units=metric";
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse weather JSON response
                JSONObject weatherJson = new JSONObject(response.toString());
                String description = weatherJson.getJSONArray("weather").getJSONObject(0).getString("description");
                double temp = weatherJson.getJSONObject("main").getDouble("temp");
                String result = "Weather: " + description + ", " + temp + "°C";

                // Update UI with weather info
                handler.post(() -> WeatherHint.setText(result));
            } catch (Exception e) {
                handler.post(() -> WeatherHint.setText("Weather unavailable"));
            }
        });
    }

    private void checkAnswer(boolean isCityGuess) {
        String currentLocationName = TextName_CityOrState.getText().toString();
        Location currentLocation = locations.stream()
                .filter(loc -> loc.getName().equals(currentLocationName))
                .findFirst()
                .orElse(null);

        if (currentLocation != null && currentLocation.isCity() == isCityGuess) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            if (correctSound != null) correctSound.start();
            countScore++;
            resetTimer();
            updateRandomLocation();
        } else {
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
            if (wrongSound != null) wrongSound.start();
            youFailed();
        }
    }

    private void startTimer() {
        if (cdt != null) {
            cdt.cancel();
        }

        // Countdown timer with color change and animation
        cdt = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                tvCdt.setText(secondsLeft + "s");
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
                youFailed();
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

    private void youFailed() {
        int finalScore = countScore;
        countScore = 0;
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(username);
        if (bestScore < finalScore) {
            userRef.child("bestScore").setValue(finalScore)
                    .addOnSuccessListener(aVoid -> {
                        if (newRecordSound != null) newRecordSound.start();
                        Toast.makeText(this, "New Record!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to save record", Toast.LENGTH_SHORT).show());
        }
        Intent toReg = new Intent(this, Real_startGame.class);
        toReg.putExtra("username", username);
        toReg.putExtra("bestScore", bestScore);
        startActivity(toReg);
    }

    private void speakLocation() {
        String locationName = TextName_CityOrState.getText().toString();
        textToSpeech.speak(locationName, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onDestroy() {
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
