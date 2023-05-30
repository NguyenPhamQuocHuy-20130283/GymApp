package com.example.gymfitnessapp.Database;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExerciseApiService {
    private static final String API_URL = "https://exercisedb.p.rapidapi.com/exercises";
    private static final String API_KEY = "YOUR KEY";
    private static final String API_HOST = "exercisedb.p.rapidapi.com";

    public static String getExercises(String type) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_URL + "/bodyPart/"+type)
                .get()
                .addHeader("X-RapidAPI-Key", API_KEY)
                .addHeader("X-RapidAPI-Host", API_HOST)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
