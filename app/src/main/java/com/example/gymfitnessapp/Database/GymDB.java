package com.example.gymfitnessapp.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


import java.util.ArrayList;
import java.util.List;


public class GymDB extends SQLiteAssetHelper {

    private static final String DB_NAME = "gym.db";
    private static final int DB_VERSION = 1;

    public GymDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @SuppressLint("Range")
    public int getSettingMode(){

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"Mode"};
        String sqlTable = "Setting";

        sqLiteQueryBuilder.setTables(sqlTable);
        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase, sqlSelect,
                null, null, null, null, null);
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex("Mode"));
    }

    public void saveSettingMode(int value){

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = "UPDATE Setting SET Mode = " + value;
        sqLiteDatabase.execSQL(query);
    }

    @SuppressLint("Range")
    public List<String> getWorkoutDays(){

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"Day"};
        String sqlTable = "WorkoutDays";

        sqLiteQueryBuilder.setTables(sqlTable);
        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase, sqlSelect,
                null, null, null, null, null);

        List<String> result = new ArrayList<>();
        if (cursor.moveToFirst())
        {
            do {
                result.add(cursor.getString(cursor.getColumnIndex("Day")));
            }while (cursor.moveToNext());
        }
        return result;
    }

    public void saveDay(String value){

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = String.format("INSERT INTO WorkoutDays(Day) VALUES('%s');",value);
        sqLiteDatabase.execSQL(query);
    }
    public void saveSelectedDate(String selectedDate, String selectedBodyPart) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Day", selectedDate);
        values.put("BodyPart", selectedBodyPart);

        db.insert("WorkoutDays", null, values);
        db.close();
    }
    public void deleteSelectedDate(String selectedDate) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "Day = ?";
        String[] whereArgs = new String[]{selectedDate};

        db.delete("WorkoutDays", whereClause, whereArgs);
        db.close();
    }
//    @SuppressLint("Range")
//    public HashSet<CalendarDay> getWorkoutDays() {
//        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
//        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
//
//        String[] sqlSelect = {"Day"};
//        String sqlTable = "WorkoutDays";
//
//        sqLiteQueryBuilder.setTables(sqlTable);
//        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase, sqlSelect, null, null, null, null, null);
//
//        HashSet<CalendarDay> result = new HashSet<>();
//        if (cursor.moveToFirst()) {
//            do {
//                String dateString = cursor.getString(cursor.getColumnIndex("Day"));
//                Date date = parseDate(dateString);
//                CalendarDay calendarDay = CalendarDay.from(date);
//                result.add(calendarDay);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return result;
//    }


}
