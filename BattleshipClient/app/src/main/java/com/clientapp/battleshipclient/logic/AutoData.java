package com.clientapp.battleshipclient.logic;


import android.util.Log;

import com.clientapp.battleshipclient.model.GameBoard;
import com.clientapp.battleshipclient.model.Ship.OrientationEnum;
import com.clientapp.battleshipclient.model.Ship.Ship;
import com.clientapp.battleshipclient.model.Ship.ShipTypeEnum;
import com.clientapp.battleshipclient.view.view_utils.ShipsConverter;
import com.clientapp.battleshipclient.model.Tile.Tile;
import com.clientapp.battleshipclient.model.Tile.TileStateEnum;
import com.clientapp.battleshipclient.model.User;

import java.util.ArrayList;

public class AutoData {
    public static int[] edgePositionsHorizontal1 = {0, 21, 41, 60, 80};
    public static int[] edgePositionsHorizontal = {4, 26, 46, 60, 87};
    public static int[] edgePositionsVertical1 = {0, 2, 4, 6, 8};
    public static int[] edgePositionsVertical = {20, 42, 14, 66, 78};


    // populate a tiles arraylist with 100 tiles




    public static GameBoard createGameBoard(User user, String userId, String gameId, String boardId, OrientationEnum orientation) {
        Log.d("DEBUG AutoData", "createGameBoard: userId: " + userId + " gameId: " + gameId + " boardId: " + boardId);
        GameBoard gameBoard = new GameBoard(user,  gameId);
        Log.d("DEBUG AutoData", "createGameBoard: gameBoard created");
        buildShips(gameBoard.getShips(), orientation);
        Log.d("DEBUG AutoData", "createGameBoard: ships created");
        buildBoard(gameBoard.getBoard() , gameBoard.getShips());
        Log.d("DEBUG AutoData", "createGameBoard: board created");
        return gameBoard;
    }

    private static void buildBoard(ArrayList<Tile> board, ArrayList<Ship> ships) {

        //loop on ships and set tiles on ships positions to be occupied
        for (Ship ship : ships) {
            //loop on size

            for (int i = 0; i < ship.getSize(); i++) {
                int position = ship.getShipPositionsArray().get(i);
                Tile tile = board.get(position);
                tile.setState(TileStateEnum.SHIP);
                tile.setShipId(ship.getId());
            }
        }
    }

    private static void buildShips(ArrayList<Ship> ships,  OrientationEnum orientation) {
        int[] edgePositions ;
        if (orientation == OrientationEnum.HORIZONTAL) {
            edgePositions = AutoData.edgePositionsHorizontal;
        } else {
            edgePositions = AutoData.edgePositionsVertical;
        }
        Log.d("DEBUG AutoData buildShips", "buildShips: orientation: " + orientation);
        ShipsConverter.initNameToIdForTopViews();
        ShipsConverter.initNameToIdForBottomViews();
        Log.d("DEBUG AutoData buildShips", "ShipsConverter set");


        for (int i=0 ; i<edgePositions.length; i++ ) {
            ShipTypeEnum shipType = ShipTypeEnum.values()[i];
            Log.d("DEBUG AutoData buildShips", "buildShips: shipType: " + shipType);

            int shipSize = ShipsConverter.getShipSizeByType(shipType);
            Log.d("DEBUG AutoData buildShips", "buildShips: shipSize: " + shipSize);

            int topId = ShipsConverter.getTopShipIdByType(shipType);
            Log.d("DEBUG AutoData buildShips", "buildShips: topId: " + topId);

            int bottomId = ShipsConverter.getBottomShipIdByType(shipType);
            Log.d("DEBUG AutoData buildShips", "buildShips: bottomId: " + bottomId);

            Ship ship = new Ship(bottomId, topId, shipSize, shipType);
            Log.d("DEBUG AutoData buildShips", "buildShips: ship created");
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
