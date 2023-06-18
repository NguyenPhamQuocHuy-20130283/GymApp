package com.example.gymfitnessapp;

import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.gymfitnessapp.API.APIConnector;
import com.example.gymfitnessapp.Database.GymDB;
import com.example.gymfitnessapp.Interface.Model.Exercise;
import com.example.gymfitnessapp.Utils.Common;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;

public class Daily_Training extends AppCompatActivity {

    FButton btnStart;
    GifImageView detail_gif;
    TextView txtGetReady, txtCountDown, txtTimer, title;
    ProgressBar progressBar;
    private APIConnector apiConnector;
    LinearLayout linearLayout;
    CalendarDay currentDay;
    private String selectedGroup;
    Spinner groupSpinner;
    int ex_id = 0;
    List<Exercise> list = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    GymDB gymDB;
    private RequestManager glideRequestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily__training);

        btnStart = (FButton)findViewById(R.id.btnStartViewExercise);
        detail_gif = (GifImageView)findViewById(R.id.detail_image);
        txtCountDown = (TextView)findViewById(R.id.txtCountDown);
        txtGetReady = (TextView)findViewById(R.id.txtGetReady);
        txtTimer = (TextView)findViewById(R.id.timer);
        linearLayout = (LinearLayout)findViewById(R.id.layout_get_ready);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        title = (TextView)findViewById(R.id.title);

        gymDB = new GymDB(this);
        glideRequestManager = Glide.with(this);
        //set data

        progressBar.setMax(list.size());
        setExerciseInformation(ex_id);
        detail_gif.setVisibility(View.INVISIBLE);
        title.setVisibility(View.INVISIBLE);

        String date = formatDate(currentDay.today().getDate());
        String workoutSaved = gymDB.getWorkoutSaved(date);
        Log.d("WORKOUT SAVED", date);
        apiConnector = new APIConnector();
        fetchDataFromAPI(workoutSaved);
        Log.d(this.getClass().getSimpleName(), "onCreate: " + list.size());
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnStart.getText().toString().toLowerCase().equals("start")){
                    showGetReady();
                }
                else if (btnStart.getText().toString().toLowerCase().equals("done"))
                {

                    if (gymDB.getSettingMode() == 0)
                        countDownTimerForEasyMode.cancel();
                    else if (gymDB.getSettingMode() == 1)
                        countDownTimerForMediumMode.cancel();
                    else if (gymDB.getSettingMode() == 2)
                        countDownTimerForHardMode.cancel();

                    restTimeCountDownTimer.cancel();

                    if (ex_id < list.size())
                    {
                        showRestTime();
                        ex_id ++;
                        progressBar.setProgress(ex_id);
                        txtTimer.setText("");
                    }
                    else
                        showFinished();
                }

                else
                    if (ex_id < list.size())
                        setExerciseInformation(ex_id);

                    else
                        showFinished();
            }
        });
    }

    private void showGetReady() {

        txtGetReady.setText("GET READY");
        new CountDownTimer(4000,1000) {

            @Override
            public void onTick(long lng) {
                txtCountDown.setText(""+(lng/1000));
                txtCountDown.setTextColor(getResources().getColor(R.color.colorBlack));
                txtCountDown.setVisibility(View.VISIBLE);

                detail_gif.setVisibility(View.INVISIBLE);
                btnStart.setVisibility(View.INVISIBLE);
                txtTimer.setVisibility(View.VISIBLE);

                linearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                showExercise();
            }
        }
        .start();
    }

    private void showExercise() {

        //list size contain all posture
        if (ex_id < list.size())
        {
            detail_gif.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.INVISIBLE);

            if (gymDB.getSettingMode() == 0)
                countDownTimerForEasyMode.start();
            else if (gymDB.getSettingMode() == 1)
                countDownTimerForMediumMode.start();
            else if (gymDB.getSettingMode() == 2)
                countDownTimerForHardMode.start();

            //set picture and name
            //temp fix
            if (!list.isEmpty()) {
                // set picture and name
                // temp fix
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // temp fix
                        Glide.with(Daily_Training.this)
                                .load(list.get(ex_id).getGifUrl())
                                .into(detail_gif);
                    }
                });
                title.setText(list.get(ex_id).getName());
                title.setVisibility(View.VISIBLE);
                btnStart.setText("done");
            } else {
                Log.e(this.getClass().getSimpleName(), "List is empty");
            }
        }
        else
            showFinished();
    }

    private void showRestTime() {
        detail_gif.setVisibility(View.INVISIBLE);
        txtTimer.setVisibility(View.INVISIBLE);
        btnStart.setText("skip");
        btnStart.setVisibility(View.VISIBLE);
        title.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.VISIBLE);

        restTimeCountDownTimer.start();
        txtGetReady.setText("Rest Time");
    }


    private void showFinished() {

        detail_gif.setVisibility(View.INVISIBLE);
        btnStart.setVisibility(View.INVISIBLE);
        txtTimer.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        title.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        txtGetReady.setText(R.string.done_today_exercises);
        txtCountDown.setVisibility(View.GONE);

        if (gymDB.getSettingMode() == 0)
            countDownTimerForEasyMode.cancel();
        else if (gymDB.getSettingMode() == 1)
            countDownTimerForMediumMode.cancel();
        else if (gymDB.getSettingMode() == 2)
            countDownTimerForHardMode.cancel();

        restTimeCountDownTimer.cancel();

        //save to database
        gymDB.saveDay("" + Calendar.getInstance().getTimeInMillis());
    }

    //count down
    CountDownTimer countDownTimerForEasyMode = new CountDownTimer(Common.TIME_LIMIT_EASY,1000) {
        @Override
        public void onTick(long lng) {
            txtTimer.setText(""+(lng/1000));
        }

        @Override
        public void onFinish() {
            if (ex_id < list.size() -1)
            {
                ex_id++;
                progressBar.setProgress(ex_id);
                txtTimer.setText("");

                setExerciseInformation(ex_id);
                btnStart.setText("start");
            }
            else {
                showFinished();
            }
        }
    };

    CountDownTimer countDownTimerForMediumMode = new CountDownTimer(Common.TIME_LIMIT_MEDIUM,1000) {
        @Override
        public void onTick(long lng) {
            txtTimer.setText(""+(lng/1000));
        }

        @Override
        public void onFinish() {
            if (ex_id < list.size() -1)
            {
                ex_id++;
                progressBar.setProgress(ex_id);
                txtTimer.setText("");

                setExerciseInformation(ex_id);
                btnStart.setText("start");
            }
            else {
                showFinished();
            }
        }
    };

    CountDownTimer countDownTimerForHardMode = new CountDownTimer(Common.TIME_LIMIT_HARD,1000) {
        @Override
        public void onTick(long lng) {
            txtTimer.setText(""+(lng/1000));
        }

        @Override
        public void onFinish() {
            if (ex_id < list.size() -1)
            {
                ex_id++;
                progressBar.setProgress(ex_id);
                txtTimer.setText("");

                setExerciseInformation(ex_id);
                btnStart.setText("start");
            }
            else {
                showFinished();
            }
        }
    };


    CountDownTimer restTimeCountDownTimer = new CountDownTimer(10000,1000) {
        @Override
        public void onTick(long lng) {
            txtCountDown.setText(""+(lng/1000));
        }

        @Override
        public void onFinish() {
            if (ex_id < list.size() ) {
                setExerciseInformation(ex_id);
                showExercise();
            }
            else
            {
                showFinished();
            }
        }
    };


    private void setExerciseInformation(int id) {
        if (!list.isEmpty()) {
            // set picture and name
            // temp fix
            // Load image using Glide on the main thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // temp fix
                    glideRequestManager
                            .load(list.get(id).getGifUrl())
                            .into(detail_gif);
                }
            });
        } else {
            Log.e(this.getClass().getSimpleName(), "List is empty");
        }
    }
    private String formatDate(Date date) {
        return dateFormat.format(date);
    }
    private void fetchDataFromAPI(String bodyPart) {
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(MainActivity.class.getSimpleName(), "Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(this.getClass().getSimpleName(), "onResponse: " + responseBody);
                    // Xử lý dữ liệu phản hồi ở đây
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            if(bodyPart.equalsIgnoreCase(jsonObject.getString("bodyPart"))){
                                Exercise exercise = new Exercise();
                                exercise.setId(jsonObject.getInt("id"));
                                exercise.setName(jsonObject.getString("name"));
                                exercise.setEquipment(jsonObject.getString("equipment"));
                                exercise.setBodyPart(jsonObject.getString("bodyPart"));
                                exercise.setGifUrl(jsonObject.getString("gifUrl"));
                                exercise.setTarget(jsonObject.getString("target"));
                                list.add(exercise);
                            }


                        }
                        // Perform necessary operations with the populated list here
                        Log.d(this.getClass().getSimpleName(), "onResponse: " + list.size());
                        progressBar.setMax(list.size());
                        setExerciseInformation(ex_id);
                        detail_gif.setVisibility(View.INVISIBLE);
                        title.setVisibility(View.INVISIBLE);

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
