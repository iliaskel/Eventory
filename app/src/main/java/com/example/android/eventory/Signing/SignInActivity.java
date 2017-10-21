package com.example.android.eventory.Signing;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.eventory.Data.EventsDbHelper;
import com.example.android.eventory.Home.HomeActivity;
import com.example.android.eventory.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by ikelasid on 10/6/2017.
 */

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";

    //TODO: ADD NETWORK NOT AVAILABLE EXCEPTION
    //TODO: ADD PROGRESS BAR

    TextView mInputMail,mInputPassword,mSignUp;
    Button mLoginButton;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        findViews();
        setUpFireBase();
        setUpSignInListener();


    }

    private void setUpSignInListener() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email= mInputMail.getText().toString();
                String pswd= mInputPassword.getText().toString();
                if(!email.equals("") && !pswd.equals("")){
                    mAuth.signInWithEmailAndPassword(email,pswd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(mAuth.getCurrentUser()==null){
                                showToast("Email or password wrong.Try again!");
                            }
                            else{
                                showToast("Successfuly signed in: "+email);
                                Intent homeIntent=new Intent(SignInActivity.this,HomeActivity.class);
                                startActivity(homeIntent);
                                finish();
                            }
                        }
                    });

                }
                else
                {
                    showToast("You didn't fill all the fields.");
                }
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent=new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

    }

    private void setUpFireBase() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Intent homeIntent=new Intent(SignInActivity.this,HomeActivity.class);
                    startActivity(homeIntent);
                    finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                }
                // ...
            }
        };
    }

    private void findViews() {
        mInputMail=(TextView)findViewById(R.id.et_input_email);
        mInputPassword=(TextView)findViewById(R.id.et_input_password);
        mLoginButton=(Button)findViewById(R.id.btn_login);
        mSignUp=(TextView)findViewById(R.id.link_signup);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showToast(String s) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }
}
