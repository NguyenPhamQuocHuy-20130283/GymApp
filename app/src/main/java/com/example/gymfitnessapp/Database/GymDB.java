package com.example.gymfitnessapp.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


import java.util.ArrayList;
import java.util.List;


public class GymDB extends SQLiteOpenHelper {

    private static final String DB_NAME = "gym_database.db";
    private static final int DB_VERSION = 1;

    public GymDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the "Setting" table
        String createSettingTable = "CREATE TABLE IF NOT EXISTS Setting (" +
                "Mode INTEGER NOT NULL UNIQUE," +
                "PRIMARY KEY(Mode)" +
                ")";
        db.execSQL(createSettingTable);

        // Create the "WorkoutDays" table
        String createWorkoutDaysTable = "CREATE TABLE IF NOT EXISTS WorkoutDays (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Day TEXT," +
                "BodyPart TEXT" +
                ")";
        db.execSQL(createWorkoutDaysTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if exist
        db.execSQL("DROP TABLE IF EXISTS Setting");
        db.execSQL("DROP TABLE IF EXISTS WorkoutDays");

        // Create tables again
        onCreate(db);
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
    public boolean isDateSaved(String selectedDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM WorkoutDays WHERE Day = ?";
        Cursor cursor = db.rawQuery(query, new String[]{selectedDate});

        boolean isSaved = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return isSaved;
    }
    @SuppressLint("Range")
    public String getWorkoutSaved(String selectedDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM WorkoutDays WHERE Day = ?";
        Cursor cursor = db.rawQuery(query, new String[]{selectedDate});

        String workout = "";

        if (cursor.moveToFirst()) {
            workout = cursor.getString(cursor.getColumnIndex("BodyPart"));
        }

        cursor.close();
        db.close();

        return (workout==""||workout==null)?"back":workout;
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

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = String.format("INSERT INTO WorkoutDays(Day) VALUES('%s');",value);
        sqLiteDatabase.execSQL(query);
    }
    public void saveSelectedDate(String selectedDate, String selectedBodyPart) {
        SQLiteDatabase db = getWritableDatabase();
        if (db == null) {
            Log.e("GymDB", "Failed to open database");
            return;
        }

        ContentValues values = new ContentValues();
        values.put("Day", selectedDate);
        values.put("BodyPart", selectedBodyPart);

        long result = db.insert("WorkoutDays", null, values);
        db.close();

        if (result == -1) {
            Log.e("GymDB", "Failed to save workout day");
        } else {
            Log.d("GymDB", "Workout day saved successfully");
        }
    }

    public void deleteSelectedDate(String selectedDate) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "Day = ?";
        String[] whereArgs = new String[]{selectedDate};

        db.delete("WorkoutDays", whereClause, whereArgs);
        db.close();
    }

    @SuppressLint("Range")
    public String getBodyPartForDate(String selectedDate) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {"BodyPart"};
        String selection = "Day = ?";
        String[] selectionArgs = {selectedDate};

        Cursor cursor = db.query("WorkoutDays", projection, selection, selectionArgs, null, null, null);
        String bodyPart = null;

        if (cursor.moveToFirst()) {
            bodyPart = cursor.getString(cursor.getColumnIndex("BodyPart"));
        }

        cursor.close();
        db.close();

        return bodyPart;
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
