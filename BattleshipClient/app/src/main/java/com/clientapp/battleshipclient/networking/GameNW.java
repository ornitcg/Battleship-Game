package com.clientapp.battleshipclient.networking;

import com.clientapp.battleshipclient.logic.GameBoard;
import com.clientapp.battleshipclient.logic.User;

public class GameNW {

    private GameBoard currPlayerBoard;
    private GameBoard opponentBoard;
    private User opponent;
    private User currPlayer;

    public GameNW(User opponent, User currPlayer) {
        this.opponent = opponent;
        this.currPlayer = currPlayer;
    }
    // get the board of the current player from server
    // get the board of the opponent from server
    // send the move to the server
    // get the result of the move from the server

    public void getBoard(User user) {
        // get the board of user from server
    }

    public void sendMove(User user, int position) {
        // send the move to the server
    }

    public void getMoveResult(User user) {
        // get the result of the move from the server
    }


}
