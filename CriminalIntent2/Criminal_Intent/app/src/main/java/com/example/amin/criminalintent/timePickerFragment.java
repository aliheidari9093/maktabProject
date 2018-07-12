package com.example.amin.criminalintent;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.sql.Time;


/**
 * A simple {@link Fragment} subclass.
 */
public class timePickerFragment extends DialogFragment {

    public static final String ARG_TIME="arg_time";

    public timePickerFragment() {
        // Required empty public constructor
    }

    public static timePickerFragment newInstance(Time time){
        Bundle args = new Bundle();
        args.getSerializable(ARG_TIME);

        timePickerFragment fragment = new timePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_time,null);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setTitle("time_picker")
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setView(view)
                .create();

        return alertDialog;
    }
}
