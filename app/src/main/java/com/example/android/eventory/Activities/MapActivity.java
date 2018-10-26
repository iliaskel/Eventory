package com.example.android.eventory.Activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.eventory.Signingformation.EventInformation;
import com.example.android.eventory.R;
import com.example.android.eventory.Utils.BottomNavigationViewHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

/**
 * Created by ikelasid on 10/1/2017.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";
    private static final int ACTIVITY_NUMBER = 1;
    private Context mContext = MapActivity.this;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 10;
    private final String[] permissionsArray = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};


    private ArrayList<EventInformation> eventsList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: entered");
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.eventory.R.layout.activity_maps);

        eventsList = EventsActivity.mEventsList;


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        setUpBottomNavigationView();
    }


    public void onMapReady(final GoogleMap googleMap) {

        Log.d(TAG, "onMapReady: list size==" + String.valueOf(eventsList.size()));
        for (int i = 0; i < eventsList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            EventInformation event;
            LatLng latLng;
            event = eventsList.get(i);
            Log.d(TAG, "onMapReady: EVENT :: " + event.toString());
            latLng = new LatLng(event.getLatitude(), event.getLongitude());
            markerOptions.position(latLng);
            markerOptions.title(event.getEvent_name());
            googleMap.addMarker(markerOptions);
        }
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // TODO: HANDLE THE PERMISSION REQUEST
            ActivityCompat.requestPermissions(this, permissionsArray, LOCATION_PERMISSION_REQUEST_CODE);

        } else {

            moveMapCamera(googleMap);
        }


    }

    private void moveMapCamera(final GoogleMap googleMap) {

        // If last Known location is available, we use this location to move the camera
        // if last known location is null, then we get the location from the network provider
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "moveMapCamera: Permissions Denied");
            return;
        }
        googleMap.setMyLocationEnabled(true);
        getLocation(googleMap);

    }

    private void getLocation(final GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(this);
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.getResult() != null) {
                        Location location = task.getResult();
                        Log.d(TAG, "onComplete: maps " + task.getResult());
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    }
                    else{
                        getLocationFromNetworkProvider(googleMap);
                    }
                }
            });
        }


    }

    private void getLocationFromNetworkProvider(final GoogleMap googleMap) {
        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: ");
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            try{
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,locationListener,null);
            }
            catch (NullPointerException e){
                Log.d(TAG, "getLocationFromNetworkProvider: NullPointerException :: "  + e.toString());
            }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    private void setUpBottomNavigationView() {
        Log.d(TAG, "setUpBottomNavigationView: setting up bottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setUpBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(MapActivity.this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }
}
