package com.clientapp.battleshipclient.view.view_utils;

import android.util.Log;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.model.Ship.ShipTypeEnum;

import java.util.HashMap;

import lombok.Data;

/** @noinspection ALL*/
@Data
public class ShipsConverter {

    /*
    *  This class is used to convert the ship type to the corresponding id of the ship in the view
    *  It contains the ship type to id mappings for the bottom and top views
    *  It also contains the lengths of the ships
    * */

    private static HashMap<ShipTypeEnum, Integer> shipNameToRes = new HashMap<>();
    private static int[] shipLengths = {5, 4, 3, 3, 2}; // the lengths of the ships
    private static HashMap<ShipTypeEnum, Integer> bottomShipTypeToId = new HashMap<>();
    private static HashMap<ShipTypeEnum, Integer> topShipTypeToId = new HashMap<>();
    public static final int SHIP_COUNT = 5;
    public static int totalTilesOccupiedByAllShips = 17;

    /*
    *  Initializes the bottom ship type to id mappings
    * It maps the ship type to the corresponding id of the ship in the bottom view
    * It logs the bottom ship type to id mappings
    * */
    public static void initNameToIdForBottomViews(){
        bottomShipTypeToId.put(ShipTypeEnum.BATTLESHIP, R.id.ship_length5_bottom_id);
        bottomShipTypeToId.put(ShipTypeEnum.SUBMARINE, R.id.ship_length4_bottom_id);
        bottomShipTypeToId.put(ShipTypeEnum.CARRIER, R.id.ship_length3_1_bottom_id);
        bottomShipTypeToId.put(ShipTypeEnum.CRUISER, R.id.ship_length3_2_bottom_id);
        bottomShipTypeToId.put(ShipTypeEnum.DESTROYER, R.id.ship_length2_bottom_id);
        // log bottomShipNameToId
        Log.d("DEBUG ShipsConverter", "initNameToIdForBottomViews: bottomShipNameToId: " + bottomShipTypeToId);
    }

    /*
    *  Initializes the top ship type to id mappings
    *  It maps the ship type to the corresponding id of the ship in the top view
    *  It logs the top ship type to id mappings
    * */
    public static void initNameToIdForTopViews(){
        topShipTypeToId.put(ShipTypeEnum.BATTLESHIP, R.id.ship_length5_top_id);
        topShipTypeToId.put(ShipTypeEnum.SUBMARINE, R.id.ship_length4_top_id);
        topShipTypeToId.put(ShipTypeEnum.CARRIER, R.id.ship_length3_1_top_id);
        topShipTypeToId.put(ShipTypeEnum.CRUISER, R.id.ship_length3_2_top_id);
        topShipTypeToId.put(ShipTypeEnum.DESTROYER, R.id.ship_length2_top_id);
        Log.d("DEBUG ShipsConverter", "initNameToIdForBottomViews: topShipNameToId: " + topShipTypeToId);

    }

    /*
    * Returns the bottom ship id by its type
    * @param shipType: the type of the ship
    * */
    public static int getBottomShipIdByType(ShipTypeEnum shipType){
        Log.d("DEBUG ShipsConverter", "getBottomShipIdByType: shipId: " + bottomShipTypeToId.get(shipType));
        if (bottomShipTypeToId.containsKey(shipType)) {
            return bottomShipTypeToId.get(shipType);
        } else {
            throw new IllegalArgumentException("No id found for ship: " + shipType);
        }
    }


    /*
    *  Returns the top ship id by its type
    *  @param shipType: the type of the ship
    * */
    public static int getTopShipIdByType(ShipTypeEnum shipType){
        if (topShipTypeToId.containsKey(shipType)) {
            return topShipTypeToId.get(shipType);
        } else {
            throw new IllegalArgumentException("No id found for ship: " + shipType);
        }
    }


    /*
    *  Returns the size of the ship by its type
    * */
    public static int getShipSizeByType(ShipTypeEnum shipType){
        switch (shipType){
            case BATTLESHIP:
                return 5;
            case SUBMARINE:
                return 4;
            case CARRIER:
                return 3;
            case CRUISER:
                return 3;
            case DESTROYER:
                return 2;
            default:
                return -1;
        }
    }


}
