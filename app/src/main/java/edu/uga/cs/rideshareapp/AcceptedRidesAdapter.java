package edu.uga.cs.rideshareapp;

import static edu.uga.cs.rideshareapp.RideRequestRecyclerAdapter.DEBUG_TAG;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.List;

public class AcceptedRidesAdapter extends RecyclerView.Adapter<AcceptedRidesAdapter.AcceptedRideViewHolder> {
    private List<RideRequest> acceptedRidesList;

    private Context context;

    // Constructor for the adapter
    public AcceptedRidesAdapter(List<RideRequest> acceptedRidesList, Context context) {
        this.acceptedRidesList = acceptedRidesList;
        this.context = context;
    }

    // ViewHolder class to hold the item view
    public static class AcceptedRideViewHolder extends RecyclerView.ViewHolder {
        public TextView dateView, fromView, toView, driverView, riderView, pointsView;
        public Button confirmButton;

        public AcceptedRideViewHolder(@NonNull View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.dateTextView);
            fromView = itemView.findViewById(R.id.fromTextView);
            toView = itemView.findViewById(R.id.toTextView);
            driverView = itemView.findViewById(R.id.driverTextView);
            riderView = itemView.findViewById(R.id.riderTextView);
            pointsView = itemView.findViewById(R.id.pointsTextView);
            confirmButton = itemView.findViewById(R.id.confirmButton);
        }
    }

    @NonNull
    @Override
    public AcceptedRideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_accepted_rides_adapter, parent, false);
        return new AcceptedRideViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AcceptedRideViewHolder holder, int position) {
        RideRequest ride = acceptedRidesList.get(position);

        if (ride != null) {
            if (holder.dateView != null) holder.dateView.setText(ride.getDate());
            if (holder.fromView != null) holder.fromView.setText(ride.getFromLocation());
            if (holder.toView != null) holder.toView.setText(ride.getToLocation());
            if (holder.driverView != null) holder.driverView.setText("Driver: " + ride.getDriverId());
            if (holder.riderView != null) holder.riderView.setText("Rider: " + ride.getUserId());
            if (holder.pointsView != null) holder.pointsView.setText("Points: " + ride.getPointsCost());

            holder.confirmButton.setOnClickListener(v -> {
                updatePoints(ride.getUserId(), ride.getDriverId(), ride.getPointsCost(), position);
                Toast.makeText(context, "Ride confirmed!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return acceptedRidesList.size();
    }


    private void updatePoints(String userId, String driverId, int pointsCost, int position) {
        if (userId == null || driverId == null) {
            Log.e(DEBUG_TAG, "User ID or Driver ID is null. Cannot update points.");
            return;
        }

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference riderRef = dbRef.child("users").child(userId).child("points");
        DatabaseReference driverRef = dbRef.child("users").child(driverId).child("points");

        // Update rider points
        riderRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer currentPoints = mutableData.getValue(Integer.class);
                if (currentPoints == null || currentPoints < pointsCost) {
                    return Transaction.abort();
                }
                mutableData.setValue(currentPoints - pointsCost);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (committed) {
                    // Successfully decreased rider's points, now increase driver's points
                    increaseDriverPoints(driverRef, pointsCost, position);
                } else {
                    Log.e(DEBUG_TAG, "Failed to decrease rider's points or points too low.");
                }
            }
        });
    }

    private void increaseDriverPoints(DatabaseReference driverRef, int pointsCost, int position) {
        driverRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer currentPoints = mutableData.getValue(Integer.class);
                if (currentPoints == null) {
                    mutableData.setValue(pointsCost);  // Set initial points if none exist
                } else {
                    mutableData.setValue(currentPoints + pointsCost);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (committed) {
                    Log.d(DEBUG_TAG, "Points updated successfully for driver.");
                    notifyItemChanged(position);
                } else {
                    Log.e(DEBUG_TAG, "Failed to update driver's points.");
                }
            }
        });
    }







}



