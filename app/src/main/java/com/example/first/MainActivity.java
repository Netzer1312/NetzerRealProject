package com.example.first;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    ImageView Logo;
    TextView LogoName;
    Button BtStart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logo = findViewById(R.id.Logo);
        LogoName = findViewById(R.id.LogoName);
        BtStart = findViewById(R.id.BtStart);

        BtStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent intent = new Intent( MainActivity.this , Welcome.class);

                startActivity(intent);
            }
        });
    }
}