package com.example.amin.criminalintent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Amin on 6/27/2018.
 */

public class CrimeLab {
    private List<Crime> mCrimes;
    private static CrimeLab mCrimeLab;

    public List<Crime> getmCrimes() {
        return mCrimes;
    }

    private CrimeLab(){
        mCrimes = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0);

            mCrimes.add(crime);
        }
    }

    public static CrimeLab getInstance() {
        if (mCrimeLab == null) {
            mCrimeLab = new CrimeLab();
        }

        return mCrimeLab;
    }

    public Crime getCrime(UUID crimeId) {
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(crimeId)) {
                return crime;
            }
        }

        return null;
    }

    public int getIndex(UUID crimeId) {
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId))
                return i;
        }

        return -1;
    }
}
