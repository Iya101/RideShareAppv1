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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Button acceptRideOfferButton;

        public RideOffersHolder(View itemView ) {
            super(itemView);

            userId = itemView.findViewById( R.id.userId);
            fromLocation = itemView.findViewById( R.id.fromLocation );
            toLocation = itemView.findViewById( R.id.toLocation );
            date = itemView.findViewById( R.id.date );
            time = itemView.findViewById( R.id.time );
            acceptRideOfferButton = itemView.findViewById(R.id.acceptRideOfferButton);

            acceptRideOfferButton.setOnClickListener(new View.OnClickListener() {
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
        String toLocation = rideOffer.getDestination();
        String time = rideOffer.getTime();
        String date = rideOffer.getDate();


        holder.userId.setText( rideOffer.getUserId());
        holder.fromLocation.setText(  rideOffer.getFromLocation());
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

    @Override
    public int getItemCount() {
        return rideOffersList.size();
    }



    public void acceptRideOffer(int position) {
        RideOffer rideOffer = rideOffersList.get(position);
        String riderId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Assumes user is logged in

        // Create a map to represent an accepted ride
        Map<String, Object> acceptedRide = new HashMap<>();
        acceptedRide.put("driverId", rideOffer.getUserId()); // the original poster is the driver
        acceptedRide.put("riderId", riderId);
        acceptedRide.put("fromLocation", rideOffer.getFromLocation());
        acceptedRide.put("toLocation", rideOffer.getDestination());
        acceptedRide.put("date", rideOffer.getDate());
        acceptedRide.put("time", rideOffer.getTime());
        acceptedRide.put("points", rideOffer.getPoints()); // assuming points are managed

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newAcceptedRef = dbRef.child("acceptedRides").push();
        newAcceptedRef.setValue(acceptedRide).addOnSuccessListener(aVoid -> {
            // Success callback
            Toast.makeText(context, "Ride offer accepted successfully!", Toast.LENGTH_SHORT).show();

            // Add this ride to both the driver's and rider's accepted rides list
            dbRef.child("users").child(rideOffer.getUserId()).child("acceptedRides").child(newAcceptedRef.getKey()).setValue(true);
            dbRef.child("users").child(riderId).child("acceptedRides").child(newAcceptedRef.getKey()).setValue(true);

            // Remove the offer from the list of available offers
            dbRef.child("rideOffers").child(rideOffer.getKey()).removeValue().addOnSuccessListener(aVoid1 -> {
                // Remove from the adapter's data set
                rideOffersList.remove(position);
                notifyItemRemoved(position);
            }).addOnFailureListener(e -> Toast.makeText(context, "Failed to remove ride offer.", Toast.LENGTH_SHORT).show());

            // Optional: Start an activity to show the ride details
            Intent intent = new Intent(context, AcceptedRidesActivity.class);
            intent.putExtra("rideKey", newAcceptedRef.getKey()); // Passing the unique key of the accepted ride
            context.startActivity(intent);
        }).addOnFailureListener(e -> Toast.makeText(context, "Failed to accept ride offer.", Toast.LENGTH_SHORT).show());
    }

}
