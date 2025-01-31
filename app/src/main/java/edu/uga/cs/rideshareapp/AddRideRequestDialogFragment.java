package edu.uga.cs.rideshareapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

// A DialogFragment class to handle job lead additions from the job lead review activity
// It uses a DialogFragment to allow the input of a new job lead.
public class AddRideRequestDialogFragment extends DialogFragment {

    private EditText userIdView;
    private EditText fromLocationView;
    private EditText toLocationView;
    private EditText dateView;
    private EditText timeView;
    private int predefinedPointsCost = 10;  // Default points cost for a ride

    // This interface will be used to obtain the new job lead from an AlertDialog.
    // A class implementing this interface will handle the new job lead, i.e. store it
    // in Firebase and add it to the RecyclerAdapter.
    public interface AddRideRequestDialogListener {
        void addRideRequest(RideRequest rideRequest);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the AlertDialog view
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.fragment_add_ride_request_dialog,
                getActivity().findViewById(R.id.root));

        // get the view objects in the AlertDialog
        userIdView = layout.findViewById( R.id.editText1 );
        fromLocationView = layout.findViewById( R.id.editText2 );
        toLocationView = layout.findViewById( R.id.editText3 );
        dateView = layout.findViewById( R.id.editText4 );
        timeView = layout.findViewById( R.id.editText5);

        // create a new AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        // Set its view (inflated above).
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle( "New Ride Request" );
        // Provide the negative button listener
        builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });
        // Provide the positive button listener
        builder.setPositiveButton( android.R.string.ok, new AddRideRequestListener() );

        // Create the AlertDialog and show it
        return builder.create();
    }

    private class AddRideRequestListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // get the new job lead data from the user
            String userId = userIdView.getText().toString();
            String fromLocation = fromLocationView.getText().toString();
            String toLocation = toLocationView.getText().toString();
            String date = dateView.getText().toString();
            String time = timeView.getText().toString();

            // create a new RideRequest object
            RideRequest rideRequest = new RideRequest(userId, fromLocation, toLocation, date, time, predefinedPointsCost);
            rideRequest.setAccepted(false); //not accepted when created

            // get the Activity's listener to add the new job lead
            AddRideRequestDialogListener listener = (AddRideRequestDialogListener) getActivity();

            // add the new job lead
            listener.addRideRequest( rideRequest );

            // close the dialog
            dismiss();
        }
    }
}
