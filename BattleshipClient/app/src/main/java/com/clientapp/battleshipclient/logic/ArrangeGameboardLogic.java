package com.clientapp.battleshipclient.logic;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.clientapp.battleshipclient.data.GameBoard;
import com.clientapp.battleshipclient.data.Ship.OrientationEnum;
import com.clientapp.battleshipclient.data.Ship.Ship;
import com.clientapp.battleshipclient.data.Tile.Tile;
import com.clientapp.battleshipclient.data.Tile.TileStateEnum;
import com.clientapp.battleshipclient.data.User;
import com.clientapp.battleshipclient.networking.GameLifecycleNW;
import com.clientapp.battleshipclient.networking.ICallbacks;
import com.clientapp.battleshipclient.networking.Netcom;
import com.clientapp.battleshipclient.view.activities.ArrangeGameBoardActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArrangeGameboardLogic {

    public static final long START_GAME_TIMEOUT_MILLIS = 30000;
    private static final int SEND_BOARD_MAX_TRIES = 10;
    public static Handler startGameTimeoutHandler = new Handler();
    private static int retrySendGameboardCounter;

    /*
     * This method checks if all ships are placed on the gameboard
     * */
    public static boolean areAllShipsPlaced(HashMap<Integer, Ship> shipCollection) {
        Ship ship = null;
        for (Map.Entry<Integer, Ship> entry : shipCollection.entrySet()) {
            ship = entry.getValue();
            if (!ship.isPlaced()) {
                return false;
            }
        }
        return true;
    }


    /*
     * This method is used to set the data of all tiles on the gameboard
     * */
    public static void setAllTilesData(ArrayList tilesList, HashMap<Integer, Ship> shipCollection) {
        ArrayList<Ship> updatedShips = new ArrayList<>(); //temporarily store the updated ships
//        ArrayList<Tile> tilesList = tilesList;
        for (Map.Entry<Integer, Ship> entry : shipCollection.entrySet()) {
            Ship ship = entry.getValue();
            if (updatedShips.contains(ship)) continue;
            updatedShips.add(ship);
            setTilesWithShipAndStatus(ship, tilesList);
            setNearShip(ship, tilesList);
        }
    }

    /* this method resets the status of the tiles around the ships */
    public static void forgetNearShipsStatus(ArrayList<Tile> tilesList) {
        for (Tile tile : tilesList) {
            if (tile.getState() == TileStateEnum.NEAR_SHIP) {
                tile.setState(TileStateEnum.SEA);
            }
        }

    }


    /*
     *  This method is used to set the tiles with the ship data and status
     * */
    public static void setTilesWithShipAndStatus(Ship ship, ArrayList<Tile> tilesList) {
        ArrayList<Integer> shipTilesPositions = ship.getShipPositionsArray();
        if (shipTilesPositions.isEmpty()) return;
        int shipsSize = ship.getSize();
//        Log.d("DEBUG setTilesWithShipDataAndStatus", "shipTilesPositions: " + shipTilesPositions);
        for (int i = 0; i < shipsSize; i++) {
            int position = shipTilesPositions.get(i);
            if (position == -1) continue;
            tilesList.get(position).setState(TileStateEnum.SHIP);
            tilesList.get(position).setShipId(String.valueOf(ship.getBottomViewId()));
        }
    }


    /*
     *  This method is used to reset all tiles to SEA state
     * */
    public static void resetAllTilesData(ArrayList<Tile> tilesList) {
        // loop on tilesDataList and reset all tiles to SEA
        for (Tile tile : tilesList) {
            tile.resetSingleTileData();
        }
    }


    /*
     * This method is used to set the stat of the tiles around the ship as NEAR_SHIP
     * @param ship - the ship object
     * @param tilesDataList - the list of tiles
     */
    public static void setNearShip(Ship ship, ArrayList<Tile> tilesDataList) {//TODO: requires changes
        ArrayList<Integer> shipTilesPositions = ship.getShipPositionsArray();
        int[] aroundTilePositionShifts = new int[]{-9, -10, -11, +9, +10, +11, -1, +1};
//        Log.d("DEBUG setNearShip", "setNearShip shipTilesPositions: " + shipTilesPositions);
        if (shipTilesPositions.isEmpty()) return;
        for (int i = 0; i < ship.getSize(); i++) {
            int position = shipTilesPositions.get(i);

            for (int shift : aroundTilePositionShifts) { // revert 4 tiles around the tile
                int aroundTilePosition = position + shift;
                if (!ArrangeGameboardLogic.isValidShift(position, shift)) continue;
                Tile tileAroundShip = tilesDataList.get(aroundTilePosition);
                if (tileAroundShip.getState() == TileStateEnum.SHIP) {
                    continue;
                } else tileAroundShip.setState(TileStateEnum.NEAR_SHIP);

            }//end inner for
        }//end outer for
    }//end setNearShip


    /**
     * This method is used to populate the gameboard with 100 SEA tiles     *
     *
     * @return ArrayList<Tile> - a list of 100 SEA tiles
     */
    public static ArrayList<Tile> populateWithTiles() {
        ArrayList<Tile> tilesList = new ArrayList<>();
        for (int i = 0; i < 100; i++) { //initialize the list of tiles with 100 SEA tiles
            tilesList.add(new Tile(i));
        }
        return tilesList;
    }


    /* This method is used to set the positions of the ship on the gameboard
     * @param position - the position of the tile where the ship is placed
     * @param ship - the ship object
     * */
    public static void setShipPositionsArray(int position, Ship ship) {
        ArrayList<Integer> shipTilesPositions = ship.getShipPositionsArray();
        int shipsSize = ship.getSize();
        int tileCol = position % 10;  //get the column of the tile
        int tileRow = position / 10;   //get the row of the tile
        int shift = (shipsSize / 2) * (-1);
        int row = 0;
        int col = 0;
        ship.forgetPositions();
        boolean isVertical = ship.getOrientation() == OrientationEnum.VERTICAL;
        for (int i = 0; i < shipsSize; i++) {
            if (isVertical) {
                row = tileRow + shift;
                if (row < 0 || row > 9) {
                    shipTilesPositions.add(-1);
                } else {
                    shipTilesPositions.add(row * 10 + tileCol);
                }
            } else { //HORIZONTAL
                col = tileCol + shift;
                if (col < 0 || col > 9) {
                    shipTilesPositions.add(-1);
                } else shipTilesPositions.add(tileRow * 10 + col);
            }//end else
            shift += 1;
        }//end for
    }


    /*
     * check if the ship is within the grid bounds
     * */
    public static boolean isWithinGridBounds(Ship ship) {
        ArrayList<Integer> shipTilesPositions = ship.getShipPositionsArray();
        for (int i = 0; i < shipTilesPositions.size(); i++) {
            if (shipTilesPositions.get(i) == -1) {
                return false;
            }
        }
        return true;
    }


    /**
     * This method is used to check if the ship can be placed at the given position     *
     */
    public static boolean isValidShift(int position, int shift) {
        int aroundTilePosition = position + shift;
        if ((aroundTilePosition < 0 || aroundTilePosition > 100) ||
                (position % 10 == 0 && (shift == -1 || shift == -11 || shift == 9)) ||
                (position % 10 == 9 && (shift == 1 || shift == 11 || shift == -9)) ||
                (position / 10 == 0 && (shift == -9 || shift == -10 || shift == -11)) ||
                (position / 10 == 9 && (shift == 9 || shift == 10 || shift == 11)))
            return false;
        return true;
    }

    public static void cancelRequests(ArrangeGameBoardActivity arrangeGameBoardActivity) {
        //cancell all requests
        Netcom.getInstance(arrangeGameBoardActivity).cancelAllRequests();
    }

    public static void prepareGameBoardForSending(Context context, User currentPlayer, String gameId, ArrayList<Tile> tilesList, HashMap<Integer, Ship> shipCollection) {
        setAllTilesData(tilesList, shipCollection);
        forgetNearShipsStatus(tilesList);
        ArrayList<Ship> shipsList = setShipsListForGameBoard(shipCollection);
        GameBoard currPlayerBoard = new GameBoard(currentPlayer, gameId, null, shipsList, tilesList); //the boardId will be set later
        sendGameboard(context, currPlayerBoard);
    }

    public static void sendGameboard(Context context, GameBoard gameBoard) {
        String gameId = gameBoard.getGameId();
        JSONObject gameBoardJson = JsonHelper.createGameBoardJson(gameBoard);
        if (gameBoard != null) {
            GameLifecycleNW.createBoard(context, gameBoardJson, new ICallbacks<String>() {
                @Override
                public void onResponseSuccess(String response) {
                    ArrangeGameBoardActivity.startGameTimeoutRunnablehandler.removeCallbacks(((ArrangeGameBoardActivity) context).startGameTimeoutRunnableTask);
                    Log.d("DEBUG ArrangeGameboardLogic", "startGameTimeoutRunnableTask stopped");
                    Log.d("DEBUG ArrangeGameboardLogic", "sendGameboard onResponse: " + response);
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(response);
                        String msg = jsonResponse.getString("msg");
                        String boardId = jsonResponse.getString("value");
                        if (msg.equals("OK")) {
                            retrySendGameboardCounter = 0;
                            Log.d("DEBUG ArrangeGameboardLogic", "msg = OK, going to gameActivity");
                            gameBoard.setBoardId(boardId);
                            ((ArrangeGameBoardActivity) context).goToGameActivity(gameId, gameBoard);
                        }
                    } catch (JSONException e) {
                        Log.e("DEBUG GAmeLogic try to make string to json", "sendGameboard onResponse: " + e);
                        retrySendGameboard(context, gameBoard);
                    }
                }// end of onResponse

                @Override
                public void onError(Exception e) {
                    retrySendGameboard(context, gameBoard);
                    Log.e("DEBUG ArrangeGameboardLogic", "sendGameboard onError from server: " + e);
                }// end of onError
            }); // end of getBoardId
        }// end of if
    }// end of sendGameboard

    /*
     *  This method retries the sendGameboard method up to GENERAL_MAX_TRIES
     *  after max failed tries it shows a message to the user and exits the game
     * */
    private static void retrySendGameboard(Context context, GameBoard gameBoard) {
        if (retrySendGameboardCounter < SEND_BOARD_MAX_TRIES) {
            retrySendGameboardCounter++;
            sendGameboard(context, gameBoard);
        } else {
            Log.e("myDEBUG GameLogic", "retrySendGameboard: MAX TRIES REACHED");
            GameLogic.handleGameEndedByServer(context);
        }
    }


    /*
     *  This method created a 5 ships list set from the shipCollection
     *  to be used in the gameboard
     * */
    private static ArrayList<Ship> setShipsListForGameBoard(HashMap<Integer, Ship> shipCollection) {
        ArrayList<Ship> ShipList = new ArrayList<>();
        Set<Ship> shipSet = new HashSet<>();
        for (Map.Entry<Integer, Ship> entry : shipCollection.entrySet()) {
            Ship ship = entry.getValue();
            if (shipSet.contains(ship)) {
                continue;
            }
            shipSet.add(ship); //to get 5 ships only
            ship.setEdgePosition();
            ShipList.add(ship);
        }
        return ShipList;
    }

//    public static void notifyGameEnd(Context context, String gameId) {
//        GameLifecycleNW.notifyGameEnded(context, gameId, new ICallbacks<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d("myDEBUG ArrangeGameboardActivity  ", "notifyGameEnd onResponse: " + response);
//                try {
//                    String state = new JSONObject(response).getString("value");
//                    Log.d("myDEBUG ArrangeGameboardActivity", "notifyGameEnd onResponse: state: " + state);
//                } catch (JSONException e) {
//                    Log.e("myDEBUG ArrangeGameboardActivity", "notifyGameEnd onResponse from server on notifyGameEnd: " + e);
//                }
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Log.e("myDEBUG ArrangeGameboardActivity", "notifyGameEnd onError from server on notifyGameEnd: " + e);
//            }
//        });
//    }
}// end of class
