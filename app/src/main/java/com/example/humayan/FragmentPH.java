package com.example.humayan;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FragmentPH extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private LineChart lineChart;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("pH Level");

        View view = inflater.inflate(R.layout.activity_fragment_ph_level, container, false);

        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }

        lineChart = view.findViewById(R.id.lineChart);

        // Set data for the chart from JSON
        setDataFromJSON(getActivity());

        return view;
    }

    private void setDataFromJSON(Context context) {
        ArrayList<Entry> entries = new ArrayList<>();

        // Read the JSON data from assets
        String jsonData = readJSONFromAsset(context, "ph_data.json");
        if (jsonData != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray phDataArray = jsonObject.getJSONArray("phData");

                for (int i = 0; i < phDataArray.length(); i++) {
                    JSONObject dataPoint = phDataArray.getJSONObject(i);
                    float phLevel = (float) dataPoint.getDouble("phLevel");
                    entries.add(new Entry(i + 1, phLevel)); // Using index as x value
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "pH Levels");
        dataSet.setColor(getResources().getColor(R.color.red));
        dataSet.setValueTextColor(getResources().getColor(R.color.black));

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); // Refresh the chart
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

        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}
