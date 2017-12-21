package com.example.android.eventory.SigningActivities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.android.eventory.Signingformation.UserInformation;
import com.example.android.eventory.R;
import com.example.android.eventory.Signingformation.PlaceInformation;
import com.example.android.eventory.Utils.PlaceAutocompleteAdapter;
import com.example.android.eventory.Utils.SignUpCredentialsChecker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ikelasid on 10/8/2017.
 */

public class SignUpActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = "SignUpActivity";
    private Context mContext=SignUpActivity.this;

    private AutoCompleteTextView mPlaceAddress;
    private EditText mUserName,mUserEmail, mUserPassword,mPlaceName,mPasswordConfirm;
    private RadioGroup mUserTypeGroup;
    private RelativeLayout mRlUserType;
    private Button mSignUpButton;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds LAT_LNG_BOUND=new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        findViews();
        setUpFireBase();
        setListeners();
        setUpAddressAutocomplete();



    }


    /**
     * ================= Sign up Methods =============================
     */

        private void createNewPlace() {
        final String email=mUserEmail.getText().toString();
        String password= mUserPassword.getText().toString();
        String passwordConfirm=mPasswordConfirm.getText().toString();
        final String username=mUserName.getText().toString();
        final String placeName=mPlaceName.getText().toString();
        final String placeAddress=mPlaceAddress.getText().toString();


        if(email.equals("") || password.equals("") || username.equals("")
                || placeName.equals("") || placeAddress.equals("") || passwordConfirm.equals("")) {
            showToast("Please fill all the fields");
            return;
        }
        if (!password.equals(passwordConfirm)) {
            showToast("Password don't match");
            return;
        }
        if (!SignUpCredentialsChecker.isUsernameValid(username)) {
            showToast("Username is now valid");
            return;
        }
        if (!SignUpCredentialsChecker.isPasswordValid(password)) {
            showToast("Password is weak");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    double latitudeDouble;
                    double longitudeDouble;
                    String addressString = mPlaceAddress.getText().toString();
                    Geocoder geocoder = new Geocoder(SignUpActivity.this);
                    List<Address> addressList = new ArrayList<>();


                    try {
                        addressList = geocoder.getFromLocationName(addressString, 1);
                        Log.d(TAG, "onComplete: address list == " + addressList);
                    } catch (IOException e) {
                        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    showToast("An error occurred getting the address.");
                                    finish();
                                }
                            }
                        });
                        Log.d(TAG, "onComplete: IOException " + e.getMessage());
                    }

                    try {
                        Log.d(TAG, "onComplete: list size == " + String.valueOf(addressList.size()));
                        if (addressList.size() > 0) {

                            Address address = addressList.get(0);
                            latitudeDouble = address.getLatitude();
                            Log.d(TAG, "onComplete: latitude===" + String.valueOf(latitudeDouble));
                            longitudeDouble = address.getLongitude();
                            Log.d(TAG, "onComplete: longitude===" + String.valueOf(longitudeDouble));
                            insertUserInformation(email, username);
                            insertPlaceInformation(placeName, addressString, latitudeDouble, longitudeDouble);
                            mAuth.signOut();
                            finish();
                        }
                    } catch (NullPointerException e) {
                        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    showToast("An error occurred getting the address.");
                                    finish();
                                }
                            }
                        });
                        Log.d(TAG, "onComplete: NullPointerException  " + e.getMessage());
                    }
                }
                else{
                    showToast("An error occurred ");
                }
            }
        });
    }
        private void insertPlaceInformation(String placeName, String placeAddress, double latitude, double longitude) {
        String userId=mAuth.getCurrentUser().getUid();
        PlaceInformation placeInfo=new PlaceInformation(placeName,placeAddress,latitude,longitude);
        myRef.child("places").child(userId).setValue(placeInfo);
    }


        private void createNewUser() {

        final String email = mUserEmail.getText().toString();
        String password = mUserPassword.getText().toString();
        final String username = mUserName.getText().toString();
        String passwordConfirm=mPasswordConfirm.getText().toString();

        if(email.equals("") || password.equals("")
                || username.equals("")
                || passwordConfirm.equals("")) {
            showToast("Please fill all the fields");
            return;
        }
        if (!password.equals(passwordConfirm)) {
            showToast("Password don't match");
            return;
        }
        if (!SignUpCredentialsChecker.isUsernameValid(username)) {
            showToast("Username is now valid");
            return;
        }
        if (!SignUpCredentialsChecker.isPasswordValid(password)) {
            showToast("Password is weak");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    showToast("Successfully created account: " + email);
                    insertUserInformation(email, username);
                    mAuth.signOut();
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    showToast("An error occurred");
                    finish();
                }
            }
        });
    }
        private void insertUserInformation(String email,String username){
        try{
            String userId=mAuth.getCurrentUser().getUid();

            if(mUserTypeGroup.getCheckedRadioButtonId()==R.id.rb_sign_up_type_user) {
                UserInformation userInfo=new UserInformation(username,email,"user");
                myRef.child("users").child(userId).setValue(userInfo);
            }
            else{
                UserInformation userInfo=new UserInformation(username,email,"owner");
                myRef.child("users").child(userId).setValue(userInfo);
            }
        }
        catch(NullPointerException e){
            Log.d(TAG, "insertUserInformation: null pointer exception");
        }
    }

    /**
     * ===============================================================
     */


    /**
     * =================== Init Methods ============================
     */
        private void findViews() {
        mUserName=(EditText)findViewById(R.id.et_sign_up_name);
        mUserPassword =(EditText)findViewById(R.id.et_sign_up_password);
        mUserEmail=(EditText)findViewById(R.id.et_sign_up_email);
        mPlaceName=(EditText)findViewById(R.id.et_sign_up_place_name);
        mPlaceAddress=(AutoCompleteTextView) findViewById(R.id.et_sign_up_place_address);
        mUserTypeGroup=(RadioGroup)findViewById(R.id.rg_sign_up_user_type);
        mRlUserType =(RelativeLayout)findViewById(R.id.rl_place_information);
        mSignUpButton=(Button)findViewById(R.id.btn_sign_up);
        mRlUserType.setVisibility(View.INVISIBLE);
        mPasswordConfirm=(EditText)findViewById(R.id.et_sign_up_password_confirm);
    }

        private void setUpAddressAutocomplete() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(mContext)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mPlaceAutocompleteAdapter=new PlaceAutocompleteAdapter(this,mGoogleApiClient,LAT_LNG_BOUND,null);

        mPlaceAddress.setAdapter(mPlaceAutocompleteAdapter);
    }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

        private void setUpFireBase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance();
        myRef=mDatabase.getReference();
        if(mAuth!=null){
            mAuth.signOut();
        }

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

        private void setListeners() {
        mUserTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (mUserTypeGroup.getCheckedRadioButtonId() == R.id.rb_sign_up_type_user) {
                    Log.d(TAG, "onClick: user");
                    mRlUserType.setVisibility(View.INVISIBLE);
                } else {
                    Log.d(TAG, "onClick: owner");
                    mRlUserType.setVisibility(View.VISIBLE);
                }
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUserTypeGroup.getCheckedRadioButtonId()==R.id.rb_sign_up_type_user){
                    createNewUser();
                }
                else{
                    createNewPlace();
                }
            }
        });

        mPlaceAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }
    /**
     * =============================================================
     */


    /**
     * =================== LifeCycle Methods ============================
     */

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

    /**
     *==================================================================
     */

    private void showToast(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }



}

