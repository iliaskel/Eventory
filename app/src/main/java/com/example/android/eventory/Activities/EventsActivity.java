package com.example.android.eventory.Activities;

/**
 * This activity displays all the events that are near, based on the user's preferred max distance.
 * It uses EventInformation object-class to retrieve events from the Google Firebase RealTime Database and display the events using recycler view
 * This Activity also contains a fab --will be displayed only if the signed-in user is of type:Owner--
 * which function is to trigger the AddNewEventActivity to add new events.
 *  **/

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.android.eventory.EventsAdapter;
import com.example.android.eventory.R;
import com.example.android.eventory.Signingformation.EventInformation;
import com.example.android.eventory.Signingformation.UserInformation;
import com.example.android.eventory.Utils.BottomNavigationViewHelper;
import com.example.android.eventory.Utils.DistanceMeasure;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

public class EventsActivity extends AppCompatActivity implements EventsAdapter.EventItemClickListener {

    private static final String TAG = "EventsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String[] permissionsArr = {FINE_LOCATION};
    private final Context mContex = EventsActivity.this;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 10;
    private final int LOCATION_ACTIVATION_RESULT_CODE = 11;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    //vars
    private static final int ACTIVITY_NUMBER = 0; //Used for displaying the bottom navigation's view proper checked button
    protected boolean mIsOwner = false;            //Used for defining if the user is owner of a place and setting up the fab
    private boolean isFirstTime = true;           //Used for defining is it's the first time displaying the event's list
    protected static ArrayList<EventInformation> mEventsList = new ArrayList<>();
    private EventsAdapter eventsAdapter = new EventsAdapter(mEventsList, this);

    //fab for adding new events || Shown only to owners
    private FloatingActionButton mAddEvent;
    private RecyclerView mEventsRecyclerView;


    // FireBase && User vars
    protected String mUserId;
    protected FirebaseAuth mAuth;
    protected FirebaseDatabase mDatabase;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected DatabaseReference myRef;
    protected FirebaseUser mCurrentUser;

    // Variable to determine how many times location is updated.
    private int locationUpdates = 0 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "entered onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setUpBottomNavigationView();
        setUpFireBase();

