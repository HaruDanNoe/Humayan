package com.example.humayan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class FragmentDashboard extends Fragment {

    private static final String TAG = "FragmentDashboard";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_dashboard, container, false);
        fetchData();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Dashboard");

        // Initialize buttons and set click listeners
        ImageButton soilPHLevelButton = view.findViewById(R.id.button_ph);
        ImageButton soilMoistureButton = view.findViewById(R.id.button_moisture);
        ImageButton waterDepthButton = view.findViewById(R.id.button_water_depth);
        ImageButton weatherButton = view.findViewById(R.id.button_weather);

        soilPHLevelButton.setOnClickListener(v -> replaceFragment(new FragmentPH()));
        soilMoistureButton.setOnClickListener(v -> replaceFragment(new FragmentMoisture()));
        waterDepthButton.setOnClickListener(v -> replaceFragment(new FragmentWater()));
        weatherButton.setOnClickListener(v -> replaceFragment(new FragmentWeather()));

        return view;
    }

    private void fetchData() {
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... voids) {
                try {
                    String phUrl = "https://zel.helioho.st/ph_level/fetch_ph_history.php";
                    String waterUrl = "https://zel.helioho.st/water_depth/fetch_water_history.php";
                    String moistureUrl = "https://zel.helioho.st/moisture_level/fetch_moisture_history.php";

                    JSONArray phData = fetchJsonData(phUrl);
                    JSONArray waterData = fetchJsonData(waterUrl);
                    JSONArray moistureData = fetchJsonData(moistureUrl);

                    JSONObject latestPhData = getLatestData(phData, "timestamp");
                    JSONObject latestWaterData = getLatestData(waterData, "timestamp");
                    JSONObject latestMoistureData = getLatestData(moistureData, "timestamp");

                    JSONObject result = new JSONObject();
                    result.put("ph", latestPhData);
                    result.put("water", latestWaterData);
                    result.put("moisture", latestMoistureData);

                    return result;

                } catch (IOException | JSONException e) {
                    Log.e(TAG, "Error fetching data", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                if (result != null) {
                    try {
                        JSONObject phData = result.getJSONObject("ph");
                        JSONObject waterData = result.getJSONObject("water");
                        JSONObject moistureData = result.getJSONObject("moisture");

                        updateUI(phData, waterData, moistureData);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing result", e);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }


    private void updateUI(JSONObject phData, JSONObject waterData, JSONObject moistureData) {
        if (getView() == null) return;

        Button phHelpButton = getView().findViewById(R.id.button_request_help_ph);
        Button waterHelpButton = getView().findViewById(R.id.button_request_help_water);
        Button moistureHelpButton = getView().findViewById(R.id.button_request_help_moisture);

        try {
            double phLevel = phData.getDouble("ph_level");
            double waterDepth = waterData.getDouble("water_depth");
            double moistureLevel = moistureData.getDouble("moisture_level");

            boolean showPhHelp = false, showWaterHelp = false, showMoistureHelp = false;

            int green = getResources().getColor(android.R.color.holo_green_dark);
            int yellow = getResources().getColor(android.R.color.holo_orange_light);
            int red = getResources().getColor(android.R.color.holo_red_dark);

            // pH Warning
            TextView phWarningView = getView().findViewById(R.id.text_ph_warning);
            if (phLevel >= 6 && phLevel <= 7) {
                phWarningView.setText("Perfect pH Level");
                phWarningView.setTextColor(green);
                phWarningView.setVisibility(View.VISIBLE);
            } else if (phLevel < 4) {
                phWarningView.setText("pH Level critically low!");
                phWarningView.setTextColor(red);
                phWarningView.setVisibility(View.VISIBLE);
                showPhHelp = true;
            } else if (phLevel < 6) {
                phWarningView.setText("pH Level Low");
                phWarningView.setTextColor(yellow);
                phWarningView.setVisibility(View.VISIBLE);
                showPhHelp = true;
            } else {
                phWarningView.setText("pH Level High");
                phWarningView.setTextColor(red);
                phWarningView.setVisibility(View.VISIBLE);
                showPhHelp = true;
            }

            // Water Warning
            TextView waterWarningView = getView().findViewById(R.id.text_water_warning);
            if (waterDepth < 10) {
                waterWarningView.setText("Water depth critically low!");
                waterWarningView.setTextColor(red);
                waterWarningView.setVisibility(View.VISIBLE);
                showWaterHelp = true;
            } else if (waterDepth <= 15) {
                waterWarningView.setText("Water depth low!");
                waterWarningView.setTextColor(yellow);
                waterWarningView.setVisibility(View.VISIBLE);
                showWaterHelp = true;
            } else if (waterDepth > 20) {
                waterWarningView.setText("Water depth too high!");
                waterWarningView.setTextColor(yellow);
                waterWarningView.setVisibility(View.VISIBLE);
                showWaterHelp = true;
            } else {
                waterWarningView.setText("Perfect Water Depth");
                waterWarningView.setTextColor(green);
                waterWarningView.setVisibility(View.VISIBLE);

            }






            // Moisture Warning
            TextView moistureWarningView = getView().findViewById(R.id.text_moisture_warning);
            if (moistureLevel < 60) {
                moistureWarningView.setText("Moisture critically low!");
                moistureWarningView.setTextColor(red);
                moistureWarningView.setVisibility(View.VISIBLE);
                showMoistureHelp = true;
            } else if (moistureLevel <= 80) {
                moistureWarningView.setText("Soil moisture low.");
                moistureWarningView.setTextColor(yellow);
                moistureWarningView.setVisibility(View.VISIBLE);
                showMoistureHelp = true;
            } else {
                moistureWarningView.setText("Perfect Soil Moisture Level");
                moistureWarningView.setTextColor(green);
                moistureWarningView.setVisibility(View.VISIBLE);
            }

            // Control Help Button Visibility
            phHelpButton.setVisibility(showPhHelp ? View.VISIBLE : View.GONE);
            waterHelpButton.setVisibility(showWaterHelp ? View.VISIBLE : View.GONE);
            moistureHelpButton.setVisibility(showMoistureHelp ? View.VISIBLE : View.GONE);

            // Set Help Button Listeners
            phHelpButton.setOnClickListener(v -> sendHelpMessage("Requesting assistance: Soil pH levels are critical for rice crops. What immediate steps should be taken to stabilize the pH?"));
            waterHelpButton.setOnClickListener(v -> sendHelpMessage("Requesting assistance: Water depth for the rice fields is at a critical level. How can we quickly restore optimal flooding?"));
            moistureHelpButton.setOnClickListener(v -> sendHelpMessage("Requesting assistance: Soil moisture levels are too low for healthy rice growth. What are the best irrigation techniques for this situation?"));

        } catch (JSONException e) {
            Log.e(TAG, "Error updating UI", e);
        }
    }


    private void sendHelpMessage(String message) {
        FragmentChat fragmentChat = new FragmentChat();

        // Pass the message as an argument to the FragmentChat
        Bundle bundle = new Bundle();
        bundle.putString("help_message", message);
        fragmentChat.setArguments(bundle);

        // Navigate to the Gemini fragment
        replaceFragment(fragmentChat);
    }


    private JSONArray fetchJsonData(String urlString) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            Scanner scanner = new Scanner(urlConnection.getInputStream());
            StringBuilder response = new StringBuilder();

            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }

            Log.d(TAG, "Response: " + response.toString());
            return new JSONArray(response.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private JSONObject getLatestData(JSONArray data, String timestampKey) throws JSONException {
        if (data == null || data.length() == 0) {
            return null;
        }

        JSONObject latestData = null;
        long latestTimestamp = Long.MIN_VALUE; // Use a very small timestamp value initially
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        // Loop through all the data and find the one with the latest timestamp
        for (int i = 0; i < data.length(); i++) {
            JSONObject dataObject = data.getJSONObject(i);
            String timestampString = dataObject.getString(timestampKey); // Use the specified timestamp key

            try {
                Date date = dateFormat.parse(timestampString); // Parse the date string
                long timestamp = date.getTime(); // Get the time in milliseconds

                // Check if this data has the latest timestamp
                if (timestamp > latestTimestamp) {
                    latestTimestamp = timestamp;
                    latestData = dataObject;
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing timestamp: " + timestampString, e);
            }
        }

        return latestData;
    }


    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
