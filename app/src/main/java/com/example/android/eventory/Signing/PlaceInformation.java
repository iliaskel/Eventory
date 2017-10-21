package com.example.android.eventory.Signing;

/**
 * Created by ikelasid on 10/8/2017.
 */

public class PlaceInformation {
    private String address,name,latitude,longitude;

    public PlaceInformation(){}

    public PlaceInformation(String name,String address,String latitude,String longitude){
        this.name=name;
        this.address=address;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
