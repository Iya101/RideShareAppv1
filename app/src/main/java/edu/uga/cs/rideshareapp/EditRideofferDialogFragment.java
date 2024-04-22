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

public class EditRideofferDialogFragment extends DialogFragment {

    public static final int SAVE = 1;   // update an existing ride offer
    public static final int DELETE = 2; // delete an existing ride offer

    private EditText destinationView;
    private EditText dateView;
    private EditText timeView;

    int position;
    String key;
    String userId; // User ID of the offer owner
    String destination;
    String date;
    String time;

    public interface EditRideOfferDialogListener {
        void updateRideOffer(int position, RideOffer rideOffer, int action);
    }

    public static EditRideofferDialogFragment newInstance(int position, String key, String userId, String destination, String date, String time) {
        EditRideofferDialogFragment fragment = new EditRideofferDialogFragment();
        Bundle args = new Bundle();
        args.putString("key", key);
        args.putString("userId", userId);
        args.putInt("position", position);
        args.putString("destination", destination);
        args.putString("date", date);
        args.putString("time", time);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        key = getArguments().getString("key");
        userId = getArguments().getString("userId");
        position = getArguments().getInt("position");
        destination = getArguments().getString("destination");
        date = getArguments().getString("date");
        time = getArguments().getString("time");

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.fragment_edit_rideoffer_dialog, null);

        destinationView = layout.findViewById(R.id.editTextDestination);
        dateView = layout.findViewById(R.id.editTextDate);
        timeView = layout.findViewById(R.id.editTextTime);

        destinationView.setText(destination);
        dateView.setText(date);
        timeView.setText(time);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout)
                .setTitle("Edit Ride Offer")
                .setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> dialog.dismiss())
                .setPositiveButton("SAVE", new SaveButtonClickListener())
                .setNeutralButton("DELETE", new DeleteButtonClickListener());

        return builder.create();
    }

    private class SaveButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String newDestination = destinationView.getText().toString();
            String newDate = dateView.getText().toString();
            String newTime = timeView.getText().toString();

            // Use existing key and userId
            RideOffer rideOffer = new RideOffer(key, userId, newDestination, newDate, newTime);

            EditRideOfferDialogListener listener = (EditRideOfferDialogListener) getActivity();
            //assert listener != null;
            listener.updateRideOffer(getArguments().getInt("position"), rideOffer, SAVE);
            dismiss();
        }
    }

    private class DeleteButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            RideOffer rideOffer = new RideOffer(key, userId, destination, date, time);

            EditRideOfferDialogListener listener = (EditRideOfferDialogListener) getActivity();
            //assert listener != null;
            listener.updateRideOffer(getArguments().getInt("position"), rideOffer, DELETE);
            dismiss();
        }
    }
}
