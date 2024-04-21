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

public class RideRequestRecyclerAdapter extends RecyclerView.Adapter<RideRequestRecyclerAdapter.RideRequestHolder> {

    public static final String DEBUG_TAG = "RideRequestRecyclerAdapter";

    private List<RideRequest> rideRequestsList;
    private Context context;

    public RideRequestRecyclerAdapter( List<RideRequest> rideRequestsList, Context context ) {
        this.rideRequestsList = rideRequestsList;
        this.context = context;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class RideRequestHolder extends RecyclerView.ViewHolder {

        TextView companyName;
        TextView phone;
        TextView url;
        TextView comments;

        public RideRequestHolder(View itemView ) {
            super(itemView);

            companyName = itemView.findViewById( R.id.companyName );
            phone = itemView.findViewById( R.id.phone );
            url = itemView.findViewById( R.id.url );
            comments = itemView.findViewById( R.id.comments );
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
        String company = rideRequest.getFromLocation();
        String phone = rideRequest.getToLocation();
        String url = rideRequest.getDate();
        String comments = rideRequest.getComments();

        holder.companyName.setText(  rideRequest.getFromLocation());
        holder.phone.setText( rideRequest.getToLocation() );
        holder.url.setText( rideRequest.getDate() );
        holder.comments.setText( rideRequest.getComments() );

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
                        EditRideRequestDialogFragment.newInstance( holder.getAdapterPosition(), key, company, phone, url, comments );
                editJobFragment.show( ((AppCompatActivity)context).getSupportFragmentManager(), null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rideRequestsList.size();
    }
}
