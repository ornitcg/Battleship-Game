package com.clientapp.battleshipclient.logic;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.clientapp.battleshipclient.model.Game.Game;
import com.clientapp.battleshipclient.model.Game.GameStateEnum;
import com.clientapp.battleshipclient.model.GameBoard;
import com.clientapp.battleshipclient.model.Ship.OrientationEnum;
import com.clientapp.battleshipclient.model.Ship.Ship;
import com.clientapp.battleshipclient.model.Ship.ShipTypeEnum;
import com.clientapp.battleshipclient.model.Tile.Tile;
import com.clientapp.battleshipclient.model.Tile.TileStateEnum;
import com.clientapp.battleshipclient.networking.GameActionNW;
import com.clientapp.battleshipclient.networking.GameLifecycleNW;
import com.clientapp.battleshipclient.networking.NWutils.RequestEnum;
import com.clientapp.battleshipclient.networking.NWutils.ServerStrings;
import com.clientapp.battleshipclient.networking.Netcom;
import com.clientapp.battleshipclient.utils.AudioEnum;
import com.clientapp.battleshipclient.utils.AudioUtils;
import com.clientapp.battleshipclient.view.view_utils.ClientMessages;
import com.clientapp.battleshipclient.view.activities.GameActivity;
import com.clientapp.battleshipclient.view.activities.PlacementActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lombok.Getter;
import lombok.NonNull;


/*
*  This class is the main logic class for the game
*  It handles the game state, the attacks, the ships, the boards and the game lifecycle
* */
public class GameLogic {
    private static final int GET_GAME_DELAY_MILLIS = 1000;
    public static final int RANDOM_ATTACK_DELAY_MILLIS = 15100;
    private static final int ATTACK_MSG_MILLIS = 2000;
    private final int KEEP_ALIVE_DELAY_MILLIS = 5000;
    private final int GET_GAME_MAX_TRIES = 10;

