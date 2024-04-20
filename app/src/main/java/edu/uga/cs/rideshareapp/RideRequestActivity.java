package edu.uga.cs.rideshareapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RideRequestActivity extends AppCompatActivity {

    private EditText destinationEditText, timeEditText;
    private Button submitRequestButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_request);

        databaseReference = FirebaseDatabase.getInstance().getReference("RideRequests");

        destinationEditText = findViewById(R.id.destinationEditText);
        timeEditText = findViewById(R.id.timeEditText);
        submitRequestButton = findViewById(R.id.submitRequestButton);

        submitRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRideRequest();
            }
        });
    }

    private void submitRideRequest() {
        String destination = destinationEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();

        if (destination.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String requestId = databaseReference.push().getKey();
        RideRequest rideRequest = new RideRequest(requestId, destination, time);
        databaseReference.child(requestId).setValue(rideRequest)
                .addOnSuccessListener(aVoid -> Toast.makeText(RideRequestActivity.this, "Ride request submitted successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(RideRequestActivity.this, "Failed to submit ride request", Toast.LENGTH_SHORT).show());
    }
}