package com.clientapp.battleshipclient.logic;

import com.clientapp.battleshipclient.networking.UserNW;

public class User {

    private String username;
    private String password;

    public User(String username, String password ) { // create a new user
        this.username = username;
        this.password = password;
    }



    public enum UserState {
        SIGNUP, SIGNIN, SIGNUP_SUCCEDED, SIGNIN_SUCCEDED, VALID,
        USER_EXISTS, SIGNIN_FAILED ,USER_NAME_EXISTS, INVALID_USERNAME, INVALID_PASSWORD
    }

    public UserState signUp(){
        UserState userStatus = isValidUser(this);  // ********** need new name for isValidUser ************
        if (userStatus == UserState.VALID) {
            UserNW userNW = new UserNW();
            userNW.addUser(this);  // requires check if successful
            return UserState.SIGNUP_SUCCEDED;
        }
        return userStatus;
    }

    public UserState signIn() {
        UserNW  userNW = new UserNW();
        if (userNW.getUsers().contains(this))
            return UserState.SIGNIN_SUCCEDED;
        return UserState.SIGNIN_FAILED;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }




    private UserState isValidUser(User user) {
        UserNW userNW = new UserNW();

        if (username.length() ==0 || username.length() >10)
            return UserState.INVALID_USERNAME;
        if (password.length() ==0 || password.length() >10)
            return UserState.INVALID_PASSWORD;
        if (userNW.getUsers().contains(user))
            return UserState.USER_EXISTS;
        if (isNameUsed(userNW)) {
            return UserState.USER_NAME_EXISTS;
        }
        return UserState.VALID;
    }


    private boolean isNameUsed(UserNW userNW) {
        for (User u : userNW.getUsers())
             if (u.getUsername().equals(username))
                 return true;
        return false;
    }






}
