package com.example.gymfitnessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gymfitnessapp.API.APIConnector;
import com.example.gymfitnessapp.Custom.MyDialogFragment;
import com.example.gymfitnessapp.Custom.TodayDecorator;
import com.example.gymfitnessapp.Custom.WorkoutSavedDecorator;
import com.example.gymfitnessapp.Database.GymDB;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Calendar extends AppCompatActivity {
    private APIConnector apiConnector;
    private MaterialCalendarView materialCalendarView;
    private HashSet<CalendarDay> savedWorkoutDays = new HashSet<>();
    private ArrayList<String> bodyParts = new ArrayList<>();
    private ArrayAdapter<String> calendarAdapter;
    private GymDB gymDB;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        gymDB = new GymDB(this);

        materialCalendarView = findViewById(R.id.calendar);
        List<String> workoutDays = gymDB.getWorkoutDays();
        // Convert the workout days to CalendarDay objects
        for (String value : workoutDays) {
            savedWorkoutDays.add(CalendarDay.from(parseDate(value)));
        }

        // Add the decorators to the calendar
        materialCalendarView.addDecorator(new WorkoutSavedDecorator(savedWorkoutDays));
        materialCalendarView.addDecorator(new TodayDecorator(Calendar.this));

        Spinner spinner = findViewById(R.id.bodyPartSpinner);
        calendarAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bodyParts);
        spinner.setAdapter(calendarAdapter);
        apiConnector = new APIConnector();
        fetchBodyPartsFromAPI();

        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            CalendarDay currentDay = CalendarDay.today();
            if (date.getCalendar().after(currentDay.getCalendar()) || date.getCalendar().equals(currentDay.getCalendar())) {
                // Retrieve the selected date from the calendar
                String selectedDate = formatDate(date.getCalendar().getTime());

                // Check if the selected date is saved in the database
                boolean isDateSaved = gymDB.isDateSaved(selectedDate);
                if (isDateSaved) {
                    // Retrieve the saved body part for the selected date
                    String savedBodyPart = gymDB.getBodyPartForDate(selectedDate);

                    // Retrieve the selected body part from the spinner
                    String selectedBodyPart = spinner.getSelectedItem().toString();

                    if (selectedBodyPart.equals(savedBodyPart)) {
                        // If the selected body part matches the saved body part, proceed with saving the workout
                        if (selected) {
                            showDeleteDateDialog(date);
                        }
                    } else {
                        // If the selected body part doesn't match the saved body part, show an error message
                        Toast.makeText(this, "Selected body part doesn't match the saved body part", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // The selected date is not saved, proceed with saving the workout
                    String selectedBodyPart = spinner.getSelectedItem().toString();
                    if (selected) {
                        showSaveDateDialog(selectedDate, selectedBodyPart);
                    }
                }
            } else if (date.getCalendar().before(currentDay.getCalendar())) {
                Toast.makeText(this, "You can't set a workout schedule for a previous date", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteDateDialog(CalendarDay date) {
        MyDialogFragment dialogFragment = MyDialogFragment.newInstance("Delete Date", "Do you want to delete the workout for this date?");
        dialogFragment.setOnDialogButtonClickListener(new MyDialogFragment.OnDialogButtonClickListener() {
            @Override
            public void onPositiveButtonClick() {
                // Handle positive button click (delete workout for the date)
                gymDB.deleteSelectedDate(formatDate(date.getDate()));
                savedWorkoutDays.remove(date);
                // Clear the existing decorators
                materialCalendarView.removeDecorators();

                // Add the decorators to the calendar
                materialCalendarView.addDecorator(new WorkoutSavedDecorator(savedWorkoutDays));
                materialCalendarView.addDecorator(new TodayDecorator(Calendar.this));
                //Toast message
                Toast.makeText(Calendar.this, "Workout deleted for " + date, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNegativeButtonClick() {
                // Handle negative button click (cancel)
                Toast.makeText(Calendar.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        dialogFragment.show(getSupportFragmentManager(), "DeleteDateDialog");
    }

    private void showSaveDateDialog(String selectedDate, String selectedBodyPart) {
        MyDialogFragment dialogFragment = MyDialogFragment.newInstance("Set Date", "Do you want to set a workout for this date?");
        dialogFragment.setOnDialogButtonClickListener(new MyDialogFragment.OnDialogButtonClickListener() {
            @Override
            public void onPositiveButtonClick() {
                // Handle positive button click (set workout for the date)
                // Get the selected body part from the spinner
                gymDB.saveSelectedDate(selectedDate, selectedBodyPart);
                savedWorkoutDays.add(CalendarDay.from(parseDate(selectedDate)));
                Toast.makeText(Calendar.this, "Workout set for " + selectedDate + " body part [" + selectedBodyPart + "]", Toast.LENGTH_SHORT).show();

                // Clear the existing decorators
                materialCalendarView.removeDecorators();

                // Add the decorators to the calendar
                materialCalendarView.addDecorator(new WorkoutSavedDecorator(savedWorkoutDays));
                materialCalendarView.addDecorator(new TodayDecorator(Calendar.this));
            }

            @Override
            public void onNegativeButtonClick() {
                // Handle negative button click (cancel)
                Toast.makeText(Calendar.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        dialogFragment.show(getSupportFragmentManager(), "SetDateDialog");
    }

    private Date parseDate(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String formatDate(Date date) {
        return dateFormat.format(date);
    }

    private void fetchBodyPartsFromAPI() {
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(MainActivity.class.getSimpleName(), "Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    // Xử lý dữ liệu phản hồi ở đây
                    Gson gson = new Gson();
                    List<String> bodyPartsList = gson.fromJson(responseBody, new TypeToken<List<String>>() {
                    }.getType());

                    // Add the body parts to the bodyParts list
                    bodyParts.addAll(bodyPartsList);
                    Log.d(this.getClass().getSimpleName(), "List: " + bodyParts.size());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            calendarAdapter.notifyDataSetChanged();
                        }
                    });

                    Log.d(this.getClass().getSimpleName(), "Response: " + responseBody);
                } else {
                    Log.e(this.getClass().getSimpleName(), "Request unsuccessful: " + response.code());
                }
            }
        };
        apiConnector.fetchBodyPartListData(callback);
    }
}
