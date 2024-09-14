package com.example.humayan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentDashboard extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_fragment_dashboard, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Dashboard");

        // Find buttons by ID
        ImageButton soilPHLevelButton = view.findViewById(R.id.button_ph);
        ImageButton soilMoistureButton = view.findViewById(R.id.button_moisture);
        ImageButton waterDepthButton = view.findViewById(R.id.button_water_depth);
        ImageButton weatherButton = view.findViewById(R.id.button_weather);

        // Set onClickListeners
        soilPHLevelButton.setOnClickListener(v -> replaceFragment(new FragmentMoisture()));
        soilMoistureButton.setOnClickListener(v -> replaceFragment(new FragmentPH()));
        waterDepthButton.setOnClickListener(v -> replaceFragment(new FragmentWater()));
        weatherButton.setOnClickListener(v -> replaceFragment(new FragmentWeather()));

        return view;
    }

    // Method to replace the current fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);  // This adds the transaction to the back stack so the user can navigate back
        fragmentTransaction.commit();
    }
}
