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

public class RideOffersRecyclerAdapter extends RecyclerView.Adapter<RideOffersRecyclerAdapter.RideOfferHolder> {

    public static final String DEBUG_TAG = "RideOffersRecyclerAdapter";

    private List<RideOffer> rideOffersList;
    private Context context;

    public RideOffersRecyclerAdapter(List<RideOffer> rideOffersList, Context context) {
        this.rideOffersList = rideOffersList;
        this.context = context;
    }

    class RideOfferHolder extends RecyclerView.ViewHolder {

        TextView destination;
        TextView date;
        TextView time;

        public RideOfferHolder(View itemView) {
            super(itemView);

            destination = itemView.findViewById(R.id.textViewDestination);
            date = itemView.findViewById(R.id.textViewDate);
            time = itemView.findViewById(R.id.textViewTime);
        }
    }

    @NonNull
    @Override
    public RideOfferHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_offer, parent, false);
        return new RideOfferHolder(view);
    }

    @Override
    public void onBindViewHolder(RideOfferHolder holder, int position) {
        RideOffer rideOffer = rideOffersList.get(position);

        Log.d(DEBUG_TAG, "onBindViewHolder: " + rideOffer);

        holder.destination.setText(rideOffer.getDestination());
        holder.date.setText(rideOffer.getDate());
        holder.time.setText(rideOffer.getTime());

        // Setup the click listener for the item view, if you need to handle item clicks
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the ride offer at this position
                int position = holder.getAdapterPosition(); // Get the current position
                RideOffer rideOffer = rideOffersList.get(position); // Get the ride offer object

                // Extract data from the ride offer object
                String key = rideOffer.getOfferId(); // Assuming getOfferId returns the unique key of the ride offer
                String userId = rideOffer.getUserId(); // Assuming getUserId returns the user ID of the person who posted the offer
                String destination = rideOffer.getDestination();
                String date = rideOffer.getDate();
                String time = rideOffer.getTime();

                // Create a new instance of the dialog fragment using the extracted data
                EditRideofferDialogFragment editRideOfferDialogFragment =
                        EditRideofferDialogFragment.newInstance(position, key, userId, destination, date, time);

                // Show the dialog fragment
                editRideOfferDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "editRideOfferDialog");
            }
        });

    }

    @Override
    public int getItemCount() {
        return rideOffersList.size();
    }
}
