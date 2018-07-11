package com.example.amin.criminalintent;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeDetailFragment extends Fragment {

    public static final int REQUEST_DATE = 0;
    public static final int REQUEST_PICK_CONTACT = 1;
    public static final String ARG_CRIME_ID = "crime_id";

    protected Crime mCrime;
    private EditText mCrimeTitle;
    private Button mCrimeDate;
    private CheckBox mCrimeSolved;
    private Button mCrimeSuspect;
    private Button mCrimeReport;

    public CrimeDetailFragment() {
        // Required empty public constructor
    }

    public static CrimeDetailFragment newInstance(UUID crimeId) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeDetailFragment fragment = new CrimeDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.getInstance(getActivity()).getCrime(crimeId);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.getInstance(getActivity()).updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crime_detail, container, false);

        mCrimeTitle = (EditText) view.findViewById(R.id.crime_title);
        mCrimeDate = (Button) view.findViewById(R.id.crime_date);
        mCrimeSolved = (CheckBox) view.findViewById(R.id.crime_solved);
        mCrimeSuspect = (Button) view.findViewById(R.id.crime_suspect);
        mCrimeReport = (Button) view.findViewById(R.id.crime_report);

        mCrimeDate.setText(mCrime.getDate().toString());
//        mCrimeDate.setEnabled(false);
        mCrimeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DatePickerFragment datePickerFragment = new DatePickerFragment();
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(mCrime.getDate());
                datePickerFragment.setTargetFragment(CrimeDetailFragment.this, REQUEST_DATE);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                datePickerFragment.show(fragmentManager, DatePickerFragment.DIALOG_DATE);
            }
        });

        mCrimeSolved.setChecked(mCrime.isSolved());
        mCrimeSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mCrimeTitle.setText(mCrime.getTitle());
        mCrimeTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (mCrime.getSuspect() != null)
            mCrimeSuspect.setText(mCrime.getSuspect());

        mCrimeSuspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_PICK_CONTACT);
            }
        });

        final Intent pickContactIntent = new Intent(Intent.ACTION_SEND);
        pickContactIntent.setType("text/plain");
        pickContactIntent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
        pickContactIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
        pickContactIntent.createChooser(pickContactIntent, getString(R.string.send_report));

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY) == null)
            mCrimeReport.setEnabled(false);

        mCrimeReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(pickContactIntent);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == REQUEST_DATE) {
            if (data == null)
                return;

            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            mCrimeDate.setText(mCrime.getDate().toString());
        } else if (requestCode == REQUEST_PICK_CONTACT) {
            if (data == null)
                return;

            Uri contactUri = data.getData();
            Log.d("Mohammad", contactUri.toString());
            String[] projection = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            Cursor cursor = getActivity().getContentResolver().query(contactUri, projection, null, null, null);

            if (cursor.getCount() == 0)
                return;

            try {
                cursor.moveToFirst();

                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                mCrime.setSuspect(displayName);
                mCrimeSuspect.setText(displayName);
            } finally {
                cursor.close();
            }

        }
    }

    private String getCrimeReport() {
        String report = "";

        String solvedString = null;
        if (mCrime.isSolved())
            solvedString = getString(R.string.crime_report_solved);
        else
            solvedString = getString(R.string.crime_report_unsolved);

        String suspectString = null;
        if (mCrime.getSuspect() == null)
            suspectString = getString(R.string.crime_report_no_suspect);
        else
            suspectString = getString(R.string.crime_report_suspect, mCrime.getSuspect());

        String dateString = new SimpleDateFormat("yyyy/MM/dd").format(mCrime.getDate());

        report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspectString);

        return report;
    }
}
