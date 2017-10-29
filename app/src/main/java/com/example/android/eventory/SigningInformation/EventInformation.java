package com.example.android.eventory.SigningInformation;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ikelasid on 10/17/2017.
 */

public class EventInformation  {

    private String event_name;
    private String place_name;
    private String type;
    private String date;

    private double latitude;
    private double longitude;

    //default constructor
    public EventInformation(){}

    public EventInformation(String event_name, String place_name, String type, String date,double latitude,double longitude){
        this.event_name=event_name;
        this.place_name=place_name;
        this.type=type;
        this.date=date;
        this.latitude=latitude;
        this.longitude=longitude;
    }



    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    @Override
    public String toString() {
        String event="";
        event+="Event Name: "+event_name;
        event+="\nPlace Name:" +place_name;
        event+="\nType\t:"+ type;
        event+="\nDate\t"+date;
        event+="\nLat\t:"+String.valueOf(latitude);
        event+="\nLng\t:"+String.valueOf(longitude);

        return event;
    }
}
