package com.clientapp.battleshipclient.logic;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.clientapp.battleshipclient.data.GameBoard;
import com.clientapp.battleshipclient.data.Ship.Ship;
import com.clientapp.battleshipclient.data.Tile.Tile;
import com.clientapp.battleshipclient.data.Tile.TileStateEnum;
import com.clientapp.battleshipclient.networking.GameActionNW;
import com.clientapp.battleshipclient.networking.GameLifecycleNW;
import com.clientapp.battleshipclient.networking.ICallbacks;
import com.clientapp.battleshipclient.networking.Netcom;
import com.clientapp.battleshipclient.utils.AudioEnum;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.view.activities.GameActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lombok.Getter;
import lombok.NonNull;

public class GameLogic {
    private static final String CONNECTION_PROBLEM = "CONNECTION PROBLEM";
    private static final String OPPONENT_TURN_MESSAGE = "OPPONENT'S TURN";
    private static final String PAUSE_MESSAGE = "GAME PAUSED";
    private static final int GET_GAME_DELAY_MILLIS = 500;
    public static final int RANDOM_ATTACK_DELAY_MILLIS = 15000;
    private static final int ATTACK_MSG_MILLIS = 2000;
    private final int KEEP_ALIVE_DELAY_MILLIS = 5000;

    private Context context;
    private GameBoard currPlayerGameBoard = null;
    public static Runnable keepAliveTask;
    public static Runnable getGameRepeatTask;
    public static Handler gameRunnablesHandler = new Handler();
    private boolean isTurnChanged = false;
    private boolean isFirstKeepAliveCalled = false;
    public boolean isGameFinished = false;
    private Runnable randomAttackTask;
    @Getter
    private int attacksCounter = 0;


    /**
     * Constructor
     * Being called from the GameActivity when there are gameBoards ready for play
     */
    public GameLogic(Context context, GameBoard currPlayerGameBoard) {
        this.context = context;
        this.currPlayerGameBoard = currPlayerGameBoard;
        setRandomAttackRunnable();

    }


    /*
     *  This method sends the gameboard to the server by calling the network method createBoard
     *  on success it goes to the gameActivity
     * */


    /*
     *  This method is called when the player's turn is timeouted
     *  It sends a random attack on the opponent's board    *
     * */
    private void setRandomAttackRunnable() {
        randomAttackTask = new Runnable() {
            @Override
            public void run() {
                randomAttack();
            }
        };
    }





    /**
     * This method is called repeatedly to get the game state
     */
    public void getGame(String gameId, String currPlayerUserId) {
        GameActionNW.getGame(context, gameId, new ICallbacks<String>() { //call network getGame
            @Override
            public void onResponseSuccess(String response) {
                Log.d("myDEBUG GameLogic", "getGame onResponse: " + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String value = jsonResponse.getString("value");
                    if (value != null) {
                        String turnUserId = new JSONObject(value).getString("turnUserId");
                        String gameState = new JSONObject(value).getString("gameState");
                        String winnerUserId = new JSONObject(value).getString("winnerUserId");
                        handleGameStateFromResponse(currPlayerUserId, gameId, turnUserId, gameState, winnerUserId);
                    }
                } catch (JSONException e) {
                    Log.e("myDEBUG GameLogic", "extractGameStateFromResponse onError from server on getGame: " + e);
                }
            }//end of onResponse

            @Override
            public void onError(Exception e) {
                Log.e("myDEBUG GameLogic", "getGame onError from server  : " + e);
            }
        });
    }


    /*
    *  Handles the case when the game is ended by the server
    * It shows a message to the user and disables the board for attacks.
    * */
    public static void handleGameEndedByServer(Context context) {
        ((GameActivity) context).disableGameboard();
        ((GameActivity) context).displayFinalMessage(CONNECTION_PROBLEM, false);
    }


