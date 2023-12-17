package com.example.mp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MyAppDatabase";
    private static final int DATABASE_VERSION = 1;

    // 테이블 생성 쿼리
    private static final String CREATE_TABLE_Diary =
            "CREATE TABLE entries (_id INTEGER PRIMARY KEY AUTOINCREMENT, date text, content text, picture text, temperature text, rainType text, sky text);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_Diary);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 업그레이드 로직 (필요에 따라 구현)
    }
}
