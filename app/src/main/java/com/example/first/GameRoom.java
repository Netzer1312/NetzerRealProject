package com.example.first;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class GameRoom extends AppCompatActivity {

    private EditText editTextRoomCode; // Input for entering a room code
    private Button buttonCreateRoom, buttonJoinRoom; // Buttons to create or join a room

    private DatabaseReference roomsRef; // Firebase reference to "rooms" node

    private String username = "Player_" + new Random().nextInt(1000); // Temporary random username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room); // Load the layout

        // Bind UI elements
        editTextRoomCode = findViewById(R.id.editTextRoomCode);
        buttonCreateRoom = findViewById(R.id.buttonCreateRoom);
        buttonJoinRoom = findViewById(R.id.buttonJoinRoom);

        // Connect to Firebase "rooms" node
        roomsRef = FirebaseDatabase.getInstance().getReference("rooms");

        // Button listeners
        buttonCreateRoom.setOnClickListener(v -> createRoom());
        buttonJoinRoom.setOnClickListener(v -> joinRoom());
    }

    private void createRoom() {
        // Generate a 4-digit random room code
        String roomCode = generateRoomCode();

        // Create a new Room object with current user as creator and player1
        Room room = new Room(roomCode, username);

        // Save the room in Firebase
        roomsRef.child(roomCode).setValue(room).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // If success, go to PlayOnlineGameActivity as player1
                Intent intent = new Intent(GameRoom.this, PlayOnlineGameActivity.class);
                intent.putExtra("roomCode", roomCode);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to create room", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void joinRoom() {
        // Get room code from input
        String roomCode = editTextRoomCode.getText().toString().trim();

        if (roomCode.isEmpty()) {
            Toast.makeText(this, "Please enter a room code", Toast.LENGTH_SHORT).show();
            return;
        }

        // Try to find the room in Firebase
        roomsRef.child(roomCode).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // Room not found
                    Toast.makeText(GameRoom.this, "Room not found", Toast.LENGTH_SHORT).show();
                } else {
                    // Room exists
                    Room room = snapshot.getValue(Room.class);
                    if (room != null && !room.isFull) {
                        // Add current user as player2
                        room.player2 = username;
                        room.isFull = true;

                        // Update room in Firebase
                        roomsRef.child(roomCode).setValue(room);

                        // Move to game screen as player2
                        Intent intent = new Intent(GameRoom.this, PlayOnlineGameActivity.class);
                        intent.putExtra("roomCode", roomCode);
                        intent.putExtra("username", username);
                        startActivity(intent);
                        finish();
                    } else {
                        // Room already full
                        Toast.makeText(GameRoom.this, "Room already full", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle Firebase read error
                Toast.makeText(GameRoom.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String generateRoomCode() {
        // Generate a random 4-digit number as a room code
        int code = 1000 + new Random().nextInt(9000);
        return String.valueOf(code);
    }
}
