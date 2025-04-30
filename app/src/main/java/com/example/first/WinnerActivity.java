package com.example.first;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class WinnerActivity extends AppCompatActivity {

    private Button buttonReturnToStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);

        buttonReturnToStart = findViewById(R.id.buttonReturnToStart);

        String username = getIntent().getStringExtra("username");

        buttonReturnToStart.setOnClickListener(v -> {
            Intent intent = new Intent(WinnerActivity.this, Real_startGame.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        });
    }
}
