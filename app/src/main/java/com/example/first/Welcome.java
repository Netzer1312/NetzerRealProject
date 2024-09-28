package com.example.first;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

public class Welcome extends AppCompatActivity {
    CountDownTimer cdt;
    TextView tvCdt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        String message = "Welcome Michal I hope you will enjoy my app   ";

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();


        tvCdt = findViewById(R.id.tvCdt);
        cdt = new CountDownTimer(7000, 1000){

            public void onTick(long millisUntilFinished){

                String stCountDown = millisUntilFinished / 1000+" ";

                tvCdt.setText(String.valueOf(stCountDown));

            }

            public  void onFinish(){

                Intent toReg = new Intent( Welcome.this , Register.class);

                startActivity(toReg);

            }

        }.start();
    }
}