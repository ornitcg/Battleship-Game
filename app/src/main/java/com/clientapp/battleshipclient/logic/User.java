package com.clientapp.battleshipclient.logic;

import android.util.Log;

import com.clientapp.battleshipclient.networking.UserNW;

public class User {

    private String username;
    private String password;

    public User(String username, String password ) { // create a new user
        this.username = username;
        this.password = password;
    }

    public String getUserID() {
        UserNW userNW = new UserNW();
        userNW.getUserID(this);
        return "MyId";
    }


    public enum UserState {
        SIGNUP_SUCCEDED, SIGNIN_SUCCEDED, VALID,
        USER_EXISTS, SIGNIN_FAILED ,USER_NAME_EXISTS, INVALID_USERNAME, INVALID_PASSWORD
    }

    public UserState signUp(){
        UserState userStatus = isValidUser(this);
        Log.d("User", "signUp: " + userStatus);
        if (userStatus != UserState.VALID)
            return userStatus;
        UserNW userNW = new UserNW();
//        return userNW.signUp(this);
        return UserState.SIGNUP_SUCCEDED;
    }

    public UserState signIn() {
        UserState userStatus = isValidUser(this);
        if (userStatus != UserState.VALID)
            return userStatus;
        UserNW userNW = new UserNW();
//        return userNW.signIn(this);  // send userName and password to server via sign in endpoint
        return UserState.SIGNIN_SUCCEDED;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }




    private UserState isValidUser(User user) {
        UserNW userNW = new UserNW();
        Log.d("User", "isValidUser: " + username );
        if (username.isEmpty() || username.length() >10)
            return UserState.INVALID_USERNAME;
        if (password.isEmpty() || password.length() >10)
            return UserState.INVALID_PASSWORD;
        Log.d("User", "isValidUser: " + User.UserState.VALID);
        return UserState.VALID;
    }









}
