package com.clientapp.battleshipclient.logic;

public class Game {
    private String currentPlayerUsername;
    private String opponentUsername;

    private GameBoard currentPlayerBoard;
    private GameBoard opponentBoard;

    public Game(String currentPlayerUsername, String opponentUsername){
        this.currentPlayerUsername = currentPlayerUsername;
        this.opponentUsername = opponentUsername;
        this.currentPlayerBoard = new GameBoard(currentPlayerUsername);  //create empty board for current player
        this.opponentBoard = new GameBoard(opponentUsername); //create empty board for opponent
    }

    //methods to be written:

    // send arrangement of ships for current player to server
    // send arrangement of ships for opponent to server

    // get player board from server
    // get opoonent board from server

    // update player board in ui
    // update opponent board in ui

    // send player move to server
    // get opponent move from server




}
