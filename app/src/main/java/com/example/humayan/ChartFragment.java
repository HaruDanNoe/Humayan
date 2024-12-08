package com.example.humayan;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChartFragment extends Fragment {

    private LineChart chart;
    private String batchId;
    private String dataType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        chart = view.findViewById(R.id.lineChart);

        if (getArguments() != null) {
            batchId = getArguments().getString("batch_id");
            dataType = getArguments().getString("data_type");
            if (batchId != null && dataType != null) {
                fetchBatchDataForChart(batchId, dataType);
            }
        }

        setupChart();
        return view;
    }
    private void fetchBatchDataForChart(String batchId, String dataType) {
        String url = getUrlForDataType(dataType) + "?batch_id=" + batchId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ArrayList<Entry> entries = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String timestamp = jsonObject.getString("timestamp");

                            // Get the value based on the data type, using getDouble() and casting to float
                            float value = (float) jsonObject.getDouble(dataType.toLowerCase().replace(" ", "_"));

                            // Use the index i as the X value to ensure even spacing
                            entries.add(new Entry(i, value));  // Use the index as X value

                        }

                        if (entries.isEmpty()) {
                            Toast.makeText(getActivity(), "No data available", Toast.LENGTH_SHORT).show();
                        } else {
                            LineDataSet dataSet = new LineDataSet(entries, dataType);
                            dataSet.setColor(getColorForDataType(dataType));
                            dataSet.setLineWidth(2f);
                            dataSet.setValueTextColor(Color.BLACK);
                            dataSet.setDrawFilled(true); // Fill the area under the line

                            LineData lineData = new LineData(dataSet);
                            chart.setData(lineData);
                            chart.invalidate(); // Refresh the chart
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getActivity(), "Error fetching data", Toast.LENGTH_SHORT).show());

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(request);
    }

    private void setupChart() {
        chart.setBackgroundColor(Color.WHITE);
        chart.getDescription().setEnabled(false); // Hide chart description
        chart.getLegend().setEnabled(false); // Hide legend

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Ensure equal spacing on the X-axis
        xAxis.setGranularity(1f); // Ensure equal spacing
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelRotationAngle(-45f); // Rotate labels for better readability

        // Optional: Set a fixed number of labels displayed on the X-axis
        xAxis.setLabelCount(5, true); // Set the number of labels to display

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setDrawGridLines(true); // Enable grid lines
        chart.getAxisRight().setEnabled(false); // Disable right Y-Axis
    }



    private String getUrlForDataType(String dataType) {
        switch (dataType) {
            case "pH Level":
                return "https://zel.helioho.st/ph_level/fetch_ph_history.php";
            case "Water Depth":
                return "https://zel.helioho.st/water_depth/fetch_water_history.php";
            case "Moisture Level":
                return "https://zel.helioho.st/moisture_level/fetch_moisture_history.php";
            default:
                return "";
        }
    }

    private int getColorForDataType(String dataType) {
        switch (dataType) {
            case "pH Level":
                return Color.RED;
            case "Water Depth":
                return Color.BLUE;
            case "Moisture Level":
                return Color.CYAN;
            default:
                return Color.BLACK;
        }
    }

    private long convertTimestampToMillis(String timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(timestamp);
            if (date != null) {
                return date.getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
