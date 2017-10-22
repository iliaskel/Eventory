package com.example.android.eventory.Home;

/**
 * Created by ikelasid on 10/17/2017.
 */

public class EventInformation {

    private String event_name;
    private String place_name;
    private String type;
    private String date;
    private String latLng;

    private double latitude;
    private double longitude;

    public EventInformation(String event_name, String place_name, String type, String date,String latLng){
        this.event_name=event_name;
        this.place_name=place_name;
        this.type=type;
        this.date=date;
        this.latLng=latLng;
    }

    public EventInformation(String event_name, String place_name, String type, String date,double latitude,double longitude){
        this.event_name=event_name;
        this.place_name=place_name;
        this.type=type;
        this.date=date;
        this.latitude=latitude;
        this.longitude=longitude;
    }



    public EventInformation(){}

    public String getLatLng(){
        return latLng;
    }

    public void setLatLng(String latLng){this.latLng=latLng;}

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


    public String toString(){
        String event="event name: "+ event_name;
        event+="\nevent place: "+place_name;
        event+="\ndate: "+date;
        event+="\ntype: "+type;
        event+="\nlatLng: "+latLng;
        return event;
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
}
