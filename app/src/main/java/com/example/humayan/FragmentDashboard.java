package com.example.humayan;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import java.util.Locale;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class FragmentDashboard extends Fragment {

    private LineChart chart;

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
        soilPHLevelButton.setOnClickListener(v -> replaceFragment(new FragmentPH()));
        soilMoistureButton.setOnClickListener(v -> replaceFragment(new FragmentMoisture()));
        waterDepthButton.setOnClickListener(v -> replaceFragment(new FragmentWater()));
        weatherButton.setOnClickListener(v -> replaceFragment(new FragmentWeather()));

        // Initialize the chart
        chart = view.findViewById(R.id.chart);
        setupChartData();

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

    // Method to load JSON data and populate the chart
    private void setupChartData() {
        ArrayList<Entry> phEntries = new ArrayList<>();
        ArrayList<Entry> moistureEntries = new ArrayList<>();
        ArrayList<Entry> waterEntries = new ArrayList<>();

        try {
            // Read and parse the JSON files from the assets folder
            JSONObject phData = loadJSONFromAsset("ph_data.json");
            JSONObject moistureData = loadJSONFromAsset("moisture_data.json");
            JSONObject waterData = loadJSONFromAsset("water_data.json");

            // Parse the data for pH levels
            JSONArray phArray = phData.getJSONArray("phData");
            for (int i = 0; i < phArray.length(); i++) {
                JSONObject phObject = phArray.getJSONObject(i);
                String timestamp = phObject.getString("timestamp");
                float phLevel = (float) phObject.getDouble("phLevel");
                long timeInMillis = convertTimestampToMillis(timestamp);
                phEntries.add(new Entry(timeInMillis, phLevel));
            }

            // Parse the data for moisture levels
            JSONArray moistureArray = moistureData.getJSONArray("moistureData");
            for (int i = 0; i < moistureArray.length(); i++) {
                JSONObject moistureObject = moistureArray.getJSONObject(i);
                String timestamp = moistureObject.getString("timestamp");
                float moistureLevel = (float) moistureObject.getDouble("moistureLevel");
                long timeInMillis = convertTimestampToMillis(timestamp);
                moistureEntries.add(new Entry(timeInMillis, moistureLevel));
            }

            // Parse the data for water levels
            JSONArray waterArray = waterData.getJSONArray("waterData");
            for (int i = 0; i < waterArray.length(); i++) {
                JSONObject waterObject = waterArray.getJSONObject(i);
                String timestamp = waterObject.getString("timestamp");
                float waterLevel = (float) waterObject.getDouble("waterLevel");
                long timeInMillis = convertTimestampToMillis(timestamp);
                waterEntries.add(new Entry(timeInMillis, waterLevel));
            }

            // pH Level - Red
            LineDataSet phDataSet = new LineDataSet(phEntries, "pH Level");
            phDataSet.setColor(Color.RED);  // Red for the line
            phDataSet.setCircleColor(Color.RED);  // Red for the circles

            // Water Level - Dark Blue
            LineDataSet waterDataSet = new LineDataSet(waterEntries, "Water Level");
            waterDataSet.setColor(Color.parseColor("#00008B"));  // Dark blue for the line
            waterDataSet.setCircleColor(Color.parseColor("#00008B"));  // Dark blue for the circles

            // Moisture Level - Sky Blue
            LineDataSet moistureDataSet = new LineDataSet(moistureEntries, "Moisture Level");
            moistureDataSet.setColor(Color.parseColor("#87CEEB"));  // Sky blue for the line
            moistureDataSet.setCircleColor(Color.parseColor("#87CEEB"));  // Sky blue for the circles

            // Add the datasets to LineData
            LineData lineData = new LineData(phDataSet, moistureDataSet, waterDataSet);

            // Set the data to the chart
            chart.setData(lineData);

            // Disable x-axis labels
            chart.getXAxis().setEnabled(false); // Disable x-axis

            chart.invalidate(); // Refresh the chart

            // Optional: Set description
            Description description = new Description();
            description.setText("Water, pH, and Moisture Levels");
            chart.setDescription(description);

        } catch (JSONException | IOException | ParseException e) {
            e.printStackTrace();
        }
    }


    // Helper method to load JSON from the assets folder
    private JSONObject loadJSONFromAsset(String filename) throws IOException, JSONException {
        AssetManager manager = getActivity().getAssets();
        InputStream is = manager.open(filename);
        Scanner scanner = new Scanner(is).useDelimiter("\\A");
        String json = scanner.hasNext() ? scanner.next() : "";
        is.close();
        return new JSONObject(json);
    }

    // Helper method to convert timestamp to milliseconds for chart
    private long convertTimestampToMillis(String timestamp) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        Date date = sdf.parse(timestamp);
        return date != null ? date.getTime() : 0;
    }
}

