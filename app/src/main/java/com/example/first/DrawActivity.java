package com.example.first;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class DrawActivity extends AppCompatActivity {

    private Button buttonReturnToStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        buttonReturnToStart = findViewById(R.id.backButton);

        buttonReturnToStart.setOnClickListener(v -> {
            Intent intent = new Intent(DrawActivity.this, Real_startGame.class);
            startActivity(intent);
            finish();
        });
    }
}