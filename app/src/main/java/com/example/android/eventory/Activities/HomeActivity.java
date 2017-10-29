package com.example.android.eventory.Activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.example.android.eventory.SigningInformation.EventInformation;
import com.example.android.eventory.HomeRecyclerView.HomeAdapter;
import com.example.android.eventory.R;
import com.example.android.eventory.Utils.DistanceMeasure;
import com.example.android.eventory.Utils.BottomNavigationViewHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private final Context mContex=HomeActivity.this;

    //vars
    private static final int ACTIVITY_NUMBER=0; //Used for displaying the bottom navigation's view proper checked button
    protected boolean mIsOwner =false;            //Used for defining if the user is owner of a place and setting up the fab
    private boolean isFirstTime=true;           //Used for defining is it's the first time displaying the event's list
    protected static ArrayList<EventInformation> mEventsList=new ArrayList<>();
    protected Location mLastKnownLocation;
    private HomeAdapter adapter=new HomeAdapter(mEventsList);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "entered onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // Setting up FireBase authentication/database
        // Getting mCurrentUser && mUserId
        setUpFireBase();
        findViews();

        //Setting up Events recycler view && defining if user==owner||user && setting up fab's visibility
        initRecyclerView();

        // Setting up **Custom Bottom Navigation view
        setUpBottomNavigationView();

    }

    /**
     * =====================================================================
     */

    private void setUpFireBase() {
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
        mAddEvent=(FloatingActionButton)findViewById(R.id.fab_add_event);
        mEventsRecyclerView=(RecyclerView)findViewById(R.id.rv_events);
        mEventsRecyclerView.setHasFixedSize(true);
    }

    private void initRecyclerView() {

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mEventsRecyclerView.setLayoutManager(layoutManager);

        // Called every time the database is changed && the first time activity is called
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) throws NullPointerException {

                //clearing the events list so events won't be duplicated
                mEventsList.clear();
               for(DataSnapshot ds:dataSnapshot.child("events").getChildren()){                     //getting all the events

                   EventInformation event=new EventInformation();
                   event.setLatitude(ds.getValue(EventInformation.class).getLatitude());
                   event.setLongitude(ds.getValue(EventInformation.class).getLongitude());

                   Location eventsLocation=new Location("");
                   eventsLocation.setLatitude(event.getLatitude());
                   eventsLocation.setLongitude(event.getLongitude());


                   if(DistanceMeasure.isNear(eventsLocation)){                                      //IF event is near **should fix the user's preferences for manual accepted distance
                       event.setEvent_name(ds.getValue(EventInformation.class).getEvent_name());
                       event.setPlace_name(ds.getValue(EventInformation.class).getPlace_name());
                       event.setDate(ds.getValue(EventInformation.class).getDate());
                       event.setType(ds.getValue(EventInformation.class).getType());

                       mEventsList.add(event);
                       Log.d(TAG, "onDataChange: " +mEventsList.size());
                   }
               }
                if(isFirstTime){
                    mIsOwner =isOwner(dataSnapshot);
                    setUpFab();
                    Log.d(TAG, "onDataChange: isOwner " +mIsOwner);
                    mEventsRecyclerView.setAdapter(adapter);
                    return;
                }
                else{
                    adapter.notifyDataSetChanged();
                }



            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean isOwner(DataSnapshot dataSnapshot) {
        String userType=dataSnapshot.child("users").child(mUserId).getValue(UserInformation.class).getType();
        if (userType.equals("owner"))
            return true;
        return false;
    }

    private void setUpFab() {
        if(mIsOwner) {
            Log.d(TAG, "setUpFab: IS OWNER");
            mAddEvent.setVisibility(View.VISIBLE);
            mAddEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: clicked");
                    Intent addEventIntent=new Intent(HomeActivity.this,AddEventActivity.class);
                    startActivity(addEventIntent);
            }
            });
        }
        else{
            Log.d(TAG, "setUpFab: IS NOT OWNER");
            mAddEvent.setVisibility(View.INVISIBLE);
        }
    }



    /**
     * ====================================================================
     */



    private void showToast(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    private void setUpBottomNavigationView(){
        Log.d(TAG, "setUpBottomNavigationView: setting up bottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx=(BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setUpBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(HomeActivity.this,bottomNavigationViewEx);
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }


    /**
     * ======== OVERRIDING METHODS=======================
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


}