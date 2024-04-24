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
public class AddRideOfferDialogFragment extends DialogFragment {

    private EditText userIdView;
    private EditText fromLocationView;
    private EditText toLocationView;
    private EditText dateView;
    private EditText timeView;

    // This interface will be used to obtain the new job lead from an AlertDialog.
    // A class implementing this interface will handle the new job lead, i.e. store it
    // in Firebase and add it to the RecyclerAdapter.
    public interface AddRideOfferDialogListener {
        void addRideOffer(RideOffer rideOffer);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the AlertDialog view
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.fragment_add_ride_offer_dialog,
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
        builder.setPositiveButton( android.R.string.ok, new AddRideOfferListener() );

        // Create the AlertDialog and show it
        return builder.create();
    }

    private class AddRideOfferListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // get the new job lead data from the user
            String offerId = userIdView.getText().toString();
            String userId = userIdView.getText().toString();
            String fromLocation = fromLocationView.getText().toString();
            String toLocation = toLocationView.getText().toString();
            String date = dateView.getText().toString();
            String time = timeView.getText().toString();

            // create a new RideOffer object
            RideOffer rideOffer = new RideOffer( offerId, userId, fromLocation, toLocation, date, time );

            // get the Activity's listener to add the new job lead
            AddRideOfferDialogListener listener = (AddRideOfferDialogListener) getActivity();

            // add the new job lead
            listener.addRideOffer( rideOffer );

            // close the dialog
            dismiss();
        }
    }
}
