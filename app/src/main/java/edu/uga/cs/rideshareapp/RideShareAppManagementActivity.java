package edu.uga.cs.rideshareapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RideShareAppManagementActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "ManagementActivity";
    private TextView signedInTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_share_app_management);
        Log.d( DEBUG_TAG, "RideShareAppManagementActivity.onCreate()" );

        Button viewRideRequests = findViewById(R.id.button1);
        Button createRideOffers = findViewById(R.id.button2);
        Button viewRideOffers = findViewById(R.id.button3);
        Button logoutButton = findViewById(R.id.logoutButton);
        signedInTextView = findViewById( R.id.textView3 );


        viewRideRequests.setOnClickListener( new RideRequestButtonClickListener() );
        createRideOffers.setOnClickListener( new RideOffersButtonClickListener() );
        viewRideOffers.setOnClickListener(new ViewRideOffersButtonClickListener() );
        logoutButton.setOnClickListener(new LogoutButtonClickListener());


        // Setup a listener for a change in the sign in status (authentication status change)
       // when it is invoked, check if a user is signed in and update the UI text view string,
       // as needed.
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if( currentUser != null ) {
                // User is signed in
                Log.d(DEBUG_TAG, "onAuthStateChanged:signed_in:" + currentUser.getUid());
                String userEmail = currentUser.getEmail();
                signedInTextView.setText( "Signed in as: " + userEmail );
            } else {
                // User is signed out
                Log.d( DEBUG_TAG, "onAuthStateChanged:signed_out" );
                signedInTextView.setText( "Signed in as: not signed in" );
            }
        }
    });
 }


    private class RideRequestButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), RideRequestActivity.class);
            view.getContext().startActivity( intent );
        }
    }

    private class RideOffersButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), RideOffersActivity.class);
            view.getContext().startActivity(intent);
        }
    }
    private class ViewRideOffersButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), ViewRideOffersActivity.class);
            view.getContext().startActivity(intent);
        }
    }

    private class LogoutButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.d(DEBUG_TAG, "Attempting to sign out.");
            FirebaseAuth.getInstance().signOut(); // Sign out from Firebase
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);
            finish();
        }
    }

    // These activity callback methods are not needed and are for educational purposes only
    @Override
    protected void onStart() {
        Log.d( DEBUG_TAG, "RideShareApp: ManagementActivity.onStart()" );
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d( DEBUG_TAG, "RideShareApp: ManagementActivity.onResume()" );
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d( DEBUG_TAG, "RideShareApp: ManagementActivity.onPause()" );
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d( DEBUG_TAG, "RideShareApp: ManagementActivity.onStop()" );
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d( DEBUG_TAG, "RideShareApp: ManagementActivity.onDestroy()" );
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d( DEBUG_TAG, "RideShareApp: ManagementActivity.onRestart()" );
        super.onRestart();
    }
}