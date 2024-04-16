package com.clientapp.battleshipclient.logic;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Ship {
     private final int inventoryId;
     private final int frameId;
     private final int size;
     private ArrayList<Integer> shipPositionsArray;
     private int hits;
     private boolean isSunk;
     private boolean isPlaced;
     private Orientation orientation;

    public Ship(int inventoryId, int frameId, int size) {
        this.inventoryId = inventoryId;
        this.frameId = frameId;
        this.size = size;
        this.hits = 0;
        this.isSunk = false;
        this.isPlaced = false;
        this.orientation = Ship.Orientation.VERTICAL; // default orientation
        this.shipPositionsArray = new ArrayList<>();
    }




    public void changeOrientation() {
        if (orientation == Orientation.HORIZONTAL) {
            orientation = Orientation.VERTICAL;
        } else {
            orientation = Orientation.HORIZONTAL;
        }

    }

    public void resetOrientation() {
        this.orientation = Orientation.VERTICAL;
    }

    public void forgetPositions() {
        this.shipPositionsArray.clear();
    }

    public void resetShip() {
        this.setPlaced(false);
        this.forgetPositions();
        this.resetOrientation();
    }

    public boolean getPlaced() {
        return isPlaced;
    }


    public enum Orientation {
        HORIZONTAL, VERTICAL
    }



    public void setShipPositionsArray(ArrayList<Integer> shipPositionsArray) {
        this.shipPositionsArray = shipPositionsArray;
    }

    public ArrayList<Integer> getShipPositionsArray() {
        return shipPositionsArray;
    }



    public void hit() {
        hits++;
        if (hits == size) {
            isSunk = true;
        }
    }

    public boolean isSunk() {
        return isSunk;
    }
}
