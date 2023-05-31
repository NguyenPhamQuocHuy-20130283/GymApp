package com.example.gymfitnessapp;

import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gymfitnessapp.Database.GymDB;
import com.example.gymfitnessapp.Model.Exercise;
import com.example.gymfitnessapp.Utils.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import info.hoang8f.widget.FButton;
import pl.droidsonroids.gif.GifImageView;

public class Daily_Training extends AppCompatActivity {

    FButton btnStart;
    GifImageView detail_gif;
    TextView txtGetReady, txtCountDown, txtTimer, title;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    private String selectedGroup;
    Spinner groupSpinner;
    int ex_id = 0;
    List<Exercise> list = new ArrayList<>();

    GymDB gymDB;

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

        //set data
        progressBar.setMax(list.size());
        setExerciseInformation(ex_id);
        detail_gif.setVisibility(View.INVISIBLE);
        title.setVisibility(View.INVISIBLE);


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
            detail_gif.setImageResource(Integer.parseInt(list.get(ex_id).getGifUrl()));
            title.setText(list.get(ex_id).getName());
            title.setVisibility(View.VISIBLE);
            btnStart.setText("done");
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
        title.setText(list.get(id).getName());
        //temp fix
        detail_gif.setImageResource(Integer.parseInt(list.get(id).getGifUrl()));
        btnStart.setText("start");

        detail_gif.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.VISIBLE);
        txtTimer.setVisibility(View.VISIBLE);

        linearLayout.setVisibility(View.INVISIBLE);
    }


}
