package edu.uga.cs.rideshareapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
        recyclerView = findViewById(R.id.recyclerView);

        // Initialize the list and adapter
        acceptedRidesList = new ArrayList<>();
        adapter = new AcceptedRidesAdapter(acceptedRidesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Fetch all accepted rides
        fetchAllAcceptedRides();

        // Check if a specific rideKey was provided for focused details
       // String rideKey = getIntent().getStringExtra("rideKey");
        //if (rideKey != null) {
        //    fetchRideDetails(rideKey); // This could be used to highlight or focus on a specific ride
        //}
    }

    private void fetchRideDetails(String rideKey) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference rideRef = dbRef.child("acceptedRides").child(rideKey);
        rideRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Raw data: " + dataSnapshot.toString());
                try {
                    RideRequest rideRequest = dataSnapshot.getValue(RideRequest.class);
                    if (rideRequest != null) {
                        acceptedRidesList.add(rideRequest);
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "Ride details added to list, list size now: " + acceptedRidesList.size());
                    }
                } catch (DatabaseException e) {
                    Log.e(TAG, "Failed to convert data to RideRequest", e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadRide:onCancelled", databaseError.toException());
            }
        });
    }

    private void fetchAllAcceptedRides() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("acceptedRides");
        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "Child added with data: " + dataSnapshot.getValue());
                RideRequest newRide = dataSnapshot.getValue(RideRequest.class);
                if (newRide != null) {
                    acceptedRidesList.add(newRide);
                    adapter.notifyItemInserted(acceptedRidesList.size() - 1);
                    Log.d(TAG, "New ride added to list, total rides: " + acceptedRidesList.size());
                } else {
                    Log.d(TAG, "Failed to parse data into RideRequest");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadAcceptedRides:onCancelled", databaseError.toException());
            }
        });
    }
}
