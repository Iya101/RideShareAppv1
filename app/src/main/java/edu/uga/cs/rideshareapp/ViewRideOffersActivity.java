package edu.uga.cs.rideshareapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewRideOffersActivity extends AppCompatActivity
        implements AddRideOfferDialogFragment.AddRideOfferDialogListener,
        EditRideOfferDialogFragment.EditRideOfferDialogListener {

    public static final String DEBUG_TAG = "ViewRideOffersActivity";

    private RecyclerView recyclerView;
    private RideOffersRecyclerAdapter recyclerAdapter;

    private List<RideOffer> rideOffersList;

    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ride_offers);

        recyclerView = findViewById( R.id.recyclerView );

        FloatingActionButton floatingButton = findViewById(R.id.floatingActionButton);
        floatingButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new AddRideOfferDialogFragment();
                newFragment.show( getSupportFragmentManager(), null);
            }
        });

        // initialize the ride request list
        rideOffersList = new ArrayList<RideOffer>();

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter with job leads is empty at first; it will be updated later
        recyclerAdapter = new RideOffersRecyclerAdapter( rideOffersList, ViewRideOffersActivity.this );
        recyclerView.setAdapter( recyclerAdapter );

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("rideoffers");

        // Set up a listener (event handler) to receive a value for the database reference.
        // This type of listener is called by Firebase once by immediately executing its onDataChange method
        // and then each time the value at Firebase changes.
        //
        // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
        // to maintain job leads.
        myRef.addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our ride request list.
                rideOffersList.clear(); // clear the current content; this is inefficient!
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    RideOffer rideOffer = postSnapshot.getValue(RideOffer.class);
                    rideOffer.setKey( postSnapshot.getKey() );
                    rideOffersList.add( rideOffer );
                    Log.d( DEBUG_TAG, "ValueEventListener: added: " + rideOffer );
                    Log.d( DEBUG_TAG, "ValueEventListener: key: " + postSnapshot.getKey() );
                }

                Log.d( DEBUG_TAG, "ValueEventListener: notifying recyclerAdapter" );
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {
                System.out.println( "ValueEventListener: reading failed: " + databaseError.getMessage() );
            }
        } );
    }

    // this is our own callback for a AddRideRequestDialogFragment which adds a new ride request.
    public void addRideOffer(RideOffer rideOffer) {
        // add the new ride request
        // Add a new element (rideOffer) to the list of job leads in Firebase.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("rideoffers");

        // First, a call to push() appends a new node to the existing list (one is created
        // if this is done for the first time).  Then, we set the value in the newly created
        // list node to store the new ride request.
        // This listener will be invoked asynchronously, as no need for an AsyncTask, as in
        // the previous apps to maintain job leads.
        myRef.push().setValue( rideOffer )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // Reposition the RecyclerView to show the rideOffer most recently added (as the last item on the list).
                        // Use of the post method is needed to wait until the RecyclerView is rendered, and only then
                        // reposition the item into view (show the last item on the list).
                        // the post method adds the argument (Runnable) to the message queue to be executed
                        // by Android on the main UI thread.  It will be done *after* the setAdapter call
                        // updates the list items, so the repositioning to the last item will take place
                        // on the complete list of items.
                        recyclerView.post( new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition( rideOffersList.size()-1 );
                            }
                        } );

                        Log.d( DEBUG_TAG, "Ride Offer saved: " + rideOffer );
                        // Show a quick confirmation
                        Toast.makeText(getApplicationContext(), "Ride Offer created for " + rideOffer.getDate(),
                                Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure( @NonNull Exception e ) {
                        Toast.makeText( getApplicationContext(), "Failed to create a ride request for " + rideOffer.getDate(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // This is our own callback for a DialogFragment which edits an existing rideOffer.
    // The edit may be an update or a deletion of this rideOffer.
    // It is called from the EditRideRequestDialogFragment.
    public void updateRideOffer( int position, RideOffer rideOffer, int action ) {
        if( action == EditRideRequestDialogFragment.SAVE ) {
            Log.d( DEBUG_TAG, "Updating ride request at: " + position + "(" + rideOffer.getDate() + ")" );

            // Update the recycler view to show the changes in the updated ride request in that view
            recyclerAdapter.notifyItemChanged( position );

            // Update this ride request in Firebase
            // Note that we are using a specific key (one child in the list)
            DatabaseReference ref = database
                    .getReference()
                    .child( "rideoffers" )
                    .child( rideOffer.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain job leads.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().setValue( rideOffer ).addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "updated ride request at: " + position + "(" + rideOffer.getDate() + ")" );
                            Toast.makeText(getApplicationContext(), "ride request updated for " + rideOffer.getDate(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to update ride request at: " + position + "(" + rideOffer.getDate() + ")" );
                    Toast.makeText(getApplicationContext(), "Failed to update " + rideOffer.getDate(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if( action == EditRideRequestDialogFragment.DELETE ) {
            Log.d( DEBUG_TAG, "Deleting ride request at: " + position + "(" + rideOffer.getDate() + ")" );

            // remove the deleted ride request from the list (internal list in the App)
            rideOffersList.remove( position );

            // Update the recycler view to remove the deleted ride request from that view
            recyclerAdapter.notifyItemRemoved( position );

            // Delete this ride request in Firebase.
            // Note that we are using a specific key (one child in the list)
            DatabaseReference ref = database
                    .getReference()
                    .child( "rideoffers" )
                    .child( rideOffer.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain job leads.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "deleted ride request at: " + position + "(" + rideOffer.getDate() + ")" );
                            Toast.makeText(getApplicationContext(), "ride request deleted for " + rideOffer.getDate(),
                                    Toast.LENGTH_SHORT).show();                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to delete ride request at: " + position + "(" + rideOffer.getDate() + ")" );
                    Toast.makeText(getApplicationContext(), "Failed to delete " + rideOffer.getDate(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
