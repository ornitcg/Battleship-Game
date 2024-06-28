package com.clientapp.battleshipclient.logic;


import static android.view.DragEvent.ACTION_DRAG_ENTERED;
import static android.view.DragEvent.ACTION_DRAG_EXITED;
import static android.view.DragEvent.ACTION_DRAG_STARTED;
import static android.view.DragEvent.ACTION_DROP;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.widget.ImageView;

import com.clientapp.battleshipclient.model.GameBoard;
import com.clientapp.battleshipclient.model.Ship.OrientationEnum;
import com.clientapp.battleshipclient.model.Ship.Ship;
import com.clientapp.battleshipclient.model.Tile.Tile;
import com.clientapp.battleshipclient.model.Tile.TileStateEnum;
import com.clientapp.battleshipclient.model.User;
import com.clientapp.battleshipclient.networking.GameLifecycleNW;
import com.clientapp.battleshipclient.networking.NWutils.ServerStrings;
import com.clientapp.battleshipclient.view.activities.PlacementActivity;
import com.clientapp.battleshipclient.view.view_utils.ClientMessages;
import com.clientapp.battleshipclient.view.view_utils.PlacementAdapter;
import com.clientapp.battleshipclient.view.view_utils.PlacementUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;


/*
 *  This class is used to handle the placement of the ships on the gameboard
 *  It contains the logic for the placement of the ships on the grid
 *  It also contains the logic for the drag and drop events on the tiles
 *
 * */
@Data
@NoArgsConstructor
public class PlacementLogic {

    public static final long START_GAME_TIMEOUT_MILLIS = 30000;
    public static Handler startGameTimeoutHandler = new Handler();
    private static int retrySendGameboardCounter;
    private Context context;
    private String gameId;
    private User currentPlayer;
    private HashMap<Integer, Ship> shipCollection;
    private ArrayList<Tile> tilesList;
    private PlacementAdapter placementAdapter;
    private String lastShipClickedId = "";
    private int lastShipClickedPosition = -1;
    private HashSet<String> clickedShipSet = new HashSet<>();

    public PlacementLogic(Context context, String gameId, User currentPlayer, HashMap<Integer, Ship> shipCollection, ArrayList<Tile> tilesList, PlacementAdapter gridLayoutAdapter) {
        this.gameId = gameId;
        this.currentPlayer = currentPlayer;
        this.shipCollection = shipCollection;
        this.tilesList = tilesList;
        this.placementAdapter = gridLayoutAdapter;
        this.context = context;
    }

