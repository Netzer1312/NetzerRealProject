package com.example.first;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Register extends AppCompatActivity {

    Button bToGame;
    Button btToLog;
    EditText UserEmailR;
    EditText UserPasswordR;
    EditText UserNameR;
    EditText UserPhoneR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        UserEmailR = findViewById(R.id.UserEmailR);
        UserPasswordR = findViewById(R.id.UserPasswordR);
        UserNameR = findViewById(R.id.UserNameR);
        UserPhoneR = findViewById(R.id.UserPhoneR);
        bToGame = findViewById(R.id.btToGame);
        btToLog = findViewById(R.id.btToLogIn);

       String EmailRe = UserEmailR.getText().toString();
       String PasswordRe = UserPasswordR.getText().toString();
       String NameRe = UserNameR.getText().toString();
       String PhoneRe = UserPhoneR.getText().toString();

        btToLog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent intent = new Intent( Register.this , Login.class);

                startActivity(intent);
            }
        });

    }

}