package com.mobilization2017.settings;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.mobilization2017.R;

/**
 * Created by Pelevin Igor on 12.04.2017.
 */

public class SettingsChildFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_preferences, rootKey);
    }
}
