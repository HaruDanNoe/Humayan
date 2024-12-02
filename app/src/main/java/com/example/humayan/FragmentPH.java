package com.example.humayan;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentPH extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private LineChart lineChart;
    private int currentBatchId = 1; // Default batch ID
    private Spinner batchSpinner;


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


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("pH Level");

        View view = inflater.inflate(R.layout.activity_fragment_ph_level, container, false);

        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }

        lineChart = view.findViewById(R.id.lineChart);
        batchSpinner = view.findViewById(R.id.batchSpinner);

        // Fetch and populate batch IDs
        fetchHistory(); // Call fetchHistory here to populate the spinner with batch data

        EditText editText = view.findViewById(R.id.editText);
        Button button = view.findViewById(R.id.button);
        Button resetButton = view.findViewById(R.id.resetButton);

        button.setOnClickListener(v -> {
            String phLevel = editText.getText().toString().trim();

            if (!phLevel.isEmpty()) {
                // Submit the pH level for the latest batch
                submitSensorData(Float.parseFloat(phLevel), 0.0f, 0.0f);
                editText.setText(""); // Clear input field
            } else {
                Toast.makeText(getActivity(), "Please enter a valid pH level", Toast.LENGTH_SHORT).show();
            }
        });

        resetButton.setOnClickListener(v -> {
            // When reset button is clicked, increment batch ID and update chart
            incrementBatchId();
            fetchBatchIds();
            lineChart.clear();
            lineChart.invalidate();
            Toast.makeText(getActivity(), "Chart reset! New batch started.", Toast.LENGTH_SHORT).show();
        });

        return view;
    }


    private void incrementBatchId() {
        String url = "https://zel.helioho.st/increment_batch_id.php"; // Correct PHP script URL
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            // Update current batch ID
                            currentBatchId = jsonResponse.getInt("new_batch_id");
                            // Display the new batch ID
                            Toast.makeText(getActivity(), "New Batch ID: " + currentBatchId, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Error: " + jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error parsing response: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        queue.add(stringRequest);
    }

    private void submitSensorData(float phLevel, float moistureLevel, float waterLevel) {
        String url = "https://zel.helioho.st/submit_sensor_data.php";  // Your PHP endpoint for submitting sensor data

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            Toast.makeText(getActivity(), "Data submitted successfully!", Toast.LENGTH_SHORT).show();
                            // Fetch the updated chart data after submitting new data
                            fetchChartData(currentBatchId);
                        } else {
                            Toast.makeText(getActivity(), "Error: " + jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error parsing response: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("batch_id", String.valueOf(currentBatchId));  // Use the updated batch_id
                params.put("ph_level", String.valueOf(phLevel));
                params.put("moisture_level", String.valueOf(moistureLevel));
                params.put("water_level", String.valueOf(waterLevel));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
    }


    private void fetchChartData(int batchId) {
        String url = "https://zel.helioho.st/get_ph_data.php?batch_id=" + batchId;
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ArrayList<Entry> entries = new ArrayList<>();
                    try {
                        // Log the raw response for debugging
                        Log.d("fetchChartData", "Response: " + response.toString());

                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject dataPoint = response.getJSONObject(i);
                                float phLevel = (float) dataPoint.getDouble("ph_level");
                                entries.add(new Entry(i, phLevel)); // Use i as the x value (index)
                            }
                            updateChart(entries);
                        } else {
                            Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Log the error message for debugging
                    Log.e("fetchChartData", "Error: " + error.getMessage());
                    Toast.makeText(getActivity(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonArrayRequest);
    }


    private void updateChart(ArrayList<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "pH Levels");
        dataSet.setColor(getResources().getColor(R.color.red));
        dataSet.setValueTextColor(getResources().getColor(R.color.black));

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); // Refresh the chart
        lineChart.setBackgroundColor(Color.WHITE);
    }

    private void fetchBatchIds() {
        String url = "https://zel.helioho.st/get_batches.php"; // PHP script URL
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.length() > 0) {
                            // Assuming the latest batch is the last one in the array
                            JSONObject latestBatch = response.getJSONObject(response.length() - 1);
                            currentBatchId = latestBatch.getInt("batch_id"); // Set to the latest batch ID

                            // Update chart with the latest batch data
                            fetchChartData(currentBatchId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error parsing batch IDs", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getActivity(), "Error fetching batches: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        queue.add(jsonArrayRequest);
    }


    private void fetchHistory() {
        String url = "https://zel.helioho.st/get_batches.php"; // PHP script URL
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ArrayList<String> batchIds = new ArrayList<>();
                    try {
                        // Log the raw response
                        Log.d("API Response", response.toString());

                        // Populate Spinner with batch IDs
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject batch = response.getJSONObject(i);
                            String batchId = batch.getString("batch_id");
                            batchIds.add(batchId);
                        }

                        // Log the batch IDs
                        Log.d("BatchIds", "Batch IDs: " + batchIds);

                        // Make sure the batchIds is not empty
                        if (batchIds.isEmpty()) {
                            Toast.makeText(getActivity(), "No batches available", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Create an ArrayAdapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, batchIds);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // Set the adapter to the spinner
                        batchSpinner.setAdapter(adapter);

                        // Set Spinner item selected listener
                        batchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                String selectedBatchId = batchIds.get(position);
                                fetchBatchData(Integer.parseInt(selectedBatchId));
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parentView) {
                                // Do nothing
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error parsing history", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getActivity(), "Error fetching batches: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("VolleyError", "Error: " + error.getMessage());
                });

        queue.add(jsonArrayRequest);
    }



    private void fetchBatchData(int batchId) {
        String url = "https://zel.helioho.st/get_ph_data.php?batch_id=" + batchId;
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ArrayList<Entry> entries = new ArrayList<>();
                    try {
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject dataPoint = response.getJSONObject(i);
                                float phLevel = (float) dataPoint.getDouble("ph_level");
                                entries.add(new Entry(i, phLevel));
                            }
                            updateChart(entries);
                        } else {
                            Toast.makeText(getActivity(), "No data found for this batch", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getActivity(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        queue.add(jsonArrayRequest);
    }
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