    private Context context;
    private GameBoard currPlayerGameBoard ;
    public static Runnable keepAliveTask;
    public static Runnable getGameRepeatTask;
    public static Handler gameRunnablesHandler = new Handler();
    private boolean isTurnChanged = false;
    private boolean isFirstKeepAliveCalled = false;
    public static boolean isGameInProgress = true;
    private int getGameRetryCounter = 0;

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
        setKeepAliveRunnable();
        setGetGameRepeatRunnable();
        setRandomAttackRunnable();
    }

    /*
    *  Sets the getGameRepeatRunnable to run every 1 second
    *  It is called when the player starts the game
    * */
    private void setGetGameRepeatRunnable() {
        getGameRepeatTask = new Runnable() {
            @Override
            public void run() {
                getGame();
            }
        };
    }


    /*
     *  This method is called when the player's turn is timed-out
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
    public void getGame() {
        String gameId = currPlayerGameBoard.getGameId();
        GameActionNW.getGame(context, gameId, new ICallbacks<String>() { //call network getGame
            @Override
            public void onResponseSuccess(String response) {
                Log.d("myDEBUG GameLogic", "getGame onResponse: " + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String value = jsonResponse.getString(ServerStrings.VALUE);
                    String turnUserId = new JSONObject(value).getString(ServerStrings.TURN_USER_ID);
                    String gameState = new JSONObject(value).getString(ServerStrings.GAME_STATE);
                    String winnerUserId = new JSONObject(value).getString(ServerStrings.WINNER_USER_ID);
                    Game game = new Game(gameId, turnUserId, GameStateEnum.fromString(gameState), winnerUserId);                    handleGameStateFromResponse(game);

                    getGameRetryCounter = 0;
                } catch (JSONException e) {
                    retryGetGame();
                    Log.e("myDEBUG GameLogic", "extractGameStateFromResponse onError from server on getGame: " + e);
                }
            }//end of onResponse

            @Override
            public void onError(Exception e) {
                retryGetGame();
                Log.e("myDEBUG GameLogic", "getGame onError from server  : " + e);
            }
        });
    }

    private void retryGetGame() {
        if (getGameRetryCounter < GET_GAME_MAX_TRIES && isGameInProgress) {
            Log.d("myDEBUG GameLogic", "retryGetGame: retrying getGame");
            getGameRetryCounter++;
            getGame();
        } else {
            Log.d("myDEBUG GameLogic", "retryGetGame max tries achieved: game is ended");
            handleGameEndedByOpponent();
        }
    }


    /*
     *  Calls methods according to the game state
     */
    private void handleGameStateFromResponse(Game game) {
        Log.d("myDEBUG GameLogic", "handleGameStateResponse: turnUserId: " + game.getTurnUserId() + " gameState: " + game.getGameState() + " winnerUserId: " + game.getWinnerUserId());
        switch (game.getGameState()) {
            case IN_PROGRESS:
                handleGameInProgress(game.getTurnUserId());
                break;
            case FINISHED:
                handleGameOver(game);
                break;
            case ENDED:
                handleGameEndedByOpponent();
                break;
            case PAUSED:
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
    private void handleGameEndedByOpponent() {
        isGameInProgress = false;
        gameRunnablesHandler.removeCallbacksAndMessages(null); //null = stop all runnables
        Log.d("myDEBUG GameLogic", "handleGameEnded: game is ended");
        AudioUtils.makeSound(context, AudioEnum.GAME_OVER);
        Log.d("myDEBUG GameLogic", "handleGameEnded: made sound for game over");
        ((GameActivity) context).disableGameboard();
        ((GameActivity) context).displayFinalMessage(GameStateEnum.ENDED, false);
    }


    /**
     * This method is called when the game state is "inProgress"
     * It sets the board for attack or wait depending on the turn
     * and starts the periodic updates of the game state
     */
    private void handleGameInProgress(String turnUserId) {
        String currPlayerId = currPlayerGameBoard.getUser().getId();
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
            ((GameActivity) context).disableBoardForAttack(ClientMessages.OPPONENT_TURN);
            if (!isFirstKeepAliveCalled) {
                keepAlive(currPlayerGameBoard.getGameId());
                isFirstKeepAliveCalled = true;
            }
            getCurrentPlayerBoard();
        }
    }


    /*
     *   Randomizes a position to attack on the opponent's board
     *   called automatically when the player's turn is timed-out
     * */
    private void randomAttack() {
        boolean isAttacked = true;
        int randomPosition = -1;
        while (isAttacked) {
            randomPosition = (int) (Math.random() * 99);
            if (((GameActivity) context).getOpponentGameBoard().getBoard().get(randomPosition).getState() == TileStateEnum.SEA)
                isAttacked = false;
        }
        Log.d("myDEBUG GameLogic", "randomAttack: randomPosition: " + randomPosition);
        attackOpponent(randomPosition);
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
        ((GameActivity) context).disableBoardForAttack(ClientMessages.PAUSE_MESSAGE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getGame();
            }
        }, GET_GAME_DELAY_MILLIS);
    }


    /*
     *  This method is called when the game is finished with a winner
     *  It shuts down the periodic updates of player being in game
     *  plays sound for win or lose
     * and shows the final message
     * */
    private void handleGameOver(Game game) {
        Log.d("myDEBUG GameLogic", "handleGameOver: game is finished");
        isGameInProgress = false;

        boolean isCurrentPlayerWinner = game.getWinnerUserId().equals(currPlayerGameBoard.getUser().getId());
        gameRunnablesHandler.removeCallbacksAndMessages(null);  //null = stop all runnables
        stopCountdown();
        getCurrentPlayerBoard(); // last call to not miss the last 'hit' attack
        AudioUtils.makeSound(context, AudioEnum.GAME_OVER);
        if (isCurrentPlayerWinner) {
            AudioUtils.makeSound(context, AudioEnum.WIN);
        } else {
            AudioUtils.makeSound(context, AudioEnum.LOSE_SOUND);
        }
        ((GameActivity) context).disableGameboard();
        ((GameActivity) context).displayFinalMessage(game.getGameState(), isCurrentPlayerWinner);
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
        Log.d("myDEBUG GameLogic", "in waitForCurrentBoard");
        GameActionNW.getBoard(boardId, new ICallbacks<String>() {
            @Override
            public void onResponseSuccess(String response) {
                ArrayList<Tile> board = JsonHelper.extractBoardFromResponse(response);
                int position = findTheAttackedPos(board); //could return -1
                updateCurrentPlayerBoardData(board);

                if (position != -1)   // only if the opponent has attacked
                    handleBeingAttackedResult(position);

                updateAllShips(board);

                if (isGameInProgress) {
                    gameRunnablesHandler.postDelayed(getGameRepeatTask, GET_GAME_DELAY_MILLIS);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("myDEBUG GameLogic", "waitForCurrentBoard onError from server : " + e);
            }
        });

    }


    /*
     *  Updates the ships with the attack result
     * */
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

    /*
     *  Updates the current player board with the attack result
     * */
    private void updateCurrentPlayerBoardData(ArrayList<Tile> board) {
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
    public void attackOpponent(int attackPosition) {
        // cancel all requests on queue
        Netcom.getInstance(context).getRequestQueue().cancelAll(RequestEnum.POST_ATTACK.getName());
        Log.d("myDEBUG GameLogic", "cancelAll on POST_ATTACK ");
        gameRunnablesHandler.removeCallbacks(randomAttackTask);
        Log.d("myDEBUG GameLogic", "removeCallbacks on randomAttackTask ");
        GameActionNW.postAttack(context, currPlayerGameBoard.getGameId(), currPlayerGameBoard.getUser().getId(), attackPosition, new ICallbacks<String>() {
            @Override
            public void onResponseSuccess(String response) {
                Log.d("myDEBUG GameLogic", "attack onResponse:  " + response);
                attacksCounter++;
                JSONObject value;
                try {
                    value = new JSONObject(response).getJSONObject(ServerStrings.VALUE);
                    Log.d("myDEBUG GameLogic", "attack onResponse: value: " + value);
                    Log.d("myDEBUG GameLogic", "attack onResponse: attackPosition: " + attackPosition);
                    handleAttackOpponentResult(attackPosition, value);
                    getGame(); //So player knows if it is his turn
                } catch (JSONException e) {
                    Log.e("myDEBUG GameLogic", "attack catch  RuntimeException onResponse: " + e);
                }//end of catch
            }//end of onResponse

            @Override
            public void onError(Exception e) {
                Log.e("myDEBUG GameLogic", "attack onError from server on postAttack: " + e);
            }
        });
    }


    /*
     *  Updates the opponent's board with the attack result
     *  and plays the sound for the attack result
     * */
    private void handleAttackOpponentResult(int attackPosition, JSONObject value) {
        Integer shipPosition = null;
        AttackResultEnum attackResult = null;
        ShipTypeEnum shipType = null;
        OrientationEnum orientation = null;
        TileStateEnum state;
        try { //some values may be null. In this case, catch the exception
            attackResult = AttackResultEnum.fromString(value.getString(ServerStrings.ATTACK_RESULT));
            shipType = ShipTypeEnum.fromString(value.getString(ServerStrings.SHIP_TYPE));
            orientation = OrientationEnum.fromString(value.getString(ServerStrings.ORIENTATION));
            shipPosition = value.getInt(ServerStrings.POSITION);
        } catch (JSONException e) {
            Log.e("myDEBUG GameLogic", "handleAttackOpponentResult onResponse catch error: " + e);
        }
        if (attackResult != null) {
            if (attackResult.equals(AttackResultEnum.SUNK))
                state = TileStateEnum.HIT; // special case since there is no tile state as SUNK
            else
                state = TileStateEnum.fromString(attackResult.getName());

            Log.d("myDEBUG GameLogic", "handleAttackOpponentResult onResponse: state: " + state);
            ((GameActivity) context).updateOpponentBoard(attackPosition, attackResult, shipType, orientation, shipPosition, state);
            AudioEnum audioEnum = AudioEnum.fromString(attackResult.getName());
            try {
                Log.d("myDEBUG GameLogic", "handleAttackOpponentResult onResponse: audioEnum: " + audioEnum);
                AudioUtils.makeSound(context, audioEnum);
            } catch (Exception e) {
                Log.e("myDEBUG GameLogic", "handleAttackResult onResponse catch error: " + e);
            }
            ((GameActivity) context).updateOpponentBoard(attackPosition, attackResult, shipType, orientation, shipPosition, state);
        }

    }


    /*
     *  notifies server about opponent leaving the game
     *  the game stated then is set to "ended"
     * */
    public static void notifyGameEnd(Context context, String gameId) {
        gameRunnablesHandler.removeCallbacks(keepAliveTask);
        gameRunnablesHandler.removeCallbacks(getGameRepeatTask);
        PlacementActivity.startGameTimeoutRunnablehandler.removeCallbacks(PlacementActivity.startGameTimeoutRunnableTask);
        Netcom.getInstance(context).getRequestQueue().cancelAll(RequestEnum.CREATE_BOARD.getName());
        Log.d("myDEBUG GameLogic", "calling notifyGameEnd on gameId : " + gameId);
        GameLifecycleNW.notifyGameEnded(context, gameId, new ICallbacks<String>() {
            @Override
            public void onResponseSuccess(String response) {
                Log.d("myDEBUG GameLogic", "notifyGameEnd onResponse: " + response);
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
                    Log.d("myDEBUG GameLogic", "keepAlive onResponse: " + response);
                } catch (Exception e) {
                    Log.e("myDEBUG GameLogic", "keepAlive onResponse catch error: " + e);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("myDEBUG GameLogic", "onError from server on keepAlive: " + e);
            }
        });
    }


    /*
     * Sets the keepAliveRunnable to run every 5 seconds
     * It is called when the player starts the game
     * */
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
                getGame();
            }

            @Override
            public void onError(Exception e) {
                Log.e("myDEBUG GameLogic", "onError from server on gameResumed: " + e);
            }
        });
    }


}
