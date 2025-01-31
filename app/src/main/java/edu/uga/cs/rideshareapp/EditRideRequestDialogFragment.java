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


// This is a DialogFragment to handle edits to a RideRequest.
// The edits are: updates and deletions of existing JobLeads.
public class EditRideRequestDialogFragment extends DialogFragment {

    // indicate the type of an edit
    public static final int SAVE = 1;   // update an existing job lead
    public static final int DELETE = 2; // delete an existing job lead

    private EditText userIdView;
    private EditText toLocationView;
    private EditText fromLocationView;
    private EditText dateView;
    private EditText commentsView;

    int position;     // the position of the edited RideRequest on the list of job leads
    String key;
    String userId;
    String fromLocation;
    String toLocation;
    String date;
    String time;
    private int predefinedPointsCost = 10;  // Default points cost for a ride

    // A callback listener interface to finish up the editing of a RideRequest.
    // ReviewJobLeadsActivity implements this listener interface, as it will
    // need to update the list of JobLeads and also update the RecyclerAdapter to reflect the
    // changes.
    public interface EditRideRequestDialogListener {
        void updateRideRequest(int position, RideRequest rideRequest, int action);
    }

    public static EditRideRequestDialogFragment newInstance(int position, String key, String fromLocation, String toLocation, String date, String time, String userId) {
        EditRideRequestDialogFragment dialog = new EditRideRequestDialogFragment();

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
        final View layout = inflater.inflate( R.layout.fragment_add_ride_request_dialog, getActivity().findViewById( R.id.root ) );

        userIdView = layout.findViewById( R.id.editText1 );
        fromLocationView = layout.findViewById( R.id.editText2 );
        toLocationView = layout.findViewById( R.id.editText3 );
        dateView = layout.findViewById( R.id.editText4 );
        commentsView = layout.findViewById( R.id.editText5);

        // Pre-fill the edit texts with the current values for this job lead.
        // The user will be able to modify them.
        fromLocationView.setText( fromLocation );
        toLocationView.setText( toLocation );
        dateView.setText( date );
        commentsView.setText( time );

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
            String userId = userIdView.getText().toString();
            String fromLocation = fromLocationView.getText().toString();
            String toLocation = toLocationView.getText().toString();
            String date = dateView.getText().toString();
            String time = commentsView.getText().toString();
            RideRequest rideRequest = new RideRequest(userId, fromLocation, toLocation, date, time, predefinedPointsCost);
            rideRequest.setAccepted(false); // Default to not accepted when created
            rideRequest.setKey( key );

            // get the Activity's listener to add the new job lead
             EditRideRequestDialogListener listener = ( EditRideRequestDialogListener) getActivity();
            // add the new job lead
            listener.updateRideRequest( position, rideRequest, SAVE );

            // close the dialog
            dismiss();
        }
    }

    private class DeleteButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick( DialogInterface dialog, int which ) {

            RideRequest rideRequest = new RideRequest( userId, fromLocation, toLocation, date, time, predefinedPointsCost );
            rideRequest.setKey( key );

            // get the Activity's listener to add the new job lead
             EditRideRequestDialogListener listener = ( EditRideRequestDialogListener) getActivity();            // add the new job lead
             listener.updateRideRequest( position, rideRequest, DELETE );
            // close the dialog
            dismiss();
        }
    }
}
