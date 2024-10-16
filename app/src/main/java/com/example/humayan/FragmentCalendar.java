package com.example.humayan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class FragmentCalendar extends Fragment {

    private CalendarView calendarView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_fragment_calendar, container, false);

        // Set the title for the toolbar
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Calendar");
        }

        // Initialize CalendarView
        calendarView = view.findViewById(R.id.calendarView);


        // Set listener for date change
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

                // Use getActivity() to get the context
                Toast.makeText(getActivity(), "Selected date: " + selectedDate, Toast.LENGTH_SHORT).show();
                // You can now use the selected date (e.g., show it in a Toast or save it)
            }
        });

        return view;
    }
}
