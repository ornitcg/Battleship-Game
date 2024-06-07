package com.clientapp.battleshipclient.data.Ship;

import android.util.Log;

import com.clientapp.battleshipclient.R;

import java.util.HashMap;

import lombok.Data;

/** @noinspection ALL*/
@Data
public class ShipsResources {
    private static HashMap<ShipTypeEnum, Integer> shipNameToRes = new HashMap<>();
    private static int[] shipLengths = {5, 4, 3, 3, 2}; // the lengths of the ships
    private static HashMap<ShipTypeEnum, Integer> bottomShipTypeToId = new HashMap<>();
    private static HashMap<ShipTypeEnum, Integer> topShipTypeToId = new HashMap<>();
    public static final int SHIP_COUNT = 5;


//    public enum ShipTypeEnum {
//        BATTLESHIP , SUBMARINE, CARRIER, CRUISER, DESTROYER;
//    }

//    public enum OrientationEnum {
//        HORIZONTAL, VERTICAL
//    }
    public static int totalTilesOccupiedByAllShips = 17;


    public static void initNameToIdForBottomViews(){
        bottomShipTypeToId.put(ShipTypeEnum.BATTLESHIP, R.id.ship_length5_bottom_id);
        bottomShipTypeToId.put(ShipTypeEnum.SUBMARINE, R.id.ship_length4_bottom_id);
        bottomShipTypeToId.put(ShipTypeEnum.CARRIER, R.id.ship_length3_1_bottom_id);
        bottomShipTypeToId.put(ShipTypeEnum.CRUISER, R.id.ship_length3_2_bottom_id);
        bottomShipTypeToId.put(ShipTypeEnum.DESTROYER, R.id.ship_length2_bottom_id);
        // log bottomShipNameToId
        Log.d("DEBUG ShipsResources", "initNameToIdForBottomViews: bottomShipNameToId: " + bottomShipTypeToId);
    }

    public static void initNameToIdForTopViews(){
        topShipTypeToId.put(ShipTypeEnum.BATTLESHIP, R.id.ship_length5_top_id);
        topShipTypeToId.put(ShipTypeEnum.SUBMARINE, R.id.ship_length4_top_id);
        topShipTypeToId.put(ShipTypeEnum.CARRIER, R.id.ship_length3_1_top_id);
        topShipTypeToId.put(ShipTypeEnum.CRUISER, R.id.ship_length3_2_top_id);
        topShipTypeToId.put(ShipTypeEnum.DESTROYER, R.id.ship_length2_top_id);
        Log.d("DEBUG ShipsResources", "initNameToIdForBottomViews: topShipNameToId: " + topShipTypeToId);

    }

    public static int getBottomShipIdByType(ShipTypeEnum shipType){
        Log.d("DEBUG ShipsResources", "getBottomShipIdByType: shipId: " + bottomShipTypeToId.get(shipType));
        if (bottomShipTypeToId.containsKey(shipType)) {
            return bottomShipTypeToId.get(shipType);
        } else {
            throw new IllegalArgumentException("No id found for ship: " + shipType);
        }
    }
    public static int getTopShipIdByType(ShipTypeEnum shipType){
        if (topShipTypeToId.containsKey(shipType)) {
            return topShipTypeToId.get(shipType);
        } else {
            throw new IllegalArgumentException("No id found for ship: " + shipType);
        }
    }
//    public static void initNameToRes(){
//        shipNameToRes.put(ShipTypeEnum.BATTLESHIP, R.drawable.ship_length5);
//        shipNameToRes.put(ShipTypeEnum.SUBMARINE, R.drawable.ship_length4);
//        shipNameToRes.put(ShipTypeEnum.CARRIER, R.drawable.ship_length3_1);
//        shipNameToRes.put(ShipTypeEnum.CRUISER, R.drawable.ship_length3_2);
//        shipNameToRes.put(ShipTypeEnum.DESTROYER, R.drawable.ship_length2);
//        //log shipNameToRes
//        Log.d("ShipsResources", "initNameToRes: shipNameToRes: " + shipNameToRes);
//    }


//    public static int getShipResourceByName(ShipTypeEnum shipName) {
//        if (shipNameToRes.containsKey(shipName)) {
//            return shipNameToRes.get(shipName);
//        } else {
//            throw new IllegalArgumentException("No resource found for ship: " + shipName);
//        }
//    }



    //TODO maybe not needed
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
