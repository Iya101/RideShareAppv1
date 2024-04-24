package edu.uga.cs.rideshareapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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

        public RideOffersHolder(View itemView ) {
            super(itemView);

            userId = itemView.findViewById( R.id.userId);
            fromLocation = itemView.findViewById( R.id.fromLocation );
            toLocation = itemView.findViewById( R.id.toLocation );
            date = itemView.findViewById( R.id.date );
            time = itemView.findViewById( R.id.time );
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
}
