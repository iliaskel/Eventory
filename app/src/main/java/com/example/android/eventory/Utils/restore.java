package com.example.android.eventory.Utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.android.eventory.R;
import com.example.android.eventory.Signing.PlaceInformation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by ikelasid on 10/23/2017.
 */

public class restore extends AppCompatActivity{

    private static final String TAG = "restore";
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        myRef=database.getReference();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.child("places").getChildren()){


                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    if(!ds.getKey().equals("7Vd7jjvKkGVlMVXAghbfJMxjRdj2")){
                        Log.d(TAG, "onDataChange: key==true");
                        Log.d(TAG, "onDataChange: VALUES"+ds.getValue());



                      String key=ds.getKey();
                      // PlaceInformation place=new PlaceInformation();

                      ////place.setAddress(ds.getValue(PlaceInformation.class).getAddress());
                      ////place.setName(ds.getValue(PlaceInformation.class).getName());
                      ////place.setLatitude(ds.getValue(PlaceInformation.class).getLatitude());
                      ////place.setLongitude(ds.getValue(PlaceInformation.class).getLongitude());



                      String latString=ds.child("latitude").getValue().toString();
                      String lngString=ds.child("longitude").getValue().toString();
                      String address=ds.child("address").getValue().toString();
                      String name=ds.child("name").getValue().toString();
                      Log.d(TAG, "onDataChange: "+latString);
                      Log.d(TAG, "onDataChange: "+lngString);


                      double lat=Double.valueOf(latString);
                      double lng=Double.valueOf(lngString);

                      PlaceInformation newPlace=new PlaceInformation(name,address,lat,lng);

                      myRef.child("places").child(key).setValue(newPlace);

                      // Toast.makeText(restore.this,"finished",Toast.LENGTH_LONG).show();
                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
