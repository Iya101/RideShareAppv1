package edu.uga.cs.rideshareapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class UserPointsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_points);

        TextView driverPointsTextView = findViewById(R.id.textView3);
        TextView riderPointsTextView = findViewById(R.id.textView4);

        // Example values, replace with actual data fetching logic
        int driverPoints = 120;  // Fetch this value from your backend or data source
        int riderPoints = 85;    // Fetch this value from your backend or data source

        driverPointsTextView.setText("Driver Points: " + driverPoints);
        riderPointsTextView.setText("Rider Points: " + riderPoints);
    }
}