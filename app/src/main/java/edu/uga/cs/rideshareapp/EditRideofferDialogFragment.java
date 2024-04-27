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


// This is a DialogFragment to handle edits to a RideOffer.
// The edits are: updates and deletions of existing JobLeads.
public class EditRideofferDialogFragment extends DialogFragment {

    // indicate the type of an edit
    public static final int SAVE = 1;   // update an existing job lead
    public static final int DELETE = 2; // delete an existing job lead

    private EditText userIdView;
    private EditText toLocationView;
    private EditText fromLocationView;
    private EditText dateView;
    private EditText timeView;

    int position;     // the position of the edited RideOffer on the list of job leads
    String key;
    String userId;
    String offerId;
    String fromLocation;
    String toLocation;
    String date;
    String time;

    // A callback listener interface to finish up the editing of a RideOffer.
    // ReviewJobLeadsActivity implements this listener interface, as it will
    // need to update the list of JobLeads and also update the RecyclerAdapter to reflect the
    // changes.
    public interface EditRideOfferDialogListener {
        void updateRideOffer(int position, RideOffer rideOffer, int action);
    }

    public static EditRideofferDialogFragment newInstance(int position, String key, String fromLocation, String toLocation, String date, String time, String userId) {
        EditRideofferDialogFragment dialog = new EditRideofferDialogFragment();

        // Supply job lead values as an argument.
        Bundle args = new Bundle();
        args.putString( "key", key );
        args.putString( "userId", userId );
        args.putInt( "position", position );
        args.putString("from where", fromLocation);
        args.putString("to where", toLocation);
        args.putString("date", date);
        args.putString("time", time);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {

        key = getArguments().getString( "key" );
        position = getArguments().getInt( "position" );
        userId = getArguments().getString( "userId" );
        fromLocation = getArguments().getString( "from where" );
        toLocation = getArguments().getString( "phone" );
        date = getArguments().getString( "url" );
        time = getArguments().getString( "time" );

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate( R.layout.fragment_add_ride_offer_dialog, getActivity().findViewById( R.id.root ) );

        userIdView = layout.findViewById( R.id.editText1 );
        fromLocationView = layout.findViewById( R.id.editText2 );
        toLocationView = layout.findViewById( R.id.editText3 );
        dateView = layout.findViewById( R.id.editText4 );
        timeView = layout.findViewById( R.id.editText5);

        // Pre-fill the edit texts with the current values for this job lead.
        // The user will be able to modify them.
        fromLocationView.setText( fromLocation );
        toLocationView.setText( toLocation );
        dateView.setText( date );
        timeView.setText( time );

        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity(), R.style.AlertDialogStyle );
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle( "Edit Job Lead" );

        // The Cancel button handler
        builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });

        // The Save button handler
        builder.setPositiveButton( "SAVE", new SaveButtonClickListener() );

        // The Delete button handler
        builder.setNeutralButton( "DELETE", new DeleteButtonClickListener() );

        // Create the AlertDialog and show it
        return builder.create();
    }

    private class SaveButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String offerId = userIdView.getText().toString();
            String userId = userIdView.getText().toString();
            String fromLocation = fromLocationView.getText().toString();
            String toLocation = toLocationView.getText().toString();
            String date = dateView.getText().toString();
            String time = timeView.getText().toString();
            RideOffer rideOffer = new RideOffer( offerId, userId, fromLocation, toLocation, date,time  );
            rideOffer.setKey( key );

            // get the Activity's listener to add the new job lead
            EditRideOfferDialogListener listener = ( EditRideOfferDialogListener) getActivity();
            // add the new job lead
            listener.updateRideOffer( position, rideOffer, SAVE );

            // close the dialog
            dismiss();
        }
    }

    private class DeleteButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick( DialogInterface dialog, int which ) {

            RideOffer rideOffer = new RideOffer( offerId, userId, fromLocation, toLocation, date, time );
            rideOffer.setKey( key );

            // get the Activity's listener to add the new job lead
            EditRideOfferDialogListener listener = ( EditRideOfferDialogListener) getActivity();            // add the new job lead
            listener.updateRideOffer( position, rideOffer, DELETE );
            // close the dialog
            dismiss();
        }
    }
}
