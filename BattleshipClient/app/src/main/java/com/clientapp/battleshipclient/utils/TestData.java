package com.clientapp.battleshipclient.utils;


import android.util.Log;

import com.clientapp.battleshipclient.data.GameBoard;
import com.clientapp.battleshipclient.data.Ship.OrientationEnum;
import com.clientapp.battleshipclient.data.Ship.Ship;
import com.clientapp.battleshipclient.data.Ship.ShipTypeEnum;
import com.clientapp.battleshipclient.data.Ship.ShipsResources;
import com.clientapp.battleshipclient.data.Tile.Tile;
import com.clientapp.battleshipclient.data.Tile.TileStateEnum;
import com.clientapp.battleshipclient.data.User;

import java.util.ArrayList;

public class TestData {
    public static int[] edgePositionsHorizontal1 = {0, 21, 41, 60, 80};
    public static int[] edgePositionsHorizontal = {4, 26, 46, 60, 87};
    public static int[] edgePositionsVertical1 = {0, 2, 4, 6, 8};
    public static int[] edgePositionsVertical = {20, 42, 14, 66, 78};


    // populate a tiles arraylist with 100 tiles




    public static GameBoard createGameBoard(User user, String userId, String gameId, String boardId, OrientationEnum orientation) {
        Log.d("DEBUG TestData", "createGameBoard: userId: " + userId + " gameId: " + gameId + " boardId: " + boardId);
        GameBoard gameBoard = new GameBoard(user,  gameId);
        Log.d("DEBUG TestData", "createGameBoard: gameBoard created");
        buildShips(gameBoard.getShips(), orientation);
        Log.d("DEBUG TestData", "createGameBoard: ships created");
        buildBoard(gameBoard.getBoard() , gameBoard.getShips());
        Log.d("DEBUG TestData", "createGameBoard: board created");
        return gameBoard;
    }

    private static void buildBoard(ArrayList<Tile> board, ArrayList<Ship> ships) {

        //loop on ships and set tiles on ships positions to be occupied
        for (Ship ship : ships) {
            //loop on size
            int shipPosition = ship.getEdgePosition();
            OrientationEnum orientation = ship.getOrientation();
            for (int i = 0; i < ship.getSize(); i++) {
                int position = ship.getShipPositionsArray().get(i);
                Tile tile = board.get(position);
                tile.setState(TileStateEnum.SHIP);
                tile.setShipId(ship.getId());
                tile.setBoardId(ship.getBoardId());
            }
        }
    }

    private static void buildShips(ArrayList<Ship> ships,  OrientationEnum orientation) {
        int[] edgePositions = null;
        if (orientation == OrientationEnum.HORIZONTAL) {
            edgePositions = TestData.edgePositionsHorizontal;
        } else {
            edgePositions = TestData.edgePositionsVertical;
        }
        Log.d("DEBUG TestData buildShips", "buildShips: orientation: " + orientation);
        ShipsResources.initNameToIdForTopViews();
        ShipsResources.initNameToIdForBottomViews();
        Log.d("DEBUG TestData buildShips", "ShipsResources set");


        for (int i=0 ; i<edgePositions.length; i++ ) {
            ShipTypeEnum shipType = ShipTypeEnum.values()[i];
            Log.d("DEBUG TestData buildShips", "buildShips: shipType: " + shipType);

            int shipSize = ShipsResources.getShipSizeByType(shipType);
            Log.d("DEBUG TestData buildShips", "buildShips: shipSize: " + shipSize);

            int topId = ShipsResources.getTopShipIdByType(shipType);
            Log.d("DEBUG TestData buildShips", "buildShips: topId: " + topId);

            int bottomId = ShipsResources.getBottomShipIdByType(shipType);
            Log.d("DEBUG TestData buildShips", "buildShips: bottomId: " + bottomId);

            Ship ship = new Ship(bottomId, topId, shipSize, shipType);
            Log.d("DEBUG TestData buildShips", "buildShips: ship created");
            ship.setOrientation(orientation);
            ship.setEdgePosition(edgePositions[i]);
            ship.setPlaced(true);
            ship.setSunk(false);
            ship.setId(topId + "");
            if(orientation == OrientationEnum.HORIZONTAL) {
                ship.setShipPositionsArray(edgePositionsHorizontal[i], OrientationEnum.HORIZONTAL);
            } else {
                ship.setShipPositionsArray(edgePositionsVertical[i], OrientationEnum.VERTICAL);
            }

            ships.add(ship);
        }
    }


}
