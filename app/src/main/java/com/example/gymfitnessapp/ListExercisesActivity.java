package com.example.gymfitnessapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gymfitnessapp.API.APIConnector;
import com.example.gymfitnessapp.Adapter.RecyclerViewAdapter;
import com.example.gymfitnessapp.Interface.Model.Exercise;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;

public class ListExercisesActivity extends AppCompatActivity {
    private APIConnector apiConnector;
    List<Exercise> exerciseList = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_exercises);

        apiConnector = new APIConnector();

        fetchDataFromAPI();
        Log.d(MainActivity.class.getSimpleName(), "LIST LENGTH: " + exerciseList.size());

        recyclerView = findViewById(R.id.list_exercise);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(getBaseContext(),exerciseList);

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setOnItemClickListener(this::onItemClick);

    }

    private void onItemClick(Exercise exercise) {
        // Create and show a dialog with enlarged GIF and information
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_exercise, null);
        dialogBuilder.setView(dialogView);

        GifImageView gifImageView = dialogView.findViewById(R.id.dialog_gif);
        TextView nameTextView = dialogView.findViewById(R.id.dialog_name);
        TextView bodyPartTextView = dialogView.findViewById(R.id.dialog_body_part);
        TextView equipmentTextView = dialogView.findViewById(R.id.dialog_equipment);
        // Set other TextViews for additional information

        Glide.with(this)
                .load(exercise.getGifUrl())
                .into(gifImageView);
        nameTextView.setText(exercise.getName());
        bodyPartTextView.setText(exercise.getBodyPart());
        equipmentTextView.setText(exercise.getEquipment());
        // Set other TextViews with additional information

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void fetchDataFromAPI() {
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
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Exercise exercise = new Exercise();
                            exercise.setId(jsonObject.getInt("id"));
                            exercise.setName(jsonObject.getString("name"));
                            exercise.setEquipment(jsonObject.getString("equipment"));
                            exercise.setBodyPart(jsonObject.getString("bodyPart"));
                            exercise.setGifUrl(jsonObject.getString("gifUrl"));
                            exercise.setTarget(jsonObject.getString("target"));
                            exerciseList.add(exercise);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerViewAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    Log.d(this.getClass().getSimpleName(), "Response: " + responseBody);
                } else {
                    Log.e(this.getClass().getSimpleName(), "Request unsuccessful: " + response.code());
                }
            }
        };

        apiConnector.fetchEquipmentData(callback, "body weight");
    }



}
