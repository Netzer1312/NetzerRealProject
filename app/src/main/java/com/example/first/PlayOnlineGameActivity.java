package com.example.first;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PlayOnlineGameActivity extends AppCompatActivity {

    private String roomCode, username;
    private TextView textViewRoomCode;
    private DatabaseReference roomRef;
    private Room currentRoom;
    private boolean movedToGame = false;
    private ValueEventListener roomListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_online_game);

        // Get room code and username from intent
        roomCode = getIntent().getStringExtra("roomCode");
        username = getIntent().getStringExtra("username");

        // Display the room code on screen
        textViewRoomCode = findViewById(R.id.textViewRoomCode);
        textViewRoomCode.setText("Room Code: " + roomCode);

        // Firebase reference to the specific room
        roomRef = FirebaseDatabase.getInstance().getReference("rooms").child(roomCode);

        // Try to join the room
        joinRoom();
    }

    private void joinRoom() {
        // Check if room already exists in Firebase
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // Case 1: Room doesn't exist - create new room as player1
                    Room newRoom = new Room(roomCode, username);
                    newRoom.creator = username;
                    newRoom.player1 = username;
                    newRoom.player2 = "";
                    newRoom.isFull = false;
                    newRoom.gameStarted = false;
                    newRoom.gameReady = false;
                    newRoom.currentPlayerTurn = "";
                    roomRef.setValue(newRoom);

                    Toast.makeText(PlayOnlineGameActivity.this, "Room created, waiting...", Toast.LENGTH_SHORT).show();
                    listenForUpdates();
                } else {
                    Room room = snapshot.getValue(Room.class);
                    if (room == null) return;

                    // Case 2: User is already player1
                    if (username.equals(room.player1)) {
                        Toast.makeText(PlayOnlineGameActivity.this, "You're already player1, waiting...", Toast.LENGTH_SHORT).show();
                        listenForUpdates();
                        return;
                    }

                    // Case 3: User is player2 but gameReady is false (fixing issue)
                    if (username.equals(room.player2)) {
                        if (!room.gameReady) {
                            room.isFull = true;
                            room.gameStarted = true;
                            room.gameReady = true;
                            room.currentPlayerTurn = room.player1;
                            roomRef.setValue(room);
                            Toast.makeText(PlayOnlineGameActivity.this, "Fixed gameReady for existing player2", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PlayOnlineGameActivity.this, "You're already player2, waiting...", Toast.LENGTH_SHORT).show();
                        }
                        listenForUpdates();
                        return;
                    }

                    // Case 4: Room has empty player2 slot - join as player2
                    if (room.player2 == null || room.player2.trim().isEmpty()) {
                        room.player2 = username;
                        room.isFull = true;
                        room.gameStarted = true;
                        room.gameReady = true;
                        room.currentPlayerTurn = room.player1;
                        roomRef.setValue(room);

                        Toast.makeText(PlayOnlineGameActivity.this, "Joined as player2, waiting to start...", Toast.LENGTH_SHORT).show();
                        listenForUpdates();
                    } else {
                        // Case 5: Room is already full
                        Toast.makeText(PlayOnlineGameActivity.this, "Room is already full.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PlayOnlineGameActivity.this, "Firebase error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenForUpdates() {
        // Listen for changes in room state from Firebase
        roomListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    finish();
                    return;
                }

                currentRoom = snapshot.getValue(Room.class);
                if (currentRoom == null) return;

                // Wait until both players joined and gameReady is true
                if (!currentRoom.gameReady) return;

                if (!movedToGame &&
                        currentRoom.player1 != null && !currentRoom.player1.trim().isEmpty() &&
                        currentRoom.player2 != null && !currentRoom.player2.trim().isEmpty()) {

                    movedToGame = true;
                    Toast.makeText(PlayOnlineGameActivity.this, "Game ready! Starting now...", Toast.LENGTH_SHORT).show();
                    moveToGame(currentRoom);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PlayOnlineGameActivity.this, "Firebase error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        // Attach the listener to the room
        roomRef.addValueEventListener(roomListener);
    }

    private void moveToGame(Room room) {
        // Stop listening to room changes
        if (roomListener != null) {
            roomRef.removeEventListener(roomListener);
        }

        // Start the actual online game activity
        Intent intent = new Intent(PlayOnlineGameActivity.this, Real_GameOnline.class);
        intent.putExtra("roomCode", roomCode);
        intent.putExtra("username", username);
        intent.putExtra("player1", room.player1);
        intent.putExtra("player2", room.player2);
        startActivity(intent);
        finish();
    }
}
