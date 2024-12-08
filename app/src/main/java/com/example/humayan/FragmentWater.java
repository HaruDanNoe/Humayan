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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentWater extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private BarChart barChart;
    private int currentBatchId = 1; // Default batch ID
    private Spinner batchSpinner;
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
        batchSpinner = view.findViewById(R.id.batchSpinner);
        fetchBatchIds(); // Call fetchBatchIds here to fetch the latest batch and display the chart

        // Fetch and populate batch IDs
        fetchHistory(); // Call fetchHistory here to populate the spinner with batch data

        EditText editText = view.findViewById(R.id.editText);
        Button button = view.findViewById(R.id.button);
        Button resetButton = view.findViewById(R.id.resetButton);

        button.setOnClickListener(v -> {
            String waterDepth = editText.getText().toString().trim();

            if (!waterDepth.isEmpty()) {
                // Submit the pH level for the latest batch
                submitwaterDepth(Float.parseFloat(waterDepth));
                editText.setText(""); // Clear input field
            } else {
                Toast.makeText(getActivity(), "Please enter a valid Water Depth", Toast.LENGTH_SHORT).show();
            }
        });

        resetButton.setOnClickListener(v -> {
            // When reset button is clicked, increment batch ID and update chart
            incrementBatchId();
            // Do not call fetchBatchIds() or refresh the chart here
            Toast.makeText(getActivity(), "New Batch started.", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void incrementBatchId() {
        String url = "https://zel.helioho.st/water_depth/increment_batch_id.php"; // Correct PHP script URL
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

    private void submitwaterDepth(Float waterDepth) {
        String url = "https://zel.helioho.st/water_depth/water_depth_insert.php";  // Your PHP endpoint for submitting sensor data

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            Toast.makeText(getActivity(), "Data submitted successfully!", Toast.LENGTH_SHORT).show();
                            // Fetch the updated chart data after submitting new data
                            fetchAndDisplayBatchData(currentBatchId);
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
                params.put("water_depth", String.valueOf(waterDepth));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
    }


    private void updateChart(ArrayList<BarEntry> entries) {
        BarDataSet dataSet = new BarDataSet(entries, "Water Depth");
        dataSet.setColor(getResources().getColor(R.color.red));
        dataSet.setValueTextColor(getResources().getColor(R.color.black));

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.invalidate(); // Refresh the chart
        barChart.setBackgroundColor(Color.WHITE);
    }



    private void fetchBatchIds() {
        String url = "https://zel.helioho.st/water_depth/get_batches_water.php"; // PHP script URL
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.length() > 0) {
                            // Get the list of batch IDs
                            ArrayList<String> batchIds = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject batch = response.getJSONObject(i);
                                batchIds.add(batch.getString("batch_id"));
                            }

                            // Get the latest batch ID using your helper method
                            String latestBatchId = getLatestBatchId(batchIds);
                            currentBatchId = Integer.parseInt(latestBatchId); // Update to the latest batch ID

                            // Create an ArrayAdapter for the spinner
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, batchIds);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            batchSpinner.setAdapter(adapter);

                            // Set the spinner to the latest batch
                            batchSpinner.setSelection(batchIds.indexOf(latestBatchId));

                            // Fetch and display data for the latest batch
                            fetchAndDisplayBatchData(currentBatchId);
                        } else {
                            Toast.makeText(getActivity(), "No batches available", Toast.LENGTH_SHORT).show();
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
        String url = "https://zel.helioho.st/water_depth/get_batches_water.php"; // PHP script URL
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ArrayList<String> batchIds = new ArrayList<>();
                    try {
                        // Log the raw response
                        Log.d("API Response", response.toString());

                        // Populate batchIds list
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

                        // Find the latest batch (largest batch ID)
                        String latestBatchId = getLatestBatchId(batchIds);

                        // Create an ArrayAdapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, batchIds);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // Set the adapter to the spinner
                        batchSpinner.setAdapter(adapter);

                        // Set the selected item to the latest batch ID
                        int latestBatchPosition = batchIds.indexOf(latestBatchId);
                        batchSpinner.setSelection(latestBatchPosition);

                        // Fetch the data for the latest batch
                        fetchAndDisplayBatchData(Integer.parseInt(latestBatchId));

                        // Set Spinner item selected listener
                        batchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                String selectedBatchId = batchIds.get(position);
                                fetchAndDisplayBatchData(Integer.parseInt(selectedBatchId));
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

    private String getLatestBatchId(ArrayList<String> batchIds) {
        // Assuming batch IDs are numerical, find the largest batch ID
        int latest = Integer.parseInt(batchIds.get(0));
        for (String batchId : batchIds) {
            int currentId = Integer.parseInt(batchId);
            if (currentId > latest) {
                latest = currentId;
            }
        }
        return String.valueOf(latest);
    }



    private void fetchAndDisplayBatchData(int batchId) {
        String url = "https://zel.helioho.st/water_depth/get_water_data.php?batch_id=" + batchId;
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ArrayList<BarEntry> entries = new ArrayList<>();
                    try {
                        if (response.length() > 0) {
                            for (int i = 1; i < response.length(); i++) {
                                JSONObject dataPoint = response.getJSONObject(i);
                                float waterDepth = (float) dataPoint.getDouble("water_depth");

                                // Only add entries where phLevel is not 0
                                if (waterDepth != 0) {
                                    entries.add(new BarEntry(i, waterDepth)); // Create BarEntry
                                }
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
