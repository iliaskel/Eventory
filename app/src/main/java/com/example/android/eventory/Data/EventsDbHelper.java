package com.example.android.eventory.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.tasks.TaskExecutors;

/**
 * Created by ikelasid on 10/16/2017.
 */

public class EventsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="events.db";
    private static final int DATABASE_VERSION=2;
    private static final String TAG = "EventsDbHelper";

    public EventsDbHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_EVENTS_TABLE= "CREATE TABLE " +
                    EventsContract.EventsEntry.TABLE_NAME +
                    "(" +
                    EventsContract.EventsEntry.COLUMN_NAME + " varchar(50) not null," +
                    EventsContract.EventsEntry.COLUMN_ADDRESS + " varchar(50) not null,"+
                    EventsContract.EventsEntry.COLUMN_TYPE + " varchar(20) not null," +
                    EventsContract.EventsEntry.COLUMN_LATITUDE + " real not null," +
                    EventsContract.EventsEntry.COLUMN_LONGITUDE + " real not null" +
                    ");";
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop="drop table if exists " + EventsContract.EventsEntry.TABLE_NAME ;
        db.execSQL(drop);
        onCreate(db);
    }
}