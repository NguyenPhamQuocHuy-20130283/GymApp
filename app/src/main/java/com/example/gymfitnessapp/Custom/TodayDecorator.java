package com.example.gymfitnessapp.Custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import androidx.core.content.ContextCompat;

import com.example.gymfitnessapp.R;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.CalendarDay;

public class TodayDecorator implements DayViewDecorator {

    private final CalendarDay today;
    private final Context context;

    public TodayDecorator(Context context) {
        today = CalendarDay.today();
        this.context = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(today);
    }

    @Override
    public void decorate(DayViewFacade view) {
        int highlightColor = ContextCompat.getColor(context, R.color.yellow); // Change the color to your desired color resource
        view.setBackgroundDrawable(new ColorDrawable(highlightColor));
    }
}

