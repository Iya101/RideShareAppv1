package edu.uga.cs.rideshareapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * this activity is for creating ride offers.
 */
public class RideOffersActivity extends  AppCompatActivity {

    private EditText destinationInput;
    private EditText fromLocationInput;
    private EditText timeInput;
    private Button postOfferButton;
    private Button dateButton;
    private DatabaseReference databaseReference;

    private String selectedDate;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_offers);

        fromLocationInput = findViewById(R.id.fromLocationInput);
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
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = currentUser.getEmail();  // Get the email of the signed-in user
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Email not available", Toast.LENGTH_SHORT).show();
            return;
        }
        String fromLocation = fromLocationInput.getText().toString();
        String destination = destinationInput.getText().toString();
        String time = timeInput.getText().toString();
        String date = selectedDate; // Use the selected date

        if (fromLocation.isEmpty()) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty() || time.isEmpty() || date == null) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        RideOffer newRideOffer = new RideOffer();
        newRideOffer.setOffer(true); // Assuming there's a method in RideOffer class to set this
        newRideOffer.setFromLocation(fromLocation);
        newRideOffer.setDestination(destination);
        newRideOffer.setTime(time);
        newRideOffer.setDate(date);
        newRideOffer.setUserId(userEmail);
        newRideOffer.setKey(databaseReference.push().getKey());  // Generate a unique key

        if (newRideOffer.getKey() != null) {
            // Creating a map to store ride offer details
            Map<String, Object> rideOfferMap = new HashMap<>();
            rideOfferMap.put("fromLocation", newRideOffer.getFromLocation());
            rideOfferMap.put("destination", newRideOffer.getDestination());
            rideOfferMap.put("time", newRideOffer.getTime());
            rideOfferMap.put("date", newRideOffer.getDate());
            rideOfferMap.put("userId", newRideOffer.getUserId());
            rideOfferMap.put("isOffer", true); // This field signifies that it is a ride offer

            databaseReference.child(newRideOffer.getKey()).setValue(rideOfferMap)
                    .addOnSuccessListener(aVoid -> Toast.makeText(RideOffersActivity.this, "Ride offer posted!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(RideOffersActivity.this, "Failed to post ride offer.", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Failed to generate unique offer ID.", Toast.LENGTH_SHORT).show();
        }
    }



}