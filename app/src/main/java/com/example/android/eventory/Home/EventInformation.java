package com.example.android.eventory.Home;

/**
 * Created by ikelasid on 10/17/2017.
 */

public class EventInformation {

    private String event_name, place_name,type,date;

    public EventInformation(){}
    public EventInformation(String event_name, String place_name, String type, String date){
        this.event_name =event_name;
        this.place_name = place_name;
        this.type=type;
        this.date=date;
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
}
