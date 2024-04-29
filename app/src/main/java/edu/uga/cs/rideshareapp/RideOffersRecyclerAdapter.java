package edu.uga.cs.rideshareapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RideOffersRecyclerAdapter extends RecyclerView.Adapter<RideOffersRecyclerAdapter.RideOffersHolder> {

    public static final String DEBUG_TAG = "RideOffersRecyclerAdapter";

    private List<RideOffer> rideOffersList;
    private Context context;

    public RideOffersRecyclerAdapter( List<RideOffer> rideOffersList, Context context ) {
        this.rideOffersList = rideOffersList;
        this.context = context;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class RideOffersHolder extends RecyclerView.ViewHolder {

        TextView userId;
        TextView fromLocation;
        TextView toLocation;
        TextView date;
        TextView time;
        Button acceptButton;

        public RideOffersHolder(View itemView ) {
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
                        acceptRideOffer(position);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public RideOffersHolder onCreateViewHolder(ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.ride_offer, parent, false );
        return new RideOffersHolder( view );
    }

    // This method fills in the values of the Views to show a RideOffer
    @Override
    public void onBindViewHolder( RideOffersHolder holder, int position ) {
        RideOffer rideOffer = rideOffersList.get( position );

        Log.d( DEBUG_TAG, "onBindViewHolder: " + rideOffer );

        String key = rideOffer.getKey();
        String userId = rideOffer.getUserId();
        String fromLocation = rideOffer.getFromLocation();
        Log.d(DEBUG_TAG, "From Location: " + rideOffer.getFromLocation());

        String toLocation = rideOffer.getDestination();
        Log.d(DEBUG_TAG, "GEt destination: " + rideOffer.getDestination());
        String time = rideOffer.getTime();
        String date = rideOffer.getDate();


        holder.userId.setText( rideOffer.getUserId());
        holder.fromLocation.setText( rideOffer.getFromLocation());
        holder.toLocation.setText( rideOffer.getDestination() );
        holder.time.setText( rideOffer.getTime() );
        holder.date.setText( rideOffer.getDate() );


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
                        EditRideRequestDialogFragment.newInstance( holder.getAdapterPosition(), key, userId, fromLocation, toLocation, time, date );
                editJobFragment.show( ((AppCompatActivity)context).getSupportFragmentManager(), null);
            }
        });
    }
    public boolean isRideRequestCard() {
        return false;
    }
    @Override
    public int getItemCount() {
        return rideOffersList.size();
    }

    public void acceptRideOffer(int position) {
        RideOffer newRideOffer = new RideOffer();
        newRideOffer.setOffer(true);

        if (position >= rideOffersList.size()) {
            Log.e(DEBUG_TAG, "Invalid index. Position: " + position + ", List size: " + rideOffersList.size());
            return; // Exit if the index is invalid
        }

        RideOffer rideOffer = rideOffersList.get(position);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Assumes user is logged in
        rideOffer.setAccepted(true);
        // Update and push to acceptedRides
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newAcceptedRef = dbRef.child("acceptedRides").push();
        newAcceptedRef.setValue(rideOffer);
        newRideOffer.setOffer(true); //set ride offer as an offer

        // Remove from rideRequests
        DatabaseReference requestRef = dbRef.child("RideOffers").child(rideOffer.getKey());
        requestRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(DEBUG_TAG, "Ride request successfully deleted from Firebase.");

                    if (position < rideOffersList.size()) {
                        rideOffersList.remove(position);
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
