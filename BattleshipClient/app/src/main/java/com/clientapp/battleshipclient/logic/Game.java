package com.clientapp.battleshipclient.logic;

public class Game {
    private String currentPlayerUsername;
    private String opponentUsername;

    private GameBoard currentPlayerBoard;
    private GameBoard opponentBoard;

    public Game(String currentPlayerUserId, String opponentUserId, GameBoard currentPlayerBoard, GameBoard opponentBoard){
        this.currentPlayerUsername = currentPlayerUserId;
        this.opponentUsername = opponentUserId;
        this.currentPlayerBoard = new GameBoard(currentPlayerUserId);  //create empty board for current player
        this.opponentBoard = new GameBoard(opponentUserId); //create empty board for opponent
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
