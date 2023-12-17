package com.example.mp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DiaryDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public DiaryDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertEntry(Diary diary) {
        ContentValues values = new ContentValues();
        values.put("date", diary.getDate());
        values.put("content", diary.getContent());
        values.put("picture", diary.getPicture());
        values.put("temperature", diary.getTemperature());
        values.put("raintType", diary.getRainType());
        values.put("sky", diary.getSky());

        return database.insert("entries", null, values);
    }

    public List<Diary> getAllEntries() {
        List<Diary> entries = new ArrayList<>();
        Cursor cursor = database.query("entries", null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Diary diary = cursorToEntry(cursor);
            entries.add(diary);
            cursor.moveToNext();
        }

        cursor.close();
        return entries;
    }

    private Diary cursorToEntry(Cursor cursor) {
        Diary diary = new Diary();
        diary.setId(cursor.getLong(0));
        diary.setDate(cursor.getString(1));
        diary.setContent(cursor.getString(2));
        diary.setPicture(cursor.getString(3));
        diary.setTemperature(cursor.getString(4));
        diary.setRainType(cursor.getString(5));
        diary.setSky(cursor.getString(6));
        return diary;
    }
}
