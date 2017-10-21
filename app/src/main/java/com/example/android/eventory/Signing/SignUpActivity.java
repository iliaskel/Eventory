package com.example.android.eventory.Signing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.android.eventory.R;
import com.example.android.eventory.Utils.SignUpCredentialsChecker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ikelasid on 10/8/2017.
 */

public class SignUpActivity extends AppCompatActivity {


    private static final String TAG = "SignUpActivity";
    private Context mContext=SignUpActivity.this;
    
    private EditText mUserName,mUserEmail, mUserPassword,mPlaceName,mPlaceAddress,mPlacePhone;
    private RadioGroup mUserTypeGroup;
    private RelativeLayout mRlUserType;
    private Button mSignUpButton;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        findViews();
        setUpFireBase();
        setListeners();



    }



    private void setUpFireBase() {
        mAuth = FirebaseAuth.getInstance();
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
    }

    private void createNewPlace() {
        final String email=mUserEmail.getText().toString();
        String password= mUserPassword.getText().toString();
        final String username=mUserName.getText().toString();
        final String placeName=mPlaceName.getText().toString();
        final String placeAddress=mPlaceAddress.getText().toString();


        if(!email.equals("") && !password.equals("") && !username.equals("")
                && !placeName.equals("") && !placeAddress.equals("")){
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    showToast("Successfully created account: " + email);
                    insertUserInformation(email,username);
                    insertPlaceInformation(placeName,placeAddress,"40.00","20.99");
                    mAuth.signOut();
                    Intent signIn=new Intent(mContext,SignInActivity.class);
                    startActivity(signIn);

                }
            });

        }
        else{
            showToast("Please fill all the fields.");
        }
    }
    private void insertPlaceInformation(String placeName, String placeAddress, String latitude, String longitude) {
        String userId=mAuth.getCurrentUser().getUid();
        PlaceInformation placeInfo=new PlaceInformation(placeName,placeAddress,latitude,longitude);
        myRef.child("places").child(userId).setValue(placeInfo);
    }


    private void createNewUser() {

        final String email = mUserEmail.getText().toString();
        String password = mUserPassword.getText().toString();
        final String username = mUserName.getText().toString();


        if (!email.equals("") && !password.equals("") && !username.equals("")) {
            if (SignUpCredentialsChecker.isUsernameValid(username)) {
                if (SignUpCredentialsChecker.isPasswordValid(password)) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                showToast("Successfully created account: " + email);
                                insertUserInformation(email, username);
                                mAuth.signOut();
                                Intent signIn = new Intent(mContext, SignInActivity.class);
                                startActivity(signIn);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                showToast("Badly formatted email address");
                            }
                        }
                    });
                }
                else
                    {
                    showToast("password is weak");
                }
            }
            else
                {
                showToast("username length must be over 6 characters.");
            }
        }
        else
            {
            showToast("Please fill all the fields.");
        }

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

    private void findViews() {
        mUserName=(EditText)findViewById(R.id.et_sign_up_name);
        mUserPassword =(EditText)findViewById(R.id.et_sign_up_password);
        mUserEmail=(EditText)findViewById(R.id.et_sign_up_email);
        mPlaceName=(EditText)findViewById(R.id.et_sign_up_place_name);
        mPlaceAddress=(EditText)findViewById(R.id.et_sign_up_place_address);
        mUserTypeGroup=(RadioGroup)findViewById(R.id.rg_sign_up_user_type);
        mRlUserType =(RelativeLayout)findViewById(R.id.rl_place_information);
        mSignUpButton=(Button)findViewById(R.id.btn_sign_up);
        mRlUserType.setVisibility(View.INVISIBLE);
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

    private void showToast(String s) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }
}