        // Here, we are checking the necessary permissions, depending on the SDK on the device
        // then, getting the location depending on if LastKnownLocation is available
        // and finally initializing the activity, based on the location we got
        getLocationAndInitActivity();
    }

    private void getLocationAndInitActivity() {
        if (Build.VERSION.SDK_INT >= 23) {
            Log.d(TAG, "onCreate: SDK_INT>23");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "onCreate: show permission dialog");
                ActivityCompat.requestPermissions(this, permissionsArr, LOCATION_PERMISSION_REQUEST_CODE);
            }
            else {
                checkIfGPSIsEnabledAndGetLocation();
            }
        }
        // SDK_INT<=23
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onCreate: SDK<23 :: Fine Permission granted");
                checkIfGPSIsEnabledAndGetLocation();
            } else {
                Log.d(TAG, "onCreate: SDK<23 :: Fine permission not granted");
            }
        }
        
    }

    // In cases when GPS provider is not enabled, shows a Dialog
    // for letting the user turn on the GPS with on click. There is an infinite loop
    // for when the user don't enable the GPS, so the dialog is showing up constantly
    // until the user let the GPS to be enabled and can get the location/start activity.
    // The result of the user's choice is handled in :: onActivityResult :: method.
    private void checkIfGPSIsEnabledAndGetLocation() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(new LocationRequest());
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        //User presses OK, GPS gets enabled and we are getting the location and starting the activity
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(TAG, "onSuccess: ");
                getLastKnownLocation();
            }
        });

        // Users presses CANCEL and the dialog is showing up constantly, until OK is pressed.
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        Log.d(TAG, "onFailure: ");
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        resolvableApiException.startResolutionForResult(EventsActivity.this,LOCATION_ACTIVATION_RESULT_CODE);
                    } catch (IntentSender.SendIntentException sendExc) {

                    }
                }
            }
        });
    }

    // Handling the user's choice on the GPS enabler dialog.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case LOCATION_ACTIVATION_RESULT_CODE:
                    checkIfGPSIsEnabledAndGetLocation();
        }
    }

    // Trying to get the device's LastKnownLocation. If the location is not available,
    // the device gets the location `through the GPS provider in :: getLocationFromGPSProvider ::.
    // Once the location is retrieved, the activity gets initialized.

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: ENTER");
        FusedLocationProviderClient mFusedLocationProviderClient = new FusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location lastKnownLocation = task.getResult();
                    if (lastKnownLocation != null) {
                        Log.d(TAG, "onComplete: getLastKnownLocation :: True :: " + lastKnownLocation);
                        initActivity(lastKnownLocation);
                    } else {
                        Log.d(TAG, "onComplete: getLastKnownLocation :: null :: ");
                        getLocationFromGPSProvider();
                    }
                }
            });
        }
    }

    // TODO: FIX THIS MOFO.
    // In cases that lastKnownLocation is null, we try to get the location via the GPS provider.
    // But, in this case, GPS is enabling but the returned location is always null.
    // Check location strategies on android documentation
    private void getLocationFromGPSProvider() {
        Log.d(TAG, "getLocationFromGPSProvider: Entered");

        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: location :: " +location.getLongitude()+ "," +location.getLatitude());
                locationUpdates++;
                Log.d(TAG, "onLocationChanged: locationUpdates :: "  + locationUpdates);
                if (locationUpdates>2)
                    initActivity(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "onStatusChanged: ");

            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "onProviderEnabled: ");

            }
            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled: ");
                Log.d(TAG, "onProviderDisabled: isProviderEnabled(GPS) :: " + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
                Log.d(TAG, "onProviderDisabled: getAllPRoviders :: " + locationManager.getAllProviders());
                getLocationFromNetworkProvider();
            }
        };

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager != null) {
                Log.d(TAG, "getLocationFromGPSProvider: single update location manager != null");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            }
            else{
                Log.d(TAG, "getLocationFromGPSProvider: location manager == null");
            }
        }

    }

    private void getLocationFromNetworkProvider() {
        Log.d(TAG, "getLocationFromNetworkProvider: ENTERED");
        final LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: NetworkProvider :: " + location);
                initActivity(location);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled: NetworkProvider");

            }
        };

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            assert locationManager != null;
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,locationListener,null);

        }


    }

    //The activity is initialized here.
    private void initActivity(Location location) {
        Log.d(TAG, "initActivity: Entered");
        findViews();
        initRecyclerView(location);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:
                if(grantResults.length>0){
                    if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                        showToast("Permission granted");
                        checkIfGPSIsEnabledAndGetLocation();
                    }
                    else{
                        Log.d(TAG, "onRequestPermissionsResult: Permission not granted");
                        showToast("Permission not granted");
                    }
                }
        }

    }

    /**
     * =================== Init Methods ============================
     */


    private void setUpFireBase() {
            Log.d(TAG, "setUpFireBase: ");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser =mAuth.getCurrentUser();
        try{
            mUserId = mCurrentUser.getUid();
        }
        catch (NullPointerException e){
            Log.d(TAG, "onCreate: "+ e.toString());
        }
        Log.d(TAG, "setUpFireBase: userID "+mUserId);
        mDatabase=FirebaseDatabase.getInstance();
        myRef=mDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    private void findViews() {
            Log.d(TAG, "findViews: ");
        mAddEvent=findViewById(R.id.fab_add_event);
        mEventsRecyclerView=findViewById(R.id.rv_events);
        mEventsRecyclerView.setHasFixedSize(true);
    }

    private void initRecyclerView(final Location location) {
        Log.d(TAG, "initRecyclerView: ");


        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mEventsRecyclerView.setLayoutManager(layoutManager);

        // Called every time the database is changed && the first time activity is called
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) throws NullPointerException {
                showToast("updated");

                // Getting the information about the type of the user who is logged in.
                // After that we set up the fab to be visible or not depending on the type of the user.
                // if(type.equals("owner")) the fab must be visible in order form him to be able to add new events.
                mIsOwner =isOwner(dataSnapshot);
                setUpFab();

                //clearing the events list so events won't be duplicated
                mEventsList.clear();

                // Getting all the events from the database so we can determine which of them should be
                // shown to the logged user. All the events near to the user are added to the eventsList
                // and after the check of all events, the recycler view is created.
                for(DataSnapshot ds:dataSnapshot.child("events").getChildren()) {
                    Log.d(TAG, "onDataChange: for loop");
                    EventInformation event = new EventInformation();
                    event.setLatitude(ds.getValue(EventInformation.class).getLatitude());
                    event.setLongitude(ds.getValue(EventInformation.class).getLongitude());

                    Location eventsLocation = new Location("");
                    eventsLocation.setLatitude(event.getLatitude());
                    eventsLocation.setLongitude(event.getLongitude());


                    if (DistanceMeasure.isNear(eventsLocation,location)) {                                      //IF event is near **should fix the user's preferences for manual accepted distance
                        Log.d(TAG, "onDataChange: isNear :: TRUE");
                        event.setEvent_name(ds.getValue(EventInformation.class).getEvent_name());
                        event.setPlace_name(ds.getValue(EventInformation.class).getPlace_name());
                        event.setDate(ds.getValue(EventInformation.class).getDate());
                        event.setType(ds.getValue(EventInformation.class).getType());
                        event.setEventID(ds.getKey());
                        mEventsList.add(event);
                        Log.d(TAG, "onDataChange: " + mEventsList.size());
                    }
                    else{
                        Log.d(TAG, "onDataChange: event is far ");
                    }
                }
                if(isFirstTime){
                    Log.d(TAG, "onDataChange: isFirstTime :: " + isFirstTime);
                    isFirstTime=false;
                    Log.d(TAG, "onDataChange: isOwner " +mIsOwner);
                    mEventsRecyclerView.setAdapter(eventsAdapter);

                }
                else{
                    Log.d(TAG, "onDataChange: isFirstTime :: " + isFirstTime);
                    eventsAdapter.notifyDataSetChanged();
                }



            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
}

    // Checking from hte FirebaseDatabase whether the logged user is an owner in order to
    // display the fab or not. Also attaching the listener to the fab(when it's visible)
    // for starting the addEventActivity
    private void setUpFab() {
        Log.d(TAG, "setUpFab: ");
    if(mIsOwner) {
        Log.d(TAG, "setUpFab: IS OWNER");
        mAddEvent.setVisibility(View.VISIBLE);
        mAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked");
                Intent addEventIntent=new Intent(EventsActivity.this,AddEventActivity.class);
                startActivity(addEventIntent);
            }
        });
    }
    else{
        Log.d(TAG, "setUpFab: IS NOT OWNER");
        mAddEvent.setVisibility(View.INVISIBLE);
    }
}


    private void setUpBottomNavigationView(){
    Log.d(TAG, "setUpBottomNavigationView: setting up bottomNavigationView");
    BottomNavigationViewEx bottomNavigationViewEx=(BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
    BottomNavigationViewHelper.setUpBottomNavigationView(bottomNavigationViewEx);
    BottomNavigationViewHelper.enableNavigation(EventsActivity.this,bottomNavigationViewEx);
    Menu menu=bottomNavigationViewEx.getMenu();
    MenuItem menuItem=menu.getItem(ACTIVITY_NUMBER);
    menuItem.setChecked(true);
}


    /**
     * ====================== LifeCycle Methods ========================
     */
        @Override
        protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        mAuth.signOut();
    }

        @Override
        public void onStart() {

        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

        @Override
        public void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }



    /**
     * ====================================================================
     */



    private void showToast(String s) {
        Toast.makeText(mContex,s,Toast.LENGTH_LONG).show();
    }


    private boolean isOwner(DataSnapshot dataSnapshot) {
        String userType=dataSnapshot.child("users").child(mUserId).getValue(UserInformation.class).getType();
        if (userType.equals("owner"))
            return true;
        return false;
    }


    @Override
    public void onEventItemClickListener(final View clickedEvent, final String eventID) {

        PopupMenu popupMenu = new PopupMenu(this,clickedEvent);
        Log.d(TAG, "onEventItemClickListener: SDK VERSION :: " + Build.VERSION.SDK_INT);
        popupMenu.inflate(R.menu.event_attending_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.attending:
                        showToast("Attending");
                        Log.d(TAG, "onMenuItemClick: userID     :: " +mUserId);
                        Log.d(TAG, "onMenuItemClick: event      :: " +clickedEvent.toString());
                        Log.d(TAG, "onMenuItemClick: eventID    :: " +eventID);
                        myRef.child("attending").child(mUserId).child(eventID).setValue(0);
                        break;
                    case R.id.not_attending:
                        showToast("Not Attending ");
                        Log.d(TAG, "onMenuItemClick: " +clickedEvent.toString());
                        Log.d(TAG, "onMenuItemClick: eventID :: "  +eventID);
                        try{
                            myRef.child("attending").child(mUserId).child(eventID).removeValue();
                        }
                        catch (NullPointerException e){
                            Log.d(TAG, "onMenuItemClick: exception :: " +e.toString());
                        }
                }
                return false;
            }
        });
        popupMenu.show();
    }



}
