package com.example.gymfitnessapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.gymfitnessapp.Custom.TodayDecorator;
import com.example.gymfitnessapp.Custom.WorkoutDoneDecorator;
import com.example.gymfitnessapp.Database.GymDB;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class Calendar extends AppCompatActivity {

    MaterialCalendarView materialCalendarView;
    HashSet<CalendarDay> list = new HashSet<>();

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
        materialCalendarView.addDecorator(new TodayDecorator()); // Add this line to highlight today's date
    }
}
