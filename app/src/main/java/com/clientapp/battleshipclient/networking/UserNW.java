package com.clientapp.battleshipclient.networking;

import com.clientapp.battleshipclient.logic.User;

import java.util.ArrayList;

public class UserNW {

    // create a new user
    public ArrayList<User> getUsers(){
        // connect to server
        // return the list of users
        return null;  //for now
    }

    // update new user to the server (signup)
    public User.UserState signUp(User user){  //add user
        // connect to server and request via signup endpoint
        // analyse server response for success/failure
        return User.UserState.SIGNUP_SUCCEDED; //for now
    }

    public User.UserState signIn(User user){ //check user exists
        // connect to server and request via signIn endpoint
        // analyse server response for success/failure
        return User.UserState.SIGNIN_SUCCEDED; //for now
    }

    public void getUserID(User user) {
        // connect to server and  send user name and password that were approved
        // analyse server response for userId
    }
}
