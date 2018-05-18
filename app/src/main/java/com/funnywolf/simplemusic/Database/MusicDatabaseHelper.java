package com.funnywolf.simplemusic.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SimpleMusic.db";
    public static final String CREATE_DATABASE = "create table  SimpleMusic"
            + "(id integer primary key autoincrement, list text, name text)";


    public MusicDatabaseHelper(Context context, String name,
                               SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists SimpleMusic");
        onCreate(db);
    }

}
