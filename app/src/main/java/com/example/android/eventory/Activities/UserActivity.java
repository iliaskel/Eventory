package com.example.android.eventory.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.eventory.AttendingEventsAdapter;
import com.example.android.eventory.R;
import com.example.android.eventory.Signingformation.EventInformation;
import com.example.android.eventory.Signingformation.UserInformation;
import com.example.android.eventory.Utils.BottomNavigationViewHelper;
import com.google.android.gms.location.SettingsClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.Duration;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ikelasid on 10/1/2017.
 * This Activity displays the user section, displaying the name, his personal photo
 * and the events he is attending
 */

public class UserActivity extends AppCompatActivity {
    private static final String TAG = "UserActivity";
    private static final int ACTIVITY_NUMBER=2;
    private final Context mContext = UserActivity.this;
    private final int PICK_IMAGE_REQUEST = 71;





    private TextView usernameTextView;
    private CircleImageView profileImageView;
    private RecyclerView attendindEventsRecyclerView;
    private AttendingEventsAdapter attendingEventsAdapter;
    private static ArrayList<EventInformation> attendingEventsList = new ArrayList<>();


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private FirebaseUser currentUser;
    private String userID;
    private String mUserId;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: entered");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        setUpBottomNavigationView();

        findViewsById();

        setUpFireBase();

        //Pop up menu for profile image functions
        setUpPopUpMenu();

        setProfilePicture();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.settings_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setProfilePicture() {
        Bitmap profileImage=null;

        try {
            File filePath = getFileStreamPath(mUserId);
            Log.d(TAG, "setProfilePicture: onActivity FILEPATH :::: " +filePath.getAbsolutePath());
            FileInputStream fi = new FileInputStream(filePath);
            profileImage = BitmapFactory.decodeStream(fi);
            profileImageView.setImageBitmap(profileImage);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "setProfilePicture: exception catched :::: " + e.getMessage());
            e.printStackTrace();
        }


    }

    private void setUpPopUpMenu() {
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: profile Image pressed");
                PopupMenu popupMenu = new PopupMenu(mContext,profileImageView);

                popupMenu.getMenuInflater().inflate(R.menu.profile_image_pop_up_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if(item.getItemId() == R.id.menu_image_upload){
                            chooseProfilePicture();
                        }
                        else if(item.getItemId() == R.id.menu_image_delete){

                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void chooseProfilePicture() {


        Intent choosePictureIntent = new Intent();
        choosePictureIntent.setType("image/*");
        choosePictureIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(choosePictureIntent, "Select Profile Picture"), PICK_IMAGE_REQUEST);

    }

    /** Changing the profile picture and store it afterwards asynchronously **/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {

            Uri imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
            new StoreProfileImage().execute(imageUri);

        }
    }


    /** TODO: We could store the username once he logs in and after that chech only the userId
     * to decide whether to keep the same username(ie its the same user) or to query the logged in
     *  users user name from FireBase**/
    private void setUpFireBase() {
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        mUserId = mCurrentUser.getUid();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in: " + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out ");
                }
            }
        };

        mCurrentUser =mAuth.getCurrentUser();
        String userId = mCurrentUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef=database.getReference();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " +dataSnapshot.getValue());
                setUserName(dataSnapshot);

                getAttendingEvents(dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //TODO: BETTER NAME
    private void getAttendingEvents(DataSnapshot dataSnapshot) {

        ArrayList<EventInformation> attendingEventsList = new ArrayList<>();
        boolean isFirstTime=true;
        if (attendingEventsList.size()!=0)
            isFirstTime=false;
        DataSnapshot attendingData = dataSnapshot.child("attending").child(mUserId);
        DataSnapshot eventsAll = dataSnapshot.child("events");
        for (DataSnapshot ds : attendingData.getChildren()) {
            String eventID = ds.getKey();
            Log.d(TAG, "getAttendingEvents: eventID " + eventID);
            EventInformation event = new EventInformation();
            event.setType(eventsAll.child(eventID).getValue(EventInformation.class).getType());
            event.setDate(eventsAll.child(eventID).getValue(EventInformation.class).getDate());
            event.setPlace_name(eventsAll.child(eventID).getValue(EventInformation.class).getPlace_name());
            event.setEvent_name(eventsAll.child(eventID).getValue(EventInformation.class).getEvent_name());
            attendingEventsList.add(event);
            Log.d(TAG, "onDataChange: User Activity\n" + event.toString());

        }
        Log.d(TAG, "onDataChange: att " + attendingEventsList.size());

        if (isFirstTime){
            attendindEventsRecyclerView.setHasFixedSize(true);
            attendindEventsRecyclerView.setLayoutManager(new LinearLayoutManager(UserActivity.this));
            attendingEventsAdapter = new AttendingEventsAdapter(attendingEventsList);
            attendindEventsRecyclerView.setAdapter(attendingEventsAdapter);
        }
        else
        {
            attendingEventsAdapter.notifyDataSetChanged();
        }


    }

    private void setUserName(DataSnapshot dataSnapshot) {
        UserInformation userInformation=new UserInformation();
        userInformation.setName(dataSnapshot.child("users").child(mUserId).getValue(UserInformation.class).getName());
        Log.d(TAG, "onDataChange: "+userInformation.getName());
        usernameTextView.setText(userInformation.getName());

    }

    private class StoreProfileImage extends AsyncTask<Uri,Void,Void>{

        @Override
        protected Void doInBackground(Uri... uris) {

            Uri imageUri = uris[0];
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                FileOutputStream fos = openFileOutput(mUserId,Context.MODE_PRIVATE);
                selectedImage.compress(Bitmap.CompressFormat.PNG,10,fos);
                Log.d(TAG, "onActivityResult: SAVED");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            showToast("Image successfully saved");
        }
    }

    private void findViewsById(){
        usernameTextView=findViewById(R.id.tv_user_user_name);
        profileImageView =findViewById(R.id.profile_image);
        ImageView settingsImageView = findViewById(R.id.setting_image_view);
        settingsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("clicked");
                Intent settingsIntent = new Intent(UserActivity.this,SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
        attendindEventsRecyclerView = findViewById(R.id.user_activity_rv_attending);

    }

    private void setUpBottomNavigationView(){
        Log.d(TAG, "setUpBottomNavigationView: setting up bottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx=(BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setUpBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(UserActivity.this,bottomNavigationViewEx);
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getAttendingEvents(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void showToast(String text){
        Toast.makeText(UserActivity.this,text,Toast.LENGTH_SHORT).show();
    }

}
