package edu.uga.cs.rideshareapp;
import static edu.uga.cs.rideshareapp.RideRequestActivity.DEBUG_TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.uga.cs.rideshareapp.R;
import edu.uga.cs.rideshareapp.RideOffer;
import edu.uga.cs.rideshareapp.RideOffersRecyclerAdapter;

public class RideOffersActivity extends AppCompatActivity implements EditRideofferDialogFragment.EditRideOfferDialogListener  {
    private RecyclerView recyclerView;
    private RideOffersRecyclerAdapter recyclerAdapter;
    private List<RideOffer> rideOffersList;
    private FloatingActionButton floatingButton;
    private DatabaseReference databaseReference;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_offers);

        recyclerView = findViewById(R.id.recyclerView);
        floatingButton = findViewById(R.id.floatingActionButton);
        databaseReference = FirebaseDatabase.getInstance().getReference("RideOffers");

        rideOffersList = new ArrayList<>();
        recyclerAdapter = new RideOffersRecyclerAdapter(rideOffersList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);

        floatingButton.setOnClickListener(v -> showDatePickerDialog());
        loadRideOffers();
    }


    public void updateRideOffer(int position, RideOffer rideOffer, int action) {
        switch (action) {
            case EditRideofferDialogFragment.SAVE:
                updateRideOfferInDatabase(rideOffer);
                break;
            case EditRideofferDialogFragment.DELETE:
                deleteRideOfferFromDatabase(rideOffer);
                break;
        }
    }

    private void updateRideOfferInDatabase(RideOffer rideOffer) {
        DatabaseReference offerRef = databaseReference.child(rideOffer.getKey());
        offerRef.setValue(rideOffer)
                .addOnSuccessListener(aVoid -> Toast.makeText(RideOffersActivity.this, "Ride offer updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(RideOffersActivity.this, "Failed to update ride offer", Toast.LENGTH_SHORT).show());
    }

    private void deleteRideOfferFromDatabase(RideOffer rideOffer) {
        DatabaseReference offerRef = databaseReference.child(rideOffer.getKey());
        offerRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RideOffersActivity.this, "Ride offer deleted successfully", Toast.LENGTH_SHORT).show();
                    rideOffersList.remove(rideOffer);  // Update the local list
                    recyclerAdapter.notifyDataSetChanged();  // Notify the adapter to refresh the view
                })
                .addOnFailureListener(e -> Toast.makeText(RideOffersActivity.this, "Failed to delete ride offer", Toast.LENGTH_SHORT).show());
    }

    private void showDatePickerDialog() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, month, dayOfMonth);
            selectedDate = dateFormat.format(newDate.getTime());
            // Call function to create a new ride offer
            promptForNewRideOffer();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void promptForNewRideOffer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_new_ride_offer, null);
        builder.setView(view);

        EditText editTextDestination = view.findViewById(R.id.editTextDestination);
        EditText editTextTime = view.findViewById(R.id.editTextTime);
        Button buttonSubmit = view.findViewById(R.id.buttonSubmit);

        AlertDialog dialog = builder.create();

        buttonSubmit.setOnClickListener(v -> {
            String destination = editTextDestination.getText().toString().trim();
            String time = editTextTime.getText().toString().trim();

            if (destination.isEmpty() || time.isEmpty()) {
                Toast.makeText(RideOffersActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                postRideOffer(destination, time);
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void postRideOffer(String destination, String time) {
        Map<String, Object> rideOffer = new HashMap<>();
        rideOffer.put("destination", destination);
        rideOffer.put("time", time);
        rideOffer.put("date", selectedDate);
        rideOffer.put("driverId", FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.push().setValue(rideOffer)
                .addOnSuccessListener(aVoid -> Toast.makeText(RideOffersActivity.this, "Ride offer posted!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(RideOffersActivity.this, "Failed to post ride offer.", Toast.LENGTH_SHORT).show());
    }




    private void loadRideOffers() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rideOffersList.clear(); // Clear existing data to avoid duplicates
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RideOffer offer = snapshot.getValue(RideOffer.class);
                    if (offer != null) {
                        offer.setKey(snapshot.getKey()); // Assuming we have a method to set the key
                        rideOffersList.add(offer);
                    }
                }
                recyclerAdapter.notifyDataSetChanged(); // Notify the adapter that data has changed
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(DEBUG_TAG, "loadRideOffers:onCancelled", databaseError.toException()); // Handle possible errors
            }
        });
    }




}
