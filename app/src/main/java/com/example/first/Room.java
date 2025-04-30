package com.example.first;

public class Room {
    public String roomCode;
    public String player1;
    public String player2;
    public boolean isFull;
    public boolean gameStarted;
    public String currentPlayerTurn;
    public boolean gameReady;
    public String creator; // 🆕 מי יצר את החדר

    public Room() {
        // נדרש ל-Firebase
    }

    public Room(String roomCode, String player1) {
        this.roomCode = roomCode;
        this.player1 = player1;
        this.player2 = "";
        this.isFull = false;
        this.gameStarted = false;
        this.currentPlayerTurn = "";
        this.gameReady = false;
        this.creator = ""; // מוסיפים ברירת מחדל
    }
}