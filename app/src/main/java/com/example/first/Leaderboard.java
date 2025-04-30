package com.example.first;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Leaderboard extends AppCompatActivity {

    private ListView listView;               // ListView to display top users
    private CustomAdapter adapter;           // Custom adapter for user display
    private List<User> topUsers = new ArrayList<>(); // Holds the top 5 users

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard); // Load layout

        listView = findViewById(R.id.listViewLeaderboard);
        adapter = new CustomAdapter();       // Create adapter instance
        listView.setAdapter(adapter);        // Bind adapter to ListView

        // Animate the list view items appearing
        listView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.list_item_animation));

        fetchTopUsers();                     // Fetch user scores from Firebase

        // Back button closes the activity
        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());
    }

    private void fetchTopUsers() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Get all user data once from the database
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topUsers.clear();
                Log.d("Leaderboard_Debug", "Starting snapshot: " + snapshot.getChildrenCount());

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Log.d("Leaderboard_Debug", "Raw user snapshot: " + userSnapshot.getValue());
                    User user = userSnapshot.getValue(User.class);

                    // Add only valid users (non-null name)
                    if (user != null && user.getName() != null && !user.getName().isEmpty()) {
                        Log.d("Leaderboard_Debug", "Parsed user: name=" + user.getName() + ", bestScore=" + user.getBestScore());
                        topUsers.add(user);
                    }
                }

                // Sort users by bestScore descending
                Collections.sort(topUsers, (u1, u2) -> Integer.compare(u2.getBestScore(), u1.getBestScore()));

                // Keep only top 5 users
                if (topUsers.size() > 5) {
                    topUsers = new ArrayList<>(topUsers.subList(0, 5));
                }

                Log.d("Leaderboard_Debug", "Top users loaded: " + topUsers.size());

                // Update the adapter
                adapter.clear();
                adapter.addAll(topUsers);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Leaderboard_Debug", "Failed to load users", error.toException());
                showToast("Error: Database error (Code 3)");
            }
        });
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(Leaderboard.this, message, Toast.LENGTH_LONG).show());
    }

    // Custom adapter to show ranked users with color and formatting
    private class CustomAdapter extends ArrayAdapter<User> {
        public CustomAdapter() {
            super(Leaderboard.this, android.R.layout.simple_list_item_1, topUsers);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull android.view.ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = view.findViewById(android.R.id.text1);

            User user = topUsers.get(position);
            textView.setText((position + 1) + ". " + user.getName() + " - " + user.getBestScore() + " pts");
            textView.setTextSize(20);

            // Set text color by rank: Gold, Silver, Bronze
            switch (position) {
                case 0:
                    textView.setTextColor(Color.parseColor("#FFD700")); // Gold
                    break;
                case 1:
                    textView.setTextColor(Color.parseColor("#C0C0C0")); // Silver
                    break;
                case 2:
                    textView.setTextColor(Color.parseColor("#CD7F32")); // Bronze
                    break;
                default:
                    textView.setTextColor(Color.BLACK); // Default color
                    break;
            }

            return view;
        }
    }
}
