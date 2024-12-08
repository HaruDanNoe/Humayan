package com.example.humayan;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentHistory extends Fragment {

    private RequestQueue requestQueue;
    private ListView listView;
    private Spinner spinner, sortByYearSpinner;
    private List<String> dataList;  // List to store the data
    private ArrayAdapter<String> listAdapter;

    private static final String URL_PH = "https://zel.helioho.st/ph_level/fetch_ph_history.php";
    private static final String URL_WATER = "https://zel.helioho.st/water_depth/fetch_water_history.php";
    private static final String URL_MOISTURE = "https://zel.helioho.st/moisture_level/fetch_moisture_history.php";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_history, container, false);

        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(getActivity());

        listView = view.findViewById(R.id.listView_data);
        spinner = view.findViewById(R.id.spinner_data_type);
        sortByYearSpinner = view.findViewById(R.id.spinner_sort_by_year);

        // Setup the spinner with data type options
        ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.data_types, android.R.layout.simple_spinner_item);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        // Setup the spinner for sorting by year
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sort_by_year, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortByYearSpinner.setAdapter(yearAdapter);

        // Initialize the data list and adapter
        dataList = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(listAdapter);

        // Set listener to fetch and display data based on selected type
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String dataType = parentView.getItemAtPosition(position).toString();
                fetchData(dataType);  // Call fetchData when data type is selected
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case where no item is selected (optional)
            }
        });

        // Set listener to fetch and display data based on selected year
        sortByYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String dataType = spinner.getSelectedItem().toString();
                fetchData(dataType);  // Call fetchData when year is changed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case where no year is selected (optional)
            }
        });

        return view;
    }

    private void fetchData(String dataType) {
        String selectedYear = sortByYearSpinner.getSelectedItem().toString();
        String url = getUrlForDataType(dataType, selectedYear);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Create a map to group data by batch_id and year
                        Map<String, List<String>> groupedData = new HashMap<>();

                        // Iterate through the response and group data by batch_id and year
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String batchId = jsonObject.getString("batch_id");
                            String timestamp = jsonObject.getString("timestamp");

                            // Extract the year from the timestamp
                            String year = timestamp.split("-")[0];  // Assuming timestamp is in "YYYY-MM-DD" format

                            // Add the data to the map, grouped by batch_id and year
                            String key = batchId + "_" + year;
                            if (!groupedData.containsKey(key)) {
                                groupedData.put(key, new ArrayList<>());
                            }
                        }

                        // Get the years that have batches
                        List<String> availableYears = new ArrayList<>();
                        for (String key : groupedData.keySet()) {
                            String year = key.split("_")[1];
                            if (!availableYears.contains(year)) {
                                availableYears.add(year);
                            }
                        }

                        // Set available years to the year spinner
                        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(getActivity(),
                                android.R.layout.simple_spinner_item, availableYears);
                        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sortByYearSpinner.setAdapter(yearAdapter);

                        // When a year is selected, update the batch list
                        sortByYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                String selectedYear = parentView.getItemAtPosition(position).toString();
                                updateBatchList(groupedData, selectedYear);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parentView) {
                                // Handle the case where no item is selected (optional)
                            }
                        });

                        // Initial update to show batches for the first available year
                        if (!availableYears.isEmpty()) {
                            updateBatchList(groupedData, availableYears.get(0));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getActivity(), "Error fetching data", Toast.LENGTH_SHORT).show();
                });

        // Add the request to the queue
        requestQueue.add(request);
    }

    private void updateBatchList(Map<String, List<String>> groupedData, String selectedYear) {
        // Clear the existing data in the list view
        dataList.clear();

        // Iterate through the grouped data and add batches for the selected year
        for (String key : groupedData.keySet()) {
            String[] parts = key.split("_");
            String batchId = parts[0];
            String year = parts[1];

            if (year.equals(selectedYear)) {
                dataList.add("Batch " + batchId);
            }
        }

        // Notify the adapter to refresh the ListView
        listAdapter.notifyDataSetChanged();

        // Set item click listener to navigate to the chart fragment
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedBatch = dataList.get(position);
            String batchId = selectedBatch.split(" ")[1]; // Extract the batch_id from the string
            String selectedDataType = spinner.getSelectedItem().toString(); // Get the selected data type
            openChartFragment(batchId, selectedDataType); // Open the chart fragment with batchId and dataType
        });
    }


    private String getUrlForDataType(String dataType, String selectedYear) {
        String baseUrl = "";

        switch (dataType) {
            case "pH Level":
                baseUrl = URL_PH;
                break;
            case "Water Depth":
                baseUrl = URL_WATER;
                break;
            case "Moisture Level":
                baseUrl = URL_MOISTURE;
                break;
        }

        // Append the selected year to the URL
        return baseUrl + "?year=" + selectedYear;
    }

    private void openChartFragment(String batchId, String dataType) {
        // Create a new fragment to show the chart
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        ChartFragment chartFragment = new ChartFragment();

        // Pass the batch_id and dataType to the chart fragment
        Bundle bundle = new Bundle();
        bundle.putString("batch_id", batchId);
        bundle.putString("data_type", dataType);
        chartFragment.setArguments(bundle);

        // Replace the current fragment with the chart fragment
        transaction.replace(R.id.fragment_container, chartFragment);
        transaction.addToBackStack(null); // Add the transaction to the back stack so the user can navigate back
        transaction.commit();
    }
}

