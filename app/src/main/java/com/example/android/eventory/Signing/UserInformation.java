package com.example.android.eventory.Signing;

/**
 * Created by ikelasid on 10/8/2017.
 */

public class UserInformation {
    private String type;
    private String name, email;


    public UserInformation(String name,String email,String type){
        this.email=email;
        this.name=name;
        this.type=type;
    }
    public UserInformation(){};


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
