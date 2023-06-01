package com.example.gymfitnessapp.API;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class APIConnector {
    private static final String API_HOST = "exercisedb.p.rapidapi.com";
    private static final String API_KEY = "5cb82f89d3mshd7540a87e7a1e58p1bc785jsne4b7843ecd18";

    private static final String url = "https://exercisedb.p.rapidapi.com/exercises";
    private OkHttpClient client;

    public APIConnector() {
        client = new OkHttpClient();
    }

    public void fetchEquipmentData(Callback callback, String equipment) {

        Request request = new Request.Builder()
                .url(url+"/equipment/"+equipment)
                .header("X-RapidAPI-Host", API_HOST)
                .header("X-RapidAPI-Key", API_KEY)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }
    public void fetchBodyPartListData(Callback callback) {

        Request request = new Request.Builder()
                .url(url+"/bodyPartList")
                .header("X-RapidAPI-Host", API_HOST)
                .header("X-RapidAPI-Key", API_KEY)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void fetchTargetListData(Callback callback) {

        Request request = new Request.Builder()
                .url(url+"/targetList")
                .header("X-RapidAPI-Host", API_HOST)
                .header("X-RapidAPI-Key", API_KEY)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

}

