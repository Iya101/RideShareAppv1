package edu.uga.cs.rideshareapp;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AcceptedRidesAdapter extends RecyclerView.Adapter<AcceptedRidesAdapter.AcceptedRideViewHolder> {
    private List<RideRequest> acceptedRidesList;

    // Constructor for the adapter
    public AcceptedRidesAdapter(List<RideRequest> acceptedRidesList) {
        this.acceptedRidesList = acceptedRidesList;
    }

    // ViewHolder class to hold the item view
    public static class AcceptedRideViewHolder extends RecyclerView.ViewHolder {
        public TextView dateView, fromView, toView, driverView, riderView, pointsView;

        public AcceptedRideViewHolder(@NonNull View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.dateTextView);
            fromView = itemView.findViewById(R.id.fromTextView);
            toView = itemView.findViewById(R.id.toTextView);
            driverView = itemView.findViewById(R.id.driverTextView);
            riderView = itemView.findViewById(R.id.riderTextView);
            pointsView = itemView.findViewById(R.id.pointsTextView);
        }
    }

    @NonNull
    @Override
    public AcceptedRideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_accepted_rides, parent, false);
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
        }
    }

    @Override
    public int getItemCount() {
        return acceptedRidesList.size();
    }
}



