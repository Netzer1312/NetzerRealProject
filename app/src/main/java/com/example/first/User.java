package com.example.first;

public class User {
    public String name;
    public String email;
    public String password;
    public String phone;
    public int bestScore;

    // קונסטרקטור ריק - חובה ל-Firebase
    public User() {}

    public User(String name, String email, String password, String phone, int bestScore) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.bestScore = bestScore;
    }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
    public int getBestScore() { return bestScore; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setBestScore(int bestScore) { this.bestScore = bestScore; }


}

