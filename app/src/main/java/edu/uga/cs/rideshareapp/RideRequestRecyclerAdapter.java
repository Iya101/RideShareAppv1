package edu.uga.cs.rideshareapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * The RideRequestRecyclerAdapter, holds all information inputted by user, and the accept button.
 */
public class RideRequestRecyclerAdapter extends RecyclerView.Adapter<RideRequestRecyclerAdapter.RideRequestHolder> {

    public static final String DEBUG_TAG = "RideRequestRecyclerAdapter";

    private List<RideRequest> rideRequestsList;
    private Context context;

    public RideRequestRecyclerAdapter( List<RideRequest> rideRequestsList, Context context ) {
        this.rideRequestsList = rideRequestsList;
        this.context = context;
    }

    public boolean isRideRequestCard() {
        return true;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class RideRequestHolder extends RecyclerView.ViewHolder {

        TextView userId;
        TextView fromLocation;
        TextView toLocation;
        TextView date;
        TextView time;
        Button acceptButton;

        public RideRequestHolder(View itemView ) {
            super(itemView);
            userId = itemView.findViewById( R.id.userId);
            fromLocation = itemView.findViewById( R.id.fromLocation );
            toLocation = itemView.findViewById( R.id.toLocation );
            date = itemView.findViewById( R.id.date );
            time = itemView.findViewById( R.id.time );
            acceptButton = itemView.findViewById(R.id.acceptButton);

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        acceptRideRequest(position);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public RideRequestHolder onCreateViewHolder(ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.ride_request, parent, false );
        return new RideRequestHolder( view );
    }

    // This method fills in the values of the Views to show a RideRequest
    @Override
    public void onBindViewHolder( RideRequestHolder holder, int position ) {
        RideRequest rideRequest = rideRequestsList.get( position );

        Log.d( DEBUG_TAG, "onBindViewHolder: " + rideRequest );

        String key = rideRequest.getKey();
        String userId = rideRequest.getUserId();
        String fromLocation = rideRequest.getFromLocation();
        String toLocation = rideRequest.getToLocation();
        String date = rideRequest.getDate();
        String time = rideRequest.getTime();

        holder.userId.setText( rideRequest.getUserId());
        holder.fromLocation.setText(  rideRequest.getFromLocation());
        holder.toLocation.setText( rideRequest.getToLocation() );
        holder.date.setText( rideRequest.getDate() );
        holder.time.setText( rideRequest.getTime() ); //time is time

        // We can attach an OnClickListener to the itemView of the holder;
        // itemView is a public field in the Holder class.
        // It will be called when the user taps/clicks on the whole item, i.e., one of
        // the job leads shown.
        // This will indicate that the user wishes to edit (modify or delete) this item.
        // We create and show an EditRideRequestDialogFragment.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d( TAG, "onBindViewHolder: getItemId: " + holder.getItemId() );
                //Log.d( TAG, "onBindViewHolder: getAdapterPosition: " + holder.getAdapterPosition() );
                EditRideRequestDialogFragment editJobFragment =
                        EditRideRequestDialogFragment.newInstance( holder.getAdapterPosition(), key, userId, fromLocation, toLocation, date, time );
                editJobFragment.show( ((AppCompatActivity)context).getSupportFragmentManager(), null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rideRequestsList.size();
    }

    public void acceptRideRequest(int position) {
        if (position >= rideRequestsList.size()) {
            Log.e(DEBUG_TAG, "Invalid index. Position: " + position + ", List size: " + rideRequestsList.size());
            return; // Exit if the index is invalid
        }

        RideRequest rideRequest = rideRequestsList.get(position);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Assumes user is logged in
        rideRequest.setAccepted(true);
        // Update and push to acceptedRides
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newAcceptedRef = dbRef.child("acceptedRides").push();
        newAcceptedRef.setValue(rideRequest);

        // Remove from rideRequests
        DatabaseReference requestRef = dbRef.child("riderequests").child(rideRequest.getKey());
        requestRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(DEBUG_TAG, "Ride request successfully deleted from Firebase.");

                    if (position < rideRequestsList.size()) {
                        rideRequestsList.remove(position);
                        notifyItemRemoved(position);
                    }
                }).addOnFailureListener(e -> {
                    Log.e(DEBUG_TAG, "Failed to delete ride request from Firebase.", e);
                });


        Intent intent = new Intent(context, AcceptedRidesActivity.class);
        intent.putExtra("rideKey", newAcceptedRef.getKey()); // Passing the unique key of the accepted ride
        context.startActivity(intent);
    }



}
