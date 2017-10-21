package com.example.android.eventory.Utils;

import android.location.Location;
import android.util.Log;

/**
 * Created by ikelasid on 10/17/2017.
 */

public class StringFixer {

    private static final String TAG = "StringFixer";

    private static double myLat=40.6328284;
    private static double myLong=22.9469633;

    public static boolean isEventNear(String stringDistance){

        String[] parts= stringDistance.split(" ");
        parts[0]=parts[0].replace(",",".");
        parts[1]=parts[1].replace(",",".");
        double latitude=Double.parseDouble(parts[0]);
        double longitude=Double.parseDouble(parts[1]);

        Location myLocation=new Location("");
        myLocation.setLatitude(myLat);
        myLocation.setLongitude(myLong);

        Location targetLocation=new Location("");
        targetLocation.setLatitude(latitude);
        targetLocation.setLongitude(longitude);

        double distance=targetLocation.distanceTo(myLocation);
        double distanceInKilometers=distance/1000;
        Log.d(TAG, "distance as generated: "+ distance);
        Log.d(TAG, "distance in kilometers" + distanceInKilometers);
        if(distanceInKilometers<30){
            Log.d(TAG, "isEventNear: true");
            return true;}

        return false;

    }


}
