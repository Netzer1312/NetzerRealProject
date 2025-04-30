package com.example.first;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class LoserActivity extends AppCompatActivity {

    private Button buttonReturnToStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loser);

        buttonReturnToStart = findViewById(R.id.buttonReturnToStart);

        buttonReturnToStart.setOnClickListener(v -> {
            Intent intent = new Intent(LoserActivity.this, Real_startGame.class);
            startActivity(intent);
            finish();
        });
    }
}
