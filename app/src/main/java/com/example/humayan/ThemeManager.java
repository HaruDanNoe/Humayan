package com.example.humayan;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {
    private static final String PREFS_NAME = "user_prefs"; // Make sure this matches in all classes
    private static final String DARK_MODE_KEY = "dark_mode";

    // Method to set dark mode
    public static void setDarkMode(Context context, boolean isDarkMode) {
        // Access shared preferences to save the state
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DARK_MODE_KEY, isDarkMode);
        editor.apply();

        // Apply the theme immediately
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    // Method to check if dark mode is enabled
    public static boolean isDarkMode(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(DARK_MODE_KEY, false);
    }
}
