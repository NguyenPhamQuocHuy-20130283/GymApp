package com.example.gymfitnessapp.Custom;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import androidx.core.content.ContextCompat;

import com.example.gymfitnessapp.R;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.CalendarDay;

public class TodayDecorator implements DayViewDecorator {

    private final CalendarDay today;
    private final Typeface typeface;

    public TodayDecorator() {
        today = CalendarDay.today();
        typeface = Typeface.defaultFromStyle(Typeface.BOLD);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(today);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(typeface);
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new ForegroundColorSpan(Color.BLUE));
    }
}

