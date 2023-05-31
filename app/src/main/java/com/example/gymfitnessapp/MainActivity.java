package com.example.gymfitnessapp;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.gymfitnessapp.API.APIConnector;

import java.io.IOException;

import info.hoang8f.widget.FButton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    FButton btnExercises, btnSetting, btnCalendar;
    ImageView btnTraining;

    private Spinner groupSpinner;
    private ArrayAdapter<String> groupAdapter;
    private String[] groupList;
    private APIConnector apiConnector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnExercises = (FButton) findViewById(R.id.btnExercise);
        btnSetting = (FButton) findViewById(R.id.btnSetting);
        btnTraining = (ImageView)findViewById(R.id.btnTraining);
        btnCalendar = (FButton)findViewById(R.id.btnCalender);

        // Khởi tạo danh sách các nhóm cơ
        groupList = new String[] {"Nhóm cơ ngực", "Nhóm cơ lưng", "Nhóm cơ vai", "Nhóm cơ chân", "Nhóm cơ bụng", "Nhóm cơ cánh tay", "Nhóm cơ đùi", "Nhóm cơ mông", "Nhóm cơ cổ chân"};

        // Khởi tạo Spinner
        groupSpinner = findViewById(R.id.groupSpinner);

        // Khởi tạo và thiết lập ArrayAdapter
        groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupList);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(groupAdapter);

        btnExercises.setOnClickListener(view -> {
            Intent intentExercise = new Intent(MainActivity.this, ListExercisesActivity.class);
            startActivity(intentExercise);
        });

        btnSetting.setOnClickListener(view -> {
            Intent intentSetting = new Intent(MainActivity.this, Setting.class);
            startActivity(intentSetting);
        });

        btnTraining.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Daily_Training.class);
            startActivity(intent);
        });

        btnCalendar.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Calendar.class);
            startActivity(intent);
        });

    }

}

