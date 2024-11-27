package com.example.humayan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class FragmentSettings extends Fragment {

    private Switch switchDarkMode;
    private LinearLayout btnLogout;
    private LinearLayout btnAboutUs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_settings, container, false);

        // Set the title for the toolbar
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Settings");
        }

        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnAboutUs = view.findViewById(R.id.btnAboutUs);

        // Load saved preferences and set switch state
        boolean darkMode = ThemeManager.isDarkMode(getActivity());
        switchDarkMode.setChecked(darkMode);

        // Save preference on switch change
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemeManager.setDarkMode(getActivity(), isChecked); // Use ThemeManager to set dark mode
            getActivity().recreate(); // Recreate activity to apply the new theme
        });

        // Set up logout button click listener
        btnLogout.setOnClickListener(v -> {
            // Clear user session data (logout)
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("app_prefs", AppCompatActivity.MODE_PRIVATE).edit();
            editor.putBoolean("user_login", false);
            editor.remove("logged_in_email"); // Remove any additional data if needed
            editor.apply();

            // Redirect to login page
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });


        btnAboutUs.setOnClickListener(v -> {
            // Navigate to FragmentAboutUs
            Fragment aboutUsFragment = new FragmentAboutUs();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, aboutUsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Update switch state every time the fragment comes into view
        boolean darkMode = ThemeManager.isDarkMode(getActivity());
        switchDarkMode.setChecked(darkMode);
    }
}
