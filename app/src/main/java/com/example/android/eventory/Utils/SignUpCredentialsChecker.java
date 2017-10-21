package com.example.android.eventory.Utils;

/**
 * Created by ikelasid on 10/18/2017.
 */

public class SignUpCredentialsChecker {


    public static boolean isUsernameValid(String username) {
        return username.length()>=6;
    }

    public static boolean isPasswordValid(String password) {
        return password.length()>6;
    }
}
