package com.example.android.eventory.Utils;

import android.location.Location;
import android.util.Log;

/**
 * Created by ikelasid on 10/23/2017.
 */

public  class DistanceMeasure {
    private static final String TAG = "DistanceMeasure";


    public static boolean isNear(Location eventsLocation){
        Location myLocation = new Location("");
        double myLat = 40.6328284;
        myLocation.setLatitude(myLat);
        double myLong = 22.9469633;
        myLocation.setLongitude(myLong);


        double distance = eventsLocation.distanceTo(myLocation);
        double distanceInKilometers = distance / 1000;

        if (distanceInKilometers < 30) {
            Log.d(TAG, "isEventNear: true");
            return true;
        }
        return false;
    }

}
