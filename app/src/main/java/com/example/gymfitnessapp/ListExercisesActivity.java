package com.example.gymfitnessapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymfitnessapp.Adapter.RecyclerViewAdapter;
import com.example.gymfitnessapp.Database.ExerciseApiService;
import com.example.gymfitnessapp.Model.Exercise;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListExercisesActivity extends AppCompatActivity {

    List<Exercise> exerciseList = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_exercises);

        initData();

        recyclerView = (RecyclerView)findViewById(R.id.list_exercise);
        recyclerViewAdapter = new RecyclerViewAdapter(exerciseList,getBaseContext());
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void initData() {

        String json = ExerciseApiService.getExercises("waist");

        try {
            // Chuyển đổi JSON thành đối tượng JSONObject
            JSONObject exerciseJson = new JSONObject(json);

            // Lấy thông tin từ JSONObject
            String id = exerciseJson.getString("id");
            String name = exerciseJson.getString("name");
            String bodyPart = exerciseJson.getString("bodyPart");
            String equipment = exerciseJson.getString("equipment");
            String gifUrl = exerciseJson.getString("gifUrl");
            String target = exerciseJson.getString("target");

            // Tạo đối tượng Exercise
            Exercise exercise = new Exercise(id, name, bodyPart, equipment, gifUrl, target);

            // Thêm đối tượng Exercise vào danh sách
            exerciseList.add(exercise);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
