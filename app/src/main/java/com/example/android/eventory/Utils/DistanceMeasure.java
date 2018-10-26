package com.example.android.eventory.Utils;

import android.location.Location;
import android.util.Log;

/**
 * Created by ikelasid on 10/23/2017.
 */

public  class DistanceMeasure {
    private static final String TAG = "DistanceMeasure";


    public static boolean isNear(Location eventsLocation){

        Location myLocationManual = new Location("");
        double myLat = 40.6328284;
        myLocationManual.setLatitude(myLat);
        double myLong = 22.9469633;
        myLocationManual.setLongitude(myLong);


        double distance = eventsLocation.distanceTo(myLocationManual);
        double distanceInKilometers = distance / 1000;
        Log.d(TAG, "isNear: distance in kms : " + distanceInKilometers);

        if (distanceInKilometers < 30) {
            Log.d(TAG, "isEventNear: true");
            return true;
        }
        return false;
    }

    public static boolean isNear(Location eventsLocation,Location myLocataion){



        double distance = eventsLocation.distanceTo(myLocataion);
        double distanceInKilometers = distance / 1000;

        Log.d(TAG, "isNear: disance in kms : " + distanceInKilometers);

        if (distanceInKilometers < 30) {
            Log.d(TAG, "isEventNear: true");
            return true;
        }
        return false;
    }

}
