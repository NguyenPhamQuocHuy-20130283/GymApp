package com.example.gymfitnessapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gymfitnessapp.API.APIConnector;
import com.example.gymfitnessapp.Custom.TodayDecorator;
import com.example.gymfitnessapp.Custom.WorkoutDoneDecorator;
import com.example.gymfitnessapp.Database.GymDB;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;


import java.io.IOException;
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
    APIConnector apiConnector;
    MaterialCalendarView materialCalendarView;
    HashSet<CalendarDay> list = new HashSet<>();
    ArrayList<String> bodyParts = new ArrayList<>();
    private ArrayAdapter<String> calendarAdapter;
    GymDB gymDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        gymDB = new GymDB(this);

        materialCalendarView = findViewById(R.id.calendar);
        List<String> workoutDay = gymDB.getWorkoutDays();
        HashSet<CalendarDay> convertedList = new HashSet<>();
        for (String value : workoutDay)
            convertedList.add(CalendarDay.from(new Date(Long.parseLong(value))));
        materialCalendarView.addDecorator(new WorkoutDoneDecorator(convertedList));
        materialCalendarView.addDecorator(new TodayDecorator(this)); // Add this line to highlight today's date

        Spinner spinner = findViewById(R.id.bodyPartSpinner);
        calendarAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bodyParts);
        spinner.setAdapter(calendarAdapter);
        apiConnector = new APIConnector();
        fetchBodyPartsFromAPI();

        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            CalendarDay currentDay = CalendarDay.today();
            if(date.getCalendar().after(currentDay)){
                // Selected date is valid
                String selectedDate = formatDate(date.getCalendar().getTime());

                // Retrieve the selected body part from the spinner
                String selectedBodyPart = spinner.getSelectedItem().toString();
                // Show a dialog to confirm setting or deleting the workout schedule
                showDialog(selectedDate, selectedBodyPart);
            }

        });
    }
    private void showDialog(String selectedDate, String selectedBodyPart){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Workout Schedule")
                .setMessage("Do you want to set a workout schedule for " + selectedDate + " with body part " + selectedBodyPart + "?")
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle setting the workout schedule
                        // You can perform the necessary actions here, such as saving the schedule to a database
                        gymDB.saveSelectedDate(selectedDate, selectedBodyPart);
                        Toast.makeText(Calendar.this, "Workout schedule set for " + selectedDate, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle deleting the workout schedule
                        // You can perform the necessary actions here, such as removing the schedule from the database
                        gymDB.deleteSelectedDate(selectedDate);
                        Toast.makeText(Calendar.this, "Workout schedule deleted for " + selectedDate, Toast.LENGTH_SHORT).show();
                    }
                })
                .setCancelable(true)
                .show();
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
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
                    List<String> bodyPartsList = gson.fromJson(responseBody, new TypeToken<List<String>>() {}.getType());

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
