package com.example.android.eventory.Home;

import android.content.Intent;
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

import com.example.android.eventory.R;
import com.example.android.eventory.Signing.SignInActivity;
import com.example.android.eventory.Signing.UserInformation;
import com.example.android.eventory.Utils.BottomNavigationViewHelper;
import com.example.android.eventory.Utils.StringFixer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity{

    //TODO: ftiaxno torato layout kai meta prepei na valo ena + an einai owner

    //vars
    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUMBER=0;
    private boolean mIsOwner =false;
    private ArrayList<EventInformation> mEventsList=new ArrayList<>();


    private RecyclerView mEventsRecyclerView;
    //fab for adding new events || Shown only to owners
    private FloatingActionButton mAddEvent;


    // FireBase && User vars
    private String mUserId;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseUser mCurrentUser;

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

        //set mAddEvent listener
        //if(mAddEvent.getVisibility()==View.VISIBLE)
         //   addEventListener();

        Log.d(TAG, "addEventListener: settting up listener");

    }

    private void addEventListener() {

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
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mIsOwner =isOwner(dataSnapshot);
                setUpFab();
                Log.d(TAG, "onDataChange: isOwner " +mIsOwner);
                for(DataSnapshot ds:dataSnapshot.child("events").getChildren()){
                    String coordinatesInString=ds.getKey();
                    if(StringFixer.isEventNear(coordinatesInString)){
                        EventInformation event=new EventInformation();
                        event.setEvent_name(ds.getValue(EventInformation.class).getEvent_name());
                        event.setPlace_name(ds.getValue(EventInformation.class).getPlace_name());
                        event.setDate(ds.getValue(EventInformation.class).getDate());
                        event.setType(ds.getValue(EventInformation.class).getType());

                        mEventsList.add(event);
                        Log.d(TAG, "onDataChange: " +mEventsList.size());
                    }
                }
                HomeAdapter adapter=new HomeAdapter(mEventsList);
                mEventsRecyclerView.setAdapter(adapter);
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
        Log.d(TAG, "setUpFab: " +mIsOwner);
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
     * ====================================================================
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.signOut();
        Intent mainIntent=new Intent(HomeActivity.this, SignInActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