    /*
     * This method checks if all ships are placed on the gameboard
     * */
    public boolean areAllShipsPlaced() {
        Ship ship;
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
    public void setAllTilesData(HashMap<Integer, Ship> shipCollection) {
        ArrayList<Ship> updatedShips = new ArrayList<>(); //temporarily store the updated ships
//        ArrayList<Tile> tilesList = tilesList;
        for (Map.Entry<Integer, Ship> entry : shipCollection.entrySet()) {
            Ship ship = entry.getValue();
            if (updatedShips.contains(ship)) continue;
            updatedShips.add(ship);
            setTilesWithShipAndStatus(ship);
            setNearShipTiles(ship);
        }
    }

    /* this method resets the status of the tiles around the ships */
    public void forgetNearShipsStatus() {
        for (Tile tile : tilesList) {
            if (tile.getState() == TileStateEnum.NEAR_SHIP) {
                tile.setState(TileStateEnum.SEA);
            }
        }

    }


    /*
     *  This method is used to set the tiles with the ship data and status
     * */
    public void setTilesWithShipAndStatus(Ship ship) {
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
    public void resetAllTilesData() {
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
    public void setNearShipTiles(Ship ship) {
        ArrayList<Integer> shipTilesPositions = ship.getShipPositionsArray();
        int[] aroundTilePositionShifts = new int[]{-9, -10, -11, +9, +10, +11, -1, +1};
//        Log.d("DEBUG setNearShip", "setNearShip shipTilesPositions: " + shipTilesPositions);
        if (shipTilesPositions.isEmpty()) return;
        for (int i = 0; i < ship.getSize(); i++) {
            int position = shipTilesPositions.get(i);

            for (int shift : aroundTilePositionShifts) { // revert 4 tiles around the tile
                int aroundTilePosition = position + shift;
                if (!PlacementUtils.isValidShift(position, shift)) continue;
                Tile tileAroundShip = tilesList.get(aroundTilePosition);
                if (tileAroundShip.getState() == TileStateEnum.SHIP) {
                    continue;
                } else tileAroundShip.setState(TileStateEnum.NEAR_SHIP);

            }//end inner for
        }//end outer for
    }//end setNearShip


    /*
     * check if the ship is within the grid bounds
     * */
    private static boolean isWithinGridBounds(Ship ship) {
        ArrayList<Integer> shipTilesPositions = ship.getShipPositionsArray();
        for (int i = 0; i < shipTilesPositions.size(); i++) {
            if (shipTilesPositions.get(i) == -1) {
                return false;
            }
        }
        return true;
    }


    /*
     *  This method is used to prepare the gameboard for sending to the server
     *  @param currentPlayer - the current player
     *
     * */
    public void prepareGameBoardForSending(User currentPlayer, String gameId, ArrayList<Tile> tilesList, HashMap<Integer, Ship> shipCollection) {
        setAllTilesData(shipCollection);
        forgetNearShipsStatus();
        ArrayList<Ship> shipsList = setShipsListForGameBoard(shipCollection);
        Log.d("myDEBUG PlacementLogic", "prepareGameBoardForSending shipsList: " + shipsList);
        GameBoard currPlayerBoard = new GameBoard(currentPlayer, gameId, null, shipsList, tilesList); //the boardId will be set later
        sendGameboard(currPlayerBoard);
    }


    /*
     *  This method is used to send the gameboard to the server
     *  @param gameBoard - the gameboard object
     * */
    public void sendGameboard(GameBoard gameBoard) {
        JSONObject gameBoardJson = JsonHelper.createJsonGameBoard(gameBoard);
        if (gameBoard != null) {
            GameLifecycleNW.createBoard(context, gameBoardJson, new ICallbacks<String>() {
                @Override
                public void onResponseSuccess(String response) {
                    Log.d("myDEBUG PlacementLogic", "sendGameboard onResponse: " + response);
                    if (!GameLogic.isGameInProgress) return;
                    PlacementActivity.startGameTimeoutRunnablehandler.removeCallbacks(((PlacementActivity) context).startGameTimeoutRunnableTask);
                    Log.d("myDEBUG PlacementLogic", "startGameTimeoutRunnableTask stopped");
                    JSONObject jsonResponse;
                    try {
                        jsonResponse = new JSONObject(response);
                        String msg = jsonResponse.getString(ServerStrings.MSG);
                        String boardId = jsonResponse.getString(ServerStrings.VALUE);
                        if (msg.equals("OK")) {
                            Log.d("myDEBUG PlacementLogic", "msg = OK, going to gameActivity");
                            gameBoard.setBoardId(boardId);
                            ((PlacementActivity) context).goToGameActivity(gameBoard);
                        }
                    } catch (JSONException e) {
                        Log.e("myDEBUG GAmeLogic try to make string to json", "sendGameboard onResponse: " + e);
                    } catch (Exception e) {
                        Log.e("myDEBUG PlacementLogic", "sendGameboard onResponse: bug hell " + e);
                    }
                }// end of onResponse

                @Override
                public void onError(Exception e) {
                    Log.e("myDEBUG PlacementLogic", "sendGameboard onError from server: " + e);
                }
            });
        }// end of if
    }// end of sendGameboard




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


    /*
     *  Refreshes the tiles on the grid
     *  used after any change in the tiles data
     * */
    public void refreshTiles() {
        placementAdapter.notifyDataSetChanged();
    }

    /*
     *  Handles the drag events on the tiles
     * @param context - the activity context
     * @param tileView - the tile view
     * @param event - the drag event
     * */
    public boolean handleTileDragEvents(Context context, DragEvent event, Ship ship, int position) {
        switch (event.getAction()) {
            case ACTION_DRAG_STARTED:
                resetAllTilesData();
                ship.setPlaced(false);
                ship.forgetPositions();
                ship.setEdgePosition();  //resets the edge position in this case
                refreshTiles();
                return true;
            case ACTION_DRAG_ENTERED:
                resetAllTilesData();
                refreshTiles();
                setShipNewPositions(ship, position, null);
                return true;
            case ACTION_DRAG_EXITED:
                //log position
                Log.d("DEBUG ACTION_DRAG_EXITED", "position: " + position);

                resetAllTilesData();
                ship.setPlaced(false);
                ship.forgetPositions();
                ship.setEdgePosition();  //resets the edge position in this case
                refreshTiles();
                return true;
            case ACTION_DROP:
                if (isValidForDrop(ship)) {
                    ImageView shipView = ((PlacementActivity)context).getShipViewsCollection().get(ship.getTopViewId());
                    PlacementUtils.setShipViewOnGrid(ship, shipView, placementAdapter.getGridLayout());
                    setTilesWithShipAndStatus(ship);
                    setNearShipTiles(ship);
                    refreshTiles();
                    ship.setPlaced(true);
                } else {
                    ship.resetShip();
                    ((PlacementActivity) context).returnShipToInventory(ship);
                    resetAllTilesData();
                    setTilesWithShipAndStatus(ship);
                    refreshTiles();
                    ((PlacementActivity) context).displayMessageForShortTime(ClientMessages.INVALID_PLACEMENT);

                }
                return true;
            default:
                break;
        }//end switch
        return true;
    }


    /*
     *  Sets the state of the tiles when a ship is dragged over them
     * */
    private void setTilesState(Ship ship) {
        ArrayList<Integer> shipTilesPositions = ship.getShipPositionsArray();
        int shipsSize = ship.getSize();
        boolean isValidForDrop = isValidForDrop(ship);
        if (isValidForDrop) {
            for (int i = 0; i < shipsSize; i++) {
                int position = shipTilesPositions.get(i);
                tilesList.get(position).setState(TileStateEnum.VALID_FOR_DROP);
                tilesList.get(position).setShipId(String.valueOf(ship.getBottomViewId())); //TODO delete
            }
//            logTilesStatus("setTilesStatusData"); // used for debugging
            return;
        }
        for (int i = 0; i < shipsSize; i++) {
            int position = shipTilesPositions.get(i);
            if (position != -1) {
                tilesList.get(position).setState(TileStateEnum.INVALID_FOR_DROP);
            }
        }
//        logTilesStatus("setTilesStatusData"); // used for debugging
    }

    /*
     *  This method is used to check if the ship (eith its ship data)  can be placed on the grid
     *  @param ship - the ship object
     * */
    public boolean isValidForDrop(Ship ship) {
        ArrayList<Integer> shipTilesPositions = ship.getShipPositionsArray();
        boolean isWithinBounds = PlacementLogic.isWithinGridBounds(ship);
//        Log.d("DEBUG isValidForDrop", "isWithinBounds: " + isWithinBounds);
        if (!isWithinBounds) return false;
        setDroppedShipTilesState();
        for (int i = 0; i < ship.getSize(); i++) {
            int position = shipTilesPositions.get(i);
            if (position <= -1 || position >= 100) {
//                Log.d("DEBUG isValidForDrop", "position is -1");
                return false;
            }
            if (tilesList.get(position).getState() == TileStateEnum.NEAR_SHIP) {
//                Log.d("DEBUG isValidForDrop", "tile is near ship");
                return false;
            }
            if (tilesList.get(position).getState() != TileStateEnum.SEA && tilesList.get(position).getState() != TileStateEnum.VALID_FOR_DROP) {
                Log.d("DEBUG isValidForDrop", "tile Status is " + tilesList.get(position).getState());
                return false;
            }
        }
        return true;
    }


    /*
     *  This method is used to set the status of the tiles to SHIP, when a ship is dropped on them
     * */
    private void setDroppedShipTilesState() {
        for (Map.Entry<Integer, Ship> entry : shipCollection.entrySet()) {
            Ship ship = entry.getValue();
            if (ship.isPlaced() == false) continue;
            setTilesWithShipAndStatus(ship);
            setNearShipTiles(ship);
            refreshTiles();
        }
    }

    /*
     *  Sets the new positionsArray of the ship when it is dragged over the grid
     *  with the new positions of tiles the ship is dropped on
     * */
    public void setShipNewPositions(Ship ship, int position, String location) {
        Log.d("DEBUG setShipNewPositions", "position: " + position);
        PlacementUtils.fillShipPositionsArray(position, ship, location); //null for default shift
        setTilesState(ship);
        refreshTiles();
    }

    /*
     *  Checks if the ship can be placed on the grid in a new orientation
     * if it can, it changes the orientation of the ship
     * if it can't, it returns the ship to its previous orientation
     * @param ship - the ship object
     * @return boolean - true if the ship can be placed in the new orientation, false if it can't
     * */
    public boolean orientationChangeLogic(Ship ship) {
        ship.setPlaced(false);
        ArrayList<Integer> shipPositionsArrayCopy = new ArrayList<>(ship.getShipPositionsArray());
        ship.changeOrientation(); //change the orientation of the ship data
        //make a copy of shipPositionsArray
//        if (lastShipClickedId.equals("") || !lastShipClickedId.equals(ship.getId())) { //if it is a new ship
//            lastShipClickedId = ship.getId();
//            clickedShipSet.add(lastShipClickedId);
//            lastShipClickedPosition = ship.getEdgePosition();
//        } else if (lastShipClickedId.equals(ship.getId())) {
//            if (clickedShipSet.contains(lastShipClickedId)) //if this ship was clicked anytime before
//                setShipNewPositions(ship, lastShipClickedPosition, OrientationEnum.EDGE.getName()); //use shift 0
//            else //if this ship was not clicked before
//                setShipNewPositions(ship, lastShipClickedPosition, null); //use default shift
//
//            if (isValidForDrop(ship)) {
//                ship.setPlaced(true);
//                setTilesWithShipAndStatus(ship);
//                setNearShipTiles(ship);
//                refreshTiles();
//                return true;
//            }
//        }

        Log.d("myDEBUG changeOrientation", "orientationChangeLogic shipPositionsArrayCopy: " + shipPositionsArrayCopy);

        for (int i = 0; i < ship.getSize(); i++) { //runs on all positions
            int rotateAxisTilePosition = shipPositionsArrayCopy.get(i); // get position
            Log.d("myDEBUG changeOrientation", "rotateAxisTilePosition onclick ship position: " + rotateAxisTilePosition);
            boolean isAxisUseful;
            isAxisUseful = isUsefullAxisforOrientationChange(ship, rotateAxisTilePosition);
            if (isAxisUseful) return true;
        }//end for
        restoreShip(ship, shipPositionsArrayCopy);
        return false;
    }


    /*
     *  This method is used to restore the ship to its previous orientation
     *  @param ship - the ship object
     *  @param shipPositionsArrayCopy - the copy of the ship positions array
     * */
    private void restoreShip(Ship ship, ArrayList<Integer> shipPositionsArrayCopy) {
        //returning to the previous orientation
        ship.changeOrientation(); //change back
        resetAllTilesData();
        ship.setShipPositionsArray(new ArrayList<>(shipPositionsArrayCopy));
        ship.setEdgePosition();
        setAllTilesData(shipCollection);
        setNearShipTiles(ship);
        refreshTiles();
        ship.setPlaced(true);
    }


    /*
     *  This method is used to check if the axis has any positions that can be used for orientation change
     *  @param ship - the ship object
     *  @param rotateAxisTilePosition - the position of the tile that is the axis of rotation
     *  @return boolean - true if the axis is useful, false if it is not
     * */
    private boolean isUsefullAxisforOrientationChange(Ship ship, int rotateAxisTilePosition) {
        boolean isValid = false;
        //for each requested orientation, check all possible positions
        if (ship.getOrientation() == OrientationEnum.HORIZONTAL) {
            isValid = checkHorizontalAxis(ship, rotateAxisTilePosition);
        } else { //vertical
            isValid = checkVerticalAxis(ship, rotateAxisTilePosition);
        }

        if (isValid) {
            ship.setPlaced(true);
            Log.d("DEBUG changeOrientation", "Orientation changed to: " + ship.getOrientation());
            setTilesWithShipAndStatus(ship);
            setNearShipTiles(ship);
            refreshTiles();
        }
        return isValid;
    }


    /*
     * This method is used to check if the ship can be placed on the grid in a vertical orientation
     * on the given axis
     * @param ship - the ship object
     * @param rotateAxisTilePosition - the position of the tile that is the axis of rotation
     * */
    private boolean checkVerticalAxis(Ship ship, int rotateAxisTilePosition) {
        int bottom = rotateAxisTilePosition + (ship.getSize() - 1) * 10;
        int top = rotateAxisTilePosition - (ship.getSize() - 1) * 10;
        boolean isValid = false;
        for (int j = top; j <= bottom; j += 10) { // loop on the whole range
            if (j < 0 || j > 99) { // if the tile out of bounds
                continue;
            }
            ship.forgetPositions();
            resetAllTilesData();
            setAllTilesData(shipCollection);
            refreshTiles();
            ship.setEdgePosition();
            setShipNewPositions(ship, j, OrientationEnum.EDGE.getName());
            isValid = isValidForDrop(ship);
            Log.d("myDEBUG changeOrientation", "setShipsOnClickOrientationChange j: " + j + " isValid: " + isValid);

            if (isValid) break;
        }
        return isValid;
    }

    /*
     *  This method is used to check if the ship can be placed on the grid in a horizontal orientation
     *  on the given axis
     * @param ship - the ship object
     * @param rotateAxisTilePosition - the position of the tile that is the axis of rotation
     * @return boolean - true if the ship can be placed in the new orientation, false if it can't
     * */
    private boolean checkHorizontalAxis(Ship ship, int rotateAxisTilePosition) {
        int min = rotateAxisTilePosition - ship.getSize() + 1;
        int max = rotateAxisTilePosition + ship.getSize() - 1;
        boolean isValid = false;
        for (int j = min; j <= max; j++) { // loop on the whole range
            if (j / 10 != rotateAxisTilePosition / 10) { // if the tile is not in the same row
                continue;
            }
            ship.forgetPositions();
            resetAllTilesData();
            setAllTilesData(shipCollection);
            refreshTiles();
            ship.setEdgePosition();
            setShipNewPositions(ship, j, OrientationEnum.EDGE.getName());
            isValid = isValidForDrop(ship);
            //log j, and sey if valid
            Log.d("myDEBUG lkk", "setShipsOnClickOrientationChange j: " + j + " isValid: " + isValid);
            if (isValid) break;
        }
        return isValid;
    }


}// end of class
