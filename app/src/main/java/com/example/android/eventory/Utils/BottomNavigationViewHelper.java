package com.example.android.eventory.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.example.android.eventory.Activities.EventsActivity;
import com.example.android.eventory.Activities.MapActivity;
import com.example.android.eventory.R;
import com.example.android.eventory.Activities.UserActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Created by ikelasid on 10/1/2017.
 */

public class BottomNavigationViewHelper {
    private static final String TAG = "BottomNavigationViewHel";

    public static void setUpBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "setUpBottomNavigationView");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.setIconsMarginTop(60);
        //bottomNavigationViewEx.enableItemShiftingMode(false);
        //bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "enableNavigation: entered");
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_home:
                        Intent homeIntent= new Intent(context, EventsActivity.class);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(homeIntent);
                        break;
                    case R.id.ic_map:
                        Intent mapIntent=new Intent(context, MapActivity.class);
                        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(mapIntent);
                        break;
                    case R.id.ic_user:
                        Intent userIntent=new Intent(context, UserActivity.class);
                        userIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(userIntent);
                        break;
                }

                return false;
            }
        });
    }
}
