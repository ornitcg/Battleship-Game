package com.clientapp.battleshipclient.networking;

import com.clientapp.battleshipclient.logic.Tile;
import com.clientapp.battleshipclient.logic.User;

import java.util.ArrayList;

public class UserNW {
    private String signInEndpoint = "http://localhost:8080/signIn";
    private String signUpEndpoint = "http://localhost:8080/signUp";
    private String getGameIdEndpoint = "http://localhost:8080/getGameBoards/{gameId}"; // where i get a gameId from the server
    private String postCreateBoardEndpoint = "http://localhost:8080/createBoard"; // where i send a board to the server, when user clicks on "I'm ready" button
//    private String postAttackPositionEndpoint = "http://localhost:8080/attackPosition"; // where i send an attack to the server
//    private String getTopScoresEndpoint = "http://localhost:8080/getTopScores"; // where i get the top scores from the server

    // gets users list from server
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

    public void getGameId(User user) {
        // connect to server and  send user name and password that were approved
        // analyse server response for gameId
    }




}
