package com.example.humayan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentDashboard extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_fragment_dashboard, container, false);

        // Find buttons by ID
        Button soilPHLevelButton = view.findViewById(R.id.button2);
        Button soilMoistureButton = view.findViewById(R.id.button3);

        Button waterDepthButton = view.findViewById(R.id.button4);
        Button weatherButton = view.findViewById(R.id.button5);

        // Set onClickListeners
        soilMoistureButton.setOnClickListener(v -> replaceFragment(new FragmentTwo()));
        soilPHLevelButton.setOnClickListener(v -> replaceFragment(new FragmentThree()));
        waterDepthButton.setOnClickListener(v -> replaceFragment(new FragmentFour()));
        weatherButton.setOnClickListener(v -> replaceFragment(new FragmentFive()));

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
