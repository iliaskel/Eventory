package com.example.android.eventory.Signingformation;

/**
 * Created by ikelasid on 10/8/2017.
 */

public class PlaceInformation {
    private String address,name, latitude, longitude;
    private double latitudeDouble, longitudeDouble;

    public PlaceInformation(){}

    public PlaceInformation(String name, String address, double latitudeDouble, double longitudeDouble){
        this.name=name;
        this.address=address;
        this.latitudeDouble = latitudeDouble;
        this.longitudeDouble = longitudeDouble;
    }

    public PlaceInformation(String name, String address, String latitude, String longitude){
        this.name=name;
        this.address=address;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public double getLatitudeDouble() {
        return latitudeDouble;
    }

    public void setLatitudeDouble(double latitudeDouble) {
        this.latitudeDouble = latitudeDouble;
    }

    public double getLongitudeDouble() {
        return longitudeDouble;
    }

    public void setLongitudeDouble(double longitudeDouble) {
        this.longitudeDouble = longitudeDouble;
    }
}
