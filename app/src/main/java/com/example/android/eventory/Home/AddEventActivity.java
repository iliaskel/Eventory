package com.example.android.eventory.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.eventory.R;
import com.example.android.eventory.Signing.SignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by ikelasid on 10/21/2017.
 */

public class AddEventActivity extends AppCompatActivity {

    private static final String TAG = "AddEventActivity";

    private EditText mNewEventName;
    private EditText mNewEventType;
    private EditText mNewEventYear;
    private EditText mNewEventMonth;
    private EditText mNewEventDay;
    private Button mCancelButton;
    private Button mAddEventBtn;


    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private FirebaseUser mCurrentUser;
    private String mUserId;


    //vars
    private String mAddress;
    private String mPlaceName;
    private String mLatitude;
    private String mLongitude;
    private String mEventsName;
    private String mEventsType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        findViews();


        // Setting up FireBase authentication/database
        // Getting mCurrentUser && mUserId
        setUpFireBase();

        // Pulling Place info stored in FireBase Database
        getUserPlacesInfo();

        setListeners();

    }

    private void setUpFireBase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef=mDatabase.getReference();
        mCurrentUser =mAuth.getCurrentUser();
        try{
            mUserId= mCurrentUser.getUid();
        }
        catch (NullPointerException e){
            showToast("You are not logged in. Please log-in");
            Intent signInIntent=new Intent(this, SignInActivity.class);
            startActivity(signInIntent);
            finish();
        }
    }

    private void findViews() {
        mNewEventName =(EditText) findViewById(R.id.et_new_event_name);
        mNewEventType =(EditText) findViewById(R.id.et_new_event_type);
        mAddEventBtn=(Button)findViewById(R.id.new_event_add_btn);
        mCancelButton=(Button)findViewById(R.id.new_event_cancel_btn);
        mNewEventYear=(EditText)findViewById(R.id.et_new_event_year);
        mNewEventMonth=(EditText)findViewById(R.id.et_new_event_month);
        mNewEventDay=(EditText)findViewById(R.id.et_new_event_day);

    }

    private void getUserPlacesInfo() {

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAddress=dataSnapshot.child("places").child(mUserId).child("address").getValue().toString();
                mPlaceName=dataSnapshot.child("places").child(mUserId).child("name").getValue().toString();
                mLatitude=dataSnapshot.child("places").child(mUserId).child("latitude").getValue().toString();
                mLongitude=dataSnapshot.child("places").child(mUserId).child("longitude").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setListeners() {
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Action canceled");
                finish();
            }
        });

        mAddEventBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: add clicked");
                String eventName=mNewEventName.getText().toString();
                Log.d(TAG, "onClick: name "+eventName);
                String eventType=mNewEventType.getText().toString();
                String eventYear=mNewEventYear.getText().toString();
                String eventMonth=mNewEventMonth.getText().toString();
                String eventDay=mNewEventDay.getText().toString();
                if(eventName.equals("") || eventType.equals("")
                        || eventYear.equals("")
                        || eventMonth.equals("")
                        || eventDay.equals("")){
                    showToast("Please fill all fields");
                }
                else{
                    StringBuilder date=new StringBuilder();
                    date.append(eventYear+"-");
                    date.append(eventMonth+"-");
                    date.append(eventDay);

                    StringBuilder latLng=new StringBuilder();
                    latLng.append(mLatitude+" ");
                    latLng.append(mLongitude);
                    String latLngString=latLng.toString().replace(".",",");
                    Log.d(TAG, "latLng" + latLngString);

                    String key=myRef.child("events").push().getKey();

                    EventInformation event=new EventInformation(eventName,mPlaceName,eventType,date.toString(),latLngString);
                    Log.d(TAG, "onClick: EVENT "+event.toString());
                    myRef.child("events").child(key).setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            showToast("successfully entered the event");
                            finish();
                        }
                    });
                }
            }
        });

    }








    private void showToast(String s){
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }


}
