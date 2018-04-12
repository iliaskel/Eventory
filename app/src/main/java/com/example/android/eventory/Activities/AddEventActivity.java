package com.example.android.eventory.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.eventory.R;
import com.example.android.eventory.SigningActivities.SignInActivity;
import com.example.android.eventory.Signingformation.EventInformation;
import com.example.android.eventory.Signingformation.PlaceInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

/**
 * Created by ikelasid on 10/21/2017.
 * This Activity's purpose is to add new events to our existing events database.
 * It uses the EventInformation class-object to store the event's info to the Google Firebase RealTime Database
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
    private LinearLayout mLlDate;


    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private FirebaseUser mCurrentUser;
    private String mUserId;


    //vars
    private String mAddress;
    private String mPlaceName;

    private double mLatitudeDouble;
    private double mLongitudeDouble;
    //delete lat/lng String after complete
    private String mLatitude;
    private String mLongitude;
    private String mEventsName;
    private String mEventsType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        findViewsById();

        // Setting up FireBase authentication/database
        // Getting mCurrentUser && mUserId
        setUpFireBase();

        // Pulling Place info stored in FireBase Database
        getUserPlacesInfo();

        // CANCEL && ADD event listeners
        setAddingEventsListeners();

        // Date picker listeners
        setCalendarListeners();



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

        private void findViewsById() {
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
                mAddress=dataSnapshot.child("places").child(mUserId).getValue(PlaceInformation.class).getAddress();
                mPlaceName=dataSnapshot.child("places").child(mUserId).getValue(PlaceInformation.class).getName();
                mLatitudeDouble=dataSnapshot.child("places").child(mUserId).getValue(PlaceInformation.class).getLatitudeDouble();
                mLongitudeDouble=dataSnapshot.child("places").child(mUserId).getValue(PlaceInformation.class).getLongitudeDouble();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }






    /**
     * ======== CALENDAR AND ITS  LISTENERS===========
     */

        private void setAddingEventsListeners() {




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

                    String key=myRef.child("events").push().getKey();

                    EventInformation event=new EventInformation(eventName,mPlaceName,eventType,date.toString(),mLatitudeDouble,mLongitudeDouble);
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

        private void setCalendarListeners() {
        /**
         * Adding both onFocus && onClick listeners cuz if you click on an edit text that
         * has already focus it won't pop up the DatePicker
         */

        mNewEventYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar();
            }
        });

        mNewEventYear.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(mNewEventYear.isFocused()) {
                    showCalendar();
                }
            }
        });

        mNewEventMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar();
            }
        });
        mNewEventMonth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(mNewEventMonth.isFocused()) {
                    showCalendar();
                }
            }
        });

        mNewEventDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar();
            }
        });


        mNewEventDay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(mNewEventDay.isFocused()) {

                    showCalendar();
                }
            }
        });

    }

        private void showCalendar() {
        final Calendar myCalendar = Calendar.getInstance();



        DatePickerDialog.OnDateSetListener dateListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(year,(month+1),dayOfMonth);
            }
        };
        DatePickerDialog dialog=new DatePickerDialog(AddEventActivity.this,
                dateListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

        private void updateLabel(int year, int month, int dayOfMonth) {
        mNewEventYear.setText(String.valueOf(year));
        mNewEventDay.setText(String.valueOf(dayOfMonth));
        mNewEventMonth.setText(String.valueOf(month));
    }


    /**
     * ==============================
     */

        private void showToast(String s){
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }


}
