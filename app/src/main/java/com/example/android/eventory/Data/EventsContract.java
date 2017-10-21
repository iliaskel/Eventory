package com.example.android.eventory.Data;

import android.provider.BaseColumns;

/**
 * Created by ikelasid on 10/16/2017.
 */

public class EventsContract  {

    private  EventsContract(){}

    public static class EventsEntry implements BaseColumns{

        public static final String TABLE_NAME="events";
        public static final String COLUMN_NAME="name";
        public static final String COLUMN_ADDRESS="address";
        public static final String COLUMN_TYPE="type";
        public static final String COLUMN_LATITUDE="latitude";
        public static final String COLUMN_LONGITUDE="longitude";

    }
}
