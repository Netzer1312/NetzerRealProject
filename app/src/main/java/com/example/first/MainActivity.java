package com.example.first;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class MainActivity extends AppCompatActivity {

    ImageView Logo;
    TextView LogoName;
    Button BtStart;

    DateBaseHelper myDb;

    private static final String CHANNEL_ID = "example_channel"; // Unique ID for the notification channel

    @Override
    protected void onCreate(Bundle savedInstanceState) {//conect betwen them
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logo = findViewById(R.id.Logo);
        LogoName = findViewById(R.id.LogoName);
        BtStart = findViewById(R.id.BtStart);


        createNotificationChannel();
            //intent between the main and the welcom
        BtStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showNotification();


                Intent intent = new Intent(MainActivity.this, Welcome.class);
                startActivity(intent);
            }
        });
    }

        public void viewAll(){ /// get out the data i chose not to show the data
        Cursor res = myDb.getAllData();
        if(res.getCount() == 0){
            //There are no data
            return;
        }
        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()){
            buffer.append("ID :"+ res.getString(0)+"\n");
            buffer.append("Name :"+ res.getString(1)+"\n");
            buffer.append("PASSWORD :"+ res.getString(2)+"\n");
            buffer.append("EMAIL :"+ res.getString(3)+"\n");
            buffer.append("PHONE_NUMBER :"+ res.getString(5)+"\n\n");


        }
        }
        //create the notification
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Example Channel"; // Name for the channel
            String description = "Come break you highst score!";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //Show Notification when button is clicked
    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.m_beenz) // Set a small icon for the notification
                .setContentTitle("Welcome!") // Title of the notification
                .setContentText("You clicked the start button!") // Text of the notification
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); // Notification priority

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build()); // Show the notification (ID = 1)
    }
}