package com.example.humayan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class FragmentSettings extends Fragment {

    private Switch switchDarkMode;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_settings, container, false);

        // Inflate the layout for this fragment

        // Set the title for the toolbar
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Settings");
        }
        // Initialize shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        switchDarkMode = view.findViewById(R.id.switchDarkMode);

        // Load saved preferences
        boolean darkMode = sharedPreferences.getBoolean("dark_mode", false);
        switchDarkMode.setChecked(darkMode);

        // Save preference on switch change
        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("dark_mode", isChecked);
                editor.apply();

                // Restart the activity to apply the theme change
                getActivity().recreate();
            }
        });

        return view;
    }
}