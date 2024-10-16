package com.example.humayan;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // First, set the theme based on user preference
        super.onCreate(savedInstanceState);
        ThemeManager.setDarkMode(this, ThemeManager.isDarkMode(this)); // Set the theme based on saved preference

        setContentView(R.layout.activity_main);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        // Load the default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FragmentDashboard())
                    .commit();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the item selected listener for the bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            // Using if-else statements for fragment selection
            if (item.getItemId() == R.id.nav_dashboard) {
                selectedFragment = new FragmentDashboard();
            } else if (item.getItemId() == R.id.nav_calendar) {
                selectedFragment = new FragmentCalendar();
            } else if (item.getItemId() == R.id.nav_account) {
                selectedFragment = new FragmentAccount();
            } else if (item.getItemId() == R.id.nav_settings) {
                selectedFragment = new FragmentSettings();
            }

            // Replace the current fragment with the selected one
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(); // Go back to previous fragment
        } else {
            super.onBackPressed(); // Exit the app
        }
    }
}
