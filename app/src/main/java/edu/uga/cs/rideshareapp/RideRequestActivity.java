package edu.uga.cs.rideshareapp;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


/**
 * This is an activity class for listing the current job leads.
 * The current job leads are listed as a RecyclerView.
 */
public class RideRequestActivity
        extends AppCompatActivity
        implements AddRideRequestDialogFragment.AddRideRequestDialogListener,
        EditRideRequestDialogFragment.EditRideRequestDialogListener {

    public static final String DEBUG_TAG = "RideRequestActivity";

    private RecyclerView recyclerView;
    private RideRequestRecyclerAdapter recyclerAdapter;

    private List<RideRequest> rideRequestsList;

    private FirebaseDatabase database;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        Log.d( DEBUG_TAG, "onCreate()" );

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_ride_request );

        recyclerView = findViewById( R.id.recyclerView );

        FloatingActionButton floatingButton = findViewById(R.id.floatingActionButton);
        floatingButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new AddRideRequestDialogFragment();
                newFragment.show( getSupportFragmentManager(), null);
            }
        });

        // initialize the ride request list
        rideRequestsList = new ArrayList<RideRequest>();

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter with job leads is empty at first; it will be updated later
        recyclerAdapter = new RideRequestRecyclerAdapter( rideRequestsList, RideRequestActivity.this );
        recyclerView.setAdapter( recyclerAdapter );

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("riderequests");

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
                rideRequestsList.clear(); // clear the current content; this is inefficient!
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    RideRequest rideRequest = postSnapshot.getValue(RideRequest.class);
                    rideRequest.setKey( postSnapshot.getKey() );
                    rideRequestsList.add( rideRequest );
                    Log.d( DEBUG_TAG, "ValueEventListener: added: " + rideRequest );
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
    public void addRideRequest(RideRequest rideRequest) {
        // add the new ride request
        // Add a new element (rideRequest) to the list of job leads in Firebase.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("riderequests");

        // First, a call to push() appends a new node to the existing list (one is created
        // if this is done for the first time).  Then, we set the value in the newly created
        // list node to store the new ride request.
        // This listener will be invoked asynchronously, as no need for an AsyncTask, as in
        // the previous apps to maintain job leads.
        myRef.push().setValue( rideRequest )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // Reposition the RecyclerView to show the rideRequest most recently added (as the last item on the list).
                        // Use of the post method is needed to wait until the RecyclerView is rendered, and only then
                        // reposition the item into view (show the last item on the list).
                        // the post method adds the argument (Runnable) to the message queue to be executed
                        // by Android on the main UI thread.  It will be done *after* the setAdapter call
                        // updates the list items, so the repositioning to the last item will take place
                        // on the complete list of items.
                        recyclerView.post( new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition( rideRequestsList.size()-1 );
                            }
                        } );

                        Log.d( DEBUG_TAG, "Ride Request saved: " + rideRequest );
                        // Show a quick confirmation
                        Toast.makeText(getApplicationContext(), "Ride Request created for " + rideRequest.getDate(),
                                Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure( @NonNull Exception e ) {
                        Toast.makeText( getApplicationContext(), "Failed to create a ride request for " + rideRequest.getDate(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // This is our own callback for a DialogFragment which edits an existing rideRequest.
    // The edit may be an update or a deletion of this rideRequest.
    // It is called from the EditRideRequestDialogFragment.
    public void updateRideRequest( int position, RideRequest rideRequest, int action ) {
        if( action == EditRideRequestDialogFragment.SAVE ) {
            Log.d( DEBUG_TAG, "Updating ride request at: " + position + "(" + rideRequest.getDate() + ")" );

            // Update the recycler view to show the changes in the updated ride request in that view
            recyclerAdapter.notifyItemChanged( position );

            // Update this ride request in Firebase
            // Note that we are using a specific key (one child in the list)
            DatabaseReference ref = database
                    .getReference()
                    .child( "riderequests" )
                    .child( rideRequest.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain job leads.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().setValue( rideRequest ).addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "updated ride request at: " + position + "(" + rideRequest.getDate() + ")" );
                            Toast.makeText(getApplicationContext(), "ride request updated for " + rideRequest.getDate(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to update ride request at: " + position + "(" + rideRequest.getDate() + ")" );
                    Toast.makeText(getApplicationContext(), "Failed to update " + rideRequest.getDate(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if( action == EditRideRequestDialogFragment.DELETE ) {
            Log.d( DEBUG_TAG, "Deleting ride request at: " + position + "(" + rideRequest.getDate() + ")" );

            // remove the deleted ride request from the list (internal list in the App)
            rideRequestsList.remove( position );

            // Update the recycler view to remove the deleted ride request from that view
            recyclerAdapter.notifyItemRemoved( position );

            // Delete this ride request in Firebase.
            // Note that we are using a specific key (one child in the list)
            DatabaseReference ref = database
                    .getReference()
                    .child( "riderequests" )
                    .child( rideRequest.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain job leads.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "deleted ride request at: " + position + "(" + rideRequest.getDate() + ")" );
                            Toast.makeText(getApplicationContext(), "ride request deleted for " + rideRequest.getDate(),
                                    Toast.LENGTH_SHORT).show();                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to delete ride request at: " + position + "(" + rideRequest.getDate() + ")" );
                    Toast.makeText(getApplicationContext(), "Failed to delete " + rideRequest.getDate(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
}
