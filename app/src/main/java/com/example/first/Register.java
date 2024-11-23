package com.example.first;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    Button bToGame;
    Button btToLog;
    EditText UserEmailR;
    EditText UserPasswordR;
    EditText UserPasswordC;
    EditText UserNameR;
    EditText UserPhoneR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//conect betwen them
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        UserEmailR = findViewById(R.id.UserEmailR);
        UserPasswordC = findViewById(R.id.UserPasswordC);
        UserPasswordR = findViewById(R.id.UserPasswordR);
        UserNameR = findViewById(R.id.UserNameR);
        UserPhoneR = findViewById(R.id.UserPhoneR);
        bToGame = findViewById(R.id.btToGame);
        btToLog = findViewById(R.id.btToLogIn);

        DateBaseHelper myDb;
        myDb = new DateBaseHelper(this);
       String EmailRe = UserEmailR.getText().toString();
       String PasswordRe = UserPasswordR.getText().toString();
       String NameRe = UserNameR.getText().toString();
       String PhoneRe = UserPhoneR.getText().toString();

        btToLog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                myDb.insertData(NameRe,PasswordRe,EmailRe,PhoneRe);
                if(PasswordRe == UserPasswordC.getText().toString()) {//check if the password and re-password are the same
                    Intent intent = new Intent(Register.this, Login.class);

                    startActivity(intent);
                }
                else{
                    Toast toast = Toast.makeText(Register.this,"The password are not same",Toast.LENGTH_SHORT);
                }
            }
        });

    }

}