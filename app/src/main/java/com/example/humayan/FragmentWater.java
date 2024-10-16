package com.example.humayan;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FragmentWater extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private BarChart barChart;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate back when the back button is pressed
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Water Level");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_fragment_water_depth, container, false);

        // Hide BottomNavigationView
        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }

        barChart = view.findViewById(R.id.barChart);

        // Set data for the chart from JSON
        setDataFromJSON(getActivity());

        return view;
    }

    private void setDataFromJSON(Context context) {
        ArrayList<BarEntry> entries = new ArrayList<>(); // Change Entry to BarEntry

        // Read the JSON data from assets
        String jsonData = readJSONFromAsset(context, "water_data.json");
        if (jsonData != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray waterDataArray = jsonObject.getJSONArray("waterData");

                for (int i = 0; i < waterDataArray.length(); i++) {
                    JSONObject dataPoint = waterDataArray.getJSONObject(i);
                    float waterLevel = (float) dataPoint.getDouble("waterLevel");
                    entries.add(new BarEntry(i + 1, waterLevel)); // Use BarEntry here
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "Water Levels");
        dataSet.setColor(getResources().getColor(R.color.blue));
        dataSet.setValueTextColor(getResources().getColor(R.color.black));

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.invalidate(); // Refresh the chart
        barChart.setBackgroundColor(Color.WHITE); // Set chart background color
    }

    private String readJSONFromAsset(Context context, String fileName) {
        StringBuilder jsonData = new StringBuilder();
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonData.append(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonData.toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Show the BottomNavigationView again when leaving this fragment
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }

        // Disable the back button when leaving this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}