    /*
     *  Calls methods according to the game state
     */
    private void handleGameStateFromResponse(String currPlayerId, String gameId, String turnUserId, String gameState, String winnerUserId) {
        Log.d("myDEBUG GameLogic", "handleGameStateResponse: turnUserId: " + turnUserId + " gameState: " + gameState + " winnerUserId: " + winnerUserId);
        switch (gameState) {
            case "inProgress":
                handleGameInProgress(currPlayerId, turnUserId, gameId);
                break;
            case "finished":
                handleGameOver(winnerUserId, currPlayerId, gameState);
                break;
            case "ended":
                handleGameEndedByOpponent(currPlayerId, gameState);
                break;
            case "paused":
                handleGamePaused();
                break;
            default:
                break;
        }
    }


    /*
     * This method is called when the game state is "ended" and not "finished"
     *  It shuts down the periodic updates of player being in game
     *  and shows the final message
     * */
    private void handleGameEndedByOpponent(String currPlayerUserId, String gameState) {
//        getGameStateRepeatHandler.removeCallbacks(getGameStateRepeatTask);
        gameRunnablesHandler.removeCallbacks(keepAliveTask);
        Log.d("myDEBUG GameLogic", "handleGameEnded: game is ended");
        AudioUtils.makeSound(context, AudioEnum.GAME_OVER);
        Log.d("myDEBUG GameLogic", "handleGameEnded: made sound for game over");
        ((GameActivity) context).disableGameboard();
        ((GameActivity) context).displayFinalMessage(gameState, false);
    }


    /**
     * This method is called when the game state is "inProgress"
     * It sets the board for attack or wait depending on the turn
     * and starts the periodic updates of the game state
     */
    private void handleGameInProgress(String currPlayerId, String turnUserId, String gameId) {
        Log.d("myDEBUG GameLogic", "handleGameInProgress onResponse: currPlayerUserId: " + currPlayerId + " turnUserId: " + turnUserId);
        Log.d("myDEBUG GameLogic", "BEING ATTACKED");

        if (turnUserId.equals(currPlayerId)) { // My turn
            if (isTurnChanged) {
                getCurrentPlayerBoard(); // another single call to not miss the last 'miss' attack
                isTurnChanged = false;
            }
            Log.d("myDEBUG GameLogic", "ATTACKING");
            ((GameActivity) context).enableBoardForAttack();
            gameRunnablesHandler.post(keepAliveTask); //starts the periodic updates
            gameRunnablesHandler.removeCallbacks(getGameRepeatTask);
            gameRunnablesHandler.postDelayed(randomAttackTask, RANDOM_ATTACK_DELAY_MILLIS);

        } else {
            isTurnChanged = true;
            // Opponent turn
            gameRunnablesHandler.removeCallbacks(keepAliveTask);//stop the periodic updates
            Log.d("myDEBUG GameLogic", "BEING ATTACKED");
            stopCountdown();
            ((GameActivity) context).disableBoardForAttack(OPPONENT_TURN_MESSAGE);
            if (!isFirstKeepAliveCalled) {
                keepAlive(currPlayerGameBoard.getGameId());
                isFirstKeepAliveCalled = true;
            }
            getCurrentPlayerBoard();
        }
    }


