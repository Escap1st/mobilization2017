package com.mobilization2017.settings;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobilization2017.R;
import com.mobilization2017.databinding.FragmentSettingsBinding;
import com.mobilization2017.databinding.ToolbarSettingsBinding;
import com.mobilization2017.main.MainActivity;

/**
 * Created by Pelevin Igor on 12.04.2017.
 */

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    private MainActivity parentActivity;
    private FragmentSettingsBinding fragmentSettingsBinding;
    private ToolbarSettingsBinding toolbarSettingsBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentSettingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        toolbarSettingsBinding = fragmentSettingsBinding.settingsToolbar;

        parentActivity = (MainActivity) getActivity();
        setChildFragment();

        return fragmentSettingsBinding.getRoot();
    }

    //дочерний фрагмент, из preferenceFragment вроде бы не создать toolbar
    private void setChildFragment() {
        SettingsChildFragment childFragment = new SettingsChildFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.settings_container, childFragment).commit();
    }
}
