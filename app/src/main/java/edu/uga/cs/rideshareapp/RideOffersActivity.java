package edu.uga.cs.rideshareapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RideOffersActivity extends AppCompatActivity {
    private EditText destinationInput;
    private EditText timeInput;
    private Button postOfferButton;

    private Button dateButton;
    private DatabaseReference databaseReference;

    private String selectedDate;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_offers);

        destinationInput = findViewById(R.id.destinationInput);
        timeInput = findViewById(R.id.timeInput);
        postOfferButton = findViewById(R.id.postOfferButton);
        dateButton = findViewById(R.id.dateButton);
        databaseReference = FirebaseDatabase.getInstance().getReference("RideOffers");


        postOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postRideOffer();
            }
        });
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
    }
    private void showDatePickerDialog() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year1, monthOfYear, dayOfMonth);
                    selectedDate = dateFormat.format(newDate.getTime());
                    dateButton.setText(selectedDate); // Display the selected date on the button
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }
    private void postRideOffer() {
        String destination = destinationInput.getText().toString();
        String time = timeInput.getText().toString();
        String date = selectedDate; // Use the selected date

        Map<String, Object> rideOffer = new HashMap<>();
        rideOffer.put("destination", destination);
        rideOffer.put("time", time);
        rideOffer.put("date", date); // Add the date here
        rideOffer.put("driverId", FirebaseAuth.getInstance().getCurrentUser().getUid());

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

       /* FirebaseDatabase.getInstance().getReference().child("rideOffers").push().setValue(rideOffer)
                .addOnSuccessListener(aVoid -> Toast.makeText(RideOffersActivity.this, "Ride offer posted!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(RideOffersActivity.this, "Failed to post ride offer.", Toast.LENGTH_SHORT).show());*/

        RideOffer rideOffers = new RideOffer(databaseReference.push().getKey(), userId, destination, date, time);
        databaseReference.child("RideOffers").child(rideOffers.getOfferId()).setValue(rideOffer)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                    Toast.makeText(RideOffersActivity.this, "Ride offer posted!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(RideOffersActivity.this, "Failed to post ride offer.", Toast.LENGTH_SHORT).show();
                });
    }
}