package com.clientapp.battleshipclient.model.Ship;

//this class represents the basic data of the extended ship

import java.io.Serializable;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Ship extends BasicShip implements Serializable {
    private final int bottomViewId;
    private final int topViewId;
    private ArrayList<Integer> shipPositionsArray;
    private boolean isSunk;
    private boolean isPlaced;
    private int hits = 0;

    //constructor for ship
    public Ship(int inventoryId, int topViewId, Integer size, ShipTypeEnum shipTypeEnum) {
        this.bottomViewId = inventoryId;
        this.topViewId = topViewId;
        this.isSunk = false;
        this.isPlaced = false;
        this.shipPositionsArray = new ArrayList<>();
        super.setId(topViewId + "");
        super.setOrientation(OrientationEnum.VERTICAL);
        super.setType(shipTypeEnum);
        super.setSize(size);
    }

    //built for opponent board (top board) only. to be used when ship gets sunk
    public Ship(int topViewId, Integer size, ShipTypeEnum shipTypeEnum, OrientationEnum orientation) {
        this.topViewId = topViewId;
        this.bottomViewId = -1;
        this.isSunk = true;
        this.isPlaced = true;
        this.shipPositionsArray = new ArrayList<>();
        super.setId(topViewId + "");
        super.setOrientation(orientation);
        super.setType(shipTypeEnum);
        super.setSize(size);
    }


    /*
    *  Sets the edge position of the ship
    * */
    public void setEdgePosition() {
        if (!shipPositionsArray.isEmpty()) {
            super.setEdgePosition(shipPositionsArray.get(0));
        } else super.setEdgePosition(-1); // no position
    }




    /*
     * Clears the ship's positions array
    * */
    public void forgetPositions() {
        this.shipPositionsArray.clear();
    }






    /*
     * Resets the ship's data to its initial state as in inventory
     * */
    public void resetShip() {
        setPlaced(false);
        forgetPositions();
        resetOrientation();
        setEdgePosition();
    }


    /**
     * Sets the ship's positions array according to the ship's orientation
     * and the edge position of the ship
     * @param edgePosition the position of the ship's edge
     * @param orientationEnum the orientation of the ship
     * */
    public void setShipPositionsArray(int edgePosition, OrientationEnum orientationEnum) {
        if (orientationEnum == OrientationEnum.HORIZONTAL) {
            for (int j = 0; j < super.getSize(); j++) {
                shipPositionsArray.add(edgePosition + j);
            }
        } else { //vertical
            for (int j = 0; j < super.getSize(); j++) {
                shipPositionsArray.add(edgePosition + j * 10);
            }
        }
    }
}



