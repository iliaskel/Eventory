package com.example.android.eventory;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.eventory.Home.EventInformation;
import com.example.android.eventory.Home.HomeActivity;
import com.example.android.eventory.Utils.BottomNavigationViewHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ikelasid on 10/1/2017.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";
    private static final int ACTIVITY_NUMBER = 2;
    private Context mContext = MapActivity.this;

    private ArrayList<EventInformation> eventsList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: entered");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        eventsList = HomeActivity.mEventsList;


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        setUpBottomNavigationView();
    }

    private void setUpBottomNavigationView() {
        Log.d(TAG, "setUpBottomNavigationView: setting up bottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setUpBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(MapActivity.this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }


    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        Log.d(TAG, "onMapReady: list size==" + String.valueOf(eventsList.size()));
        for (int i = 0; i < eventsList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            EventInformation event;
            LatLng latLng;
            event = eventsList.get(i);
            Log.d(TAG, "onMapReady: EVENT++"+event.toString());
            latLng = new LatLng(event.getLatitude(), event.getLongitude());
            markerOptions.position(latLng);
            markerOptions.title(event.getEvent_name());
            googleMap.addMarker(markerOptions);
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(40.6328284, 22.9469633)));
    }
}
