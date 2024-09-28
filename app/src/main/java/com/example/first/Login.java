package com.example.first;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity {
     EditText UserEmailLg;
     EditText UserPasswordLg;
     Button btReg;
     Button btHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btReg = findViewById(R.id.btReg);
        btHome = findViewById(R.id.btHome);
        btReg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent toReg = new Intent( Login.this , Register.class);

                startActivity(toReg);
            }
        });
        btHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent toReg = new Intent( Login.this , MainActivity.class);

                startActivity(toReg);
            }
        });

    }
}