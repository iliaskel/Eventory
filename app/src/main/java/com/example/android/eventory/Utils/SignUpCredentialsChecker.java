package com.example.android.eventory.Utils;

/**
 * Created by ikelasid on 10/18/2017.
 */

public class SignUpCredentialsChecker {


    public static boolean isUsernameValid(String username) {
        return username.length()>=6;
    }

    public static boolean isPasswordValid(String password) {
        boolean hasLetter=false;
        boolean hasDigit = false;
        if (password.length() >= 8) {
            for (int i = 0; i < password.length(); i++) {
                char x = password.charAt(i);
                if (Character.isLetter(x)) {

                    hasLetter = true;
                }

                else if (Character.isDigit(x)) {

                    hasDigit = true;
                }

                // no need to check further, break the loop
                if(hasLetter && hasDigit){

                    break;
                }

            }
            if (hasLetter && hasDigit) {
                return true;
            } else {
                return false;
            }
        } else {
            System.out.println("HAVE AT LEAST 8 CHARACTERS");
        }
        return false;
    }
}
