package edu.uga.cs.rideshareapp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AcceptedRidesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AcceptedRidesAdapter adapter;
    private List<RideRequest> acceptedRidesList;

    public static final String TAG = "AcceptedRidesActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_rides);
        recyclerView = findViewById(R.id.recyclerView); // Ensure you have a RecyclerView with this ID in your layout

        // Initialize the list and adapter
        acceptedRidesList = new ArrayList<>();
        adapter = new AcceptedRidesAdapter(acceptedRidesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Retrieve the data from the Intent
        String rideKey = getIntent().getStringExtra("rideKey");
        if (rideKey != null) {
            fetchRideDetails(rideKey);
        } else {
            fetchAllAcceptedRides(); // Fetch all if no specific rideKey is provided
        }
    }

    private void fetchRideDetails(String rideKey) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference rideRef = dbRef.child("acceptedRides").child(rideKey);
        rideRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RideRequest rideRequest = dataSnapshot.getValue(RideRequest.class);
                if (rideRequest != null) {
                    acceptedRidesList.add(rideRequest);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadRide:onCancelled", databaseError.toException());
            }
        });
    }

    private void fetchAllAcceptedRides() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        Query query = dbRef.child("acceptedRides").orderByChild("date"); // Assuming each RideRequest has a 'date' field
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RideRequest rideRequest = snapshot.getValue(RideRequest.class);
                    if (rideRequest != null) {
                        acceptedRidesList.add(rideRequest);
                    }
                }
                Collections.sort(acceptedRidesList, (o1, o2) -> o1.getDate().compareTo(o2.getDate())); // Sort by date
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadAcceptedRides:onCancelled", databaseError.toException());
            }
        });
    }
}