    /*
     *   Randomizes a position to attack on the opponent's board
     *   called automatically when the player's turn is timeouted
     * */
    private void randomAttack() {
        boolean isAttacked = true;
        int randomPosition = -1;
        while (isAttacked) {
            randomPosition = (int) (Math.random() * 99);
            if (((GameActivity) context).getOpponentGameBoard().getBoard().get(randomPosition).getState() == TileStateEnum.SEA)
                isAttacked = false;
        }
        attackOpponent(currPlayerGameBoard.getGameId(), currPlayerGameBoard.getUser().getId(), randomPosition);
        ((GameActivity) context).getAutoAttackMsgView().setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((GameActivity) context).getAutoAttackMsgView().setVisibility(View.GONE);
            }
        }, ATTACK_MSG_MILLIS);
        Log.d("myDEBUG GameLogic", "randomAttack performed on randomPosition: " + randomPosition);
    }



    /*
    *  Handles the case when the game is paused
    *  It shows a message to the user and disables the board for attack
    *  and starts the periodic updates of the game state
    * */
    private void handleGamePaused() {
        ((GameActivity) context).disableBoardForAttack(PAUSE_MESSAGE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getGame(currPlayerGameBoard.getGameId(), currPlayerGameBoard.getUser().getId());
            }
        }, GET_GAME_DELAY_MILLIS);
    }


    /*
     *  This method is called when the game is finished with a winner
     *  It shuts down the periodic updates of player being in game
     *  plays sound for win or lose
     * and shows the final message
     * */
    private void handleGameOver(String winnerUserId, String currPlayerUserId, String gameState) {
        isGameFinished = true;
        boolean isCurrentPlayerWinner = winnerUserId.equals(currPlayerUserId);
        gameRunnablesHandler.removeCallbacksAndMessages(null);  //stop all runnables
        stopCountdown();
        getCurrentPlayerBoard(); // another single call to not miss the last 'hit' attack
        AudioUtils.makeSound(context, AudioEnum.GAME_OVER);
        if (isCurrentPlayerWinner) {
            AudioUtils.makeSound(context, AudioEnum.WIN);
        } else {
            AudioUtils.makeSound(context, AudioEnum.LOSE_SOUND);
        }
        ((GameActivity) context).disableGameboard();
        ((GameActivity) context).displayFinalMessage(gameState, isCurrentPlayerWinner);
    }


    /*
     *  Stops the countdown timer
     * */
    private void stopCountdown() {
        ((GameActivity) context).stopCountdown();
    }


    /*
     *  Gets the current player board from the server
     *  updates the board with the attack result
     *  and checks if the ship is sunk
     *  if the ship is sunk it hides the ship from the board
     *  and plays the sound for sunk
     * */
    private void getCurrentPlayerBoard() {
        String boardId = currPlayerGameBoard.getBoardId();
        String currPlayerId = currPlayerGameBoard.getUser().getId();
        String gameId = currPlayerGameBoard.getGameId();
        Log.d("myDEBUG GameLogic", "in waitForCurrentBoard");
        GameActionNW.getBoard(boardId, new ICallbacks<String>() {
            @Override
            public void onResponseSuccess(String response) {
                ArrayList<Tile> board = JsonHelper.extractBoardFromResponse(response);
                int position = findTheAttackedPos(board); //could return -1
                updateCurrentPlayerBoardData(board, gameId);

                if (position != -1)   // only if the opponent has attacked
                    handleBeingAttackedResult(position);

                updateAllShips(board);

                if (!isGameFinished) {
                    gameRunnablesHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getGame(gameId, currPlayerId);
                        }
                    }, GET_GAME_DELAY_MILLIS);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("myDEBUG GameLogic", "waitForCurrentBoard onError from server : " + e);
            }
        });

    }

    private void updateAllShips(@NonNull ArrayList<Tile> board) {
        //loop on my ships
        for (Ship ship : currPlayerGameBoard.getShips()) {
            //loop on ship positions array
            int shipHits = 0;
            int size = ship.getShipPositionsArray().size();
            for (int i = 0; i < size; i++) {
                int position = ship.getShipPositionsArray().get(i);
                if (board.get(position).getState() == TileStateEnum.HIT) {
                    shipHits++;
                }
            }
            ship.setHits(shipHits);
            if (shipHits == size) {
                ship.setSunk(true);
                ((GameActivity) context).hideShip(String.valueOf(ship.getBottomViewId()));
            }

        }
    }


    /*
     *  This method handles the result of the attack on the current player board
     *  called after the board is updated with the attack result
     * */
    private void handleBeingAttackedResult(int position) {
        TileStateEnum state = currPlayerGameBoard.getBoard().get(position).getState();
        AudioEnum audioEnum = AudioEnum.MISS;
        boolean isSunk = false;
        Ship ship = null;
        if (state == TileStateEnum.HIT) {
            Log.d("myDEBUG GameLogic", "handleBeingAttackedResult: HIT");
            audioEnum = AudioEnum.HIT;
            currPlayerGameBoard.updateShipHits(currPlayerGameBoard.getBoard().get(position).getShipId());
            ship = currPlayerGameBoard.getShipById(currPlayerGameBoard.getBoard().get(position).getShipId());
            isSunk = ship.isSunk();
        }
        if (ship != null)
            Log.d("myDEBUG GameLogic", "handleBeingAttackedResult: ship hits: " + ship.getHits());
        if (isSunk) {
            ((GameActivity) context).hideShip(String.valueOf(ship.getBottomViewId()));
            audioEnum = AudioEnum.SUNK;
            AudioUtils.makeSound(context, audioEnum);
        } else {
            AudioUtils.makeSound(context, audioEnum);
        }
    }


    private void updateCurrentPlayerBoardData(ArrayList<Tile> board, String gameId) {
        for (int i = 0; i < board.size(); i++) {
            Tile tile = board.get(i);
            currPlayerGameBoard.getBoard().get(i).setState(tile.getState());
        }
        ((GameActivity) context).updateCurrentPlayerBoardView();
    }


    /*
     *  This method finds the position of the attacked tile
     *  by comparing the server board with the current player board
     *  if the state of the tile is different it returns the position
     *  otherwise it returns -1
     * */
    private int findTheAttackedPos(ArrayList<Tile> serverBoard) {
        ArrayList<Tile> currPlayerBoard = currPlayerGameBoard.getBoard();
        if (serverBoard == null) {
            return -1;
        }
        for (int i = 0; i < serverBoard.size(); i++) {
            if (serverBoard.get(i).getState() != currPlayerBoard.get(i).getState()) {
                return i;
            }
        }
        return -1;
    }


    /*
     *  This method sends the attack on position on the opponent's board to the server
     *  on success it updates the opponent's board
     * */
    public void attackOpponent(String gameId, String currPlayerUserId, int attackPosition) {
        // cancel all requests on queue
        Netcom.getInstance(context).cancelAllRequests();

        gameRunnablesHandler.removeCallbacks(randomAttackTask);
        //log removed callbacks
        Log.d("myDEBUG GameLogic", "attackOpponent: removed randomAttackTask");
        Log.d("myDEBUG GameLogic", "in attackOpponent");
        GameActionNW.postAttack(context, gameId, currPlayerUserId, attackPosition, new ICallbacks<String>() {
            @Override
            public void onResponseSuccess(String response) {
                Log.d("myDEBUG GameLogic", "attack onResponse:  " + response);
                attacksCounter++;
                JSONObject value = null;
                try {
                    value = new JSONObject(response).getJSONObject("value");
                    String attackResult = value.getString("attackResult");
                    String shipType = value.getString("shipType");
                    String orientation = value.getString("orientation");
                    Log.d("myDEBUG GameLogic", "attack onResponse: attackPos:" + attackPosition + " attackResult: " + attackResult + " shipType: " + shipType + " orientation: " + orientation);
//                    retryAttackCounter = 0;
                    handleAttackOpponentResult(attackPosition, attackResult, shipType, orientation, value);
                    getGame(gameId, currPlayerUserId); //SO player knows if it is his turn
                } catch (JSONException e) {
                    Log.e("myDEBUG GameLogic", "attack catch  RuntimeException onResponse: " + e);
//                    retryAttack(attackPosition);
                }//end of catch
            }//end of onResponse

            @Override
            public void onError(Exception e) {
                Log.e("myDEBUG GameLogic", "attack onError from server on postAttack: " + e);
//                retryAttack(attackPosition);
            }
        });
    }


    private void handleAttackOpponentResult(int attackPosition, String attackResult, String
            shipType, String orientation, JSONObject value) {
        Integer shipPosition = null;
        TileStateEnum state = TileStateEnum.fromString(attackResult);
        if (attackResult.equals("sunk")) {
            state = TileStateEnum.HIT; // special case since there is no tile state as SUNK
            try {
                shipPosition = value.getInt("position"); //TODO could be null  - taken care at catch
            } catch (JSONException e) {
                Log.e("myDEBUG GameLogic", "handleAttackResult onResponse catch error: " + e);
            }
        }
        AudioEnum audioEnum = AudioEnum.fromString(attackResult);
        try {
            AudioUtils.makeSound(context, audioEnum);
        } catch (Exception e) {
            Log.e("myDEBUG GameLogic", "handleAttackResult onResponse catch error: " + e);
        }
        ((GameActivity) context).updateOpponentBoard(attackPosition, attackResult, shipType, orientation, shipPosition, state);
    }


    /*
     *  notifies server about opponent leaving the game
     *  the game stated then is set to "ended"
     * */
    public static void notifyGameEnd(Context context, String gameId) {
        gameRunnablesHandler.removeCallbacks(keepAliveTask);
        GameLifecycleNW.notifyGameEnded(context, gameId, new ICallbacks<String>() {
            @Override
            public void onResponseSuccess(String response) {
                Log.d("myDEBUG GameLogic", "notifyGameEnd onResponse: " + response);
                try {
                    String state = new JSONObject(response).getString("value");
                } catch (JSONException e) {
                    Log.e("myDEBUG GameLogic", "notifyGameEnd onResponse catch error: " + e);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("myDEBUG GameLogic", "notifyGameEnd onError from server on notifyGameEnd: " + e);
            }
        });
    }


    /*
     * This method is called on each game turn to keep the game alive
     * It sends a request to the server to keep the game alive
     * after a certain number of tries it shows a message to the user and exits the game
     * */
    public void keepAlive(String gameId) {
        GameLifecycleNW.keepGameAlive(context, gameId, new ICallbacks<String>() {
            @Override
            public void onResponseSuccess(String response) {
                try {
                    String value = new JSONObject(response).getString("value");
                    if (value.equals("OK")) {
                    }
                } catch (JSONException e) {
                    Log.e("myDEBUG GameLogic", "keepAlive onResponse catch error: " + e);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("myDEBUG GameLogic", "onError from server on keepAlive: " + e);
            }
        });
    }


    public void setKeepAliveRunnable() {
        keepAliveTask = new Runnable() {
            @Override
            public void run() {
                Log.d("myDEBUG GameLogic", "run: keepAliveTask");
                keepAlive(currPlayerGameBoard.getGameId());
                // Reschedule this Runnable to run again after keepAlivePostDelayMillis
                gameRunnablesHandler.postDelayed(this, KEEP_ALIVE_DELAY_MILLIS);

            }
        };
    }


    /*
     *  This method is called when the player pauses the game
     *  It sends a request to the server to pause the game
     * */
    public void pauseGame(String gameId) {
        GameLifecycleNW.notifyGamePaused(context, gameId, new ICallbacks<String>() {
            @Override
            public void onResponseSuccess(String response) {
                Log.d("myDEBUG GameLogic", "pauseGame onResponse: " + response);
                gameRunnablesHandler.removeCallbacks(GameLogic.keepAliveTask);
                gameRunnablesHandler.removeCallbacks(randomAttackTask);
                stopCountdown();

            }

            @Override
            public void onError(Exception e) {
                Log.e("myDEBUG GameLogic", "onError from server on gamePaused: " + e);
            }
        });
    }


    /*
     *  This method is called when the player resumes the game
     *  It sends a request to the server to resume the game
     * */
    public void resumeGame(String gameId) {
        GameLifecycleNW.notifyGameResumed(context, gameId, new ICallbacks<String>() {
            @Override
            public void onResponseSuccess(String response) {
                Log.d("myDEBUG GameLogic", "resumeGame onResponse: " + response);
                getGame(gameId, currPlayerGameBoard.getUser().getId());
            }

            @Override
            public void onError(Exception e) {
                Log.e("myDEBUG GameLogic", "onError from server on gameResumed: " + e);
            }
        });
    }


}
