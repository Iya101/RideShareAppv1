package edu.uga.cs.rideshareapp;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * AcceptedRidesAdapter, holds the card view of for displaying in the activity.
 */
public class AcceptedRidesAdapter extends RecyclerView.Adapter<AcceptedRidesAdapter.AcceptedRideViewHolder> {

    private List<Ride> acceptedRidesList;
    private Context context;

    // Constructor for the adapter
    public AcceptedRidesAdapter(List<Ride> acceptedRidesList) {
        this.acceptedRidesList = acceptedRidesList;
    }

    // ViewHolder class to hold the item view
    public static class AcceptedRideViewHolder extends RecyclerView.ViewHolder {
        public TextView offerView, dateView, fromView, toView, driverView, riderView, pointsView;
        public Button confirmButton;

        public AcceptedRideViewHolder(@NonNull View itemView) {
            super(itemView);
            offerView =  itemView.findViewById(R.id.offerTextView);
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
        Ride ride = acceptedRidesList.get(position);
        if (ride != null) {

            holder.offerView.setText(ride.isOffer() ? "Offer" : "Request");
            holder.dateView.setText(ride.getDate());
            holder.fromView.setText(ride.getFromLocation());
            holder.toView.setText(ride.getToLocation());
            holder.driverView.setText("Driver: " + ride.getDriverId());
            holder.riderView.setText("Rider: " + ride.getUserId());
            holder.pointsView.setText("Points: " + ride.getPointsCost());

            holder.confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ride.isOffer()) {
                        // If it's a ride offer, decrease rider points
                        // Add your logic here to decrease rider points
                        int currentPoints = ride.getPointsCost();
                        // Decrease points logic here
                        int newPoints = currentPoints - 10; // DECREASE_POINTS_AMOUNT is the amount to decrease
                        ride.setPointsCost(newPoints);
                        notifyDataSetChanged(); // Notify adapter of data change
                    } else {
                        // If it's a ride request, increase driver points
                        // Add your logic here to increase driver points
                        int currentPoints = ride.getPointsCost();
                        // Increase points logic here
                        int newPoints = currentPoints + 10; // INCREASE_POINTS_AMOUNT is the amount to increase
                        ride.setPointsCost(newPoints);
                        notifyDataSetChanged(); // Notify adapter of data change
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return acceptedRidesList.size();
    }
}







