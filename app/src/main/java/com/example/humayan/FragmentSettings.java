package com.example.humayan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class FragmentSettings extends Fragment {

    private Switch switchDarkMode;
    private SharedPreferences sharedPreferences;
    private Button btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_settings, container, false);

        // Set the title for the toolbar
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Settings");
        }

        // Initialize shared preferences
        sharedPreferences = getActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE); // Using the correct SharedPreferences for login

        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        btnLogout = view.findViewById(R.id.btnLogout); // Initialize the logout button

        // Load saved preferences
        boolean darkMode = sharedPreferences.getBoolean("dark_mode", false);
        switchDarkMode.setChecked(darkMode);
        applyTheme(darkMode); // Apply the theme based on saved preference

        // Save preference on switch change
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();

            applyTheme(isChecked); // Apply the theme change
            getActivity().recreate();
        });

        // Set up logout button click listener
        btnLogout.setOnClickListener(v -> {
            // Clear user session data (logout)
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_logged_in", false); // Reset the login session flag
            editor.remove("logged_in_email"); // Optionally remove the logged-in email
            editor.apply();

            // Redirect to login page
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear activity stack and start a new task
            startActivity(intent);
            getActivity().finish(); // Close the current activity
        });

        return view;
    }

    private void applyTheme(boolean darkMode) {
        if (darkMode) {
            getActivity().setTheme(R.style.DarkTheme);
        } else {
            getActivity().setTheme(R.style.LightTheme);
        }
    }
}
